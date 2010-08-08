/*
 * UnifiedFileOperator.java
 *
 * Copyright (c) 2010 Flaviano Petrocchi <flavianopetrocchi at gmail.com>.
 * All rights reserved.
 *
 * This file is part of JPdfBookmarks.
 *
 * JPdfBookmarks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JPdfBookmarks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JPdfBookmarks.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.flavianopetrocchi.jpdfbookmarks.unifiedfileoperator;

import it.flavianopetrocchi.jpdfbookmarks.pdfview.IPdfView;
//import it.flavianopetrocchi.jpdfbookmarks.itextbookmarksconverter.iTextBookmarksConverter;
import it.flavianopetrocchi.bookmark.IBookmarksConverter;
import it.flavianopetrocchi.bookmark.Bookmark;
//import it.flavianopetrocchi.jpdfbookmarks.JPedalViewPanel;
//import it.flavianopetrocchi.jpdfbookmarks.PdfViewAdapter;
import it.flavianopetrocchi.jpdfbookmarks.jpdfviewpanel.JPedalViewPanel;
import it.flavianopetrocchi.jpdfbookmarks.res.Res;
import it.flavianopetrocchi.utilities.FileOperationEvent;
import it.flavianopetrocchi.utilities.FileOperationListener;
import java.io.File;
import java.util.ArrayList;
import javax.management.ServiceNotFoundException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.openide.util.Lookup;

public class UnifiedFileOperator {

    //private IPdfView viewPanel = new PdfViewAdapter();
    //private IPdfView viewPanel = new PdfRendererViewPanel();
    private IPdfView viewPanel;// = new JPedalViewPanel();
    private String filePath;
    private File file;
    private boolean showOnOpen = false;
    private Bookmark root;

    public UnifiedFileOperator() {
        viewPanel = new JPedalViewPanel();
    }

    private IPdfView getPdfView() throws ServiceNotFoundException {
        IPdfView pdfView = Lookup.getDefault().lookup(IPdfView.class);
        if (pdfView == null) {
            throw new ServiceNotFoundException(Res.getString("ERROR_PDFVIEW_PROVIDER_NOT_FOUND"));
        } else {
            return pdfView;
        }
    }

    public File getFile() {
        return file;
    }

    public String getFilePath() {
        return filePath;
    }
    private boolean fileChanged = false;
    private ArrayList<FileOperationListener> fileOperationListeners =
            new ArrayList<FileOperationListener>();

    private IBookmarksConverter getBookmarksConverter() throws ServiceNotFoundException {
        IBookmarksConverter bookmarksConverter = Lookup.getDefault().lookup(IBookmarksConverter.class);
        if (bookmarksConverter == null) {
            throw new ServiceNotFoundException(Res.getString("ERROR_BOOKMARKS_CONVERTER_NOT_FOUND"));
        } else {
            return bookmarksConverter;
        }
    }

    public void open(File file) throws Exception {
        this.file = file;
        filePath = file.getAbsolutePath();
        //IBookmarksConverter bookmarksConverter = new iTextBookmarksConverter(filePath);
        IBookmarksConverter bookmarksConverter = getBookmarksConverter();
        showOnOpen = bookmarksConverter.showBookmarksOnOpen();
        root = bookmarksConverter.getRootBookmark();
//        if (viewPanel instanceof PdfViewAdapter) {
//            PdfViewAdapter v = (PdfViewAdapter) viewPanel;
//            v.setIBookmarksConverter(bookmarksConverter);
//        } else {
            bookmarksConverter.close();
            bookmarksConverter = null;
//        }
        viewPanel.open(file);
        fireFileOperationEvent(new FileOperationEvent(this, filePath,
                FileOperationEvent.Operation.FILE_OPENED));
    }

    public Bookmark getRootBookmark() {
        return root;
    }

    public void close() {
        viewPanel.close();
        fileChanged = false;
        fireFileOperationEvent(new FileOperationEvent(this, filePath,
                FileOperationEvent.Operation.FILE_CLOSED));
        filePath = null;
    }

    public void setFileChanged(boolean changed) {
        if (fileChanged != changed) {
            fileChanged = changed;
            fireFileOperationEvent(new FileOperationEvent(this, filePath,
                    FileOperationEvent.Operation.FILE_CHANGED));
        }
    }

    public boolean getFileChanged() {
        return fileChanged;
    }

    public boolean save(Bookmark root) {
        return saveAs(root, filePath);
    }

    public boolean saveAs(Bookmark root, String path) {
        try {
            //IBookmarksConverter bookmarksConverter = new iTextBookmarksConverter(filePath);
            IBookmarksConverter bookmarksConverter = Lookup.getDefault().lookup(IBookmarksConverter.class);
            bookmarksConverter.setShowBookmarksOnOpen(showOnOpen);
            bookmarksConverter.rebuildBookmarksFromTreeNodes(root);
            bookmarksConverter.save(path);
            bookmarksConverter.close();
            bookmarksConverter = null;
            this.filePath = path;
            this.file = new File(path);
            viewPanel.reopen(file);
            fireFileOperationEvent(new FileOperationEvent(this, path,
                    FileOperationEvent.Operation.FILE_SAVED));
            setFileChanged(false);
            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "JPdfBookmarks",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public IPdfView getViewPanel() throws ServiceNotFoundException {
        if (viewPanel == null) {
            return getPdfView();
        }
        return viewPanel;
    }

    public void addFileOperationListener(FileOperationListener listener) {
        fileOperationListeners.add(listener);
    }

    public void removeFileOperationListener(FileOperationListener listener) {
        fileOperationListeners.remove(listener);
    }

    public boolean getShowBookmarksOnOpen() {
        return showOnOpen;
    }

    public void setShowBookmarksOnOpen(boolean show) {
        showOnOpen = show;
        setFileChanged(true);
    }

    private void fireFileOperationEvent(FileOperationEvent e) {
        if (SwingUtilities.isEventDispatchThread()) {
            for (FileOperationListener listener : fileOperationListeners) {
                listener.fileOperation(e);
            }
        } else {
            SwingUtilities.invokeLater(new FireInEventThread(e));
        }
    }

    private class FireInEventThread implements Runnable {

        FileOperationEvent e;

        public FireInEventThread(FileOperationEvent e) {
            this.e = e;
        }

        public void run() {
            fireFileOperationEvent(e);
        }
    }
}

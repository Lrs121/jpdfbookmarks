package it.flavianopetrocchi.jpdfbookmarks.pdffiletype;

import it.flavianopetrocchi.bookmark.Bookmark;
import it.flavianopetrocchi.cursortoolkit.CursorToolkit;
import it.flavianopetrocchi.jpdfbookmarks.pdfview.IPdfView;
import it.flavianopetrocchi.jpdfbookmarks.pdfviewadapter.PdfViewAdapter;
import it.flavianopetrocchi.jpdfbookmarks.pdfviewer.PdfTopComponent;
import it.flavianopetrocchi.jpdfbookmarks.res.Res;
import it.flavianopetrocchi.jpdfbookmarks.unifiedfileoperator.UnifiedFileOperator;
import java.awt.Component;
import java.io.File;
import javax.management.ServiceNotFoundException;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.cookies.CloseCookie;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.OpenSupport;
import org.openide.windows.CloneableTopComponent;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

public class PdfOpenSupport extends OpenSupport implements OpenCookie, CloseCookie {

    private final UnifiedFileOperator fileOperator;
    private IPdfView viewPanel;

    public PdfOpenSupport(MultiDataObject.Entry entry) {
        super(entry);
        fileOperator = new UnifiedFileOperator();
    }

    protected CloneableTopComponent createCloneableTopComponent() {
        PdfDataObject dobj = (PdfDataObject) entry.getDataObject();
        PdfTopComponent tc = new PdfTopComponent();
        try {
            viewPanel = fileOperator.getViewPanel();
        } catch (ServiceNotFoundException ex) {
            viewPanel = new PdfViewAdapter();
        }
        tc.add((Component)viewPanel);
        org.openide.windows.Mode explorer = WindowManager.getDefault().findMode("explorer");
        TopComponent[] comps = WindowManager.getDefault().getOpenedTopComponents(explorer);
        openFileAsync(FileUtil.toFile(dobj.getPrimaryFile()), null, comps[0]);
        return tc;
    }

    public void openFileAsync(final File file, final Bookmark target, final JComponent comp) {
        //setProgressBar(Res.getString("WAIT_LOADING_FILE"));
        CursorToolkit.startWaitCursor(comp);

        SwingWorker opener = new SwingWorker<Bookmark, Void>() {

            @Override
            protected Bookmark doInBackground() throws Exception {
                fileOperator.open(file);
                Bookmark root = fileOperator.getRootBookmark();
                return root;
            }

            @Override
            protected void done() {
                Bookmark root = null;
                try {
                    root = get();
                    if (root != null) {
//                        bookmarksTreeModel.setRoot(root);
//                        recreateNodesOpenedState();
                    } else {
//                        bookmarksTreeModel.setRoot(new Bookmark());
                    }
//                    bookmarksTree.setRootVisible(false);
//                    bookmarksTree.setEditable(true);
//                    bookmarksTree.treeDidChange();
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            if (target != null) {
//                                followBookmarkInView(target);
                            } else {
                                viewPanel.goToFirstPage();
                            }
                        }
                    });
                } catch (Exception ex) {
                    NotifyDescriptor nd = new NotifyDescriptor.Message(Res.getString("ERROR_OPENING_FILE") + " "
                            + file.getName());
                    DialogDisplayer.getDefault().notify(nd);

                } finally {
                    CursorToolkit.stopWaitCursor(comp);
//                    removeProgressBar();
                }
            }
        };
        opener.execute();
    }
}

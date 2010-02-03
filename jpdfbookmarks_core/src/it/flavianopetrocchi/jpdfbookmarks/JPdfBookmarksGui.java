/*
 * JPdfBookmarksGui.java
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
package it.flavianopetrocchi.jpdfbookmarks;

import it.flavianopetrocchi.colors.ColorsListPanel;
import it.flavianopetrocchi.labelvertical.VerticalLabel;
import it.flavianopetrocchi.labelvertical.VerticalLabelUI;
import it.flavianopetrocchi.linklabel.LinkLabel;
import it.flavianopetrocchi.mousedraggabletree.MouseDraggableTree;
import it.flavianopetrocchi.utilities.FileOperationEvent;
import it.flavianopetrocchi.utilities.FileOperationListener;
import it.flavianopetrocchi.utilities.IntegerTextField;
import it.flavianopetrocchi.utilities.SimpleFileFilter;
import it.flavianopetrocchi.utilities.Ut;
import it.flavianopetrocchi.mousedraggabletree.UndoableNodeMoved;
import it.flavianopetrocchi.mousedraggabletree.Visitor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.CellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;

class JPdfBookmarksGui extends JFrame implements FileOperationListener,
        PageChangedListener, ViewChangedListener, TreeExpansionListener,
        UndoableEditListener, TreeSelectionListener, CellEditorListener,
        RenderingStartListener, TextCopiedListener {

    // <editor-fold defaultstate="collapsed" desc="Members">
    private final int ZOOM_STEP = 10;
    private int windowState;
    private JSplitPane centralSplit;
    private String title = "JPdfBookmarks";
    private Prefs userPrefs = new Prefs();
    private int numPages = 0;
    private JScrollPane bookmarksScroller;
    private BookmarksTree bookmarksTree;
    private DefaultTreeModel bookmarksTreeModel;
    private JTabbedPane leftTabbedPane;
    private UnifiedFileOperator fileOperator;
    private IPdfView viewPanel;
    private JToolBar navigationToolbar;
    private ButtonGroup zoomMenuItemsGroup;
    private JRadioButtonMenuItem rbFitWidth;
    private JRadioButtonMenuItem rbFitHeight;
    private JRadioButtonMenuItem rbFitPage;
    private JRadioButtonMenuItem rbFitNative;
    private JRadioButtonMenuItem rbTopLeftZoom;
    private JRadioButtonMenuItem rbFitRect;
    private JCheckBoxMenuItem cbBold;
    private JCheckBoxMenuItem cbItalic;
    private JCheckBoxMenuItem cbEditMenuBold;
    private JCheckBoxMenuItem cbEditMenuItalic;
    private JCheckBoxMenuItem cbShowOnOpen;
    private JCheckBoxMenuItem cbSelectText;
    private JCheckBoxMenuItem cbConnectToClipboard;
    private ButtonGroup zoomButtonsGroup;
    private JToggleButton tbShowOnOpen;
    private JToggleButton tbFitWidth;
    private JToggleButton tbFitHeight;
    private JToggleButton tbFitPage;
    private JToggleButton tbFitNative;
    private JToggleButton tbTopLeftZoom;
    private JToggleButton tbFitRect;
    private JToggleButton tbBold;
    private JToggleButton tbItalic;
    private JLabel lblPageOfPages;
    private JLabel lblMouseOverNode;
    private JLabel lblSelectedNode;
    private JLabel lblCurrentView;
    private JLabel lblPercent;
    private JLabel lblStatus;
    private IntegerTextField txtGoToPage;
    private IntegerTextField txtZoom;
    private ExtendedUndoManager undoManager;
    private UndoableEditSupport undoSupport;
    private JPopupMenu treeMenu;
    private JColorChooser colorChooser;
    private JProgressBar progressBar;
    private Box busyPanel;
    private JCheckBox checkInheritTop;
    private JCheckBox checkInheritLeft;
    private JCheckBox checkInheritZoom;
    private VerticalLabel lblInheritLeft;
    private JMenu openRecent;
    private JToggleButton tbSelectText;
    private JToggleButton tbConnectToClipboard;// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Actions">
    private Action quitAction;
    //File actions
    private Action openAction;
    private Action saveAction;
    private Action saveAsAction;
    private Action closeAction;
    private Action dumpAction;
    private Action loadAction;
    //Navigation actions
    private Action goNextPageAction;
    private Action goLastPageAction;
    private Action goPreviousPageAction;
    private Action goFirstPageAction;
    private Action goToPageAction;
    //Zoom actions
    private Action fitWidthAction;
    private Action fitContentWidthAction;
    private Action fitHeightAction;
    private Action fitContentHeightAction;
    private Action fitNativeAction;
    private Action fitPageAction;
    private Action fitContentAction;
    private Action topLeftZoomAction;
    private Action fitRectAction;
    private Action zoomInAction;
    private Action zoomOutAction;
    //Bookmarks actions
    private Action undoAction;
    private Action redoAction;
    private Action expandAllAction;
    private Action collapseAllAction;
    private Action addSiblingAction;
    private Action addChildAction;
    private Action setBoldAction;
    private Action setItalicAction;
    private Action deleteAction;
    private Action renameAction;
    private Action changeColorAction;
    private Action setDestFromViewAction;
    private Action showOnOpenAction;
    private Action addWebLinkAction;
    private Action applyPageOffset;
    private Action optionsDialogAction;
    private Action checkUpdatesAction;
    private Action readOnlineManualAction;
    private Action goToAuthorBlog;
    private Action selectText;
    private Action connectToClipboard;// </editor-fold>

    private void saveWindowState() {
        userPrefs.setWindowState(windowState);
        if (windowState == JFrame.MAXIMIZED_BOTH) {
            userPrefs.setLocation(null);
            userPrefs.setSize(null);
        } else {
            userPrefs.setLocation(getLocation());
            userPrefs.setSize(getSize());
        }
        userPrefs.setSplitterLocation(centralSplit.getDividerLocation());
    }

    private void loadWindowState() {
        Ut.changeLAF(userPrefs.getLAF(), this);
        setSize(userPrefs.getSize());
        setLocation(userPrefs.getLocation());
        setExtendedState(userPrefs.getWindowState());
    }

    public JPdfBookmarksGui() {
        Authenticator.setDefault(new ProxyAuthenticator(this, true));

        undoManager = new ExtendedUndoManager();
        undoSupport = new UndoableEditSupport(this);

        setTitle(title);
        setIconImage(Res.getIcon(getClass(), "gfx/jpdfbookmarks.png").getImage());
        loadWindowState();

        fileOperator = new UnifiedFileOperator();
        viewPanel = fileOperator.getViewPanel();
        viewPanel.addTextCopiedListener(this);
        
        initComponents();

        fileOperator.addFileOperationListener(this);
        viewPanel.addPageChangedListener(this);
        viewPanel.addViewChangedListener(this);
        undoSupport.addUndoableEditListener(undoManager);
        undoSupport.addUndoableEditListener(this);

        //set window close button event to ask for save option
        WindowAdapter wndCloser = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                saveWindowState();
                exitApplication();
            }

            @Override
            public void windowStateChanged(WindowEvent e) {
                windowState = e.getNewState();
            }

            @Override
            public void windowOpened(WindowEvent e) {
                if (userPrefs.getCheckUpdatesOnStart()) {
                    checkUpdates(true);
                }
            }
        };

        addWindowListener(wndCloser);
        addWindowStateListener(wndCloser);
    }

    public boolean askCloseWithoutSave() {
        if (!fileOperator.getFileChanged()) {
            return true;
        }

        int response = JOptionPane.showConfirmDialog(
                this,
                Res.getString("ASK_SAVE_CHANGES"), title,
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE);

        switch (response) {
            case JOptionPane.YES_OPTION:
                return fileOperator.save((Bookmark) bookmarksTreeModel.getRoot());
            case JOptionPane.NO_OPTION:
                return true;
            case JOptionPane.CANCEL_OPTION:
                return false;
        }

        return true;
    }

    private void exitApplication() {
        if (!askCloseWithoutSave()) {
            return;
        }

        fileOperator.close();

        System.exit(0);
    }

    public void fileOperation(FileOperationEvent evt) {
        if (evt.getOperation() == FileOperationEvent.Operation.FILE_OPENED) {
            setTitle(title + ": " + evt.getPathToFile());
            userPrefs.setLastDirectory(evt.getPathToFile());
            userPrefs.addRecentFile(evt.getPathToFile());
            createRecentFilesItems();
            lblPageOfPages.setText(String.format(" / %d ",
                    viewPanel.getNumPages()));
            Ut.enableComponents(true, lblPageOfPages, txtGoToPage, txtZoom,
                    lblPercent);
            Ut.enableActions(true, saveAsAction, closeAction, fitWidthAction,
                    fitHeightAction, fitPageAction, fitNativeAction,
                    zoomInAction, zoomOutAction, goToPageAction, fitRectAction,
                    expandAllAction, collapseAllAction, topLeftZoomAction,
                    addSiblingAction, showOnOpenAction, dumpAction, loadAction,
                    selectText, connectToClipboard);
            tbShowOnOpen.setSelected(fileOperator.getShowBookmarksOnOpen());
            cbShowOnOpen.setSelected(fileOperator.getShowBookmarksOnOpen());
            switch (viewPanel.getFitType()) {
                case FitWidth:
                    tbFitWidth.setSelected(true);
                    rbFitWidth.setSelected(true);
                    break;
                case FitHeight:
                    tbFitHeight.setSelected(true);
                    rbFitHeight.setSelected(true);
                    break;
                case FitPage:
                    tbFitPage.setSelected(true);
                    rbFitPage.setSelected(true);
                    break;
                case FitNative:
                    tbFitNative.setSelected(true);
                    rbFitNative.setSelected(true);
                    break;
                case FitRect:
                    tbFitRect.setSelected(true);
                    rbFitRect.setSelected(true);
                    break;
            }
        } else if (evt.getOperation() == FileOperationEvent.Operation.FILE_CLOSED) {
            setTitle(title);
            txtGoToPage.setText("0");
            lblPageOfPages.setText(" / 0 ");
            txtZoom.setText("0");
            Ut.enableComponents(false, lblPageOfPages, txtGoToPage, txtZoom,
                    lblPercent);
            Ut.enableActions(false, saveAsAction, closeAction, fitWidthAction,
                    fitHeightAction, fitPageAction, fitNativeAction,
                    zoomInAction, zoomOutAction, goFirstPageAction,
                    goPreviousPageAction, goNextPageAction, goLastPageAction,
                    goToPageAction, expandAllAction, collapseAllAction,
                    topLeftZoomAction, fitRectAction, addSiblingAction,
                    addChildAction, deleteAction, undoAction, redoAction,
                    showOnOpenAction, setBoldAction, setItalicAction,
                    renameAction, setDestFromViewAction, changeColorAction,
                    dumpAction, loadAction, addWebLinkAction, saveAction,
                    applyPageOffset, selectText, connectToClipboard);
            lblMouseOverNode.setText(" ");
            lblSelectedNode.setText(" ");
            lblCurrentView.setText(" ");
            setEmptyBookmarksTree();
            undoManager.die();
        } else if (evt.getOperation() == FileOperationEvent.Operation.FILE_CHANGED) {
            if (fileOperator.getFileChanged()) {
                setTitle(title + ": " + evt.getPathToFile() + " *");
                Ut.enableActions(true, saveAction);
            }
        } else if (evt.getOperation() == FileOperationEvent.Operation.FILE_SAVED) {
            setTitle(title + ": " + evt.getPathToFile());
            userPrefs.setLastDirectory(evt.getPathToFile());
            userPrefs.addRecentFile(evt.getPathToFile());
            createRecentFilesItems();
            Ut.enableActions(false, saveAction);
        }
    }

    public void pageChanged(PageChangedEvent evt) {
        int currentPage = evt.getCurrentPage();
        txtGoToPage.setInteger(currentPage);
        if (evt.hasPrevious()) {
            Ut.enableActions(true, goPreviousPageAction, goFirstPageAction);
        } else {
            Ut.enableActions(false, goPreviousPageAction, goFirstPageAction);
        }

        if (evt.hasNext()) {
            Ut.enableActions(true, goNextPageAction, goLastPageAction);
        } else {
            Ut.enableActions(false, goNextPageAction, goLastPageAction);
        }
    }

    private void enableInheritChecks(boolean top, boolean left, boolean zoom) {
        checkInheritTop.setEnabled(top);
        checkInheritLeft.setEnabled(left);
        lblInheritLeft.setEnabled(left);
        checkInheritZoom.setEnabled(zoom);
    }

    public void viewChanged(ViewChangedEvent evt) {
        FitType fitType = evt.getFitType();
        float scale = evt.getScale();
        int zoom = Math.round(scale * 100);
        Bookmark bookmark = evt.getBookmark();
//		lblCurrentView.setText(Res.getString("CURRENT_VIEW") + ": [" +
//				fitType + "  zoom: " + zoom + " %]");
        lblCurrentView.setText(Res.getString("CURRENT_VIEW") + ": " + bookmark.getDescription(userPrefs.getUseThousandths()));
        txtZoom.setInteger(zoom);
        switch (fitType) {
            case FitWidth:
                enableInheritChecks(true, false, false);
                tbFitWidth.setSelected(true);
                rbFitWidth.setSelected(true);
                break;
            case FitHeight:
                enableInheritChecks(false, true, false);
                tbFitHeight.setSelected(true);
                rbFitHeight.setSelected(true);
                break;
            case FitPage:
                enableInheritChecks(false, false, false);
                tbFitPage.setSelected(true);
                rbFitPage.setSelected(true);
                break;
            case FitNative:
                enableInheritChecks(true, true, true);
                tbFitNative.setSelected(true);
                rbFitNative.setSelected(true);
                break;
            case TopLeftZoom:
                enableInheritChecks(true, true, true);
                tbTopLeftZoom.setSelected(true);
                rbTopLeftZoom.setSelected(true);
                break;
            case FitRect:
                enableInheritChecks(false, false, false);
                tbFitRect.setSelected(true);
                rbFitRect.setSelected(true);
                break;
        }
    }

    public void treeExpanded(TreeExpansionEvent event) {
        TreePath path = event.getPath();
        Bookmark bookmark = (Bookmark) path.getLastPathComponent();
        if (!bookmark.isOpened()) {
            bookmark.setOpened(true);
            fileOperator.setFileChanged(true);
        }
        recreateNodesOpenedState();
    }

    public void treeCollapsed(TreeExpansionEvent event) {
        TreePath path = event.getPath();
        Bookmark bookmark = (Bookmark) path.getLastPathComponent();
        if (bookmark.isOpened()) {
            bookmark.setOpened(false);
            fileOperator.setFileChanged(true);
        }
    }

    public void undoableEditHappened(UndoableEditEvent e) {
        UndoableEdit[] undoableEdits = undoManager.getUndoableEdits();
        if (undoableEdits.length > 0) {
            UndoableEdit undo = undoableEdits[0];
            if (undo instanceof UndoableNodeMoved) {
                recreateNodesOpenedState();
            }
        }
        updateUndoRedoPresentation();
        fileOperator.setFileChanged(true);
    }

    public void valueChanged(TreeSelectionEvent e) {
        Bookmark bookmark = getSelectedBookmark();
        Ut.enableActions((bookmark != null), addChildAction, deleteAction,
                setBoldAction, setItalicAction, renameAction, changeColorAction,
                setDestFromViewAction, addWebLinkAction, applyPageOffset);
        if (bookmark != null) {
            updateStyleButtons(bookmark);
        }
    }

    private Bookmark getSelectedBookmark() {
        TreePath path = bookmarksTree.getSelectionPath();
        if (path == null) {
            return null;
        }

        Bookmark treeNode = null;
        try {
            treeNode = (Bookmark) path.getLastPathComponent();
        } catch (ClassCastException e) {
        }
        return treeNode;
    }

    public void editingStopped(ChangeEvent e) {
        Bookmark treeNode = getSelectedBookmark();
        if (treeNode == null) {
            return;
        }
        String oldValue = treeNode.getTitle().trim();
        CellEditor treeEditor = (CellEditor) e.getSource();
        String value = treeEditor.getCellEditorValue().toString().trim();

        UndoableCellEdit undoableCellEdit = new UndoableCellEdit(
                bookmarksTreeModel, treeNode, value);
        undoableCellEdit.doEdit();
        if (oldValue.equals(value) ||
                oldValue.equals(Res.getString("DEFAULT_TITLE").trim())) {
        } else {
            undoSupport.postEdit(undoableCellEdit);
        }
    }

    public void editingCanceled(ChangeEvent e) {
    }

    public void renderingStart(RenderingStartEvent evt) {
        lblStatus.setText("Rendering page " + evt.getPageNumber() + " wait ...");
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    @Override
    public void textCopied(TextCopiedEvent evt) {
        String text = evt.getText();
        if (text == null) {
            text = "";
        }
        
        lblStatus.setText(Res.getString("EXTRACTED") + ": " + text);
    }

    abstract class ActionBuilder extends AbstractAction {

        public ActionBuilder(String resName, String resDescription,
                String accelerator, String resIcon, boolean enabled) {
            super(Res.getString(resName));
            String description = null;
            if (resDescription != null) {
                description = Res.getString(resDescription);
            }
            if (accelerator != null) {
                putValue(Action.ACCELERATOR_KEY,
                        KeyStroke.getKeyStroke(accelerator));
                description += " [" + accelerator.toUpperCase() + "]";
            }
            if (description != null) {
                putValue(Action.SHORT_DESCRIPTION, description);
            }

            if (resIcon != null) {
                putValue(Action.SMALL_ICON, Res.getIcon(getClass(),
                        "gfx16/" + resIcon));
                putValue(Action.LARGE_ICON_KEY, Res.getIcon(getClass(),
                        "gfx22/" + resIcon));
            }
            setEnabled(enabled);
        }
    }

    private void openDialog() {
//		if (!askCloseWithoutSave()) {
//			return;
//		}
//		fileOperator.close();


        JFileChooser chooser = new JFileChooser(userPrefs.getLastDirectory());
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Pdf File",
                "pdf");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(filter);

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            final File file = chooser.getSelectedFile();
            if (file != null && file.isFile()) {
                close();
                openFileAsync(file);
            }
        }
    }

    private void setProgressBar(String message) {
        lblStatus.setText(message);
        busyPanel.add(progressBar);
    }

    private void removeProgressBar() {
        lblStatus.setText(" ");
        busyPanel.remove(progressBar);
    }

    public void openFileAsync(final File file) {
        setProgressBar(Res.getString("WAIT_LOADING_FILE"));
        CursorToolkit.startWaitCursor(tbBold);

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
                        bookmarksTreeModel.setRoot(root);
                        recreateNodesOpenedState();
                    } else {
                        bookmarksTreeModel.setRoot(new Bookmark());
                    }
                    bookmarksTree.setRootVisible(false);
                    bookmarksTree.setEditable(true);
                    bookmarksTree.treeDidChange();
                    SwingUtilities.invokeLater(new Runnable() {

                        public void run() {
                            viewPanel.goToFirstPage();
                        }
                    });
                } catch (Exception ex) {
                    showErrorMessage(Res.getString("ERROR_OPENING_FILE") + " " +
                            file.getName());
                } finally {
                    CursorToolkit.stopWaitCursor(tbBold);
                    removeProgressBar();
                }
            }
        };
        opener.execute();
    }

    private void recreateNodesOpenedState() {

        bookmarksTree.visitAllNodes(new Visitor<Bookmark>() {

            public void process(Bookmark bookmark) {
                TreePath path = new TreePath(bookmark.getPath());
                if (bookmark.isOpened() && bookmarksTree.isVisible(path)) {
                    bookmarksTree.expandPath(path);
                }
            }
        });
    }

    private class ColorChooserListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            TreePath[] paths = bookmarksTree.getSelectionPaths();
            for (TreePath path : paths) {
                Bookmark bookmark = (Bookmark) path.getLastPathComponent();
                bookmark.setColor(colorChooser.getColor());
            }
        }
    }

    private void undo() {
        try {
            undoManager.undo();
        } catch (CannotUndoException ex) {
        } finally {
            updateUndoRedoPresentation();
            recreateNodesOpenedState();
        }
    }

    private void redo() {
        try {
            undoManager.redo();
        } catch (CannotUndoException ex) {
        } finally {
            updateUndoRedoPresentation();
            recreateNodesOpenedState();
        }
    }

    private void adjustInheritValues(Bookmark bookmark) {
        if (checkInheritTop.isSelected()) {
            bookmark.setTop(-1);
        }
        if (checkInheritLeft.isSelected()) {
            bookmark.setLeft(-1);
        }
        if (checkInheritZoom.isSelected()) {
            bookmark.setZoom(0.0f);
        }
    }

    private void addSibling() {
        Bookmark bookmark = viewPanel.getBookmarkFromView();
        adjustInheritValues(bookmark);
        Bookmark selected = getSelectedBookmark();
        Bookmark parent;
        if (selected == null) {
            parent = (Bookmark) bookmarksTreeModel.getRoot();
            parent.add(bookmark);
        } else {
            parent = (Bookmark) selected.getParent();
            int selectedPosition = parent.getIndex(selected);
            parent.insert(bookmark, selectedPosition + 1);
        }
        bookmarksTreeModel.nodeStructureChanged(parent);
        recreateNodesOpenedState();
        bookmarksTree.startEditingAtPath(
                new TreePath(bookmark.getPath()));
        fileOperator.setFileChanged(true);

    }

    private void addChild() {
        Bookmark bookmark = viewPanel.getBookmarkFromView();
        adjustInheritValues(bookmark);
        Bookmark selected = getSelectedBookmark();
        if (selected != null) {
            selected.add(bookmark);
            bookmarksTreeModel.nodeStructureChanged(selected);
            recreateNodesOpenedState();
            bookmarksTree.startEditingAtPath(
                    new TreePath(bookmark.getPath()));
        }
        fileOperator.setFileChanged(true);
    }

    private void setWebLink() {
        String address = JOptionPane.showInputDialog(this,
                Res.getString("INPUT_WEB_ADDRESS") + ": ");

        if (address == null) {
            return;
        }

        Bookmark bookmark = getSelectedBookmark();
        if (bookmark != null) {
            bookmark.setType(BookmarkType.Uri);
            bookmark.setUri(address);
        }
        fileOperator.setFileChanged(true);

        goToWebLink(address);
    }

    private void goToWebLink(String uri) {
        int answer = JOptionPane.showConfirmDialog(this,
                Res.getString("MSG_LAUNCH_BROWSER"), title,
                JOptionPane.OK_CANCEL_OPTION);

        if (answer != JOptionPane.OK_OPTION) {
            return;
        }

        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URI(uri));
        } catch (URISyntaxException ex) {
            showErrorMessage(Res.getString("ERROR_WRONG_URI"));
        } catch (IOException ex) {
            showErrorMessage(Res.getString("ERROR_LAUNCHING_BROWSER"));
        }
    }

    private void delete() {

        ArrayList<Bookmark> deleteList = new ArrayList<Bookmark>();
        TreePath[] paths = bookmarksTree.getSelectionPaths();
        for (TreePath path : paths) {
            deleteList.add((Bookmark) path.getLastPathComponent());
        }

        UndoableDeleteBookmark undoableDelete =
                new UndoableDeleteBookmark(
                bookmarksTreeModel, deleteList);

        undoableDelete.doEdit();
        recreateNodesOpenedState();
        undoSupport.postEdit(undoableDelete);
    }

    private void setBold(boolean bold) {
        TreePath[] paths = bookmarksTree.getSelectionPaths();
        for (TreePath path : paths) {
            Bookmark bookmark = (Bookmark) path.getLastPathComponent();
            bookmark.setBold(bold);
        }
        //try to trigger a selection to update the menu presentation
//		bookmarksTree.setSelectionPaths(paths);
        SwingUtilities.updateComponentTreeUI(bookmarksTree);
        valueChanged(null);
        fileOperator.setFileChanged(true);
    }

    private void setItalic(boolean italic) {
        TreePath[] paths = bookmarksTree.getSelectionPaths();
        for (TreePath path : paths) {
            Bookmark bookmark = (Bookmark) path.getLastPathComponent();
            bookmark.setItalic(italic);
        }
        SwingUtilities.updateComponentTreeUI(bookmarksTree);
        valueChanged(null);
        fileOperator.setFileChanged(true);
    }

    private void changeColor() {
        colorChooser = new JColorChooser();
        ColorsListPanel panel = new ColorsListPanel();
        panel.setName(Res.getString("BROWSERS_KNOWN_COLORS"));
        colorChooser.addChooserPanel(panel);
        colorChooser.setColor(getSelectedBookmark().getColor());
        JColorChooser.createDialog(JPdfBookmarksGui.this,
                Res.getString("ACTION_CHANGE_COLOR"), true, colorChooser,
                new ColorChooserListener(), null).setVisible(true);

        fileOperator.setFileChanged(true);
    }

    private void rename() {
        bookmarksTree.startEditingAtPath(
                bookmarksTree.getSelectionPath());
    }

    private void destFromView() {
        int answer = JOptionPane.showConfirmDialog(JPdfBookmarksGui.this,
                Res.getString("MSG_SET_DESTINATION"), title,
                JOptionPane.OK_CANCEL_OPTION);

        if (answer != JOptionPane.OK_OPTION) {
            return;
        }

        Bookmark bookmark = getSelectedBookmark();
        if (bookmark != null) {
            Bookmark fromView = viewPanel.getBookmarkFromView();
            adjustInheritValues(fromView);
            UndoableSetDestination undoable =
                    new UndoableSetDestination(bookmarksTreeModel,
                    bookmark, fromView);
            undoable.doEdit();
            undoSupport.postEdit(undoable);
        }
    }

    public void showErrorMessage(String resMessage) {
        JOptionPane.showMessageDialog(JPdfBookmarksGui.this,
                resMessage, title,
                JOptionPane.ERROR_MESSAGE);
    }

    private void save() {
        if (!fileOperator.save((Bookmark) bookmarksTreeModel.getRoot())) {
            showErrorMessage(Res.getString("ERROR_SAVING_FILE"));
        }
    }

    private void saveAs() {

        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new SimpleFileFilter("pdf", "PDF File"));
        chooser.setCurrentDirectory(fileOperator.getFile().getParentFile());


        if (chooser.showSaveDialog(JPdfBookmarksGui.this) !=
                JFileChooser.APPROVE_OPTION) {
            return;
        }

        File f = chooser.getSelectedFile();
        if (f == null) {
            return;
        }

        String filename = f.getAbsolutePath();
        if (filename.endsWith(".pdf") == false) {
            filename = filename + ".pdf";
            f = new File(filename);
        }

        if (f.exists()) {
            int response = JOptionPane.showConfirmDialog(
                    JPdfBookmarksGui.this,
                    Res.getString("WARNING_OVERWRITE"),
                    title,
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (response != JOptionPane.YES_OPTION) {
                return;
            }
        }

        fileOperator.saveAs((Bookmark) bookmarksTreeModel.getRoot(),
                f.getAbsolutePath());
    }

    private void close() {
        if (!askCloseWithoutSave()) {
            return;
        }

        fileOperator.close();
    }

    private void load() {

        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new SimpleFileFilter("txt", "Text Files"));
        chooser.setCurrentDirectory(fileOperator.getFile().getParentFile());

        chooser.setDialogTitle(Res.getString("LOAD_DIALOG_TITLE"));

        if (chooser.showOpenDialog(this) !=
                JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        if (file == null || !file.isFile()) {
            return;
        }

        try {
            IBookmarksConverter converter =
                    new iTextBookmarksConverter(fileOperator.getFilePath());
            Bookmark root = Bookmark.outlineFromFile(converter,
                    file.getAbsolutePath(), userPrefs.getIndentationString(),
                    userPrefs.getPageSeparator(),
                    userPrefs.getAttributesSeparator());
            converter.close();
            UndoableLoadBookmarks undoableLoad = new UndoableLoadBookmarks(
                    bookmarksTreeModel, bookmarksTree, root);
            undoableLoad.doEdit();
            undoSupport.postEdit(undoableLoad);
            fileOperator.setFileChanged(true);
            recreateNodesOpenedState();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            showErrorMessage(Res.getString("ERROR_LOADING_TEXT_FILE"));
        }
    }

    private JPanel newVersionAvailable(boolean available) {
        JPanel panel = new JPanel(new FlowLayout());
        if (available) {
            panel.add(new JLabel(Res.getString("NEW_VERSION_AVAILABLE")));
            LinkLabel address;
            try {
                address = new LinkLabel(new URI(JPdfBookmarks.DOWNLOAD_URL),
                        " " + Res.getString("DOWNLOAD_PAGE"));
                address.setUnderlineVisible(false);
                address.setBorder(null);
                address.init();
                panel.add(address);
            } catch (URISyntaxException ex) {
                showErrorMessage(Res.getString("ERROR_CHECKING_UPDATES"));
            }
        } else {
            panel.add(new JLabel(Res.getString("NO_NEW_VERSION_AVAILABLE")));
        }
        return panel;
    }

    private class LastVersionWebChecker extends SwingWorker<Boolean, Void> {

        boolean quietMode = false;

        public LastVersionWebChecker(boolean quietMode) {
            this.quietMode = quietMode;
        }

        @Override
        protected Boolean doInBackground() throws Exception {
            Proxy proxy = Proxy.NO_PROXY;
            if (userPrefs.getUseProxy()) {
                SocketAddress addr = new InetSocketAddress(
                        userPrefs.getProxyAddress(), userPrefs.getProxyPort());
                String proxyType = userPrefs.getProxyType();
                proxy = new Proxy(Proxy.Type.valueOf(proxyType), addr);
            }

            URL altervista = null;
            BufferedReader in;
            boolean newVersionAvailable = false;
            altervista = new URL(JPdfBookmarks.LAST_VERSION_PROPERTIES_URL);
            HttpURLConnection connection = (HttpURLConnection) altervista.openConnection(proxy);
            Properties prop = new Properties();
            Reader reader = new InputStreamReader(connection.getInputStream());
            prop.load(reader);
            String inputLine = prop.getProperty("VERSION");
            String[] newVersionNumbers = inputLine.split("\\.");
            String[] thisVersionNumbers = JPdfBookmarks.VERSION.split("\\.");
            for (int i = 0; i < newVersionNumbers.length; i++) {
                int newVerN = Integer.parseInt(newVersionNumbers[i]);
                int thisVerN = Integer.parseInt(thisVersionNumbers[i]);
                if (newVerN > thisVerN) {
                    newVersionAvailable = true;
                    break;
                } else if (thisVerN > newVerN) {
                    break;
                }
            }
            reader.close();
            connection.disconnect();
            return newVersionAvailable;
        }

        @Override
        protected void done() {
            boolean newVersion = false;
            try {
                newVersion = get();
                if (newVersion || !quietMode) {
                    JOptionPane.showMessageDialog(JPdfBookmarksGui.this, newVersionAvailable(newVersion),
                            JPdfBookmarks.APP_NAME, JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                if (!quietMode) {
                    showErrorMessage(Res.getString("ERROR_CHECKING_UPDATES"));
                }
            }

        }
    }

    private class UpdatesChecker extends SwingWorker<Boolean, Void> {

        boolean quietMode = false;

        public UpdatesChecker(boolean quietMode) {
            this.quietMode = quietMode;
        }

        @Override
        protected Boolean doInBackground() throws Exception {
            Proxy proxy = Proxy.NO_PROXY;
            if (userPrefs.getUseProxy()) {
                SocketAddress addr = new InetSocketAddress(
                        userPrefs.getProxyAddress(), userPrefs.getProxyPort());
                String proxyType = userPrefs.getProxyType();
                proxy = new Proxy(Proxy.Type.valueOf(proxyType), addr);
            }

            URL altervista = null;
            BufferedReader in;
            boolean newVersionAvailable = false;
            altervista = new URL(JPdfBookmarks.LAST_VERSION_URL);
            HttpURLConnection connection = (HttpURLConnection) altervista.openConnection(proxy);
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String[] newVersionNumbers = inputLine.split("\\.");
                String[] thisVersionNumbers = JPdfBookmarks.VERSION.split("\\.");
                for (int i = 0; i < newVersionNumbers.length; i++) {
                    if (Integer.parseInt(newVersionNumbers[i]) >
                            Integer.parseInt(thisVersionNumbers[i])) {
                        newVersionAvailable = true;
                    }
                }
            }
            in.close();
            return newVersionAvailable;
        }

        @Override
        protected void done() {
            boolean newVersion = false;
            try {
                newVersion = get();
                if (newVersion || !quietMode) {
                    JOptionPane.showMessageDialog(JPdfBookmarksGui.this, newVersionAvailable(newVersion),
                            JPdfBookmarks.APP_NAME, JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                if (!quietMode) {
                    showErrorMessage(Res.getString("ERROR_CHECKING_UPDATES"));
                }
            }

        }
    }

    private void checkUpdates(boolean quietMode) {
//		UpdatesChecker updatesChecker = new UpdatesChecker(quietMode);
//		updatesChecker.execute();
        LastVersionWebChecker updatesChecker = new LastVersionWebChecker(quietMode);
        updatesChecker.execute();
    }

    private void dump() {

        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new SimpleFileFilter("txt", "Text Files"));
        chooser.setCurrentDirectory(fileOperator.getFile().getParentFile());

        chooser.setDialogTitle(Res.getString("DUMP_DIALOG_TITLE"));
        if (chooser.showSaveDialog(this) !=
                JFileChooser.APPROVE_OPTION) {
            return;
        }
        File f = chooser.getSelectedFile();
        if (f == null) {
            return;
        }

        String filename = f.getName();

        if (filename.endsWith(".txt") == false) {
            filename = f.getParent() + File.separatorChar + filename + ".txt";
            f = new File(filename);
        }

        if (f.exists()) {
            int response = JOptionPane.showConfirmDialog(
                    this,
                    Res.getString("WARNING_OVERWRITE"),
                    title,
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (response != JOptionPane.YES_OPTION) {
                return;
            }
        }

        Dumper dumper = new Dumper(null, userPrefs.getIndentationString(),
                userPrefs.getPageSeparator(), userPrefs.getAttributesSeparator());
        String hierarchy = dumper.getBookmarks(
                (Bookmark) bookmarksTreeModel.getRoot());

        FileWriter writer = null;
        try {
            writer = new FileWriter(f);
            writer.append(hierarchy);
            writer.flush();
            writer.close();
        } catch (Exception exc) {
            JOptionPane.showMessageDialog(this,
                    Res.getString("ERROR_SAVING_FILE"),
                    title, JOptionPane.WARNING_MESSAGE);
            return;
        }
    }

    private void applyPageOffsetDialog() {
        TreePath[] paths = bookmarksTree.getSelectionPaths();
        int maxPageNumber = -1, minPageNumber = viewPanel.getNumPages();
        for (TreePath path : paths) {
            Bookmark bookmark = (Bookmark) path.getLastPathComponent();
            int targetPage = bookmark.getPageNumber();
            if (targetPage > maxPageNumber) {
                maxPageNumber = targetPage;
            }
            if (targetPage < minPageNumber) {
                minPageNumber = targetPage;
            }
        }
        Bookmark selected = getSelectedBookmark();
        PageOffsetDialog pageOffsetDialog = new PageOffsetDialog(
                this, viewPanel.getCurrentPage() - selected.getPageNumber(),
                viewPanel.getNumPages() - maxPageNumber,
                -minPageNumber + 1);
        pageOffsetDialog.setVisible(true);
        if (pageOffsetDialog.operationNotAborted()) {
            UnboablePageOffset undoablePageOffset = new UnboablePageOffset(
                    bookmarksTreeModel, paths, pageOffsetDialog.getOffsetValue());

            undoablePageOffset.doEdit();
            recreateNodesOpenedState();
            undoSupport.postEdit(undoablePageOffset);
        }
    }

    private void goToPageDialog() {
        GoToPageDialog goToPageDialog = new GoToPageDialog(
                JPdfBookmarksGui.this,
                viewPanel.getCurrentPage(), viewPanel.getNumPages());
        goToPageDialog.setVisible(true);
        if (goToPageDialog.operationNotAborted()) {
            viewPanel.goToPage(goToPageDialog.getPage());
        }
    }

    private void createActions() {

        quitAction = new ActionBuilder("ACTION_QUIT", "ACTION_QUIT_DESCR",
                "alt F4", "system-log-out.png", true) {

            public void actionPerformed(ActionEvent e) {
                exitApplication();
            }
        };

        // <editor-fold defaultstate="collapsed" desc="File Actions">
        openAction = new ActionBuilder("ACTION_OPEN", "ACTION_OPEN_DESCR",
                "ctrl O", "document-open.png", true) {

            public void actionPerformed(ActionEvent e) {
                openDialog();
            }
        };

        saveAction = new ActionBuilder("ACTION_SAVE", "ACTION_SAVE_DESCR",
                "ctrl S", "document-save.png", false) {

            public void actionPerformed(ActionEvent e) {
                save();
            }
        };

        saveAsAction = new ActionBuilder("ACTION_SAVE_AS", "ACTION_SAVE_AS_DESCR",
                "ctrl A", "document-save-as.png", false) {

            public void actionPerformed(ActionEvent e) {
                saveAs();
            }
        };

        closeAction = new ActionBuilder("ACTION_CLOSE", "ACTION_CLOSE_DESCR",
                "ctrl F4", "process-stop.png", false) {

            public void actionPerformed(ActionEvent e) {
                close();
            }
        };

        showOnOpenAction = new ActionBuilder("ACTION_SHOW_ON_OPEN",
                "ACTION_SHOW_ON_OPEN_DESCR", null, "show-on-open.png", false) {

            public void actionPerformed(ActionEvent e) {
                if (e.getSource().equals(tbShowOnOpen)) {
                    cbShowOnOpen.setSelected(tbShowOnOpen.isSelected());
                    fileOperator.setShowBookmarksOnOpen(tbShowOnOpen.isSelected());
                } else {
                    tbShowOnOpen.setSelected(cbShowOnOpen.isSelected());
                    fileOperator.setShowBookmarksOnOpen(cbShowOnOpen.isSelected());
                }
            }
        };

        dumpAction = new ActionBuilder("ACTION_DUMP", "ACTION_DUMP_DESCR",
                "ctrl alt D", "dump.png", false) {

            public void actionPerformed(ActionEvent e) {
                dump();
            }
        };

        loadAction = new ActionBuilder("ACTION_LOAD", "ACTION_LOAD_DESCR",
                "ctrl alt L", "load.png", false) {

            public void actionPerformed(ActionEvent e) {
                load();
            }
        };
        // </editor-fold>

        undoAction = new ActionBuilder("ACTION_UNDO", "ACTION_UNDO_DESCR",
                "ctrl Z", "edit-undo.png", false) {

            public void actionPerformed(ActionEvent e) {
                undo();
            }
        };

        redoAction = new ActionBuilder("ACTION_REDO", "ACTION_REDO_DESCR",
                "ctrl shift Z", "edit-redo.png", false) {

            public void actionPerformed(ActionEvent e) {
                redo();
            }
        };

        addSiblingAction = new ActionBuilder("ACTION_ADD_SIBLING",
                "ACTION_ADD_SIBLING_DESCR",
                "ctrl alt S", "add-sibling.png", false) {

            public void actionPerformed(ActionEvent e) {
                addSibling();
            }
        };

        addChildAction = new ActionBuilder("ACTION_ADD_CHILD",
                "ACTION_ADD_CHILD_DESCR", "ctrl alt F", "add-child.png", false) {

            public void actionPerformed(ActionEvent e) {
                addChild();
            }
        };

        addWebLinkAction = new ActionBuilder("ACTION_ADD_WEB_LINK",
                "ACTION_ADD_WEB_LINK_DESCR", "ctrl alt W", "bookmark-web.png", false) {

            public void actionPerformed(ActionEvent e) {
                setWebLink();
            }
        };

        deleteAction = new ActionBuilder("ACTION_DELETE", "ACTION_DELETE_DESCR",
                "ctrl DELETE", "user-trash.png", false) {

            public void actionPerformed(ActionEvent e) {
                delete();
            }
        };

        setBoldAction = new ActionBuilder("ACTION_SET_BOLD", "ACTION_SET_BOLD_DESCR",
                "ctrl G", "format-text-bold.png", false) {

            public void actionPerformed(ActionEvent e) {
                AbstractButton btn = (AbstractButton) e.getSource();
                setBold(btn.isSelected());
            }
        };

        setItalicAction = new ActionBuilder("ACTION_SET_ITALIC", "ACTION_SET_ITALIC_DESCR",
                "ctrl I", "format-text-italic.png", false) {

            public void actionPerformed(ActionEvent e) {
                AbstractButton btn = (AbstractButton) e.getSource();
                setItalic(btn.isSelected());
            }
        };

        changeColorAction = new ActionBuilder("ACTION_CHANGE_COLOR",
                "ACTION_CHANGE_COLOR_DESCR", null, "applications-graphics.png",
                false) {

            public void actionPerformed(ActionEvent e) {
                changeColor();
            }
        };

        renameAction = new ActionBuilder("ACTION_RENAME", "ACTION_RENAME_DESCR",
                null, "edit-select-all.png", false) {

            public void actionPerformed(ActionEvent e) {
                rename();
            }
        };

        setDestFromViewAction = new ActionBuilder("ACTION_DEST_FROM_VIEW",
                "ACTION_DEST_FROM_VIEW_DESCR", "ctrl alt A",
                "dest-from-view.png", false) {

            public void actionPerformed(ActionEvent e) {
                destFromView();
            }
        };

        applyPageOffset = new ActionBuilder("ACTION_PAGE_OFFSET",
                "ACTION_PAGE_OFFSET_DESCR", null, "page-offset.png", false) {

            public void actionPerformed(ActionEvent e) {
                applyPageOffsetDialog();
            }
        };

        selectText = new ActionBuilder("ACTION_SELECT_TEXT", "ACTION_SELECT_TEXT_DESCR", "ctrl alt T",
                "select-text.png", false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource().equals(tbSelectText)) {
                    cbSelectText.setSelected(tbSelectText.isSelected());
                } else {
                    tbSelectText.setSelected(cbSelectText.isSelected());
                }

                viewPanel.setTextSelectionMode(tbSelectText.isSelected());
            }

        };

        connectToClipboard = new ActionBuilder("ACTION_CONNECT_CLIPBOARD",
                "ACTION_CONNECT_CLIPBOARD_DESCR", "ctrl alt C", "edit-paste.png", false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource().equals(tbConnectToClipboard)) {
                    cbConnectToClipboard.setSelected(tbConnectToClipboard.isSelected());
                } else {
                    tbConnectToClipboard.setSelected(cbConnectToClipboard.isSelected());
                }

                viewPanel.setConnectToClipboard(tbConnectToClipboard.isSelected());
            }
        };

        // <editor-fold defaultstate="collapsed" desc="Navigation Actions">
        goNextPageAction = new ActionBuilder("ACTION_GO_NEXT",
                "ACTION_GO_NEXT_DESCR", "ctrl alt RIGHT", "go-next.png", false) {

            public void actionPerformed(ActionEvent e) {
                viewPanel.goToNextPage();
            }
        };

        goFirstPageAction = new ActionBuilder("ACTION_GO_FIRST",
                "ACTION_GO_FIRST_DESCR", "ctrl alt HOME", "go-first.png", false) {

            public void actionPerformed(ActionEvent e) {
                viewPanel.goToFirstPage();
            }
        };

        goLastPageAction = new ActionBuilder("ACTION_GO_LAST",
                "ACTION_GO_LAST_DESCR", "ctrl alt END", "go-last.png", false) {

            public void actionPerformed(ActionEvent e) {
                viewPanel.goToLastPage();
            }
        };

        goPreviousPageAction = new ActionBuilder("ACTION_GO_PREV",
                "ACTION_GO_PREV_DESCR", "ctrl alt LEFT", "go-previous.png", false) {

            public void actionPerformed(ActionEvent e) {
                viewPanel.goToPreviousPage();
            }
        };

        goToPageAction = new ActionBuilder("ACTION_GO_PAGE",
                "ACTION_GO_PAGE_DESCR", "ctrl alt INSERT", null, false) {

            public void actionPerformed(ActionEvent e) {
                goToPageDialog();
            }
        };// </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="Zoom Actions">
        fitRectAction = new ActionBuilder("ACTION_FIT_RECT",
                "ACTION_FIT_RECT_DESCR", "ctrl R", "fit-rect.png",
                false) {

            public void actionPerformed(ActionEvent e) {
                if (e.getSource() instanceof JToggleButton) {
                    rbFitRect.setSelected(true);
                } else {
                    tbFitRect.setSelected(true);
                }
                viewPanel.setFitRect(null);
            }
        };

        fitWidthAction = new ActionBuilder("ACTION_FIT_WIDTH",
                "ACTION_FIT_WIDTH_DESCR", "ctrl W", "fit-width.png",
                false) {

            public void actionPerformed(ActionEvent e) {
                if (e.getSource() instanceof JToggleButton) {
                    rbFitWidth.setSelected(true);
                } else {
                    tbFitWidth.setSelected(true);
                }

                viewPanel.setFitWidth(-1);
            }
        };

        fitHeightAction = new ActionBuilder("ACTION_FIT_HEIGHT",
                "ACTION_FIT_HEIGHT_DESCR", "ctrl H", "fit-height.png",
                false) {

            public void actionPerformed(ActionEvent e) {
                if (e.getSource() instanceof JToggleButton) {
                    rbFitHeight.setSelected(true);
                } else {
                    tbFitHeight.setSelected(true);
                }

                viewPanel.setFitHeight(-1);
            }
        };

        fitNativeAction = new ActionBuilder("ACTION_FIT_NATIVE",
                "ACTION_FIT_NATIVE_DESCR", "ctrl N", "fit-native.png",
                false) {

            public void actionPerformed(ActionEvent e) {
                if (e.getSource() instanceof JToggleButton) {
                    rbFitNative.setSelected(true);
                } else {
                    tbFitNative.setSelected(true);
                }

                viewPanel.setFitNative();
            }
        };

        fitPageAction = new ActionBuilder("ACTION_FIT_PAGE",
                "ACTION_FIT_PAGE_DESCR", "ctrl G", "fit-page.png",
                false) {

            public void actionPerformed(ActionEvent e) {
                if (e.getSource() instanceof JToggleButton) {
                    rbFitPage.setSelected(true);
                } else {
                    tbFitPage.setSelected(true);
                }

                viewPanel.setFitPage();
            }
        };

        topLeftZoomAction = new ActionBuilder("ACTION_TOP_LEFT_ZOOM",
                "ACTION_TOP_LEFT_ZOOM_DESCR", null, "top-left-zoom.png",
                false) {

            public void actionPerformed(ActionEvent e) {
                if (e.getSource() instanceof JToggleButton) {
                    rbTopLeftZoom.setSelected(true);
                } else {
                    tbTopLeftZoom.setSelected(true);
                }

                viewPanel.setTopLeftZoom(-1, -1, 0f);
            }
        };

        zoomInAction = new ActionBuilder("ACTION_ZOOM_IN",
                "ACTION_ZOOM_IN_DESCR", "alt +", "zoom-in.png", false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                float scale = (txtZoom.getInteger() + ZOOM_STEP) / 100f;
                viewPanel.setTopLeftZoom(-1, -1, scale);
            }
        };

        zoomOutAction = new ActionBuilder("ACTION_ZOOM_OUT",
                "ACTION_ZOOM_OUT_DESCR", "alt -", "zoom-out.png", false) {

            public void actionPerformed(ActionEvent e) {
                float scale = (txtZoom.getInteger() - ZOOM_STEP) / 100f;
                viewPanel.setTopLeftZoom(-1, -1, scale);
            }
        };// </editor-fold>

        expandAllAction = new ActionBuilder("ACTION_EXPAND_ALL",
                "ACTION_EXPAND_ALL_DESCR", "ctrl E", null, false) {

            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < bookmarksTree.getRowCount(); i++) {
                    bookmarksTree.expandRow(i);
                }
            }
        };

        collapseAllAction = new ActionBuilder("ACTION_COLLAPSE_ALL",
                "ACTION_COLLAPSE_ALL_DESCR", "ctrl P", null, false) {

            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < bookmarksTree.getRowCount(); i++) {
                    bookmarksTree.collapseRow(i);
                }
            }
        };

        optionsDialogAction = new ActionBuilder("ACTION_OPTIONS_DIALOG",
                "ACTION_OPTIONS_DIALOG_DESCR", "ctrl alt O",
                "preferences-system.png", true) {

            public void actionPerformed(ActionEvent e) {
//				OptionsDialog optionsDlg =
//						new OptionsDialog(JPdfBookmarksGui.this, true);
                OptionsDlg optionsDlg = new OptionsDlg(JPdfBookmarksGui.this,
                        true);
                optionsDlg.setLocationRelativeTo(JPdfBookmarksGui.this);
                optionsDlg.setVisible(true);
            }
        };

        checkUpdatesAction = new ActionBuilder("ACTION_CHECK_UPDATES",
                "ACTION_CHECK_UPDATES_DESCR", null,
                "system-software-update.png", true) {

            public void actionPerformed(ActionEvent e) {
                checkUpdates(false);
            }
        };

        readOnlineManualAction = new ActionBuilder("ACTION_READ_MANUAL",
                "ACTION_READ_MANUAL_DESCR", null, "help-browser.png", true) {

            public void actionPerformed(ActionEvent e) {
                goToWebLink(JPdfBookmarks.MANUAL_URL);
            }
        };

        goToAuthorBlog = new ActionBuilder("ACTION_GO_TO_BLOG",
                "ACTION_GO_TO_BLOG_DESCR", null, "internet-web-browser.png", true) {

            public void actionPerformed(ActionEvent e) {
                goToWebLink(JPdfBookmarks.BLOG_URL);
            }
        };
    }

    private void updateStyleButtons(Bookmark bookmark) {
        cbBold.setSelected(bookmark.isBold());
        cbEditMenuBold.setSelected(bookmark.isBold());
        tbBold.setSelected(bookmark.isBold());
        cbItalic.setSelected(bookmark.isItalic());
        cbEditMenuItalic.setSelected(bookmark.isItalic());
        tbItalic.setSelected(bookmark.isItalic());
    }

    private void updateUndoRedoPresentation() {
        undoAction.setEnabled(undoManager.canUndo());
        redoAction.setEnabled(undoManager.canRedo());

        String redoPresentation = "";
        String undoPresentation = "";
        UndoableEdit[] undoableEdits = undoManager.getUndoableEdits();
        if (undoableEdits.length > 0) {
            UndoableEdit undo = undoableEdits[0];
            undoPresentation = getUndoablePresentation(undo);
        }
        undoAction.putValue(Action.NAME,
                Res.getString("ACTION_UNDO") + " " + undoPresentation);

        UndoableEdit[] redoableEdits = undoManager.getRedoableEdits();
        if (redoableEdits.length > 0) {
            UndoableEdit redo = redoableEdits[0];
            redoPresentation = getUndoablePresentation(redo);
        }
        redoAction.putValue(Action.NAME,
                Res.getString("ACTION_REDO") + " " + redoPresentation);
    }

    private String getUndoablePresentation(UndoableEdit undoable) {
        String presentation = "";
        if (undoable instanceof UndoableNodeMoved) {
            presentation = Res.getString("MOVE_EDIT");
        } else if (undoable instanceof UndoableCellEdit) {
            presentation = Res.getString("CELL_EDIT");
        } else if (undoable instanceof UndoableDeleteBookmark) {
            presentation = Res.getString("ACTION_DELETE");
        } else if (undoable instanceof UndoableSetDestination) {
            presentation = Res.getString("ACTION_DEST_FROM_VIEW");
        } else if (undoable instanceof UnboablePageOffset) {
            presentation = Res.getString("UNDOABLE_OFFSET");
        } else if (undoable instanceof UndoableLoadBookmarks) {
            presentation = Res.getString("UNDOABLE_LOAD_BOOKMARKS");
        }
        return presentation;
    }

    private class RecentFileListener implements ActionListener {

        private File f;

        public RecentFileListener(File f) {
            this.f = f;
        }

        public void actionPerformed(ActionEvent e) {
            if (f != null && f.isFile()) {
                close();
                openFileAsync(f);
            } else {
                showErrorMessage(Res.getString("ERROR_OPENING_FILE") + " " + f.getName());
            }
        }
    }

    private void createRecentFilesItems() {
        openRecent.removeAll();
        String[] paths = userPrefs.getRecentFiles();
        JMenuItem item;
        for (String path : paths) {
            if (!path.equals("")) {
                File f = new File(path);
                if (f.exists()) {
                    item = new JMenuItem(f.getName());
                    item.setToolTipText(f.getAbsolutePath());
                    item.addActionListener(new RecentFileListener(f));
                    openRecent.add(item);
                }
            }
        }

    }

    private JMenuBar createMenus() {
        JMenuBar menuBar = new JMenuBar();

        JMenuItem item;

        JMenu menuFile = new JMenu(Res.getString("MENU_FILE"));
        menuFile.setMnemonic(Res.mnemonicFromRes("MENU_FILE_MNEMONIC"));
        item = menuFile.add(openAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_OPEN_MNEMONIC"));

        openRecent = new JMenu(Res.getString("MENU_OPEN_RECENT"));
        openRecent.setMnemonic(Res.mnemonicFromRes("MENU_OPEN_RECENT_MNEMONIC"));
        createRecentFilesItems();
        menuFile.add(openRecent);

        item = menuFile.add(saveAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_SAVE_MNEMONIC"));
        item = menuFile.add(saveAsAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_SAVE_AS_MNEMONIC"));
        item = menuFile.add(closeAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_CLOSE_MNEMONIC"));

        menuFile.addSeparator();

        item = menuFile.add(quitAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_QUIT_MNEMONIC"));
        menuBar.add(menuFile);

        JMenu menuEdit = new JMenu(Res.getString("MENU_EDIT"));
        menuEdit.setMnemonic(Res.mnemonicFromRes("MENU_EDIT_MNEMONIC"));
        item = menuEdit.add(undoAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_UNDO_MNEMONIC"));
        item = menuEdit.add(redoAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_REDO_MNEMONIC"));
        menuEdit.addSeparator();
        item = menuEdit.add(addSiblingAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_ADD_SIBLING_MNEMONIC"));
        item = menuEdit.add(addChildAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_ADD_CHILD_MNEMONIC"));
        menuEdit.addSeparator();
        item = menuEdit.add(renameAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_RENAME_MNEMONIC"));
        item = menuEdit.add(deleteAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_DELETE_MNEMONIC"));
        menuEdit.addSeparator();
        cbEditMenuBold = new JCheckBoxMenuItem(setBoldAction);
        cbEditMenuBold.setMnemonic(Res.mnemonicFromRes("MENU_SET_BOLD_MNEMONIC"));
        menuEdit.add(cbEditMenuBold);
        cbEditMenuItalic = new JCheckBoxMenuItem(setItalicAction);
        cbEditMenuItalic.setMnemonic(Res.mnemonicFromRes("MENU_SET_ITALIC_MNEMONIC"));
        menuEdit.add(cbEditMenuItalic);
        item = menuEdit.add(changeColorAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_CHANGE_COLOR_MNEMONIC"));
        menuEdit.addSeparator();
        item = menuEdit.add(addWebLinkAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_ADD_WEB_LINK_MNEMONIC"));
        item = menuEdit.add(setDestFromViewAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_SET_DESTINATION_MNEMONIC"));
        menuBar.add(menuEdit);

        JMenu menuView = new JMenu(Res.getString("MENU_VIEW"));
        menuView.setMnemonic(Res.mnemonicFromRes("MENU_VIEW_MNEMONIC"));
        item = menuView.add(goFirstPageAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_GO_FIRST_MNEMONIC"));
        item = menuView.add(goPreviousPageAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_GO_PREV_MNEMONIC"));
        item = menuView.add(goNextPageAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_GO_NEXT_MNEMONIC"));
        item = menuView.add(goLastPageAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_GO_LAST_MNEMONIC"));
        item = menuView.add(goToPageAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_GO_TO_PAGE_MNEMONIC"));
        menuView.addSeparator();

        zoomMenuItemsGroup = new ButtonGroup();
        rbFitWidth = new JRadioButtonMenuItem(fitWidthAction);
        rbFitWidth.setMnemonic(Res.mnemonicFromRes("MENU_FIT_WIDTH_MNEMONIC"));
        menuView.add(rbFitWidth);
        zoomMenuItemsGroup.add(rbFitWidth);

        rbFitHeight = new JRadioButtonMenuItem(fitHeightAction);
        rbFitHeight.setMnemonic(Res.mnemonicFromRes("MENU_FIT_HEIGHT_MNEMONIC"));
        menuView.add(rbFitHeight);
        zoomMenuItemsGroup.add(rbFitHeight);

        rbFitPage = new JRadioButtonMenuItem(fitPageAction);
        rbFitPage.setMnemonic(Res.mnemonicFromRes("MENU_FIT_PAGE_MNEMONIC"));
        menuView.add(rbFitPage);
        zoomMenuItemsGroup.add(rbFitPage);

        rbFitNative = new JRadioButtonMenuItem(fitNativeAction);
        rbFitNative.setMnemonic(Res.mnemonicFromRes("MENU_FIT_NATIVE_MNEMONIC"));
        menuView.add(rbFitNative);
        zoomMenuItemsGroup.add(rbFitNative);

        rbTopLeftZoom = new JRadioButtonMenuItem(topLeftZoomAction);
        rbTopLeftZoom.setMnemonic(Res.mnemonicFromRes("MENU_TOP_LEFT_ZOOM_MNEMONIC"));
        menuView.add(rbTopLeftZoom);
        zoomMenuItemsGroup.add(rbTopLeftZoom);

        rbFitRect = new JRadioButtonMenuItem(fitRectAction);
        rbFitRect.setMnemonic(Res.mnemonicFromRes("MENU_FIT_RECT_MNEMONIC"));
        menuView.add(rbFitRect);
        zoomMenuItemsGroup.add(rbFitRect);

        menuView.addSeparator();

        menuView.add(expandAllAction).setMnemonic(
                Res.mnemonicFromRes("MENU_EXPAND_ALL_MNEMONIC"));
        menuView.add(collapseAllAction).setMnemonic(
                Res.mnemonicFromRes("MENU_COLLAPSE_ALL_MNEMONIC"));

        menuBar.add(menuView);

        JMenu menuTools = new JMenu(Res.getString("MENU_TOOLS"));
        menuTools.setMnemonic(Res.mnemonicFromRes("MENU_TOOLS_MNEMONIC"));

        JMenu menuSetLAF = new JMenu(Res.getString("MENU_LAF"));
        menuSetLAF.setMnemonic(Res.mnemonicFromRes("MENU_LAF_MNEMONIC"));
        ButtonGroup group = new ButtonGroup();
        String currentLAF = UIManager.getLookAndFeel().getName();
        LookAndFeelInfo[] infoArray = UIManager.getInstalledLookAndFeels();
        for (LookAndFeelInfo info : infoArray) {
            JRadioButtonMenuItem rb = new JRadioButtonMenuItem(info.getName());
            group.add(rb);
            menuSetLAF.add(rb);
            if (currentLAF.equals(info.getName())) {
                rb.setSelected(true);
            }
            rb.addActionListener(new ActionListenerSetLAF(info.getClassName()));
        }
        //menuTools.add(menuSetLAF);

        cbSelectText = new JCheckBoxMenuItem(selectText);
        cbSelectText.setSelected(false);
        menuTools.add(cbSelectText);
        cbConnectToClipboard = new JCheckBoxMenuItem(connectToClipboard);
        cbConnectToClipboard.setSelected(false);
        menuTools.add(cbConnectToClipboard);

        menuTools.addSeparator();
        
        cbShowOnOpen = new JCheckBoxMenuItem(showOnOpenAction);
        cbShowOnOpen.setMnemonic(Res.mnemonicFromRes("MENU_SHOW_ON_OPEN_MNEMONIC"));
        menuTools.add(cbShowOnOpen);

        menuTools.addSeparator();

        item = menuTools.add(dumpAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_DUMP_MNEMONIC"));
        item = menuTools.add(loadAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_LOAD_MNEMONIC"));

        menuTools.addSeparator();

        item = menuTools.add(applyPageOffset);
        item.setMnemonic(Res.mnemonicFromRes("MENU_PAGE_OFFSET_MNEMONIC"));

        JCheckBoxMenuItem checkItem = new JCheckBoxMenuItem(Res.getString("MENU_CONVERT_NAMED_DEST"));
        checkItem.setToolTipText(Res.getString("MENU_CONVERT_NAMED_DEST_DESCR"));
        checkItem.setMnemonic(Res.mnemonicFromRes("MENU_CONVERT_NAMED_DEST_MNEMONIC"));
        checkItem.setSelected(userPrefs.getConvertNamedDestinations());
        checkItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
                userPrefs.setConvertNamedDestinations(item.isSelected());
                JOptionPane.showMessageDialog(JPdfBookmarksGui.this,
                        Res.getString("CONVERT_NAMED_DEST_MSG"), title,
                        JOptionPane.WARNING_MESSAGE);
            }
        });
        //menuTools.add(checkItem);

        menuTools.addSeparator();

        item = menuTools.add(optionsDialogAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_OPTIONS_MNEMONIC"));

        menuBar.add(menuTools);

        JMenu menuWindow = new JMenu(Res.getString("MENU_WINDOW"));
        menuWindow.setMnemonic(Res.mnemonicFromRes("MENU_WINDOW_MNEMONIC"));
        menuWindow.add(menuSetLAF);

        menuBar.add(menuWindow);

        JMenu menuHelp = new JMenu(Res.getString("MENU_HELP"));
        menuHelp.setMnemonic(Res.mnemonicFromRes("MENU_HELP_MNEMONIC"));
        item = menuHelp.add(checkUpdatesAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_CHECK_UPDATES_MNEMONIC"));
        item = menuHelp.add(goToAuthorBlog);
        item.setMnemonic(Res.mnemonicFromRes("MENU_GO_TO_BLOG_MNEMONIC"));
        item = menuHelp.add(readOnlineManualAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_READ_MANUAL_MNEMONIC"));

        menuHelp.addSeparator();

        item = new JMenuItem(Res.getString("MENU_ABOUT_BOX") + " ...");
        item.setMnemonic(Res.mnemonicFromRes("MENU_ABOUT_BOX_MNEMONIC"));
        item.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                AboutBox aboutBox = new AboutBox(JPdfBookmarksGui.this, true);
                aboutBox.setLocationRelativeTo(JPdfBookmarksGui.this);
                aboutBox.setVisible(true);
            }
        });
        menuHelp.add(item);

        menuBar.add(menuHelp);

        return menuBar;
    }

    private void createTreeMenu() {
        treeMenu = new JPopupMenu();

        JMenuItem item = treeMenu.add(addSiblingAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_ADD_SIBLING_MNEMONIC"));
        item = treeMenu.add(addChildAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_ADD_CHILD_MNEMONIC"));
        treeMenu.addSeparator();
        item = treeMenu.add(renameAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_RENAME_MNEMONIC"));
        item = treeMenu.add(deleteAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_DELETE_MNEMONIC"));
        treeMenu.addSeparator();
        cbBold = new JCheckBoxMenuItem(setBoldAction);
        cbBold.setMnemonic(Res.mnemonicFromRes("MENU_SET_BOLD_MNEMONIC"));
        treeMenu.add(cbBold);
        cbItalic = new JCheckBoxMenuItem(setItalicAction);
        cbItalic.setMnemonic(Res.mnemonicFromRes("MENU_SET_ITALIC_MNEMONIC"));
        treeMenu.add(cbItalic);
        item = treeMenu.add(changeColorAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_CHANGE_COLOR_MNEMONIC"));
        treeMenu.addSeparator();
        item = treeMenu.add(addWebLinkAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_ADD_WEB_LINK_MNEMONIC"));
        item = treeMenu.add(setDestFromViewAction);
        item.setMnemonic(Res.mnemonicFromRes("MENU_SET_DESTINATION_MNEMONIC"));
    }

    private class ActionListenerSetLAF implements ActionListener {

        private String laf;

        public ActionListenerSetLAF(String lookAndFeelClass) {
            super();
            laf = lookAndFeelClass;
        }

        public void actionPerformed(ActionEvent arg0) {
            Ut.changeLAF(laf, JPdfBookmarksGui.this);
            lblInheritLeft.setUI(new VerticalLabelUI(false));
            userPrefs.setLAF(laf);
        }
    }

    private JPanel createToolbarsPanel() {
        JPanel toolbarsPanel = new JPanel(
                new WrapFlowLayout(WrapFlowLayout.LEFT));

        JToolBar fileToolbar = new JToolBar();
        fileToolbar.add(openAction);
        fileToolbar.add(saveAction);
        fileToolbar.add(saveAsAction);
        fileToolbar.add(closeAction);

        JToolBar undoToolbar = new JToolBar();
        undoToolbar.add(undoAction);
        undoToolbar.add(redoAction);

        navigationToolbar = new JToolBar();
        JButton btn = navigationToolbar.add(goFirstPageAction);
        btn = navigationToolbar.add(goPreviousPageAction);

        txtGoToPage = new IntegerTextField(4);
        txtGoToPage.setText("0");
        txtGoToPage.setEnabled(false);
        txtGoToPage.setHorizontalAlignment(JTextField.CENTER);
        txtGoToPage.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                viewPanel.goToPage(txtGoToPage.getInteger());
            }
        });
        navigationToolbar.add(txtGoToPage);
        lblPageOfPages = new JLabel(String.format(" / %5d", numPages));
        lblPageOfPages.setEnabled(false);
        navigationToolbar.add(lblPageOfPages);

        btn = navigationToolbar.add(goNextPageAction);
        btn = navigationToolbar.add(goLastPageAction);

        JToolBar fitTypeToolbar = new JToolBar();

        zoomButtonsGroup = new ButtonGroup();

        tbFitWidth = new JToggleButton(fitWidthAction);
        tbFitWidth.setText("");
        fitTypeToolbar.add(tbFitWidth);
        zoomButtonsGroup.add(tbFitWidth);

        tbFitHeight = new JToggleButton(fitHeightAction);
        tbFitHeight.setText("");
        fitTypeToolbar.add(tbFitHeight);
        zoomButtonsGroup.add(tbFitHeight);

        tbFitPage = new JToggleButton(fitPageAction);
        tbFitPage.setText("");
        fitTypeToolbar.add(tbFitPage);
        zoomButtonsGroup.add(tbFitPage);

        tbFitNative = new JToggleButton(fitNativeAction);
        tbFitNative.setText("");
        fitTypeToolbar.add(tbFitNative);
        zoomButtonsGroup.add(tbFitNative);

        tbTopLeftZoom = new JToggleButton(topLeftZoomAction);
        tbTopLeftZoom.setText("");
        fitTypeToolbar.add(tbTopLeftZoom);
        zoomButtonsGroup.add(tbTopLeftZoom);

        tbFitRect = new JToggleButton(fitRectAction);
        tbFitRect.setText("");
        fitTypeToolbar.add(tbFitRect);
        zoomButtonsGroup.add(tbFitRect);

        JToolBar zoomToolbar = new JToolBar();
        btn = zoomToolbar.add(zoomInAction);
        txtZoom = new IntegerTextField(4);
        txtZoom.setText("0");
        txtZoom.setEnabled(false);
        txtZoom.setHorizontalAlignment(JTextField.CENTER);
        txtZoom.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                float scale = txtZoom.getInteger() / 100f;
                viewPanel.setTopLeftZoom(-1, -1, scale);
            }
        });
        zoomToolbar.add(txtZoom);
        lblPercent = new JLabel(" % ");
        lblPercent.setEnabled(false);
        zoomToolbar.add(lblPercent);
        btn = zoomToolbar.add(zoomOutAction);

        JToolBar othersToolbar = new JToolBar();
        tbSelectText = new JToggleButton(selectText);
        tbSelectText.setText("");
        othersToolbar.add(tbSelectText);
        tbConnectToClipboard = new JToggleButton(connectToClipboard);
        tbConnectToClipboard.setText("");
        othersToolbar.add(tbConnectToClipboard);
        othersToolbar.addSeparator();
        tbShowOnOpen = new JToggleButton(showOnOpenAction);
        tbShowOnOpen.setText("");
        othersToolbar.add(tbShowOnOpen);
        othersToolbar.add(dumpAction);
        othersToolbar.add(loadAction);
        othersToolbar.add(applyPageOffset);

        JToolBar webToolbar = new JToolBar();
        webToolbar.add(checkUpdatesAction);
        webToolbar.add(goToAuthorBlog);
        webToolbar.add(readOnlineManualAction);

        toolbarsPanel.add(fileToolbar);
        toolbarsPanel.add(undoToolbar);
        toolbarsPanel.add(fitTypeToolbar);
        toolbarsPanel.add(zoomToolbar);
        toolbarsPanel.add(navigationToolbar);
        toolbarsPanel.add(othersToolbar);
        toolbarsPanel.add(webToolbar);

        return toolbarsPanel;
    }

    private JPanel createStatusBar() {
        JPanel statusPanel = new JPanel(new GridLayout(1, 4));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

        busyPanel = Box.createHorizontalBox();
        busyPanel.setBorder(BorderFactory.createEtchedBorder());
        lblStatus = new JLabel(" ");
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        busyPanel.add(lblStatus);
        busyPanel.add(Box.createHorizontalGlue());
        statusPanel.add(busyPanel, 0);

        lblMouseOverNode = new JLabel(" ");
        lblMouseOverNode.setBorder(BorderFactory.createEtchedBorder());
        statusPanel.add(lblMouseOverNode, 1);

        lblSelectedNode = new JLabel(" ");
        lblSelectedNode.setBorder(BorderFactory.createEtchedBorder());
        statusPanel.add(lblSelectedNode, 2);

        lblCurrentView = new JLabel(" ");
        lblCurrentView.setBorder(BorderFactory.createEtchedBorder());
        statusPanel.add(lblCurrentView, 3);
        return statusPanel;
    }

    private void setEmptyBookmarksTree() {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(
                Res.getString("NO_PDF_LOADED"));
        bookmarksTreeModel = new DefaultTreeModel(top);
        bookmarksTree = new BookmarksTree();
        bookmarksTree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        bookmarksTree.setShowsRootHandles(true);
        bookmarksTree.setRootVisible(true);
        bookmarksScroller.setViewportView(bookmarksTree);

        MouseAdapter mouseAdapter = new MouseOverTree();
        bookmarksTree.addMouseMotionListener(mouseAdapter);
        bookmarksTree.addMouseListener(mouseAdapter);
        bookmarksTree.addKeyListener(new KeysOverTree());
        bookmarksTree.addTreeSelectionListener(this);
        bookmarksTree.addTreeExpansionListener(this);
        bookmarksTree.getCellEditor().addCellEditorListener(this);
        //the extended undo manager recevies events from the tree and relaunch
        //to the gui with additional information
        bookmarksTree.addUndoableEditListener(undoManager);
        undoManager.addUndoableEditListener(this);
        bookmarksTree.setModel(bookmarksTreeModel);
        bookmarksTree.treeDidChange();
    }

    private JPanel createBookmarksPanel() {
        JPanel bookmarksPanel = new JPanel(new BorderLayout());
        JPanel toolbarsPanel = new JPanel(new WrapFlowLayout(
                WrapFlowLayout.LEFT));

        bookmarksScroller = new JScrollPane();
        setEmptyBookmarksTree();

        bookmarksPanel.add(bookmarksScroller, BorderLayout.CENTER);
        bookmarksPanel.add(toolbarsPanel, BorderLayout.NORTH);

        JToolBar addToolbar = new JToolBar();
        addToolbar.add(addSiblingAction);
        addToolbar.add(addChildAction);
//		addToolbar.add(setDestFromViewAction);
//		addToolbar.add(addWebLinkAction);

        JToolBar changeToolbar = new JToolBar();
        changeToolbar.add(renameAction);
        changeToolbar.add(deleteAction);

        JToolBar styleToolbar = new JToolBar();
        tbBold = new JToggleButton(setBoldAction);
        tbBold.setText("");
        styleToolbar.add(tbBold);
        tbItalic = new JToggleButton(setItalicAction);
        tbItalic.setText("");
        styleToolbar.add(tbItalic);
        styleToolbar.add(changeColorAction);

        JToolBar setDestToolbar = new JToolBar();
        setDestToolbar.add(addWebLinkAction);
        setDestToolbar.add(setDestFromViewAction);

        toolbarsPanel.add(addToolbar);
        toolbarsPanel.add(changeToolbar);
        toolbarsPanel.add(styleToolbar);
        toolbarsPanel.add(setDestToolbar);

        return bookmarksPanel;
    }

    private void initComponents() {
        UIManager.put("Tree.leafIcon", Res.getIcon(getClass(), "gfx16/bookmark.png"));
        UIManager.put("Tree.openIcon", Res.getIcon(getClass(), "gfx16/bookmark.png"));
        UIManager.put("Tree.closedIcon", Res.getIcon(getClass(), "gfx16/bookmarks.png"));

        createActions();

        JMenuBar menuBar = createMenus();
        setJMenuBar(menuBar);

        createTreeMenu();

        JPanel toolbarsPanel = createToolbarsPanel();
        add(toolbarsPanel, BorderLayout.NORTH);

        leftTabbedPane = new JTabbedPane();
        leftTabbedPane.add(Res.getString("BOOKMARKS_TAB_TITLE"),
                createBookmarksPanel());

        JPanel centralPanel = new JPanel(new BorderLayout());
        centralPanel.add((Component) viewPanel, BorderLayout.CENTER);

        Color headersColor = new Color(230, 163, 4);
        JPanel verticalScrollbarHeader = new JPanel(
                new BorderLayout());
        verticalScrollbarHeader.setBackground(headersColor);
        String inheriTop = Res.getString("INHERIT_TOP");
//		JLabel lblInheritTop = new JLabel(inheriTop);
        checkInheritTop = new JCheckBox();
        checkInheritTop.setBorder(BorderFactory.createEmptyBorder(0, 2, 1, 2));
        checkInheritTop.setBackground(headersColor);
        checkInheritTop.setText(inheriTop);
        checkInheritTop.setMnemonic(Res.mnemonicFromRes("INHERIT_TOP_MNEMONIC"));
        checkInheritTop.setHorizontalTextPosition(SwingConstants.LEFT);
        checkInheritTop.setEnabled(false);
        verticalScrollbarHeader.add(checkInheritTop, BorderLayout.EAST);

        centralPanel.add(verticalScrollbarHeader, BorderLayout.NORTH);
//		lblInheritTop.setDisplayedMnemonic(Res.mnemonicFromRes("INHERIT_TOP_MNEMONIC"));
//		lblInheritTop.setLabelFor(checkInheritTop);

        //JPanel horizontalScrollbarHeader = new JPanel(new BoxLayout(this, WIDTH));
        Box horizontalScrollbarHeader = Box.createVerticalBox();
        horizontalScrollbarHeader.setOpaque(true);
        horizontalScrollbarHeader.setBackground(headersColor);
        horizontalScrollbarHeader.add(Box.createVerticalGlue());
        String inheriLeft = Res.getString("INHERIT_LEFT");
        lblInheritLeft = new VerticalLabel(inheriLeft, false);
        lblInheritLeft.setBackground(headersColor);
        horizontalScrollbarHeader.add(lblInheritLeft);
        horizontalScrollbarHeader.add(Box.createVerticalStrut(4));
        checkInheritLeft = new JCheckBox();
        checkInheritLeft.setBorder(BorderFactory.createEmptyBorder(0, 1, 3, 2));
        checkInheritLeft.setBackground(headersColor);
        checkInheritLeft.setEnabled(false);
        horizontalScrollbarHeader.add(checkInheritLeft);
        lblInheritLeft.setDisplayedMnemonic(Res.mnemonicFromRes("INHERIT_LEFT_MNEMONIC"));
        lblInheritLeft.setLabelFor(checkInheritLeft);
        lblInheritLeft.setEnabled(false);
        centralPanel.add(horizontalScrollbarHeader, BorderLayout.WEST);

//		JLabel lblInheritZoom = new JLabel(Res.getString("INHERIT_ZOOM"));
//		lblInheritZoom.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
//		verticalScrollbarHeader.add(lblInheritZoom, BorderLayout.WEST);
        checkInheritZoom = new JCheckBox();
        checkInheritZoom.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 2));
        checkInheritZoom.setBackground(headersColor);
        checkInheritZoom.setText(Res.getString("INHERIT_ZOOM"));
        checkInheritZoom.setHorizontalTextPosition(SwingConstants.RIGHT);
        checkInheritZoom.setMnemonic(
                Res.mnemonicFromRes("INHERIT_ZOOM_MNEMONIC"));
        checkInheritZoom.setEnabled(false);
        verticalScrollbarHeader.add(checkInheritZoom, BorderLayout.WEST);

//		lblInheritZoom.setDisplayedMnemonic(
//				Res.mnemonicFromRes("INHERIT_ZOOM_MNEMONIC"));
//		lblInheritZoom.setLabelFor(checkInheritZoom);

        centralSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                true, leftTabbedPane, centralPanel);
        centralSplit.setDividerLocation(userPrefs.getSplitterLocation());
        add(centralSplit, BorderLayout.CENTER);

        add(createStatusBar(), BorderLayout.SOUTH);

    }

    private void followBookmarkInView(Bookmark bookmark) {
        if (bookmark == null) {
            return;
        }

        lblSelectedNode.setText(Res.getString("SELECTED_BOOKMARK") + ": " +
                bookmark.getDescription(userPrefs.getUseThousandths()));

        if (bookmark.getType() == BookmarkType.Named) {
            followBookmarkInView(bookmark.getNamedTarget());
            return;
        }

        if (bookmark.getType() == BookmarkType.Uri) {
            goToWebLink(bookmark.getUri());
            return;
        }

        int destPage = bookmark.getPageNumber();
        viewPanel.goToPage(destPage);

        switch (bookmark.getType()) {
            case FitWidth:
                checkInheritTop.setSelected(bookmark.getTop() < 0);
                viewPanel.setFitWidth(bookmark.getTop());
                break;
            case FitHeight:
                checkInheritLeft.setSelected(bookmark.getLeft() < 0);
                viewPanel.setFitHeight(bookmark.getLeft());
                break;
            case FitPage:
                viewPanel.setFitPage();
                break;
            case FitRect:
                viewPanel.setFitRect(bookmark.getTop(), bookmark.getLeft(),
                        bookmark.getBottom(), bookmark.getRight());
                break;
            case TopLeft:
            case TopLeftZoom:
                checkInheritTop.setSelected(bookmark.getTop() < 0);
                checkInheritLeft.setSelected(bookmark.getLeft() < 0);
                checkInheritZoom.setSelected(bookmark.getZoom() <= 0);
                viewPanel.setTopLeftZoom(bookmark.getTop(),
                        bookmark.getLeft(), bookmark.getZoom());
                break;
            case Unknown:
                break;
        }
    }

    private class MouseOverTree extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            super.mousePressed(e);

            TreePath path = bookmarksTree.getPathForLocation(e.getX(), e.getY());

            if (e.isPopupTrigger()) {
                if (path != null) {
                    bookmarksTree.setSelectionPath(path);
                    treeMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            super.mouseExited(e);
            lblMouseOverNode.setText(" ");
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            super.mouseMoved(e);
            if ((e.getSource() instanceof MouseDraggableTree) == false) {
                return;
            }

            MouseDraggableTree tree = (MouseDraggableTree) e.getSource();
            TreePath overPath = tree.getPathForLocation(e.getX(), e.getY());
            if (overPath == null) {
                lblMouseOverNode.setText(" ");
                return;
            }
            Object obj = overPath.getLastPathComponent();
            if (obj != null && obj instanceof Bookmark) {
                Bookmark bookmark = (Bookmark) obj;
                lblMouseOverNode.setText(Res.getString("MOUSE_OVER_BOOKMARK") +
                        ": " + bookmark.getDescription(userPrefs.getUseThousandths()));
            }
        }

        /* I use this instead of TreeSelectionListener to avoid changing view
         * while dragging */
        @Override
        public void mouseReleased(MouseEvent e) {
            super.mouseClicked(e);

            TreePath path = bookmarksTree.getPathForLocation(e.getX(), e.getY());

            if (e.isPopupTrigger()) {
                if (path != null) {
                    bookmarksTree.setSelectionPath(path);
                    treeMenu.show(e.getComponent(), e.getX(), e.getY());
                }
                return;
            }

            if (bookmarksTree.isDragging()) {
                return;
            }

            if (path != null && !e.isControlDown() && !e.isAltDown() &&
                    !e.isShiftDown()) {
                Bookmark bookmark = null;
                try {
                    bookmark = (Bookmark) path.getLastPathComponent();
                } catch (ClassCastException exc) {
                }
                if (bookmark != null) {
                    followBookmarkInView(bookmark);
                }
            }
        }
    }

    private class KeysOverTree extends KeyAdapter {
        /* I use this instead of TreeSelectionListener to avoid changing view
         * while dragging */

        @Override
        public void keyReleased(KeyEvent e) {
            super.keyReleased(e);
            int code = e.getKeyCode();
            int[] triggers = new int[]{KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
                KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_HOME,
                KeyEvent.VK_END, KeyEvent.VK_PAGE_DOWN,
                KeyEvent.VK_PAGE_UP};

            for (int trigger : triggers) {
                if (trigger == code) {
                    followBookmarkInView(getSelectedBookmark());
                    break;
                }
            }
        }
    }
}

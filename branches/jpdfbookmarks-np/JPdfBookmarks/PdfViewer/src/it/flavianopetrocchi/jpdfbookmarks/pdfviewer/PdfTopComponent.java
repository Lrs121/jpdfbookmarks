/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.flavianopetrocchi.jpdfbookmarks.pdfviewer;

import java.util.logging.Logger;
import javax.tools.FileObject;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.util.ImageUtilities;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.windows.CloneableTopComponent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//it.flavianopetrocchi.jpdfbookmarks.pdfviewer//Pdf//EN",
autostore = false)
public final class PdfTopComponent extends CloneableTopComponent {

    private static PdfTopComponent instance;
    /** path to the icon used by the component and its open action */
    static final String ICON_PATH = "it/flavianopetrocchi/jpdfbookmarks/pdfviewer/pdf16.png";
    private static final String PREFERRED_ID = "PdfTopComponent";

    public PdfTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(PdfTopComponent.class, "CTL_PdfTopComponent"));
        setToolTipText(NbBundle.getMessage(PdfTopComponent.class, "HINT_PdfTopComponent"));
        setIcon(ImageUtilities.loadImage(ICON_PATH, true));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link #findInstance}.
     */
    public static synchronized PdfTopComponent getDefault() {
        if (instance == null) {
            instance = new PdfTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the PdfTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized PdfTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(PdfTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof PdfTopComponent) {
            return (PdfTopComponent) win;
        }
        Logger.getLogger(PdfTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID
                + "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    Object readProperties(java.util.Properties p) {
        if (instance == null) {
            instance = this;
        }
        instance.readPropertiesImpl(p);
        return instance;
    }

    private void readPropertiesImpl(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }
}

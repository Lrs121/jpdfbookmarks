package it.flavianopetrocchi.jpdfbookmarks;

import java.awt.Component;
import java.awt.Image;
import static java.awt.Image.SCALE_DEFAULT;
import java.io.IOException;

import java.awt.image.BufferedImage;
import static java.lang.Math.min;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.rendering.PDFRenderer;

/**
 * The thumbnails JScrollPane
 *
 * @author rmfritz
 */
public class ThumbnailsPanel extends JScrollPane implements PageChangedListener {

    static final float THUMBSIZE = 108;

    private final PDDocument document;
    private Box thumbnailBox;
    private final PDFRenderer thumbnailRenderer;

    /**
     * This rather meager constructor just stores a reference to the PDFBox
     * document; most of the work is done in setupThumbnails().
     *
     * @param doc the PDF document for which thumbnails will be displayed
     */
    public ThumbnailsPanel(PDDocument doc) {
        document = doc;
        thumbnailRenderer = new PDFRenderer(doc);
        // Can't do anything else yet; got to wait until the thumbnailBox has been created and passed to
        // setupThumbnails.
    }

    // TBD: make this work
    @Override
    public void pageChanged(PageChangedEvent evt) {
        // resetHighlightedThumbnail(evt.getCurrentPage() - 1);
        // generateOtherVisibleThumbnails(evt.getCurrentPage());
    }

    /**
     * Create the Box for thumbnail buttons and populate it.
     *
     */
    public void setupThumbnails() {
        // Create the Box that will contain the thumbnail buttons
        thumbnailBox = Box.createVerticalBox();
        // Attach it to the scrolling viewport of the JScrollPane
        this.getViewport().add(thumbnailBox);
        // create page thumbnail buttons and page numbers, TBD: possibly include page labels
        // for each page
        Image thumb;
        ImageIcon icon;
        ImageIcon nothumb = new ImageIcon(
                getClass().getResource("/it/flavianopetrocchi/jpdfbookmarks/gfx/nothumb.png"));
        // For each page
        for (int pageIndex = 0; pageIndex < document.getNumberOfPages(); pageIndex++) {
            // Create the thumbnail button and add it to the box
            ThumbnailButton tb = new ThumbnailButton(pageIndex + 1);
            tb.setVerticalTextPosition(AbstractButton.BOTTOM);
            tb.setHorizontalTextPosition(AbstractButton.CENTER);
            // TBD: generate the page thumbnails as the button becomes visible.
            // Currently, this generates page thumbnails for the first 10 pages, if there are that many.
            thumb = null;
            if (pageIndex < 10)
                thumb = getThumb(pageIndex);
            if (thumb != null)
                tb.setIcon(new ImageIcon(thumb));
            else
                tb.setIcon(nothumb);
            thumbnailBox.add(tb);
        }
    }

    /**
     * Return an ArrayList of all the thumbnail buttons in the thumbnail button
     * Box. This is used to attach listeners to the buttons.
     *
     * @return ArrayList
     */
    public ArrayList<ThumbnailButton> getThumbnailButtons() {
        ArrayList<ThumbnailButton> tba = new ArrayList<>(document.getNumberOfPages());
        for (Component co : thumbnailBox.getComponents()) {
            if (co instanceof ThumbnailButton) {
                tba.add((ThumbnailButton) co);
            }
        }
        return tba;
    }

    /**
     * Find or create a page thumbnail.
     *
     * @param pIndex - the PDF page index
     * @return
     */
    private Image getThumb(int pIndex) {
        // Get the stored thumbnail, if any.
        BufferedImage thumbnail = null;
        PDPage page = document.getPage(pIndex);
        COSStream strm = page.getCOSObject().getCOSStream(COSName.THUMB);
        // If the page thumbnail can be found, try to get it
        if (strm != null) {
            try {
                thumbnail = PDImageXObject.createThumbnail(strm).getImage();
            } catch (IOException e) {
                thumbnail = null;
            }
        }
        if (thumbnail != null) {
            // A thumbnail found in the PDF file
            // Scale the image to fit in THUMBSIZE
            int w =thumbnail.getWidth();
            int h = thumbnail.getHeight();
            if (h > THUMBSIZE && h >= w)
                thumbnail = (BufferedImage) thumbnail.getScaledInstance(-1, (int) THUMBSIZE, SCALE_DEFAULT);
            else if (w > THUMBSIZE)
                thumbnail = (BufferedImage) thumbnail.getScaledInstance((int) THUMBSIZE, -1, SCALE_DEFAULT);
            // Return the image
            return thumbnail;
        }
        // No thumbnail stored, generate a thumbnail.
        // 
        PDRectangle rect = page.getCropBox();
        if (rect == null)
            rect = page.getMediaBox();
        if (rect == null)
            return null;
        float hscale = THUMBSIZE / rect.getWidth();
        float wscale = THUMBSIZE / rect.getHeight();
        float scale = min(hscale, wscale);
        try {
            thumbnail = thumbnailRenderer.renderImage(pIndex, scale);
        } catch (IOException e) {
            thumbnail = null;
        }
        return thumbnail;
    }

//        // Set up to monitor viewport size changes
//        this.getViewport().addChangeListener(new viewportSizeTracker());
//    private class viewportSizeTracker implements ChangeListener {
//        
//        @Override
//         public void stateChanged(ChangeEvent e) {
//            JViewport vp = (JViewport) e.getSource();
//            System.out.println("Width = " + vp.getWidth() + "  " + "Height = " + vp.getHeight());
//        }
//        
//    }
}

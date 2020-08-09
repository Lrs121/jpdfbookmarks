package it.flavianopetrocchi.jpdfbookmarks;

import java.awt.Component;
import java.awt.Image;
import static java.awt.Image.SCALE_DEFAULT;
import java.awt.Rectangle;
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
 * Displays clickable thumbnails of pages in a PDF file. The thumbnails are of
 * class ThumbnailButton, a subclass of JButton. They are organized into a Box,
 * which is the single child of the JViewport of the ThumbnailsPane, itself a
 * subclass of JScrollPane.
 *
 * TBD: this code would probably be significantly improved if the size of the
 * thumbnail icons was fixed whenever the pane's size was set.
 *
 * @author rmfritz
 */
public class ThumbnailsPane extends JScrollPane implements PageChangedListener {

    // Maximum size of a thumbnail icon in Adobe points. 1.5 inches or about 38 mm.
    static final float THUMBSIZE = 108;

    private final PDDocument document;
    private Box thumbnailBox;

    public Box getThumbnailBox() {
        return thumbnailBox;
    }
    private final PDFRenderer thumbnailRenderer;

    /**
     * This rather meager constructor just stores a reference to the PDFBox
     * document and creates a renderer object for the thumbnails; most of the
     * work is done in setupThumbnails().
     *
     * @param doc the PDF document for which thumbnails will be displayed
     */
    public ThumbnailsPane(PDDocument doc) {
        document = doc;
        thumbnailRenderer = new PDFRenderer(doc);
        // Can't do anything else yet; got to wait until the thumbnailBox has been created and passed to
        // setupThumbnails.
    }

    /**
     * Responds to a change in the display PDF page, making the appropriate
     * thumbnail visible in the thumbnails windows. It will be confused by
     * changes in the sizes of displayed thumbnails, which means the need to fix
     * those sizes is pressing.
     *
     * @param e the event fired when the page is changed in the
     * JPDFBoxViewPanel.
     */
    @Override
    public void pageChanged(PageChangedEvent e) {
        ThumbnailsPane tp = (ThumbnailsPane) ((JPDFBoxViewPanel) e.getSource()).getThumbnails();
        ThumbnailButton tb = null;
        // Find the ThumbnailButton that corresponds to the page that has just been selected
        for (Component co : tp.getThumbnailBox().getComponents()) {
            if (co instanceof ThumbnailButton) {
                tb = (ThumbnailButton) co;
                if (tb.getPageNum() == e.getCurrentPage()) {
                    break;
                }
            }
        }
        // If we've actually found the button (which should be always, but just in case) make it visible.
        if (tb != null) {
            Rectangle buttonRect = tb.getBounds();
            tp.getViewport().scrollRectToVisible(buttonRect);
        }
    }

    /**
     * Create the Box for thumbnail buttons and populate it.
     *
     */
    public void setupThumbnails() {
        // Set up scrolling speed for the thumbnail pane
        this.getVerticalScrollBar().setUnitIncrement((int) (THUMBSIZE / 3));
        // Monitor the JViewport's size
        this.getViewport().addChangeListener(new thumbnailGenControl());
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
            tb.setThumb(nothumb, false);
            thumbnailBox.add(tb);
        }
    }

    /**
     * Return an ArrayList of all the thumbnail buttons in the thumbnail button
     * Box. This is used to attach listeners to the buttons and to generate
     * thumbnails for the buttons.
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
     * Find or create a page thumbnail. All thumbnails fit in a THUMBSIZE
     * square.
     *
     * @param pIndex - the PDF page index
     * @return an Image containing the thumbnail or null.
     */
    private Image getThumb(int pIndex) {
        // Get the stored thumbnail, if any, from the PDF document.
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
        // If we found a stored thumbnail, scale it to fit within THUMBSIZE.
        if (thumbnail != null) {
            // A thumbnail found in the PDF file
            // Scale the image to fit in THUMBSIZE
            int w = thumbnail.getWidth();
            int h = thumbnail.getHeight();
            if (h > THUMBSIZE && h >= w) {
                thumbnail = (BufferedImage) thumbnail.getScaledInstance(-1, (int) THUMBSIZE, SCALE_DEFAULT);
            } else if (w > THUMBSIZE) {
                thumbnail = (BufferedImage) thumbnail.getScaledInstance((int) THUMBSIZE, -1, SCALE_DEFAULT);
            }
            // Return the image
            return thumbnail;
        }
        // No thumbnail stored in PDF, generate a thumbnail.
        // 
        PDRectangle rect = page.getCropBox();
        if (rect == null) {
            rect = page.getMediaBox();
        }
        if (rect == null) {
            return null;
        }
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

    /**
     * Generates the thumbnails for the ThumbnailButton objects. It is invoked
     * whenever the ThumbnailsPane changes size. Thumbnail generation is a
     * relatively slow operation, so this only generates thumbnails as the
     * ThumbnailButton objects become visible.
     */
    private static class thumbnailGenControl implements ChangeListener {

        /**
         * ThumbnailsPane has changed size; generate thumbnails as required.
         *
         * @param e the size change event
         */
        @Override
        public void stateChanged(ChangeEvent e) {
            JViewport vp = (JViewport) e.getSource();
            ThumbnailsPane tp = (ThumbnailsPane) vp.getParent();
            // if there's no Box yet in the JViewport, there's nothing to do, return.
            if (vp.getView() == null) {
                return;
            }
            // Get a list of ThumbnailButton instances, in page number order.
            ArrayList<ThumbnailButton> tbs = tp.getThumbnailButtons();
            // Get the visible rectangle of the JViewport, in internal coordinates
            Rectangle viewRect = vp.getViewRect();
            int topView = viewRect.y;                                  // Top of the viewport
            int botView = viewRect.y + viewRect.height;     // Bottom of the viewport
            /**
             * For each button, decide if the button is visible. If it is, and
             * it doesn't yet have a thumbnail, generate one. The code is
             * confusing enough that it is extensively commented. It does seem
             * to work, but may have problems with edge cases.
             */
            for (ThumbnailButton tb : tbs) {
                /**
                 * If we're past the first page of the document, and the
                 * thumbnail still shows a y position of zero, the button isn't
                 * actually visible yet; don't generate a thumbnail for it.
                 *
                 * This hack will generate a thumbnail for the first page when
                 * it is not needed, but that is acceptable and beats trying to
                 * figure out how to test for visibility.
                 */
                if (tb.getPageNum() > 1 && tb.getY() == 0) {
                    break;
                }

                // Get the top and bottom of the button.
                int topTb = tb.getY();
                int botTb = tb.getY() + tb.getHeight();

                /**
                 * If the top of the button is below the bottom of the visible
                 * area of the pane, we're past the last visible button; stop
                 * looking.
                 */
                if (topTb > botView) {
                    break;
                }

                /**
                 * If the bottom of the button is before the top of the visible
                 * area of the pane, skip the button.
                 */
                if (botTb < topView) {
                    continue;
                }

                /**
                 * if the button already has a real thumbnail, skip it.
                 */
                if (tb.hasRealThumb()) {
                    continue;
                }

                /**
                 * The button is visible and does not have a real thumbnail.
                 * Generate a thumbnail and assign it to the button
                 *
                 */
                Image thumb = tp.getThumb(tb.getPageNum() - 1);
                if (thumb != null) {
                    tb.setThumb(new ImageIcon(thumb), true);
                }
            }
        }
    }
}

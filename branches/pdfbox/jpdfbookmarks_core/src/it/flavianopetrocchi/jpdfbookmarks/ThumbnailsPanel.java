package it.flavianopetrocchi.jpdfbookmarks;

import java.awt.Component;
import java.io.IOException;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;

import org.apache.pdfbox.cos.*;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

/**
 * The thumbnails JScrollPane
 * *
 * @author fla
 * @author rmfritz
 */
public class ThumbnailsPanel extends JScrollPane implements PageChangedListener {

    private final PDDocument document;
    private Box thumbnailBox;

    /**
     * This rather meager constructor just stores a reference to the PDFBox
     * document; most of the work is done in setupThumbnails().
     *
     * @param doc the PDF document for which thumbnails will be displayed
     */
    public ThumbnailsPanel(PDDocument doc) {
        document = doc;
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
     * Create the Box for thumbnail buttons and populate it. TBD: implement the
     * button listener
     *
     */
    public void setupThumbnails() {
        // Create the Box that will contain the thumbnail buttons
        thumbnailBox = Box.createVerticalBox();
        // Attach it to the scrolling viewport of the JScrollPane
        this.getViewport().add(thumbnailBox);
        // create page thumbnail buttons and page numbers, possibly include page labels
        // attach listener to buttons
        // for each page
        int pageNum = 0;
        ImageIcon nothumb = new ImageIcon(
                getClass().getResource("/it/flavianopetrocchi/jpdfbookmarks/gfx/nothumb.png"));
        // For each page
        for (PDPage page : document.getPages()) {
            // maintain the page count, since pages don't store their numbers internally
            pageNum++;
            // Get the thumbnail, if any
            BufferedImage thumbnail = null;
            COSStream strm = page.getCOSObject().getCOSStream(COSName.THUMB);
            if (strm != null) {
                try {
                    thumbnail = PDImageXObject.createThumbnail(strm).getImage();
                } catch (IOException e) {
                    thumbnail = null;
                }
            }
            ImageIcon icon;
            icon = (thumbnail != null) ? new ImageIcon(thumbnail) : nothumb;
            // Create the thumbnail button and add it to the box
            ThumbnailButton tb = new ThumbnailButton(pageNum, icon);
            tb.setVerticalTextPosition(AbstractButton.BOTTOM);
            tb.setHorizontalTextPosition(AbstractButton.CENTER);
            thumbnailBox.add(tb);
        }
    }

    /**
     * Return an ArrayList of all the thumbnail buttons in the thumbnail button Box.
     * 
     * @return ArrayList
     */
    public ArrayList<ThumbnailButton> getThumbnailButtons() {
        ArrayList<ThumbnailButton> tba = new ArrayList<>();
        for (Component co : thumbnailBox.getComponents()) {
            if (co instanceof ThumbnailButton)
                tba.add((ThumbnailButton) co);
        }
        return tba;
    }
}

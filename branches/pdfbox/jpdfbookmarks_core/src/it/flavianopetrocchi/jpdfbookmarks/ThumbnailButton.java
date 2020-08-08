/*
 * Copyright (C) 2020 rfritz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.flavianopetrocchi.jpdfbookmarks;

import javax.swing.JButton;


/**
 * A button which carries the thumbnail of a PDF document page. The only thing this adds to the basic
 * JButton class is a page number.
 * 
 * @author rfritz
 */
public class ThumbnailButton extends JButton {

    int pageNum;

    public ThumbnailButton(int pnum) {
        super("" + pnum);
        pageNum = pnum;
    }
    
    public int getPageNum() {
        return pageNum;
    }
}

// This fragment gets the viewport the button belongs to, then gets its size and outputs it.
// The size is the actual size of the viewport, not the virtual size of the box it contains.
// The parent of the button is the Box in which the buttons are placed, then the parent
// of that is the JViewport belonging to the JScrollPane.
//        Container vp = this.getParent().getParent();
//        System.out.println("Width = " + vp.getWidth() + "  " + "Height = " + vp.getHeight());
// it would probably be sturdier code if there was an actual test for a JViewport, perhaps like
// for (Container vp = this.getParent(); vp != null; vp = this.getParent())
//    if (vp instanceof JViewport)
//      break;
// if (vp == null)
//    error…

// This fragment gets a thumbnail stored in a PDF file, if any.
//            // Get the thumbnail, if any
//            BufferedImage thumbnail = null;
//            COSStream strm = page.getCOSObject().getCOSStream(COSName.THUMB);
//            if (strm != null) {
//                try {
//                    thumbnail = PDImageXObject.createThumbnail(strm).getImage();
//                } catch (IOException e) {
//                    thumbnail = null;
//                }
//            }
//            ImageIcon icon;
//            icon = (thumbnail != null) ? new ImageIcon(thumbnail) : nothumb;

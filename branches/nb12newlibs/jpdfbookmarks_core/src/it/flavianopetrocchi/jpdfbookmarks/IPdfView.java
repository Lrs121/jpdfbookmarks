/*
 * IPdfView.java
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

import it.flavianopetrocchi.jpdfbookmarks.bookmark.Bookmark;
import java.awt.Rectangle;
import java.io.File;
import javax.swing.JScrollPane;

/**
 * An interface to a PDF rendering class. This is provided so that one PDF
 * rendering system can be swapped for another.
 *
 * Each instance of an implementation provides rendering services for one PDF
 * file.
 *
 * @author fla
 * @version $Id$
 */
public interface IPdfView {

    // <editor-fold defaultstate="collapsed" desc="File Methods">
    /**
     * Open a PDF file without a password.
     *
     * @param file
     * @throws Exception
     */
    public void open(File file) throws Exception;

    /**
     * Open a PDF file.
     *
     * @param file The File to be opened.
     * @param password A String, the file password, if any.
     * @throws Exception
     */
    public void open(File file, String password) throws Exception;

    /**
     * Close the PDF file associated with this instance, opening a different
     * version of the same file in its place, displaying the same page. This is
     * used for update-by-copy operations.
     *
     * @param file The new File to be opened in place of the currently open
     * file.
     * @throws Exception
     */
    public void reopen(File file) throws Exception;

    /**
     * Close the PDF file associated with this instance, freeing data associated
     * with the instance.
     */
    public void close();
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Page Number Methods">
    /**
     * Get the number of pages in the displayed PDF file. Pages are numbered
     * from 1 to the end of the file. The local page numbers (i, A-2, etc.)
     * which PDF supports are not used here.
     *
     * @return An int, the number of pages in the PDF file
     */
    public int getNumPages();

    /**
     * Get the number of the current page.
     *
     * @return An int, the current page number.
     */
    public int getCurrentPage();

    /**
     * Display the first page in the PDF file.
     */
    public void goToFirstPage();

    /**
     * Return to the current page after scrolling away from it.
     */
    public void goToPreviousPage();

    /**
     * Display page numPage. NumPage counts actual pages in the document
     * starting at one. If numPage is less than one, goToPage goes to page one.
     * If numPage is greater than the number of pages in the document, goToPage
     * goes to the end of the document.
     *
     * @param numPage An int which gives the page number
     */
    public void goToPage(int numPage);

    /**
     * Display the next page in the document.
     */
    public void goToNextPage();

    /**
     * Display the last page in the document.
     */
    public void goToLastPage();
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fit methods">
    public FitType getFitType();

    public void setFitNative();

    public void setFitWidth(int top);

    public void setFitHeight(int left);

    public void setFitPage();

    public void setFitRect(int top, int left, int bottom, int right);

    public void setFitRect(Rectangle rect);
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Bookmark Methods">
    /*public void goToBookmark(Bookmark bookmark);*/
    public Bookmark getBookmarkFromView();
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Listeners">
    public void addPageChangedListener(PageChangedListener listener);

    public void removePageChangedListener(PageChangedListener listener);

    public void addViewChangedListener(ViewChangedListener listener);

    public void removeViewChangedListener(ViewChangedListener listener);

    public void addTextCopiedListener(TextCopiedListener listener);

    public void removeTextCopiedListener(TextCopiedListener listener);

    public void addRenderingStartListener(RenderingStartListener listener);

    public void removeRenderingStartListener(RenderingStartListener listener);
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Miscellaneous Methods">
    public void setTopLeftZoom(int top, int left, float zoom);

    /**
     * Turn on or off text selection mode
     * 
     * @param set A boolean - if true, enable text selection mode.
     */
    public void setTextSelectionMode(boolean set);

    public void setConnectToClipboard(Boolean set);

    /**
     * Extract text from a selected rectangle.
     * 
     * @param rectInCrop The Rectangle which describes the selection.
     * @return A String containing the extracted text
     */
    public String extractText(Rectangle rectInCrop);

    /**
     * Get the pane which displays the document thumbnails
     * 
     * @return A JScrollPane
     */
    public JScrollPane getThumbnails();
    // </editor-fold>

}

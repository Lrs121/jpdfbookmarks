/*
 * RenderingStartEvent.java
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

import java.util.EventObject;

/**
 * Fired when the app begins rendering a PDF page.
 * 
 * @author fla
 */
public class RenderingStartEvent extends EventObject {
	private int pageNumber;

	public RenderingStartEvent(Object source, int pageNumber) {
		super(source);
		this.pageNumber = pageNumber;
	}

	public int getPageNumber() {
		return this.pageNumber;
	}
		

}

/**
 * ditaa - Diagrams Through Ascii Art
 * 
 * Copyright (C) 2004-2011 Efstathios Sideris
 *
 * ditaa is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * ditaa is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with ditaa.  If not, see <http://www.gnu.org/licenses/>.
 *   
 */
package org.stathissideris.ascii2image.core;

import java.awt.Color;
import java.util.HashMap;

import org.stathissideris.ascii2image.graphics.CustomShapeDefinition;

/**
 * 
 * @author Usha Lokala
 */

/**
 * This is class RenderingOptions
 */
public class RenderingOptions {

	private HashMap<String, CustomShapeDefinition> customShapes;
	
	private boolean dropShadows = true;
	private boolean renderDebugLines = false;
	private boolean antialias = true;
    private boolean fixedSlope = false;

	private int cellWidth = 10;
	private int cellHeight = 14;
	
	private float scale = 1;
	
	private Color backgroundColor = Color.white;

	public enum ImageType { PNG, SVG }

    private ImageType imageType = ImageType.PNG;

	/**
	 * THIS IS getImageType METHOD
	 * @return imageType enum
	 */
	public ImageType getImageType() { return imageType; }

	/**
	 * This is setImageType method
	 * @param type
	 */
	public void setImageType(ImageType type) { imageType = type; }

	private String fontFamily = "Courier";
	private String fontURL = null;

	/**
	 *
	 * @return fontFamily of type String
	 */
	public String getFontFamily() { return fontFamily; }

	/**
	 *
	 * @return fontURL of type String
	 */
	public String getFontURL() { return fontURL; }

	/**
	 *
	 * @param url
	 */
	public void setFontURL(String url) { fontFamily = "Custom"; fontURL = url; }

	/**
	 *
	 * @return cellHeight of type int
	 */
	public int getCellHeight() {
		return cellHeight;
	}

	/**
	 *
	 * @return cellWidth of type int
	 */
	public int getCellWidth() {
		return cellWidth;
	}

	/**
	 *
	 * @return dropShadows of type boolean;
	 */
	public boolean dropShadows() {
		return dropShadows;
	}

	/**
	 *
	 * @return renderDebugLines of type boolean;
	 */
	public boolean renderDebugLines() {
		return renderDebugLines;
	}

	/**
	 *
	 * @return scale of type float
	 */
	public float getScale() {
		return scale;
	}

	/**
	 *
	 * @param b
	 */
	public void setDropShadows(boolean b) {
		dropShadows = b;
	}

	/**
	 *
	 * @param b
	 */
	public void setRenderDebugLines(boolean b) {
		renderDebugLines = b;
	}

	/**
	 *
	 * @param f
	 */
	public void setScale(float f) {
		scale = f;
		cellWidth *= scale;
		cellHeight *= scale;
	}

	/**
	 *
	 * @return antialias;
	 */
	public boolean performAntialias() {
		return antialias;
	}

	/**
	 *
	 * @param b
	 */
	public void setAntialias(boolean b) {
		antialias = b;
	}

	/**
	 *
	 * @return backgroundColor;
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 *
	 * @param backgroundColor
	 */
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	/**
	 *
	 * @return boolean
	 */
	public boolean needsTransparency() {
		return backgroundColor.getAlpha() < 255;
	}

	/**
     * Should the sides of trapezoids and parallelograms have fixed width (false, default)
     * or fixed slope (true)?
     * @return true for fixed slope, false for fixed width
     */
    public boolean isFixedSlope() {
        return fixedSlope;
    }

    /**
     * Should the sides of trapezoids and parallelograms have fixed width (false, default)
     * or fixed slope (true)?
     * @param b true for fixed slope, false for fixed width
     */
    public void setFixedSlope(boolean b) {
        this.fixedSlope = b;
    }
}

/**
 * ditaa - Diagrams Through Ascii Art
 * <p>
 * Copyright (C) 2004-2011 Efstathios Sideris
 * <p>
 * ditaa is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * <p>
 * ditaa is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with ditaa.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.stathissideris.ascii2image.text;

/**
 *
 * @author Efstathios Sideris
 * This class represents a cell on a grid.
 * This class is misnamed, because it has no abstract methods and nothing inherits it.
 * The static methods of this class are factories for this type.
 */
public class AbstractCell {

    public int rows[][] = new int[3][3];

    {
        for (int y = 0; y < 3; y++)
            for (int x = 0; x < 3; x++)
                rows[x][y] = 0;
    }

    /**
     * Draw a horizontal line
     * @return An AbstractCell containing a horizontal line
     */
    static AbstractCell makeHorizontalLine() {
        AbstractCell result = new AbstractCell();
        result.rows[0][1] = 1;
        result.rows[1][1] = 1;
        result.rows[2][1] = 1;
        return result;
    }

    /**
     * Draw a vertical line
     * @return An AbstractCell containing a vertical line
     */
    static AbstractCell makeVerticalLine() {
        AbstractCell result = new AbstractCell();
        result.rows[1][0] = 1;
        result.rows[1][1] = 1;
        result.rows[1][2] = 1;
        return result;
    }

    /**
     * Draw a corner
     * @return An AbstractCell containing a cornder
     */
    static AbstractCell makeCorner1() {
        AbstractCell result = new AbstractCell();
        result.rows[1][1] = 1;
        result.rows[1][2] = 1;
        result.rows[2][1] = 1;
        return result;
    }

    /**
     * Draw the second kind of corner.
     * @return AbstractCell containing second kind of corner
     */
    static AbstractCell makeCorner2() {
        AbstractCell result = new AbstractCell();
        result.rows[0][1] = 1;
        result.rows[1][1] = 1;
        result.rows[1][2] = 1;
        return result;
    }

    /**
     * Draw the third kind of corner.
     * @return AbstractCell containing third kind of corner
     */
    static AbstractCell makeCorner3() {
        AbstractCell result = new AbstractCell();
        result.rows[0][1] = 1;
        result.rows[1][1] = 1;
        result.rows[1][0] = 1;
        return result;
    }

    /**
     * Draw the fourth kind of corner
     * @return AbstractCell containing fourth kind of corner
     */
    static AbstractCell makeCorner4() {
        AbstractCell result = new AbstractCell();
        result.rows[2][1] = 1;
        result.rows[1][1] = 1;
        result.rows[1][0] = 1;
        return result;
    }

    /**
     * Make a T-shape
     * @return AbstractCell containing a T shape
     */
    static AbstractCell makeT() {
        AbstractCell result = AbstractCell.makeHorizontalLine();
        result.rows[1][2] = 1;
        return result;
    }

    /**
     * Make an upside-down-T shape
     * @return AbstractCell containing an upside-down T shape
     */
    static AbstractCell makeInverseT() {
        AbstractCell result = AbstractCell.makeHorizontalLine();
        result.rows[1][0] = 1;
        return result;
    }

    /**
     * Make a K shape
     * @return AbstractCell containing K shape.
     */
    static AbstractCell makeK() {
        AbstractCell result = AbstractCell.makeVerticalLine();
        result.rows[2][1] = 1;
        return result;
    }

    /**
     * Make a backwards K shape
     * @return A Cell containing a backwards K
     */
    static AbstractCell makeInverseK() {
        AbstractCell result = AbstractCell.makeVerticalLine();
        result.rows[0][1] = 1;
        return result;
    }

    /**
     * Make a cross shape
     * @return AbstractCell containing a cross shape
     */
    static AbstractCell makeCross() {
        AbstractCell result = AbstractCell.makeVerticalLine();
        result.rows[0][1] = 1;
        result.rows[2][1] = 1;
        return result;
    }

    static AbstractCell makeStar() {
        AbstractCell result = AbstractCell.makeVerticalLine();
        for (int y = 0; y < 3; y++)
            for (int x = 0; x < 3; x++)
                result.rows[x][y] = 1;
        return result;
    }


}

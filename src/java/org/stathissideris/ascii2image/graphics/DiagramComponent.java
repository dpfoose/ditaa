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
package org.stathissideris.ascii2image.graphics;

import org.stathissideris.ascii2image.text.CellSet;
import org.stathissideris.ascii2image.text.TextGrid;

/**
 * Implements abstract class for Diagram components
 * @author Efstathios Sideris
 */
public abstract class DiagramComponent {

    private static final boolean DEBUG = false;

    /**
     * Creates and returns a ShapePoint object for given <code>cell</code> on <code>grid</code>
     * The type of the point is inferred from cell value.
     * @param cell
     * @param grid
     * @param cellWidth
     * @param cellHeight
     * @param allRound
     * @return
     */
    protected static ShapePoint makePointForCell(TextGrid.Cell cell, TextGrid grid, int cellWidth, int cellHeight, boolean allRound) {
        if (DEBUG)
            System.out.println("Found point at cell " + cell);
        if (grid.isCorner(cell) && allRound) {
            return new ShapePoint(
                    cell.x * cellWidth + cellWidth / 2,
                    cell.y * cellHeight + cellHeight / 2,
                    ShapePoint.TYPE_ROUND
            );
        } else if (grid.isNormalCorner(cell)) {
            return new ShapePoint(
                    cell.x * cellWidth + cellWidth / 2,
                    cell.y * cellHeight + cellHeight / 2,
                    ShapePoint.TYPE_NORMAL
            );
        } else if (grid.isRoundCorner(cell)) {
            return new ShapePoint(
                    cell.x * cellWidth + cellWidth / 2,
                    cell.y * cellHeight + cellHeight / 2,
                    ShapePoint.TYPE_ROUND
            );
        } else if (grid.isLinesEnd(cell)) {
            return new ShapePoint(
                    cell.x * cellWidth + cellWidth / 2,
                    cell.y * cellHeight + cellHeight / 2,
                    ShapePoint.TYPE_NORMAL
            );
        } else if (grid.isIntersection(cell)) {
            return new ShapePoint(
                    cell.x * cellWidth + cellWidth / 2,
                    cell.y * cellHeight + cellHeight / 2,
                    ShapePoint.TYPE_NORMAL
            );
        }
        throw new RuntimeException("Cannot make point for cell " + cell);
    }

    public static DiagramComponent createClosedFromBoundaryCells(TextGrid grid, CellSet cells, int cellWidth, int cellHeight) {
        return createClosedFromBoundaryCells(grid, cells, cellWidth, cellHeight, false);
    }

    /**
     * @param grid
     * @param cells
     * @param cellWidth
     * @param cellHeight
     * @param allRound
     * @return
     */
    public static DiagramComponent createClosedFromBoundaryCells(TextGrid grid, CellSet cells, int cellWidth, int cellHeight, boolean allRound) {
        if (cells.getType(grid) == CellSet.TYPE_OPEN)
            throw new IllegalArgumentException("CellSet is closed and cannot be handled by this method");
        if (cells.size() < 2) return null;

        DiagramShape shape = new DiagramShape();
        shape.setClosed(true);
        if (grid.containsAtLeastOneDashedLine(cells)) shape.setStrokeDashed(true);

        TextGrid workGrid = new TextGrid(grid.getWidth(), grid.getHeight());
        grid.copyCellsTo(cells, workGrid);

        if (DEBUG) {
            System.out.println("Making closed shape from buffer:");
            workGrid.printDebug();
        }

        TextGrid.Cell start = cells.getFirst();
        if (workGrid.isCorner(start))
            shape.addToPoints(makePointForCell(start, workGrid, cellWidth, cellHeight, allRound));
        TextGrid.Cell previous = start;
        TextGrid.Cell cell = null;
        CellSet nextCells = workGrid.followCell(previous);
        if (nextCells.size() == 0) return null;
        cell = nextCells.getFirst();
        if (workGrid.isCorner(cell))
            shape.addToPoints(makePointForCell(cell, workGrid, cellWidth, cellHeight, allRound));

        while (!cell.equals(start)) {
            nextCells = workGrid.followCell(cell, previous);
            if (nextCells.size() == 1) {
                previous = cell;
                cell = nextCells.getFirst();
                if (!cell.equals(start) && workGrid.isCorner(cell))
                    shape.addToPoints(makePointForCell(cell, workGrid, cellWidth, cellHeight, allRound));
            } else if (nextCells.size() > 1) {
                return null;
            } else {
                throw new RuntimeException("cannot create closed shape from boundary cells, nowhere to go from "
                        + cell + " coming from " + previous + " in grid:\n" + grid
                        + "\nmaybe you have an edge pointing nowhere?");
            }
        }

        return shape;

    }
}

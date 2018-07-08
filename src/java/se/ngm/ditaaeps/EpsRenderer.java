/*
 * EPS extension to DiTAA (Diagrams Through Ascii Art)
 *
 * Copyright (C) 2006 Nordic Growth Market NGM AB,
 * Mikael Brannstrom.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package se.ngm.ditaaeps;

import org.stathissideris.ascii2image.core.RenderingOptions;
import org.stathissideris.ascii2image.core.Shape3DOrderingComparator;
import org.stathissideris.ascii2image.core.ShapeAreaComparator;
import org.stathissideris.ascii2image.graphics.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Modification of the BitmapRenderer to render EPS instead of PNG.
 *
 * @author Efstathios Sideris
 * @author Mikael Brannstrom
 */
public class EpsRenderer extends ImageRenderer {

    private static final boolean DEBUG = false;

    private static String[] markupModeAllowedValues = {"use", "ignore", "render"};

    public EpsRenderer(String toFilename, RenderingOptions renderingOptions) {
        super(toFilename, renderingOptions);
    }

    /**
     * Render an EPS image from input <code>diagram</code> using <code>options</code> config parameters.
     *
     * @param diagram
     * @param out
     * @param options
     */
    private void renderToEps(Diagram diagram, PrintWriter out, RenderingOptions options) {
        //RenderedImage renderedImage = image;
        EpsGraphics2D g2 = new EpsGraphics2D(out, new Rectangle2D.Double(0, -diagram.getHeight(), diagram.getWidth(), diagram.getHeight()));
        g2.scale(1, -1); // g2 origo is top-left, eps is bottom-left
        g2.setColor(Color.white);

        //TODO: find out why the next line does not work
        //g2.fillRect(0, 0, image.getWidth()+10, image.getHeight()+10);
    /*for(int y = 0; y < diagram.getHeight(); y ++)
      g2.drawLine(0, y, diagram.getWidth(), y);*/

        g2.setStroke(new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));


        if (DEBUG) System.out.println("Rendering " + diagram.getAllDiagramShapes().size() + " shapes (groups flattened)");

        if (options.dropShadows()) {
            //render shadows
            renderShadows(diagram, g2);
        }

        renderObjects(diagram, g2);
        g2.dispose();
    }


    @Override
    public void renderImage(Diagram d) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(getOutFile());
            renderToEps(d, writer, getOptions());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void renderCustomShape(DiagramShape shape, Graphics2D g2) {
        throw new RuntimeException("Custom shapes not supported for EPS output!");
    }

    @Override
    protected void renderText(List<DiagramText> textObjects, Graphics2D g2){
        for (DiagramText text: textObjects) {
            g2.setColor(text.getColor());
            g2.setFont(text.getFont());
            g2.drawString(text.getText(), text.getXPos(), text.getYPos());
        }
    }
}

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
import org.stathissideris.ascii2image.graphics.AbstractRenderer;
import org.stathissideris.ascii2image.graphics.Diagram;
import org.stathissideris.ascii2image.graphics.DiagramShape;
import org.stathissideris.ascii2image.graphics.DiagramText;

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
public class EpsRenderer extends AbstractRenderer {

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
    private static void renderToEps(Diagram diagram, PrintWriter out, RenderingOptions options) {
        //RenderedImage renderedImage = image;
        EpsGraphics2D g2 = new EpsGraphics2D(out, new Rectangle2D.Double(0, -diagram.getHeight(), diagram.getWidth(), diagram.getHeight()));

        g2.scale(1, -1); // g2 origo is top-left, eps is bottom-left

        Object antialiasSetting = antialiasSetting = RenderingHints.VALUE_ANTIALIAS_OFF;
        if (options.performAntialias())
            antialiasSetting = RenderingHints.VALUE_ANTIALIAS_ON;

        //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antialiasSetting);

        g2.setColor(Color.white);
        //TODO: find out why the next line does not work
        //g2.fillRect(0, 0, image.getWidth()+10, image.getHeight()+10);
    /*for(int y = 0; y < diagram.getHeight(); y ++)
      g2.drawLine(0, y, diagram.getWidth(), y);*/

        g2.setStroke(new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));

        ArrayList<DiagramShape> shapes = diagram.getAllDiagramShapes();

        if (DEBUG) System.out.println("Rendering " + shapes.size() + " shapes (groups flattened)");

        Iterator shapesIt;
        if (options.dropShadows()) {
            //render shadows
            for (DiagramShape shape : shapes) {
                if (shape.getPoints().isEmpty()) continue;
                GeneralPath path = shape.makeIntoRenderPath(diagram, options);
                float offset = diagram.getMinimumOfCellDimension() / 3.333f;
                if (path != null && shape.dropsShadow()) {
                    GeneralPath shadow = new GeneralPath(path);
                    AffineTransform translate = new AffineTransform();
                    translate.setToTranslation(offset, offset);
                    shadow.transform(translate);
                    g2.setColor(new Color(150, 150, 150));
                    g2.fill(shadow);
                }
            }


            //blur shadows

            //            if(true) {
            //                int blurRadius = 6;
            //                int blurRadius2 = blurRadius * blurRadius;
            //                float blurRadius2F = blurRadius2;
            //                float weight = 1.0f / blurRadius2F;
            //                float[] elements = new float[blurRadius2];
            //                for (int k = 0; k < blurRadius2; k++)
            //                    elements[k] = weight;
            //                Kernel myKernel = new Kernel(blurRadius, blurRadius, elements);
            //
            //                //if EDGE_NO_OP is not selected, EDGE_ZERO_FILL is the default which creates a black border
            //                ConvolveOp simpleBlur =
            //                        new ConvolveOp(myKernel, ConvolveOp.EDGE_NO_OP, null);
            //                //BufferedImage destination = new BufferedImage(image.getWidth()+blurRadius, image.getHeight()+blurRadius, image.getType());
            //                BufferedImage destination =
            //                        new BufferedImage(
            //                        image.getWidth(),
            //                        image.getHeight(),
            //                        image.getType());
            //                simpleBlur.filter(image, destination);
            //                //destination = destination.getSubimage(blurRadius/2, blurRadius/2, image.getWidth(), image.getHeight());
            //                g2 = destination.createGraphics();
            //                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antialiasSetting);
            //                renderedImage = destination;
            //            }
        }


        //fill and stroke

        float dashInterval = Math.min(diagram.getCellWidth(), diagram.getCellHeight()) / 2;
        //Stroke normalStroke = g2.getStroke();

        float strokeWeight = diagram.getMinimumOfCellDimension() / 10;

        Stroke normalStroke =
                new BasicStroke(
                        strokeWeight,
                        //10,
                        BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND
                );

        Stroke dashStroke =
                new BasicStroke(
                        strokeWeight,
                        BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_ROUND,
                        0,
                        new float[]{dashInterval},
                        0
                );

        //filter shapes by type
        List<DiagramShape> storageShapes = shapes.stream()
                .filter(shape -> shape.getType() == DiagramShape.TYPE_STORAGE)
                .collect(Collectors.toList());
        List<DiagramShape> pointMarkers = shapes.stream()
                .filter(shape -> shape.getType() == DiagramShape.TYPE_POINT_MARKER)
                .collect(Collectors.toList());
        List<DiagramShape> customShapes = shapes.stream()
                .filter(shape -> shape.getType() == DiagramShape.TYPE_CUSTOM)
                .collect(Collectors.toList());
        List<DiagramShape> otherShapes = shapes.stream()
                .filter(shape -> shape.getType() != DiagramShape.TYPE_CUSTOM
                        && shape.getType() != DiagramShape.TYPE_STORAGE
                        && shape.getType() != DiagramShape.TYPE_POINT_MARKER
                        && !shape.getPoints().isEmpty())
                .collect(Collectors.toList());

        //render storage shapes
        //special case since they are '3d' and should be
        //rendered bottom to top
        //TODO: known bug: if a storage object is within a bigger normal box, it will be overwritten in the main drawing loop
        //(BUT this is not possible since tags are applied to all shapes overlaping shapes)
        storageShapes.sort(new Shape3DOrderingComparator());
        g2.setStroke(normalStroke);
        for (DiagramShape shape : storageShapes) {
            GeneralPath path;
            path = shape.makeIntoRenderPath(diagram, options);
            if (!shape.isStrokeDashed()) {
                g2.setColor(shape.getFillColor() != null ? shape.getFillColor() : Color.white);
                g2.fill(path);
            }
            g2.setStroke(shape.isStrokeDashed() ? dashStroke : normalStroke);
            g2.setColor(shape.getStrokeColor());
            g2.draw(path);
        }


        //render the rest of the shapes
        shapesIt = shapes.iterator();
        for (DiagramShape shape : otherShapes) {
            GeneralPath path = shape.makeIntoRenderPath(diagram, options);
            if (path != null && shape.isClosed() && !shape.isStrokeDashed()) {
                g2.setColor(shape.getFillColor() != null ? shape.getFillColor() : Color.white);
                g2.fill(path);
            }
            if (shape.getType() != DiagramShape.TYPE_ARROWHEAD) {
                g2.setColor(shape.getStrokeColor());
                g2.setStroke(shape.isStrokeDashed() ? dashStroke : normalStroke);
                g2.draw(path);
            }
        }

        //render point markers
        g2.setStroke(normalStroke);
        for (DiagramShape shape : pointMarkers) {
            GeneralPath path = shape.makeIntoRenderPath(diagram, options);
            g2.setColor(Color.white);
            g2.fill(path);
            g2.setColor(shape.getStrokeColor());
            g2.draw(path);
        }


        //handle text
        for (DiagramText text : diagram.getTextObjects()) {
            g2.setColor(text.getColor());
            g2.setFont(text.getFont());
            g2.drawString(text.getText(), text.getXPos(), text.getYPos());
        }

        if (options.renderDebugLines() || DEBUG) {
            Stroke debugStroke =
                    new BasicStroke(
                            1,
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND
                    );
            g2.setStroke(debugStroke);
            g2.setColor(new Color(170, 170, 170));
            g2.setXORMode(Color.white);
            for (int x = 0; x < diagram.getWidth(); x += diagram.getCellWidth())
                g2.drawLine(x, 0, x, diagram.getHeight());
            for (int y = 0; y < diagram.getHeight(); y += diagram.getCellHeight())
                g2.drawLine(0, y, diagram.getWidth(), y);
        }


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
}

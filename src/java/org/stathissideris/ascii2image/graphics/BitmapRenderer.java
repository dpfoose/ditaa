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

import org.stathissideris.ascii2image.core.ConversionOptions;
import org.stathissideris.ascii2image.core.RenderingOptions;
import org.stathissideris.ascii2image.core.Shape3DOrderingComparator;
import org.stathissideris.ascii2image.core.ShapeAreaComparator;
import org.stathissideris.ascii2image.text.TextGrid;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.RenderedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class implements methods to render Bitmap image from Diagram
 *
 * @author Efstathios Sideris
 */
public class BitmapRenderer extends AbstractRenderer {

    private static final boolean DEBUG = false;
    private static final boolean DEBUG_LINES = false;

    private static final String IDREGEX = "^.+_vfill$";

    Stroke normalStroke;
    Stroke dashStroke;

    public BitmapRenderer(String file, RenderingOptions options) {
        super(file, options);
    }

    public static void main(String[] args) throws Exception {


        long startTime = System.currentTimeMillis();

        ConversionOptions options = new ConversionOptions();

        TextGrid grid = new TextGrid();

        String filename = "bug18.txt";

        grid.loadFrom("tests/text/" + filename);

        Diagram diagram = new Diagram(grid, options);
        new BitmapRenderer("tests/images/" + filename + ".png", options.renderingOptions).renderImage(diagram);
        long endTime = System.currentTimeMillis();
        long totalTime = (endTime - startTime) / 1000;
        System.out.println("Done in " + totalTime + "sec");

        File workDir = new File("tests/images");
        //Process p = Runtime.getRuntime().exec("display "+filename+".png", null, workDir);
    }

    /**
     * Render <code>diagram</code> to a png format image
     *
     * @param diagram
     * @param filename
     * @param options
     */
    private boolean renderToPNG(Diagram diagram, String filename, RenderingOptions options) {
        RenderedImage image = renderToImage(diagram, options);

        try {
            File file = new File(filename);
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            //e.printStackTrace();
            System.err.println("Error: Cannot write to file " + filename);
            return false;
        }
        return true;
    }

    /**
     * Public method to render an image from <code>diagram</code> using the configurations in <code>options</code>
     *
     * @param diagram
     * @param options
     * @return RenderedImage object
     */
    public RenderedImage renderToImage(Diagram diagram, RenderingOptions options) {
        BufferedImage image;
        if (options.needsTransparency()) {
            image = new BufferedImage(
                    diagram.getWidth(),
                    diagram.getHeight(),
                    BufferedImage.TYPE_INT_ARGB);
        } else {
            image = new BufferedImage(
                    diagram.getWidth(),
                    diagram.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
        }

        return render(diagram, image, options);
    }

    /**
     * The method implements rendering functionality using a <code> Diagram</code> object.
     * The features like shadow, antialiasing are added to destination image based on <code>options</code>.
     * It also handles rendering of custom shapes.
     *
     * @param diagram
     * @param image
     * @param options
     * @return
     */
    public RenderedImage render(Diagram diagram, BufferedImage image, RenderingOptions options) {
        RenderedImage renderedImage = image;
        Graphics2D g2 = image.createGraphics();

        Object antialiasSetting = RenderingHints.VALUE_ANTIALIAS_OFF;
        if (options.performAntialias())
            antialiasSetting = RenderingHints.VALUE_ANTIALIAS_ON;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antialiasSetting);

        g2.setColor(options.getBackgroundColor());
        //TODO: find out why the next line does not work
        g2.fillRect(0, 0, image.getWidth() + 10, image.getHeight() + 10);
		/*for(int y = 0; y < diagram.getHeight(); y ++)
			g2.drawLine(0, y, diagram.getWidth(), y);*/

        g2.setStroke(new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));

        ArrayList<DiagramShape> shapes = diagram.getAllDiagramShapes();

        if (DEBUG) System.out.println("Rendering " + shapes.size() + " shapes (groups flattened)");

        Iterator<DiagramShape> shapesIt;
        if (options.dropShadows()) {
            //render shadows
            shapesIt = shapes.iterator();
            while (shapesIt.hasNext()) {
                DiagramShape shape = shapesIt.next();

                if (shape.getPoints().isEmpty()) continue;

                //GeneralPath path = shape.makeIntoPath();
                GeneralPath path;
                path = shape.makeIntoRenderPath(diagram, options);

                float offset = diagram.getMinimumOfCellDimension() / 3.333f;

                if (path != null
                        && shape.dropsShadow()
                        && shape.getType() != DiagramShape.TYPE_CUSTOM) {
                    GeneralPath shadow = new GeneralPath(path);
                    AffineTransform translate = new AffineTransform();
                    translate.setToTranslation(offset, offset);
                    shadow.transform(translate);
                    g2.setColor(new Color(150, 150, 150));
                    g2.fill(shadow);

                }
            }


            //blur shadows

            if (true) {
                int blurRadius = 6;
                int blurRadius2 = blurRadius * blurRadius;
                float blurRadius2F = blurRadius2;
                float weight = 1.0f / blurRadius2F;
                float[] elements = new float[blurRadius2];
                for (int k = 0; k < blurRadius2; k++)
                    elements[k] = weight;
                Kernel myKernel = new Kernel(blurRadius, blurRadius, elements);

                //if EDGE_NO_OP is not selected, EDGE_ZERO_FILL is the default which creates a black border
                ConvolveOp simpleBlur =
                        new ConvolveOp(myKernel, ConvolveOp.EDGE_NO_OP, null);

                BufferedImage destination =
                        new BufferedImage(
                                image.getWidth(),
                                image.getHeight(),
                                image.getType());

                simpleBlur.filter(image, destination);

                //destination = destination.getSubimage(blurRadius/2, blurRadius/2, image.getWidth(), image.getHeight());
                g2 = (Graphics2D) destination.getGraphics();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antialiasSetting);
                renderedImage = destination;
            }
        }


        //fill and stroke

        float dashInterval = Math.min(diagram.getCellWidth(), diagram.getCellHeight()) / 2;
        //Stroke normalStroke = g2.getStroke();

        float strokeWeight = diagram.getMinimumOfCellDimension() / 10;

        normalStroke =
                new BasicStroke(
                        strokeWeight,
                        //10,
                        BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND
                );

        dashStroke =
                new BasicStroke(
                        strokeWeight,
                        BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_ROUND,
                        0,
                        new float[]{dashInterval},
                        0
                );

        //TODO: at this stage we should draw the open shapes first in order to make sure they are at the bottom (this is useful for the {mo} shape)


        //find storage shapes
        //render storage shapes
        //special case since they are '3d' and should be
        //rendered bottom to top
        //TODO: known bug: if a storage object is within a bigger normal box, it will be overwritten in the main drawing loop
        //(BUT this is not possible since tags are applied to all shapes overlaping shapes)


        //sort so that the largest shapes are rendered first
        shapes.sort(new ShapeAreaComparator());

        //render the rest of the shapes
        //ArrayList<DiagramShape> pointMarkers = new ArrayList<DiagramShape>();
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

        //handle storage shapes
        storageShapes.sort(new Shape3DOrderingComparator());
        g2.setStroke(normalStroke);
        for (DiagramShape shape : storageShapes) {
            GeneralPath path = shape.makeIntoRenderPath(diagram, options);

            if (!shape.isStrokeDashed()) {
                g2.setColor(shape.getFillColor() != null ? shape.getFillColor() : Color.white);
                g2.fill(path);
            }

            g2.setStroke(shape.isStrokeDashed() ? dashStroke : normalStroke);
            g2.setColor(shape.getStrokeColor());
            g2.draw(path);
        }

        //handle custom shapes

        g2.setStroke(normalStroke);
        for (DiagramShape shape : customShapes) renderCustomShape(shape, g2);

        //render shapes that are not storage shapes, pointmarkers or customshapes

        g2.setStroke(normalStroke);
        for (DiagramShape shape : otherShapes) {
            GeneralPath path = shape.makeIntoRenderPath(diagram, options);
            //fill
            if (path != null && shape.isClosed() && !shape.isStrokeDashed()) {
                g2.setColor(shape.getFillColor() != null ? shape.getFillColor() : Color.white);
                g2.fill(path);
            }

            //draw
            if (shape.getType() != DiagramShape.TYPE_ARROWHEAD) {
                g2.setColor(shape.getStrokeColor());
                g2.setStroke(shape.isStrokeDashed() ? dashStroke : normalStroke);
                g2.draw(path);
            }
        }

        //render point markers
        g2.setStroke(normalStroke);
        //we will transform this to use a functional approach.
        for (DiagramShape shape : pointMarkers) {
            GeneralPath path = shape.makeIntoRenderPath(diagram, options);
            g2.setColor(Color.white);
            g2.fill(path);
            g2.setColor(shape.getStrokeColor());
            g2.draw(path);
        }

        //handle text
        new TextCanvas(diagram.getTextObjects()).paint(g2);

        if (options.renderDebugLines() || DEBUG_LINES) {
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

        return renderedImage;
    }

    /**
     * @param textObjects
     * @param width
     * @param height
     * @return
     */
    private RenderedImage renderTextLayer(ArrayList<DiagramText> textObjects, int width, int height) {
        TextCanvas canvas = new TextCanvas(textObjects);
        Image image = canvas.createImage(width, height);
        Graphics g = image.getGraphics();
        canvas.paint(g);
        return (RenderedImage) image;
    }

    @Override
    public void renderImage(Diagram d) {
        RenderedImage image = renderToImage(d, getOptions());
        try {
            OutputStream stream = ("-".equals(super.getOutFile())) ? System.out : new PrintStream(new FileOutputStream(super.getOutFile()));
            ImageIO.write(image, "png", stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("Error: Cannot write to file " + getOutFile() + " -- skipping");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error: Cannot write to file " + getOutFile() + " -- skipping");
        }

    }

    /**
     * @param shape
     * @param g2
     */
    private void renderCustomShape(DiagramShape shape, Graphics2D g2) {
        CustomShapeDefinition definition = shape.getDefinition();

        Rectangle bounds = shape.getBounds();

        if (definition.hasBorder()) {
            g2.setColor(shape.getStrokeColor());
            if (shape.isStrokeDashed())
                g2.setStroke(dashStroke);
            else
                g2.setStroke(normalStroke);
            g2.drawLine(bounds.x, bounds.y, bounds.x + bounds.width, bounds.y);
            g2.drawLine(bounds.x + bounds.width, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height);
            g2.drawLine(bounds.x, bounds.y + bounds.height, bounds.x + bounds.width, bounds.y + bounds.height);
            g2.drawLine(bounds.x, bounds.y, bounds.x, bounds.y + bounds.height);

//			g2.drawRect(bounds.x, bounds.y, bounds.width, bounds.height); //looks different!
        }

        //TODO: custom shape distinction relies on filename extension. Make this more intelligent
        //NOTE: this precludes custom shapes on non-png bitmap outputs!
        if (definition.getFilename().endsWith(".png")) {
            renderCustomPNGShape(shape, g2);
        } else if (definition.getFilename().endsWith(".svg")) {
            renderCustomSVGShape(shape, g2);
        }
    }

    /**
     * Render shape in SVG format
     * @param shape
     * @param g2
     */
    private void renderCustomSVGShape(DiagramShape shape, Graphics2D g2) {
        CustomShapeDefinition definition = shape.getDefinition();
        Rectangle bounds = shape.getBounds();
        Image graphic;
        try {
            if (shape.getFillColor() == null) {
                graphic = ImageHandler.instance().renderSVG(
                        definition.getFilename(), bounds.width, bounds.height, definition.stretches());
            } else {
                graphic = ImageHandler.instance().renderSVG(
                        definition.getFilename(), bounds.width, bounds.height, definition.stretches(), IDREGEX, shape.getFillColor());
            }
            g2.drawImage(graphic, bounds.x, bounds.y, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Render shape in PNG format
     * @param shape
     * @param g2
     */
    private void renderCustomPNGShape(DiagramShape shape, Graphics2D g2) {
        CustomShapeDefinition definition = shape.getDefinition();
        Rectangle bounds = shape.getBounds();
        Image graphic = ImageHandler.instance().loadImage(definition.getFilename());

        int xPos, yPos, width, height;

        if (definition.stretches()) { //occupy all available space
            xPos = bounds.x;
            yPos = bounds.y;
            width = bounds.width;
            height = bounds.height;
        } else { //decide how to fit
            int newHeight = bounds.width * graphic.getHeight(null) / graphic.getWidth(null);
            if (newHeight < bounds.height) { //expand to fit width
                height = newHeight;
                width = bounds.width;
                xPos = bounds.x;
                yPos = bounds.y + bounds.height / 2 - graphic.getHeight(null) / 2;
            } else { //expand to fit height
                width = graphic.getWidth(null) * bounds.height / graphic.getHeight(null);
                height = bounds.height;
                xPos = bounds.x + bounds.width / 2 - graphic.getWidth(null) / 2;
                yPos = bounds.y;
            }
        }

        g2.drawImage(graphic, xPos, yPos, width, height, null);
    }

    private class TextCanvas extends Canvas {
        ArrayList<DiagramText> textObjects;

        public TextCanvas(ArrayList<DiagramText> textObjects) {
            this.textObjects = textObjects;
        }

        public void paint(Graphics g) {
            Graphics g2 = g;
            Iterator<DiagramText> textIt = textObjects.iterator();
            while (textIt.hasNext()) {
                DiagramText text = textIt.next();
                g2.setFont(text.getFont());
                if (text.hasOutline()) {
                    g2.setColor(text.getOutlineColor());
                    g2.drawString(text.getText(), text.getXPos() + 1, text.getYPos());
                    g2.drawString(text.getText(), text.getXPos() - 1, text.getYPos());
                    g2.drawString(text.getText(), text.getXPos(), text.getYPos() + 1);
                    g2.drawString(text.getText(), text.getXPos(), text.getYPos() - 1);
                }
                g2.setColor(text.getColor());
                g2.drawString(text.getText(), text.getXPos(), text.getYPos());
            }
        }
    }
}

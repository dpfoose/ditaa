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
public class BitmapRenderer extends ImageRenderer {


    private static final String IDREGEX = "^.+_vfill$";

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
    private RenderedImage render(Diagram diagram, BufferedImage image, RenderingOptions options) {
        RenderedImage renderedImage = image;
        Graphics2D g2 = image.createGraphics();

        Object antialiasSetting = (options.performAntialias() ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antialiasSetting);

        g2.setColor(options.getBackgroundColor());
        g2.fillRect(0, 0, image.getWidth() + 10, image.getHeight() + 10);

        g2.setStroke(new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));

        if (DEBUG) System.out.println("Rendering " + diagram.getAllDiagramShapes().size() + " shapes (groups flattened)");

        if (options.dropShadows()) {
            //render shadows
            renderShadows(diagram, g2);

            //blur shadows
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

        renderObjects(diagram, g2);
        g2.dispose();

        return renderedImage;
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
    @Override
    protected void renderCustomShape(DiagramShape shape, Graphics2D g2) {
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

    @Override
    protected void renderText(List<DiagramText> textObjects, Graphics2D g2){
        for (DiagramText text: textObjects) {
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

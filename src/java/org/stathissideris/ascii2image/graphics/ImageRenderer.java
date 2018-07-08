package org.stathissideris.ascii2image.graphics;

import org.stathissideris.ascii2image.core.RenderingOptions;
import org.stathissideris.ascii2image.core.Shape3DOrderingComparator;
import org.stathissideris.ascii2image.core.ShapeAreaComparator;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ImageRenderer extends AbstractRenderer{

    public ImageRenderer(String toFilename, RenderingOptions renderingOptions) {
        super(toFilename, renderingOptions);
    }

    protected Stroke normalStroke;
    protected Stroke dashStroke;


    protected static final boolean DEBUG = false;
    protected static final boolean DEBUG_LINES = false;

    protected abstract void renderText(List<DiagramText> textObjects, Graphics2D g2);
    protected void renderShadows(Diagram diagram, Graphics2D g2) {
        for (DiagramShape shape: diagram.getAllDiagramShapes()) {
            if (shape.getPoints().isEmpty()) continue;
            GeneralPath path = shape.makeIntoRenderPath(diagram, options);
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
    }

    protected void renderObjects(Diagram diagram, Graphics2D g2){
        ArrayList<DiagramShape> shapes = diagram.getAllDiagramShapes();
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

        shapes.sort(new ShapeAreaComparator()); //renders largest shapes first

        //filter shapes by type
        //storage shapes are special since they are '3d' and should be rendered bottom to top.
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
        for (DiagramShape shape: customShapes) renderCustomShape(shape, g2);

        //render the rest of the shapes
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
        renderText(diagram.getTextObjects(), g2);

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
    }

    abstract protected void renderCustomShape(DiagramShape shape, Graphics2D g2);
}

package org.stathissideris.ascii2image.graphics;

import org.stathissideris.ascii2image.core.RenderingOptions;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * Created by Jean Lazarou.
 */
public class SVGRenderer extends AbstractRenderer {

    public SVGRenderer(String toFilename, RenderingOptions options) {
        super(toFilename, options);
    }

    /**
     * Construct SVG string from <code>diagram</code>
     *
     * @param diagram
     * @return
     */
    @Override
    public void renderImage(Diagram diagram) {
        SVGBuilder builder = new SVGBuilder(diagram, getOptions());
        PrintStream stream = null;
        try {
            stream = ("-".equals(super.getOutFile())) ? System.out : new PrintStream(new FileOutputStream(super.getOutFile()));
            String content = builder.build();
            stream.print(content);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

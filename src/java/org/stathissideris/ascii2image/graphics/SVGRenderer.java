package org.stathissideris.ascii2image.graphics;

import org.stathissideris.ascii2image.core.RenderingOptions;

/**
 * Created by Jean Lazarou.
 */
public class SVGRenderer {

    /**
     * Construct SVG string from <code>diagram</code>
     * @param diagram
     * @param options
     * @return
     */
    public String renderToImage(Diagram diagram, RenderingOptions options) {

        SVGBuilder builder = new SVGBuilder(diagram, options);

        return builder.build();

    }

}

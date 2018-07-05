package org.stathissideris.ascii2image.graphics;

import org.stathissideris.ascii2image.core.RenderingOptions;

public abstract class AbstractRenderer {
    String outFile;
    RenderingOptions options;

    public RenderingOptions getOptions() {
        return options;
    }

    public AbstractRenderer(String file, RenderingOptions options) {
        this.outFile = file;
        this.options = options;
    }

    public String getOutFile() {
        return outFile;
    }


    abstract public void renderImage(Diagram d);
}

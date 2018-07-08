package org.stathissideris.ascii2image.graphics;

import org.stathissideris.ascii2image.core.RenderingOptions;

import java.awt.*;

public abstract class AbstractRenderer {
    private static final boolean DEBUG = false;
    String outFile;
    RenderingOptions options;

    public AbstractRenderer(String file, RenderingOptions options) {
        this.outFile = file;
        this.options = options;
    }

    public static boolean isColorDark(Color color) {
        int brightness = Math.max(color.getRed(), color.getGreen());
        brightness = Math.max(color.getBlue(), brightness);
        if (brightness < 200) {
            if (DEBUG) System.out.println("Color " + color + " is dark");
            return true;
        }
        if (DEBUG) System.out.println("Color " + color + " is not dark");
        return false;
    }

    public RenderingOptions getOptions() {
        return options;
    }

    public String getOutFile() {
        return outFile;
    }

    abstract public void renderImage(Diagram d);
    private void render() {

    }
}

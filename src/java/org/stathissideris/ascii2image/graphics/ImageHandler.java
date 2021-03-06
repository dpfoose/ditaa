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

import org.stathissideris.ascii2image.core.FileUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

public class ImageHandler {

    private static final MediaTracker tracker = new MediaTracker(new JLabel());
    private static OffScreenSVGRenderer svgRenderer =
            new OffScreenSVGRenderer();
    private static ImageHandler instance = new ImageHandler();

    /**
     * @return static instance of the class for rendering
     */
    public static ImageHandler instance() {
        return instance;
    }

    public static void main(String[] args) throws IOException {

        OffScreenSVGRenderer renderer = new OffScreenSVGRenderer();

        //BufferedImage image = instance.renderSVG("sphere.svg", 200, 200, false);

        //BufferedImage image = renderer.renderToImage("file:///Users/sideris/Documents/workspace/ditaa/joystick.svg", FileUtils.readFile(new File("joystick.svg")), 400, 200, false);
//		BufferedImage image = renderer.renderToImage(
//			null, FileUtils.readFile(new File("sphere.svg")).replaceFirst("#187637", "#3333FF"), 200, 200, false);

        String content = FileUtils.readFile(new File("sphere.svg")).replaceAll("#187637", "#1133FF");

        System.out.println(content);

//		BufferedImage image = renderer.renderToImage(
//				"file:/K:/devel/ditaa/sphere.svg", content, 200, 200, false);

        BufferedImage image = renderer.renderXMLToImage(content, 200, 200, false, null, null);


        try {
            File file = new File("testing.png");
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            //e.printStackTrace();
            System.err.println("Error: Cannot write to file");
        }

    }

    /**
     * BufferedImage from File object
     * @param file
     * @return
     * @throws IOException
     */
    public BufferedImage loadBufferedImage(File file) throws IOException {
        return ImageIO.read(file);
    }

    /**
     * Construct Image from filename. The <code>filename</code> can be URL or file resource
     * @param filename
     * @return
     */
    public Image loadImage(String filename) {
        URL url = ClassLoader.getSystemResource(filename);
        Image result = null;
        if (url != null)
            result = Toolkit.getDefaultToolkit().getImage(url);
        else
            result = Toolkit.getDefaultToolkit().getImage(filename);
//			result = null;

        //wait for the image to load before returning
        tracker.addImage(result, 0);
        try {
            tracker.waitForID(0);
        } catch (InterruptedException e) {
            System.err.println("Failed to load image " + filename);
            e.printStackTrace();
        }
        tracker.removeImage(result, 0);

        return result;
    }

    /**
     * Render SVG image for a shape with no color fill
     * @param filename
     * @param width
     * @param height
     * @param stretch
     * @return
     * @throws IOException
     */
    public BufferedImage renderSVG(String filename, int width, int height, boolean stretch) throws IOException {
        File file = new File(filename);
        URI uri = file.toURI();
        return svgRenderer.renderToImage(uri.toString(), width, height, stretch, null, null);
    }

    /**
     * Render SVG image for a shape with color fill
     * @param filename
     * @param width
     * @param height
     * @param stretch
     * @param idRegex
     * @param color
     * @return
     * @throws IOException
     */
    public BufferedImage renderSVG(String filename, int width, int height, boolean stretch, String idRegex, Color color) throws IOException {
        File file = new File(filename);
        URI uri = file.toURI();
        return svgRenderer.renderToImage(uri.toString(), width, height, stretch, idRegex, color);
    }
}

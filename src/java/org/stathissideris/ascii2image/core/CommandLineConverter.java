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
package org.stathissideris.ascii2image.core;

import org.apache.commons.cli.*;
import org.apache.commons.io.FilenameUtils;
import org.stathissideris.ascii2image.graphics.BitmapRenderer;
import org.stathissideris.ascii2image.graphics.Diagram;
import org.stathissideris.ascii2image.graphics.SVGRenderer;
import org.stathissideris.ascii2image.text.TextGrid;
import se.ngm.ditaaeps.EpsRenderer;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * This is commandline converter using String notice and String methods
 */
public class CommandLineConverter {
    private static String notice = "ditaa version 0.11, Copyright (C) 2004--2017  Efstathios (Stathis) Sideris";

    private static String[] markupModeAllowedValues = {"use", "ignore", "render"};

    /**
     * The main function for the package. Parses command line options.
     * */
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        Options cmdLnOptions = new Options();
        cmdLnOptions.addOption(
                Option.builder().longOpt("help")
                        .desc("Prints usage help.")
                        .build()
        );

        cmdLnOptions.addOption("v", "verbose", false, "Makes ditaa more verbose.");
        cmdLnOptions.addOption("o", "overwrite", false, "If the filename of the destination image already exists, an alternative name is chosen. If the overwrite option is selected, the image file is instead overwriten.");
        cmdLnOptions.addOption("S", "no-shadows", false, "Turns off the drop-shadow effect.");
        cmdLnOptions.addOption("A", "no-antialias", false, "Turns anti-aliasing off.");
        cmdLnOptions.addOption("W", "fixed-slope", false, "Makes sides of parallelograms and trapezoids fixed slope instead of fixed width.");
        cmdLnOptions.addOption("d", "debug", false, "Renders the debug grid over the resulting image.");
        cmdLnOptions.addOption("r", "round-corners", false, "Causes all corners to be rendered as round corners.");
        cmdLnOptions.addOption("E", "no-separation", false, "Prevents the separation of common edges of shapes.");
        cmdLnOptions.addOption("h", "html", false, "In this case the input is an HTML file. The contents of the <pre class=\"textdiagram\"> tags are rendered as diagrams and saved in the images directory and a new HTML file is produced with the appropriate <img> tags.");
        cmdLnOptions.addOption("T", "transparent", false, "Causes the diagram to be rendered on a transparent background. Overrides --background.");

        cmdLnOptions.addOption(
                Option.builder("e").longOpt("encoding")
                        .desc("The encoding of the input file.")
                        .hasArg()
                        .argName("ENCODING")
                        .build()
        );

        cmdLnOptions.addOption(
                Option.builder("s").longOpt("scale")
                        .desc("A natural number that determines the size of the rendered image. The units are fractions of the default size (2.5 renders 1.5 times bigger than the default).")
                        .hasArg()
                        .argName("SCALE")
                        .build()
        );

        cmdLnOptions.addOption(
                Option.builder("t").longOpt("tabs")
                        .desc("Tabs are normally interpreted as 8 spaces but it is possible to change that using this option. It is not advisable to use tabs in your diagrams.")
                        .hasArg()
                        .argName("TABS")
                        .build()
        );

        cmdLnOptions.addOption(
                Option.builder("b").longOpt("background")
                        .desc("The background colour of the image. The format should be a six-digit hexadecimal number (as in HTML, FF0000 for red). Pass an eight-digit hex to define transparency. This is overridden by --transparent.")
                        .hasArg()
                        .argName("BACKGROUND")
                        .build()
        );

        cmdLnOptions.addOption(
                Option.builder().longOpt("svg")
                        .desc("Write an SVG image as destination file.")
                        .build()
        );

        cmdLnOptions.addOption(
                Option.builder().longOpt("eps")
                        .desc("Write an EPS image as destination file.")
                        .build()
        );
        cmdLnOptions.addOption(
                Option.builder().longOpt("svg-font-url")
                        .desc("SVG font URL.")
                        .hasArg()
                        .argName("FONT")
                        .build()
        );

//TODO: uncomment this for next version:
//		cmdLnOptions.addOption(
//				OptionBuilder.withLongOpt("config")
//				.withDescription( "The shape configuration file." )
//				.hasArg()
//				.withArgName("CONFIG_FILE")
//				.create('c') );

        CommandLine cmdLine = null;


        ///// parse command line options
        try {
            // parse the command line arguments
            CommandLineParser parser = new PosixParser();

            cmdLine = parser.parse(cmdLnOptions, args);

            // validate that block-size has been set
            if (cmdLine.hasOption("block-size")) {
                // print the value of block-size
                System.out.println(cmdLine.getOptionValue("block-size"));
            }

        } catch (org.apache.commons.cli.ParseException e) {
            System.err.println(e.getMessage());
            new HelpFormatter().printHelp("java -jar ditaa.jar <INPFILE> [OUTFILE]", cmdLnOptions, true);
            System.exit(2);
        }


        if (cmdLine.hasOption("help") || args.length == 0) {
            new HelpFormatter().printHelp("java -jar ditaa.jar <INPFILE> [OUTFILE]", cmdLnOptions, true);
            System.exit(0);
        }

        if (cmdLine.hasOption("eps") && cmdLine.hasOption("svg")) {
            System.err.println("Invalid options --svg and --eps. Must select only one file format");
            System.exit(1);
        }

        ConversionOptions options = null;
        try {
            options = new ConversionOptions(cmdLine);
        } catch (UnsupportedEncodingException e2) {
            System.err.println("Error: " + e2.getMessage());
            System.exit(2);
        } catch (IllegalArgumentException e2) {
            System.err.println("Error: " + e2.getMessage());
            new HelpFormatter().printHelp("java -jar ditaa.jar <INPFILE> [OUTFILE]", cmdLnOptions, true);
            System.exit(2);
        }

        args = cmdLine.getArgs();

        if (args.length == 0) {
            System.err.println("Error: Please provide the input file filename");
            new HelpFormatter().printHelp("java -jar ditaa.jar <inpfile> [outfile]", cmdLnOptions, true);
            System.exit(2);
        }

        if (cmdLine.hasOption("html")) {
            /////// print options before running
            printRunInfo(cmdLine);
            String filename = args[0];

            boolean overwrite = false;
            if (options.processingOptions.overwriteFiles()) overwrite = true;

            String toFilename;
            if (args.length == 1) {
                toFilename = FileUtils.makeTargetPathname(filename, "html", "_processed", true);
            } else {
                toFilename = args[1];
            }
            File target = new File(toFilename);
            if (!overwrite && target.exists()) {
                System.out.println("Error: File " + toFilename + " exists. If you would like to overwrite it, please use the --overwrite option.");
                System.exit(0);
            }

            new HTMLConverter().convertHTMLFile(filename, toFilename, "ditaa_diagram", "images", options);
            System.exit(0);

        } else { //simple mode

            TextGrid grid = new TextGrid();
            if (options.processingOptions.getCustomShapes() != null) {
                grid.addToMarkupTags(options.processingOptions.getCustomShapes().keySet());
            }

            // "-" means stdin / stdout
            String fromFilename = args[0];
            boolean stdIn = "-".equals(fromFilename);

            String toFilename;
            boolean stdOut;

            boolean overwrite = false;
            if (options.processingOptions.overwriteFiles()) overwrite = true;

            if (args.length == 1) {
                if (stdIn) { // if using stdin and no output specified, use stdout
                    stdOut = true;
                    toFilename = "-";
                } else {
                    String ext = cmdLine.hasOption("svg") ? "svg" : "png";
                    toFilename = FileUtils.makeTargetPathname(fromFilename, ext, overwrite);
                    stdOut = false;
                }
            } else {
                toFilename = args[1];
                stdOut = "-".equals(toFilename);
            }

            if (!stdOut) {
                /////// print options before running
                printRunInfo(cmdLine);
                System.out.println("Reading " + (stdIn ? "standard input" : "file: " + fromFilename));
            }

            try {
                if (!grid.loadFrom(fromFilename, options.processingOptions)) {
                    System.err.println("Cannot open file " + fromFilename + " for reading");
                }
            } catch (UnsupportedEncodingException e1) {
                System.err.println("Error: " + e1.getMessage());
                System.exit(1);
            } catch (FileNotFoundException e1) {
                System.err.println("Error: File " + fromFilename + " does not exist");
                System.exit(1);
            } catch (IOException e1) {
                System.err.println("Error: Cannot open file " + fromFilename + " for reading");
                System.exit(1);
            }

            if (options.processingOptions.printDebugOutput()) {
                if (!stdOut) System.out.println("Using grid:");
                grid.printDebug();
            }

            Diagram diagram = new Diagram(grid, options);
            if (!stdOut) System.out.println("Rendering to file: " + toFilename);

            //Begin new file format inferrence
            String fileFormat = "";
            //SVG and EPS flags cannot be defined together
            //intention is to deprecate this and prefer inferrence from extension
            //but must retain this for compatibility
            if (cmdLine.hasOption("svg")) {
                fileFormat = "svg";
            } else if (cmdLine.hasOption("eps")) {
                fileFormat = "eps";
            } else {
                String fileExtension = FilenameUtils.getExtension(toFilename);
                //check if the file extension is in the list of supported output formats
                boolean extensionIsValid = fileExtension.equals("svg")
                        || fileExtension.equals("eps")
                        || (fileExtension.length() > 0 && ImageIO.getImageWritersBySuffix(fileExtension).hasNext());
                fileFormat = extensionIsValid ? fileExtension : "png";
            }
            switch (fileFormat) {
                case "svg":
                    SVGRenderer svgRenderer = new SVGRenderer(toFilename, options.renderingOptions);
                    svgRenderer.renderImage(diagram);
                    break;
                case "eps":
                    EpsRenderer epsRenderer = new EpsRenderer(toFilename, options.renderingOptions);
                    epsRenderer.renderImage(diagram);
                    break;
                default:
                    BitmapRenderer bitmapRenderer = new BitmapRenderer(toFilename, options.renderingOptions);
                    bitmapRenderer.renderImage(diagram);
            }

            //BitmapRenderer.renderToPNG(diagram, toFilename, options.renderingOptions);

            long endTime = System.currentTimeMillis();
            long totalTime = (endTime - startTime) / 1000;
            if (!stdOut) System.out.println("Done in " + totalTime + "sec");

//			try {
//			Thread.sleep(Long.MAX_VALUE);
//			} catch (InterruptedException e) {
//			e.printStackTrace();
//			}

        }
    }

    /**
     * This is printRunInfo using commandLINE AND cmdLine
     * @param cmdLine
     */
    private static void printRunInfo(CommandLine cmdLine) {
        System.out.println("\n" + notice + "\n");

        System.out.println("Running with options:");
        Option[] opts = cmdLine.getOptions();
        for (Option option : opts) {
            if (option.hasArgs()) {
                for (String value : option.getValues()) {
                    System.out.println(option.getLongOpt() + " = " + value);
                }
            } else if (option.hasArg()) {
                System.out.println(option.getLongOpt() + " = " + option.getValue());
            } else {
                System.out.println(option.getLongOpt());
            }
        }
    }
}

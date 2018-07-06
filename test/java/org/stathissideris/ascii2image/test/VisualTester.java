/**
 * ditaa - Diagrams Through Ascii Art
 * 
 * Copyright (C) 2004-2011 Efstathios Sideris
 *
 * ditaa is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * ditaa is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with ditaa.  If not, see <http://www.gnu.org/licenses/>.
 *   
 */
package org.stathissideris.ascii2image.test;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;
import org.stathissideris.ascii2image.core.ConversionOptions;
import org.stathissideris.ascii2image.graphics.BitmapRenderer;
import org.stathissideris.ascii2image.graphics.Diagram;
import org.stathissideris.ascii2image.graphics.ImageHandler;
import org.stathissideris.ascii2image.graphics.SVGRenderer;
import org.stathissideris.ascii2image.text.TextGrid;

/**
 * If ran as a Java application, it produces an HTML report for manual 
 * inspection. If ran as a junit test it runs a pixel-by-pixel
 * comparison between the images in the "images-expected" and the
 * images generated by the test.
 * 
 * @author Efstathios Sideris
 */
@RunWith(Parameterized.class)
public class VisualTester {

	private static final String HTMLReportName = "test_suite";
	private static final String expectedDir = "test-resources/images-expected";
	private static final String actualDir = "test-resources/images";
	
	private File textFile;
	private int index;
	
	public static void main(String[] args){
		generate();
	}

	public static void generate() {
		String reportDir = "test-resources/images";
		VisualTester.createHTMLTestReport(getFilesToRender(), reportDir, HTMLReportName);
		System.out.println("Tests completed");
	}

	@Test
	public void compareImages() throws IOException {
		ConversionOptions options = new ConversionOptions();
		File actualFile = new File(actualDir + File.separator + textFile.getName() + ".png");
		File expectedFile = new File(expectedDir + File.separator + textFile.getName() + ".png");

		System.out.println(index + ") Rendering "+textFile+" to "+actualFile);
		
		if(!expectedFile.exists()){
			System.out.println("Skipping " + textFile + " -- reference image does not exist");
			throw new FileNotFoundException("Reference image "+expectedFile+" does not exist");
		}
		
		TextGrid grid = new TextGrid();
		grid.loadFrom(textFile.toString());
		Diagram diagram = new Diagram(grid, options);

		new BitmapRenderer(actualFile.getAbsolutePath(), options.renderingOptions).renderImage(diagram);
	
		//compare images pixel-by-pixel
		BufferedImage actualImage = ImageHandler.instance().loadBufferedImage(actualFile);
		BufferedImage expectedImage = ImageHandler.instance().loadBufferedImage(expectedFile);
		
		assertTrue("Images are not the same size", actualImage.getWidth() == expectedImage.getWidth()
				&& actualImage.getHeight() == expectedImage.getHeight());

		boolean pixelsEqual = true;
		int x = 0;
		int y = 0;
		
		OUTER:
		for(y = 0; y < expectedImage.getHeight(); y++) {
			for(x = 0; x < expectedImage.getWidth(); x++) {
				int expectedPixel = expectedImage.getRGB(x, y);
				int actualPixel = actualImage.getRGB(x, y);
				if(actualPixel != expectedPixel) {
					pixelsEqual = false;
					break OUTER;
				}
			}
		}
		
		assertTrue("Images for "+textFile.getName()+" are not pixel-identical, first different pixel at: "+x+","+y, pixelsEqual);
	}
	
	public VisualTester(File textFile, int index) {
		this.textFile = textFile;
		this.index = index;
	}
	
	@Parameters
	public static Collection getTestParameters() {
		List<File> filesToRender = getFilesToRender();
		Object[] params = new Object[filesToRender.size()];
		
		int i = 0;
		for(File file : filesToRender) {
			params[i] = new Object[]{ file, i };
			i++;
		}
		
		return Arrays.asList(params);
	}
	
	public static List<File> getFilesToRender() {
		String textDir = "test-resources/text";
		
		File textDirObj = new File(textDir);
		ArrayList<File> textFiles
			= new ArrayList<File>(Arrays.asList(textDirObj.listFiles()));
	
		Set<String> excludedFiles = new HashSet<String>();
		excludedFiles.addAll( Arrays.asList("dak_orgstruktur_vs_be.ditaa.OutOfMemoryError.txt",
				"dak_orgstruktur_vs_be.ditaa.OutOfMemoryError.2.txt",
				"dak_orgstruktur_vs_be.ditaa.OutOfMemoryError.3.txt",
				"dak_orgstruktur_vs_be.ditaa.OutOfMemoryError.4.txt",
				"dak_orgstruktur_vs_be.ditaa.OutOfMemoryError.edit.txt",
				"dak_orgstruktur_vs_be.ditaa.txt"));
		
		Iterator<File> it = textFiles.iterator();
		while(it.hasNext()){
			String filename = it.next().toString();
			if(!filename.matches(".+\\.txt$") || isInExcluded(filename, excludedFiles)){
				it.remove();
			}
		}

		return textFiles;
	}
	
	private static boolean isInExcluded(String filename, Set<String> excludedSet) {
		for(String excluded : excludedSet) {
			if(filename.endsWith(excluded)) return true;
		}
		return false;
	}
	
	public static void generateImages(List<File> textFiles, String destinationDir) {
		
		ConversionOptions options = new ConversionOptions();
		
		for(File textFile : textFiles) {
			TextGrid grid = new TextGrid();

			File toFile = new File(destinationDir + File.separator + textFile.getName() + ".png");
			
			
			long a = java.lang.System.nanoTime();
			long b;
			try {
				System.out.println("Rendering "+textFile+" to "+toFile);
				
				grid.loadFrom(textFile.toString());
				Diagram diagram = new Diagram(grid, options);

				new BitmapRenderer(toFile.getAbsolutePath(), options.renderingOptions).renderImage(diagram);
				
				b = java.lang.System.nanoTime();
		        java.lang.System.out.println( "Done in " + Math.round((b - a)/10e6) + "msec");
			} catch (Exception e) {
				System.err.println("!!! Failed to render: "+textFile+" !!!\n");
				System.err.println(grid.getDebugString()+"\n");
				e.printStackTrace(System.err);
				
				continue;
			}			
		}

	}
	
	
	public static boolean createHTMLTestReport(List<File> textFiles, String reportDir, String reportName){

		ConversionOptions options = new ConversionOptions();

		String reportFilename = reportDir+"/"+reportName+".html";

		if(!(new File(reportDir).exists())){
			File dir = new File(reportDir);
			dir.mkdir();
		}

		PrintWriter s = null;
		try {
			s = new PrintWriter(new FileWriter(reportFilename));
		} catch (IOException e) {
			System.err.println("Cannot open file "+reportFilename+" for writing:");
			e.printStackTrace();
			return false;
		}

		s.println("<html><body>");
		s.println("<h1>ditaa test suite</h1>");
		s.println("<h2>generated on: "+Calendar.getInstance().getTime()+"</h2>");


		for(File textFile : textFiles) {
			TextGrid grid = new TextGrid();

			File toFilePng = new File(reportDir + File.separator + textFile.getName() + ".png");
			File toFileSvg = new File(reportDir + File.separator + textFile.getName() + ".svg");

			long a = java.lang.System.nanoTime();
			long b;
			try {
				System.out.println("Rendering " + textFile + " to " + toFilePng);
				
				grid.loadFrom(textFile.toString());
				Diagram diagram = new Diagram(grid, options);

				new BitmapRenderer(toFilePng.getAbsolutePath(), options.renderingOptions).renderImage(diagram);

				b = java.lang.System.nanoTime();
		        java.lang.System.out.println( "Done in " + Math.round((b - a)/10e6) + "msec");


				//SVG
				System.out.println("Rendering " + textFile + " to " + toFileSvg);
				try {
					new SVGRenderer(toFileSvg.getAbsolutePath(), options.renderingOptions).renderImage(diagram);
				} catch (Exception e) {
					System.err.println("Error: Cannot write to file " + toFileSvg);
					System.exit(1);
				}


			} catch (Exception e) {
				s.println("<b>!!! Failed to render: "+textFile+" !!!</b>");
				s.println("<pre>\n"+grid.getDebugString()+"\n</pre>");
				s.println(e.getMessage());
				s.println("<hr />");
				s.flush();
				
				System.err.println("!!! Failed to render: "+textFile+" !!!");
				e.printStackTrace(System.err);
				
				continue;
			}
			
			s.println(makeReportTable(textFile.getName(), grid, toFilePng.getName(), toFileSvg.getName(), b - a));
			s.println("<hr />");
			s.flush();
		}
		
		s.println("</body></html>");

		s.flush();
		s.close();
		
		
		System.out.println("Wrote HTML report to " + new File(reportFilename).getAbsolutePath());
		
		return true;

	}

	private static String makeReportTable(String gridURI, TextGrid grid, String imagePngURI, String imageSvgURI, long time){
		StringBuffer buffer = new StringBuffer("<center><table border=\"0\">");
		buffer.append("<th colspan=\"3\"><h3>"+gridURI+" ("+Math.round(time/10e6)+"msec)</h3></th>");
		buffer.append("<tr><td><pre>\n"+grid.getDebugString()+"\n</pre></td>");
		buffer.append("<td><img border=\"0\" src=\""+imagePngURI+"\"</td>");
		buffer.append("<td><img border=\"0\" src=\""+imageSvgURI+"\"</td></tr>");
		buffer.append("</table></center>");
		return buffer.toString();
	}

}

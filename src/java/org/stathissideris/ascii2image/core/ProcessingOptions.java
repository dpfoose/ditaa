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
package org.stathissideris.ascii2image.core;

import java.util.HashMap;

import org.stathissideris.ascii2image.graphics.CustomShapeDefinition;

/**
 * @author Usha Lokala
 *
 */

/**
 * This is class ProcessingOptions
 */
public class ProcessingOptions {

	private HashMap<String, CustomShapeDefinition> customShapes = new HashMap<String, CustomShapeDefinition>();
	
	private boolean beVerbose = false;
	private boolean printDebugOutput = false;
	private boolean overwriteFiles = false;
	private boolean performSeparationOfCommonEdges = true;
	private boolean allCornersAreRound = false;

	public static final int USE_TAGS = 0;
	public static final int RENDER_TAGS = 1;
	public static final int IGNORE_TAGS = 2;
	private int tagProcessingMode = USE_TAGS;

	public static final int USE_COLOR_CODES = 0;
	public static final int RENDER_COLOR_CODES = 1;
	public static final int IGNORE_COLOR_CODES = 2;
	private int colorCodesProcessingMode = USE_COLOR_CODES;

	public static final int FORMAT_JPEG = 0;
	public static final int FORMAT_PNG = 1;
	public static final int FORMAT_GIF = 2;
	private int exportFormat = FORMAT_PNG;

	public static final int DEFAULT_TAB_SIZE = 8;
	private int tabSize = DEFAULT_TAB_SIZE;

	private String inputFilename;
	private String outputFilename;
	
	private String characterEncoding = null;
	
	/** This is areAllCornersRound method
	 * @return allCornersAreRound;
	 */
	public boolean areAllCornersRound() {
		return allCornersAreRound;
	}

	/** This is getColorCodesProcessingMode(
	 * @return colorCodesProcessingMode;
	 */
	public int getColorCodesProcessingMode() {
		return colorCodesProcessingMode;
	}

	/** This is getExportFormat
	 * @return exportFormat;
	 */
	public int getExportFormat() {
		return exportFormat;
	}

	/** This is performSeparationOfCommonEdges
	 * @return performSeparationOfCommonEdges;
	 */
	public boolean performSeparationOfCommonEdges() {
		return performSeparationOfCommonEdges;
	}

    /** This is getTagProcessingMode method
	 * @return tagProcessingMode;
	 */
	public int getTagProcessingMode() {
		return tagProcessingMode;
	}

	/** This is setAllCornersAreRound(
	 * @param b
	 */
	public void setAllCornersAreRound(boolean b) {
		allCornersAreRound = b;
	}

	/** This is setColorCodesProcessingMode(
	 * @param i
	 */
	public void setColorCodesProcessingMode(int i) {
		colorCodesProcessingMode = i;
	}

	/**This is setExportFormat(
	 * @param i
	 */
	public void setExportFormat(int i) {
		exportFormat = i;
	}

	/**This is setPerformSeparationOfCommonEdges(
	 * @param b
	 */
	public void setPerformSeparationOfCommonEdges(boolean b) {
		performSeparationOfCommonEdges = b;
	}

    /**This is setTagProcessingMode
	 * @param i
	 */
	public void setTagProcessingMode(int i) {
		tagProcessingMode = i;
	}

	/**This is getInputFilename
	 * @return inputFilename;
	 */
	public String getInputFilename() {
		return inputFilename;
	}

	/**This is getOutputFilename method
	 * @return outputFilename;
	 */
	public String getOutputFilename() {
		return outputFilename;
	}

	/**This is setInputFilename(
	 * @param string
	 */
	public void setInputFilename(String string) {
		inputFilename = string;
	}

	/**
	 * This is setOutputFilename method
	 * @param string
	 */
	public void setOutputFilename(String string) {
		outputFilename = string;
	}

	/**
	 * This is verbose method and returns boolean
	 * @return beVerbose;
	 */
	public boolean verbose() {
		return beVerbose;
	}

	/**
	 * This is printDebugOutput and returns type boolean
	 * @return printDebugOutput;
	 */
	public boolean printDebugOutput() {
		return printDebugOutput;
	}

	/**
	 * This is setVerbose method
	 * @param b
	 */
	public void setVerbose(boolean b) {
		beVerbose = b;
	}

	/**
	 * This is setPrintDebugOutput method
	 * @param b
	 */
	public void setPrintDebugOutput(boolean b) {
		printDebugOutput = b;
	}

	/**
	 * This is overwriteFiles method
	 * @return overwriteFiles;
	 */
	public boolean overwriteFiles() {
		return overwriteFiles;
	}

	/**
	 * This is setOverwriteFiles method
	 * @param b
	 */
	public void setOverwriteFiles(boolean b) {
		overwriteFiles = b;
	}

	/**
	 * This is getTabSize method
	 * @return  tabSize;
	 */
	public int getTabSize() {
		return tabSize;
	}

	/**
	 * This is setTabSize method
	 * @param i
	 */
	public void setTabSize(int i) {
		tabSize = i;
	}

	/**
	 * This is getCharacterEncoding method
	 * @return  characterEncoding;
	 */
	public String getCharacterEncoding() {
		return characterEncoding;
	}

	/**
	 * This is setCharacterEncoding method
	 * @param characterEncoding
	 */
	public void setCharacterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
	}

	/**
	 * This is HashMap method
	 * @return hashmap customShapes;
	 */
	public HashMap<String, CustomShapeDefinition> getCustomShapes() {
		return customShapes;
	}

	/**
	 * This is setCustomShapes method
	 * @param customShapes
	 */
	public void setCustomShapes(HashMap<String, CustomShapeDefinition> customShapes) {
		this.customShapes = customShapes;
	}

	/**
	 * This is putAllInCustomShapes
	 * @param customShapes
	 */
	public void putAllInCustomShapes(HashMap<String, CustomShapeDefinition> customShapes) {
		this.customShapes.putAll(customShapes);
	}

	/**
	 * This is getFromCustomShapes
	 * @param tagName
	 * @return type CustomShapeDefinition
	 */
	public CustomShapeDefinition getFromCustomShapes(String tagName){
		return customShapes.get(tagName);
	}
	
	

}

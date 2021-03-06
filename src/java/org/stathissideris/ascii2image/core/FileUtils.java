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

import java.io.*;

/**
 *
 * @author Usha Lokala
 */

/**
 * This class is FileUtils and has file utilities methods
 */
public class FileUtils {

    //private static final

    /**
     * This is makeTargetPathname and it is static method
     * @param sourcePathname
     * @param extension
     * @param overwrite
     * @return
     */
    public static String makeTargetPathname(String sourcePathname, String extension, boolean overwrite) {
        return makeTargetPathname(sourcePathname, extension, "", overwrite);
    }

    /**
     * This is static makeTargetPathname method
     * @param sourcePathname
     * @param extension
     * @param postfix
     * @param overwrite
     * @return
     */
    public static String makeTargetPathname(String sourcePathname, String extension, String postfix, boolean overwrite) {
        File sourceFile =
                new File(sourcePathname);

        String path = "";
        if (sourceFile.getParentFile() != null) {
            path = sourceFile.getParentFile().getAbsolutePath();
            if (!path.endsWith(File.separator)) path += File.separator;
        }
        String baseName = getBaseName(sourceFile.getName());

        String targetName = path + baseName + postfix + "." + extension;
        if (new File(targetName).exists() && !overwrite)
            targetName = makeAlternativePathname(targetName);
        return targetName;
    }

    /**
     * This is static makeAlternativePathname( method
     * @param pathName
     * @return
     */
    public static String makeAlternativePathname(String pathName) {
        int limit = 100;

        for (int i = 2; i <= limit; i++) {
            String alternative = getBaseName(pathName) + "_" + i;
            String extension = getExtension(pathName);
            if (extension != null) alternative += "." + extension;
            if (!(new File(alternative).exists())) return alternative;
        }
        return null;
    }

    /**
     * This is staatic method getExtension(
     * @param pathName
     * @return
     */
    public static String getExtension(String pathName) {
        if (pathName.lastIndexOf('.') == -1) return null;
        return pathName.substring(pathName.lastIndexOf('.') + 1);
    }

    /**
     * This is getBaseName static method
     * @param pathName
     * @return
     */
    public static String getBaseName(String pathName) {
        if (pathName.lastIndexOf('.') == -1) return pathName;
        return pathName.substring(0, pathName.lastIndexOf('.'));
    }

    /**
     * This is readFile method
     * @param file
     * @return
     * @throws IOException
     */
    public static String readFile(File file) throws IOException {
        return readFile(file, null);
    }

    /**
     * This is readFile method
     * @param file
     * @param encoding
     * @return
     * @throws IOException
     */
    public static String readFile(File file, String encoding) throws IOException {
        long length = file.length();

        if (length > Integer.MAX_VALUE) {
            // File is too large
            // TODO: we need some feedback for the case of the file being too large
        }

        return readFile(new FileInputStream(file), file.getName(), encoding, length);
    }

    /**
     * This is readFile method
     * @param is
     * @param name
     * @param encoding
     * @return
     * @throws IOException
     */
    public static String readFile(InputStream is, String name, String encoding) throws IOException {
        return readFile(is, name, encoding, -1);
    }

    /**
     * This is readFile method
     * @param is
     * @param name
     * @param encoding
     * @param length
     * @return
     * @throws IOException
     */
    public static String readFile(InputStream is, String name, String encoding, long length) throws IOException {

        if (length < 0) {
            LineNumberReader reader = new LineNumberReader(new InputStreamReader(is));
            StringBuilder builder = new StringBuilder();
            while (true) {
                String line = reader.readLine();
                if (line == null) break;
                else builder.append(line).append("\n");
            }
            return builder.toString();
        } else {
            // Create the byte array to hold the data
            byte[] bytes = new byte[(int) length];

            // Read in the bytes
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length
                    && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }

            // Ensure all the bytes have been read in
            if (offset < bytes.length) {
                throw new IOException("Could not completely read file " + name);
            }

            // Close the input stream and return bytes
            is.close();
            if (encoding == null) {
                return new String(bytes);
            } else {
                return new String(bytes, encoding);
            }
        }
    }

    /**
     * This is main method
     * @param args
     */
    public static void main(String[] args) {
        System.out.println(makeTargetPathname("C:\\Files\\papar.txt", "jpg", false));
        System.out.println(makeTargetPathname("C:\\Files\\papar", "jpg", false));
        System.out.println(makeTargetPathname("papar.txt", "jpg", false));
        System.out.println(makeTargetPathname("/home/sideris/tsourekia/papar.txt", "jpg", false));
        System.out.println(makeTargetPathname("D:\\diagram.max", "jpg", false));
        System.out.println(makeAlternativePathname("C:\\Files\\papar.txt"));
        System.out.println(makeAlternativePathname("C:\\Files\\papar"));
        System.out.println(getExtension("pipi.jpeg"));
        System.out.println(getExtension("pipi"));
    }
}

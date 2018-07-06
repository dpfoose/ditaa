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

// using SAX

import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;


/**
 * This class is DocBookConverter
 * It extends class DefaultHandler
 * contains public methods startElement, characters,list method and main method
 */
public class DocBookConverter {

    /**
     * This is main method
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        new DocBookConverter().list();
    }

    /**
     * This method is list and throws exception
     * @throws Exception
     */
    public void list() throws Exception {
        XMLReader parser =
                XMLReaderFactory.createXMLReader
                        ("org.apache.crimson.parser.XMLReaderImpl");
        parser.setContentHandler(new HowToHandler());
        parser.parse("howto.xml");
    }

    class HowToHandler extends DefaultHandler {
        boolean title = false;
        boolean url = false;

        /**
         * This method is startElement
         * @param nsURI
         * @param strippedName
         * @param tagName
         * @param attributes
         * @returns void
         */
        public void startElement(
                String nsURI,
                String strippedName,
                String tagName,
                Attributes attributes) {
            if (tagName.equalsIgnoreCase("title"))
                title = true;
            if (tagName.equalsIgnoreCase("url"))
                url = true;
        }

        /**
         * This method is characters
         * @param ch
         * @param start
         * @param length
         * @returns void
         */
        public void characters(char[] ch, int start, int length) {
            if (title) {
                System.out.println("Title: " + new String(ch, start, length));
                title = false;
            } else if (url) {
                System.out.println("Url: " + new String(ch, start, length));
                url = false;
            }
        }
    }
}

/*
 * Copyright (C) 2016 Sony Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.sony.smarttennissensor.sample.mp4parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ParseUUID {

  public static ArrayList<String> extractXML(byte[] uuidContents) {

    String metadataString;
    ArrayList<String> metadataContents = new ArrayList<>();
    final String[] metadataElementTags = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
        "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T"};

    DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
    df.setNamespaceAware(true);
    DocumentBuilder db;

    try {
      db = df.newDocumentBuilder();
      Document document = db.parse(new ByteArrayInputStream(uuidContents));
      NodeList nodeList = document.getElementsByTagName("sd");

      for (int i = 0; i < nodeList.getLength(); i++) {
        Node shotDataNode = nodeList.item(i);

        //check that Shot Data is an element
        if (shotDataNode.getNodeType() == Node.ELEMENT_NODE) {
          Element currentShotElement = (Element) shotDataNode;
          metadataContents.add("Shot Data:");

          //search for all tags in Shot Data
          for (String tag : metadataElementTags) {
            NodeList elementList = currentShotElement.getElementsByTagName(tag);

            //if nothing in tag, display "empty"
            if (elementList.getLength() == 0) {
              metadataString = "Empty";
            } else {
              Element e = (Element) elementList.item(0);
              metadataString = e.getFirstChild().getNodeValue();
            }
            metadataContents.add(tag + ":" + metadataString);
          }
        }
      }

    } catch (SAXException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    }
    return metadataContents;
  }
}

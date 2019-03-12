/*
 * Copyright (C) 2019 Dylan Vicchiarelli
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.florence.model.mob;

import com.florence.model.Position;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class StaticMobsDocumentParser {

    public static final File XML_DATA_FILE = new File("./data/mobs/static-spawns.xml");
    public static final String ELEMENT_TAG_NAME = "spawn";

    public static final void load() {
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();

            final Document document = builder.parse(XML_DATA_FILE);
            document.getDocumentElement().normalize();

            final NodeList nodes = document.getElementsByTagName(ELEMENT_TAG_NAME);
            for (int i = 0; i < nodes.getLength(); i++) {
                final Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    final Element element = (Element) node;

                    final int identity = Integer.parseInt(element.getAttribute("id"));
                    final int x = Integer.parseInt(element.getElementsByTagName("x").item(0).getTextContent());
                    final int y = Integer.parseInt(element.getElementsByTagName("y").item(0).getTextContent());
                    final int z = Integer.parseInt(element.getElementsByTagName("z").item(0).getTextContent());
                    final int hitpoints = Integer.parseInt(element.getElementsByTagName("hp").item(0).getTextContent());
                    final int size = Integer.parseInt(element.getElementsByTagName("size").item(0).getTextContent());

                    final Mob spawn = new Mob(identity, size);
                    spawn.setHitpoints(hitpoints);
                    spawn.setMaximumHitpoints(hitpoints);
                    spawn.setPosition(Position.create(x, y, z));
                    spawn.add();
                }
            }
            System.out.println("Successfully loaded " + nodes.getLength() + " static mob spawns.");
        } catch (Exception exception) {
            exception.printStackTrace(System.out);
        }
    }
}

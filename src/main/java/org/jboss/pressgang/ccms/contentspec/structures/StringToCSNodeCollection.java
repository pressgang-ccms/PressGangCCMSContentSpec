/*
  Copyright 2011-2014 Red Hat, Inc

  This file is part of PressGang CCMS.

  PressGang CCMS is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  PressGang CCMS is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with PressGang CCMS.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.jboss.pressgang.ccms.contentspec.structures;

import java.util.ArrayList;

import org.jboss.pressgang.ccms.wrapper.CSNodeWrapper;


/**
 * This class is used to map a translation string to collections of XML Nodes.
 * This way the Nodes can be replaced with the XML formed by the translation
 * string.
 */
public class StringToCSNodeCollection {
    private String translationString;
    /**
     * The translationString may be unique, while mapping to several sequences
     * of nodes
     */
    private ArrayList<CSNodeWrapper> nodeCollections;

    public ArrayList<CSNodeWrapper> getNodeCollections() {
        return nodeCollections;
    }

    public void setNodeCollections(ArrayList<CSNodeWrapper> nodeCollections) {
        this.nodeCollections = nodeCollections;
    }

    public String getTranslationString() {
        return translationString;
    }

    public void setTranslationString(String translationString) {
        this.translationString = translationString;
    }

    public StringToCSNodeCollection() {

    }

    public StringToCSNodeCollection(final String translationString, final ArrayList<CSNodeWrapper> nodeCollections) {
        this.translationString = translationString;
        this.nodeCollections = nodeCollections;
    }

    public StringToCSNodeCollection(final String translationString, final CSNodeWrapper node) {
        this.translationString = translationString;
        addNode(node);
    }

    public StringToCSNodeCollection(final String translationString) {
        this.translationString = translationString;
    }

    public StringToCSNodeCollection addNodeCollection(final ArrayList<CSNodeWrapper> nodes) {
        if (nodeCollections == null) nodeCollections = new ArrayList<CSNodeWrapper>();
        nodeCollections.addAll(nodes);
        return this;
    }

    public StringToCSNodeCollection addNode(final CSNodeWrapper node) {
        if (nodeCollections == null) nodeCollections = new ArrayList<CSNodeWrapper>();
        nodeCollections.add(node);
        return this;
    }
}

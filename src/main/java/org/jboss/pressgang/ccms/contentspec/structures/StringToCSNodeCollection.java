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
        if (this.nodeCollections == null) this.nodeCollections = new ArrayList<CSNodeWrapper>();
        this.nodeCollections.addAll(nodes);
        return this;
    }

    public StringToCSNodeCollection addNode(final CSNodeWrapper node) {
        if (this.nodeCollections == null) this.nodeCollections = new ArrayList<CSNodeWrapper>();
        this.nodeCollections.add(node);
        return this;
    }
}

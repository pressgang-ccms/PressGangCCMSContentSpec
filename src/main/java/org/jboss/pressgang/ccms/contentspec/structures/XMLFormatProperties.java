package org.jboss.pressgang.ccms.contentspec.structures;

import java.util.List;

public class XMLFormatProperties {
    private List<String> verbatimElements;
    private List<String> inlineElements;
    private List<String> contentsInlineElements;

    public List<String> getContentsInlineElements() {
        return contentsInlineElements;
    }

    public void setContentsInlineElements(List<String> contentsInlineElements) {
        this.contentsInlineElements = contentsInlineElements;
    }

    public List<String> getInlineElements() {
        return inlineElements;
    }

    public void setInlineElements(List<String> inlineElements) {
        this.inlineElements = inlineElements;
    }

    public List<String> getVerbatimElements() {
        return verbatimElements;
    }

    public void setVerbatimElements(List<String> verbatimElements) {
        this.verbatimElements = verbatimElements;
    }
}

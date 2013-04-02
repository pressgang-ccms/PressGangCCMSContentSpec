package org.jboss.pressgang.ccms.contentspec;

/**
 * A very basic class that represents the lowest form of a Node in a Content Specification.
 *
 * @author lnewson
 */
public abstract class Node {
    protected static final String SPACER = "  ";

    protected final int lineNumber;
    protected String text;
    protected Node parent;
    protected String uniqueId = null;
    protected String translationUniqueId = null;

    public Node(final int lineNumber, final String text) {
        this.lineNumber = lineNumber;
        this.text = text;
    }

    public Node(final String text) {
        this.lineNumber = -1;
        this.text = text;
    }

    public Node() {
        this.lineNumber = -1;
        this.text = null;
    }

    /**
     * Gets the line number that the node is on in a Content Specification.
     *
     * @return The Line Number for the node.
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Gets the text for the node's line.
     *
     * @return The line of text for the node.
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text for the node.
     *
     * @param text The nodes text.
     */
    protected void setText(final String text) {
        this.text = text;
    }

    /**
     * Gets the step of the node in the Content Specification.
     *
     * @return The Step of the node.
     */
    public abstract Integer getStep();

    /**
     * Get the parent of the node.
     *
     * @return The nodes parent.
     */
    public Node getParent() {
        return parent;
    }

    /**
     * Sets the nodes parent.
     *
     * @param parent The parent node.
     */
    protected void setParent(final Node parent) {
        this.parent = parent;
    }

    /**
     * Gets the column the node starts at.
     *
     * @return The column the node starts at.
     */
    public Integer getColumn() {
        return parent == null ? 0 : (parent.getColumn() + 1);
    }

    /**
     * Removes the node from its parent.
     */
    protected abstract void removeParent();

    /**
     * Set the Unique ID for the Content Specification Topic, as well as cleans the string to be alphanumeric.
     *
     * @param uniqueId The Unique Content Specification Topic ID.
     */
    public void setUniqueId(final String uniqueId) {
        this.uniqueId = uniqueId == null ? null : uniqueId.replaceAll("[^\\w\\d\\-]", "");
    }

    /**
     * Gets the Content Specification Unique ID for the topic.
     *
     * @return The Unique CS Topic ID.
     */
    public String getUniqueId() {
        return uniqueId;
    }

    /**
     * Set the Unique ID for the Content Specification Topic, as well as cleans the string to be alphanumeric.
     *
     * @param translationUniqueId The Unique Content Specification Translated Topic ID.
     */
    public void setTranslationUniqueId(final String translationUniqueId) {
        this.translationUniqueId = translationUniqueId == null ? null : translationUniqueId.replaceAll("[^\\w\\d\\-]", "");
    }

    /**
     * Gets the Content Specification Translation Unique ID for the topic.
     *
     * @return The Unique CS Translated Topic ID.
     */
    public String getTranslationUniqueId() {
        return translationUniqueId;
    }
}

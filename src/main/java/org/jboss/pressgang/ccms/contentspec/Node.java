/*
  Copyright 2011-2014 Red Hat

  This file is part of PresGang CCMS.

  PresGang CCMS is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  PresGang CCMS is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with PresGang CCMS.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.jboss.pressgang.ccms.contentspec;

/**
 * A very basic class that represents the lowest form of a Node in a Content Specification.
 *
 * @author lnewson
 */
public abstract class Node {
    protected static final String SPACER = "  ";

    protected int lineNumber;
    protected String text;
    protected Node parent;
    protected String uniqueId = null;
    protected String translationUniqueId = null;

    protected Node(final int lineNumber, final String text) {
        this.lineNumber = lineNumber;
        this.text = text;
    }

    protected Node(final String text) {
        lineNumber = -1;
        this.text = text;
    }

    protected Node() {
        lineNumber = -1;
        text = null;
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
     * Sets the line number that the node is on in a Content Specification.
     *
     * @param lineNumber The Line Number for the node.
     */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
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

    /**
     * Gets the spacer string to append before nodes in their toString methods.
     *
     * @return A string containing the amount of space to use for the node.
     */
    protected String getSpacer() {
        final StringBuilder output = new StringBuilder();
        final int indentationSize = parent != null ? getColumn() : 0;
        for (int i = 1; i < indentationSize; i++) {
            output.append(SPACER);
        }
        return output.toString();
    }
}

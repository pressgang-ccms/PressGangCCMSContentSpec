package org.jboss.pressgang.ccms.contentspec;

public class TextNode extends Node {
    public TextNode(final int lineNumber, final String text) {
        super(lineNumber, text);
    }

    public TextNode(final String text) {
        super(text);
    }

    @Override
    public Integer getStep() {
        final Node parent = getParent();
        if (parent == null)
            return null;
        Integer previousNode = 0;

        if (parent instanceof Level) {
            // Get the position of the level in its parents nodes
            final Integer nodePos = ((Level) parent).getChildNodes().indexOf(this);

            // If the level isn't the first node then get the previous nodes step
            if (nodePos > 0) {
                final Node node = ((Level) parent).getChildNodes().get(nodePos - 1);
                previousNode = node.getStep();
                // If the add node is a level then add the number of nodes it contains
                if (node instanceof Level) {
                    previousNode = (previousNode == null ? 0 : previousNode) + ((Level) node).getTotalNumberOfChildren();
                }
                // The node is the first item so use the parent levels step
            } else {
                previousNode = getParent().getStep();
            }
            // Make sure the previous nodes step isn't 0
            previousNode = previousNode == null ? 0 : previousNode;

            // Add one since we got the previous nodes step
            return previousNode + 1;
        } else {
            return null;
        }
    }

    @Override
    protected void removeParent() {
        if (parent instanceof ContentSpec) {
            ((ContentSpec) parent).removeChild(this);
        } else if (parent instanceof Level) {
            ((Level) parent).removeChild(this);
        }
        this.parent = null;
    }

    @Override
    public String toString() {
        return getText();
    }
}

package org.jboss.pressgang.ccms.contentspec;

import org.jboss.pressgang.ccms.contentspec.enums.LevelType;
import org.jboss.pressgang.ccms.contentspec.utils.ContentSpecUtilities;

public class CommonContent extends SpecNode {

    public CommonContent(final String title, final int lineNumber, final String specLine) {
        super(lineNumber, specLine);
        setTitle(title);
    }

    public CommonContent(final int lineNumber, final String specLine) {
        super(lineNumber, specLine);
    }

    public CommonContent(final String title) {
        setTitle(title);
    }

    @Override
    public String getUniqueLinkId(Integer fixedUrlPropertyTagId, boolean useFixedUrls) {
        return getFixedTitle();
    }

    public String getFixedTitle() {
        String fixedTitle = getTitle();
        if (fixedTitle != null) {
            if (!fixedTitle.endsWith(".xml")) {
                fixedTitle += ".xml";
            }
            if (fixedTitle.contains(" ")) {
                fixedTitle = fixedTitle.replaceAll("\\s+", "_");
            }
        }
        return fixedTitle;
    }

    @Override
    public Integer getStep() {
        if (getParent() == null) {
            return null;
        } else if (getParent() instanceof Level) {
            final Level parent = (Level) getParent();
            Integer previousNode = 0;

            // Get the position of the level in its parents nodes
            Integer nodePos = parent.nodes.indexOf(this);

            // If the level isn't the first node then get the previous nodes step
            if (nodePos > 0) {
                Node node = parent.nodes.get(nodePos - 1);
                previousNode = node.getStep();
                // If the add node is a level then add the number of nodes it contains
                if (node instanceof Level) {
                    previousNode = (previousNode == null ? 0 : previousNode) + ((Level) node).getTotalNumberOfChildren();
                }
            } else if (nodePos == -1) {
                // The node is a front matter topic, so use the parents step
                if (parent.getLevelType() == LevelType.BASE) {
                    previousNode = -1;
                } else {
                    previousNode = parent.getStep() - 1;
                }
            } else {
                previousNode = parent.getStep();
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
    public Level getParent() {
        return (Level) super.getParent();
    }

    @Override
    protected void removeParent() {
        getParent().removeChild(this);
        setParent(null);
    }

    @Override
    public String getText() {
        final StringBuilder output = new StringBuilder(ContentSpecUtilities.escapeTitle(title));
        output.append(" [Common Content]");
        return output.toString();
    }

    @Override
    public String toString() {
        return getSpacer() + getText() + "\n";
    }
}

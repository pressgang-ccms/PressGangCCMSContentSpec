/*
  Copyright 2011-2014 Red Hat

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
        if (parent == null) return null;
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
        parent = null;
    }

    @Override
    public String toString() {
        return getText();
    }
}

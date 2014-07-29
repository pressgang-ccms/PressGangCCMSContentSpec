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

import org.jboss.pressgang.ccms.contentspec.enums.LevelType;

class BaseLevel extends Level {
    private final ContentSpec contentSpec;

    public BaseLevel(final ContentSpec contentSpec) {
        super("Initial Level", 0, null, LevelType.BASE);
        this.contentSpec = contentSpec;
    }

    public ContentSpec getContentSpec() {
        return contentSpec;
    }

    /**
     * Finds the closest node in the contents of a level.
     *
     * @param topic           The node we need to find the closest match for.
     * @param callerNode      TODO
     * @param checkParentNode TODO
     * @return TODO
     */
    @Override
    public SpecTopic getClosestTopic(final SpecTopic topic, final SpecNode callerNode, final boolean checkParentNode) {
        final SpecTopic retValue = super.getClosestTopic(topic, callerNode, checkParentNode);
        if (retValue != null) {
            return retValue;
        } else {
            // Look up the metadata topics
            final ContentSpec contentSpec = getContentSpec();
            for (final Node contentSpecNode : contentSpec.getNodes()) {
                if (contentSpecNode instanceof KeyValueNode && ((KeyValueNode) contentSpecNode).getValue() instanceof SpecTopic) {
                    final SpecTopic childTopic = (SpecTopic) ((KeyValueNode) contentSpecNode).getValue();
                    if (childTopic == topic || childTopic.getId().equals(topic.getId())) {
                        return childTopic;
                    }
                }
            }

            return null;
        }
    }

    /**
     * This function checks the levels nodes and child nodes to see if it can match a spec topic for a topic database id.
     *
     * @param DBId            The topic database id
     * @param callerNode      The node that called this function so that it isn't rechecked
     * @param checkParentNode If the function should check the levels parents as well
     * @return The closest available SpecTopic that matches the DBId otherwise null.
     */
    public SpecTopic getClosestTopicByDBId(final Integer DBId, final SpecNode callerNode, final boolean checkParentNode) {
        final SpecTopic retValue = super.getClosestTopicByDBId(DBId, callerNode, checkParentNode);
        if (retValue != null) {
            return retValue;
        } else {
            // Look up the metadata topics
            final ContentSpec contentSpec = getContentSpec();
            for (final Node contentSpecNode : contentSpec.getNodes()) {
                if (contentSpecNode instanceof KeyValueNode && ((KeyValueNode) contentSpecNode).getValue() instanceof SpecTopic) {
                    final SpecTopic childTopic = (SpecTopic) ((KeyValueNode) contentSpecNode).getValue();
                    if (childTopic.getDBId().equals(DBId)) {
                        return childTopic;
                    }
                }
            }

            return null;
        }
    }

    /**
     * This function checks the levels nodes and child nodes to see if it can match a spec topic for a topic database id.
     *
     * @param targetId            The topic database id
     * @param callerNode      The node that called this function so that it isn't rechecked
     * @param checkParentNode If the function should check the levels parents as well
     * @return The closest available SpecTopic that matches the DBId otherwise null.
     */
    public SpecNode getClosestSpecNodeByTargetId(final String targetId, final SpecNode callerNode, final boolean checkParentNode) {
        final SpecNode retValue = super.getClosestSpecNodeByTargetId(targetId, callerNode, checkParentNode);
        if (retValue != null) {
            return retValue;
        } else {
            // Look up the metadata topics
            final ContentSpec contentSpec = getContentSpec();
            for (final Node contentSpecNode : contentSpec.getNodes()) {
                if (contentSpecNode instanceof KeyValueNode && ((KeyValueNode) contentSpecNode).getValue() instanceof SpecTopic) {
                    final SpecTopic childTopic = (SpecTopic) ((KeyValueNode) contentSpecNode).getValue();
                    if (childTopic.getTargetId() != null && childTopic.getTargetId().equals(targetId)) {
                        return childTopic;
                    }
                }
            }

            return null;
        }
    }

    @Override
    public Integer getStep() {
        if (getContentSpec() == null) return null;
        return getContentSpec().getNodes().size();
    }
}

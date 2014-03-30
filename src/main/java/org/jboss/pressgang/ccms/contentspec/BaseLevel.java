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

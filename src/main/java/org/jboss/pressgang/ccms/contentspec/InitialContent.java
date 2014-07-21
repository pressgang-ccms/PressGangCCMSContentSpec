package org.jboss.pressgang.ccms.contentspec;

import org.jboss.pressgang.ccms.contentspec.constants.CSConstants;
import org.jboss.pressgang.ccms.contentspec.enums.LevelType;
import org.jboss.pressgang.ccms.contentspec.enums.TopicType;

public class InitialContent extends Level {

    /**
     * Constructor
     *
     * @param specLine   The Content Specification Line that is used to create the Initial Content.
     * @param lineNumber The Line Number of Initial Content in the Content Specification.
     */
    public InitialContent(final int lineNumber, final String specLine) {
        super(CSConstants.LEVEL_INITIAL_CONTENT, lineNumber, specLine, LevelType.INITIAL_CONTENT);
    }

    /**
     * Constructor
     *
     */
    public InitialContent() {
        super(CSConstants.LEVEL_INITIAL_CONTENT, LevelType.INITIAL_CONTENT);
    }

    /**
     * Adds a Content Specification Topic to the Level. If the Topic already has a parent, then it is removed from that parent
     * and added to this level.
     *
     * @param specTopic The Content Specification Topic to be added to the level.
     */
    public void appendSpecTopic(final SpecTopic specTopic) {
        specTopic.setTopicType(TopicType.INITIAL_CONTENT);
        super.appendSpecTopic(specTopic);
    }

    @Override
    public String getUniqueLinkId(final boolean useFixedUrls) {
        return getParent().getUniqueLinkId(useFixedUrls);
    }
}

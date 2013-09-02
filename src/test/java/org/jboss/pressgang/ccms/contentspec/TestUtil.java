package org.jboss.pressgang.ccms.contentspec;

import static com.google.common.collect.Maps.newHashMap;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.pressgang.ccms.utils.constants.CommonConstants;
import org.jboss.pressgang.ccms.wrapper.CSNodeWrapper;

/**
 * Shared methods to assist with testing.
 *
 * @author kamiller@redhat.com (Katie Miller)
 */
public class TestUtil {
    public static <T> T selectRandomListItem(List<T> list) {
        return list.get(nextInt(list.size()));
    }

    public static CSNodeWrapper createValidCommentMock(String text) {
        CSNodeWrapper commentChildNode = mock(CSNodeWrapper.class);
        given(commentChildNode.getNodeType()).willReturn(CommonConstants.CS_NODE_COMMENT);
        given(commentChildNode.getTitle()).willReturn(text);
        return commentChildNode;
    }

    public static CSNodeWrapper createValidTopicMock() {
        CSNodeWrapper topicNode = mock(CSNodeWrapper.class);
        given(topicNode.getNodeType()).willReturn(CommonConstants.CS_NODE_TOPIC);
        return topicNode;
    }

    public static CSNodeWrapper createValidLevelMock(Integer levelType) {
        CSNodeWrapper levelNode = mock(CSNodeWrapper.class);
        given(levelNode.getNodeType()).willReturn(levelType);
        return levelNode;
    }

    public static CSNodeWrapper createValidFileMock() {
        CSNodeWrapper fileNode = mock(CSNodeWrapper.class);
        given(fileNode.getNodeType()).willReturn(CommonConstants.CS_NODE_FILE);
        return fileNode;
    }

    public static CSNodeWrapper createMetaDataMock(String title, String additionalText) {
        CSNodeWrapper metaDataNode = mock(CSNodeWrapper.class);
        given(metaDataNode.getNodeType()).willReturn(CommonConstants.CS_NODE_META_DATA);
        given(metaDataNode.getTitle()).willReturn(title);
        given(metaDataNode.getAdditionalText()).willReturn(additionalText);
        return metaDataNode;
    }

    public static Map<Integer, Class> getLevelTypeMapping() {
        Map<Integer, Class> levelTypeMapping = newHashMap();
        levelTypeMapping.put(CommonConstants.CS_NODE_APPENDIX, Appendix.class);
        levelTypeMapping.put(CommonConstants.CS_NODE_CHAPTER, Chapter.class);
        levelTypeMapping.put(CommonConstants.CS_NODE_PART, Part.class);
        levelTypeMapping.put(CommonConstants.CS_NODE_PROCESS, Process.class);
        levelTypeMapping.put(CommonConstants.CS_NODE_PREFACE, Preface.class);
        levelTypeMapping.put(CommonConstants.CS_NODE_SECTION, Section.class);
        return levelTypeMapping;
    }

    public static Integer getRandomLevelType() {
        return selectRandomListItem(new ArrayList<Integer>(getLevelTypeMapping().keySet()));
    }
}


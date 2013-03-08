package org.jboss.pressgang.ccms.contentspec.utils;

import net.sf.ipsedixit.annotation.Arbitrary;
import net.sf.ipsedixit.annotation.ArbitraryString;
import net.sf.ipsedixit.core.StringType;
import org.jboss.pressgang.ccms.contentspec.*;
import org.jboss.pressgang.ccms.contentspec.wrapper.CSNodeWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;
import org.jboss.pressgang.ccms.utils.constants.CommonConstants;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.jboss.pressgang.ccms.contentspec.TestUtil.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;

/**
 * @author kamiller@redhat.com (Katie Miller)
 */
public class CSTransformerLevelTest extends CSTransformerTest {

    @ArbitraryString(type = StringType.ALPHANUMERIC) String title;
    @ArbitraryString(type = StringType.ALPHANUMERIC) String condition;
    @ArbitraryString(type = StringType.ALPHANUMERIC) String targetId;
    @ArbitraryString(type = StringType.ALPHANUMERIC) String text;
    @Arbitrary Integer id;
    @Arbitrary Integer anotherId;
    @Mock CSNodeWrapper nodeWrapper;
    @Mock CollectionWrapper<CSNodeWrapper> nodeChildren;
    List<CSNodeWrapper> relationshipFromNodes = new ArrayList<CSNodeWrapper>();
    Map<Integer, Node> nodes = newHashMap();
    Map<String, SpecTopic> targetTopics = newHashMap();
    Map<String, List<SpecTopic>> specTopicMap = newHashMap();

    @Test
    public void shouldThrowExceptionIfNodeNotLevel() throws Exception {
        // Given a node that is not a level
        given(nodeWrapper.getNodeType()).willReturn(CommonConstants.CS_NODE_COMMENT);

        // When transformLevel is called
        try {
            transformer.transformLevel(nodeWrapper, nodes, specTopicMap, targetTopics, relationshipFromNodes);

            // Then an exception should be thrown
            fail(ILLEGAL_ARG_EX_MISSING);
        } catch (IllegalArgumentException e) {

            // And an appropriate error message should be included
            assertThat(e.getMessage(), containsString("The passed node is not a Level"));
        }
    }

    @Test
    public void shouldCreateLevelOfCorrectType() throws Exception {
        // Given a node with a particular valid level type
        Integer levelType = getRandomLevelType();
        given(nodeWrapper.getNodeType()).willReturn(levelType);

        // When transformLevel is called
        Level result = transformer.transformLevel(nodeWrapper, nodes, specTopicMap, targetTopics, relationshipFromNodes);

        // Then a level of that type is created
        assertThat(result.getClass().equals(getLevelTypeMapping().get(levelType)), is(true));
    }

    @Test
    public void shouldTransfromNodeValues() throws Exception {
        // Given a node with a valid level type
        given(nodeWrapper.getNodeType()).willReturn(getRandomLevelType());
        // And other node values set
        given(nodeWrapper.getTitle()).willReturn(title);
        given(nodeWrapper.getCondition()).willReturn(condition);
        given(nodeWrapper.getTargetId()).willReturn(targetId);
        given(nodeWrapper.getId()).willReturn(id);

        // When transformLevel is called
        Level result = transformer.transformLevel(nodeWrapper, nodes, specTopicMap, targetTopics, relationshipFromNodes);

        // Then those values are transformed and set on the level as expected
        assertThat(result.getTitle(), is(title));
        assertThat(result.getConditionStatement(), is(condition));
        assertThat(result.getTargetId(), is(targetId));
        assertThat(result.getUniqueId(), is(id.toString()));
    }

    @Test
    public void shouldAddNodeToProcessedNodeList() throws Exception {
        // Given a node with a valid level type
        given(nodeWrapper.getNodeType()).willReturn(getRandomLevelType());
        // And an id set
        given(nodeWrapper.getId()).willReturn(id);
        // And that there is nothing in the processed nodes store
        assertThat(nodes.size(), is(0));

        // When transformLevel is called
        Level result = transformer.transformLevel(nodeWrapper, nodes, specTopicMap, targetTopics, relationshipFromNodes);

        // Then the level should be added to the processed nodes
        assertThat(nodes.get(nodeWrapper.getId()), is((Node) result));
    }

    @Test
    public void shouldAddChildTopic() throws Exception {
        // Given a node with a valid level type
        given(nodeWrapper.getNodeType()).willReturn(getRandomLevelType());
        // And a child topic node
        CSNodeWrapper topicNode = createValidTopicMock();
        setChildNodes(asList(topicNode));
        // And appropriate values for sorting
        given(topicNode.getPreviousNodeId()).willReturn(null);
        given(topicNode.getNextNodeId()).willReturn(null);

        // When transformLevel is called
        Level result = transformer.transformLevel(nodeWrapper, nodes, specTopicMap, targetTopics, relationshipFromNodes);

        // Then the topic is transformed and added as a level child
        assertThat(result.getChildNodes().size(), is(1));
        assertThat(result.getChildNodes().get(0).getClass().equals(SpecTopic.class), is(true));
    }

    @Test
    public void shouldAddChildComment() throws Exception {
        // Given a node with a valid level type
        given(nodeWrapper.getNodeType()).willReturn(getRandomLevelType());
        // And a child comment node
        CSNodeWrapper commentNode = createValidCommentMock(text);
        setChildNodes(asList(commentNode));
        // And appropriate values for sorting
        given(commentNode.getPreviousNodeId()).willReturn(null);
        given(commentNode.getNextNodeId()).willReturn(null);

        // When transformLevel is called
        Level result = transformer.transformLevel(nodeWrapper, nodes, specTopicMap, targetTopics, relationshipFromNodes);

        // Then the comment is transformed and added as a level child
        assertThat(result.getChildNodes().size(), is(1));
        assertThat(result.getChildNodes().get(0).getClass().equals(Comment.class), is(true));
    }

    @Test
    public void shouldAddChildLevel() throws Exception {
        // Given a node with a valid level type
        given(nodeWrapper.getNodeType()).willReturn(getRandomLevelType());
        // And a child level that is not a Chapter or Part
        CSNodeWrapper levelNode = createValidLevelMock(CommonConstants.CS_NODE_APPENDIX);
        setChildNodes(asList(levelNode));
        // And appropriate values for sorting
        given(levelNode.getPreviousNodeId()).willReturn(null);
        given(levelNode.getNextNodeId()).willReturn(null);

        // When transformLevel is called
        Level result = transformer.transformLevel(nodeWrapper, nodes, specTopicMap, targetTopics, relationshipFromNodes);

        // Then the level is transformed and added as a level child
        assertThat(result.getChildNodes().size(), is(1));
        assertThat(result.getChildNodes().get(0).getClass().equals(Appendix.class), is(true));
    }

    @Test
    public void shouldAddNewLineToSeparateChaptersOrParts() throws Exception {
        // Given a node with a valid level type
        given(nodeWrapper.getNodeType()).willReturn(getRandomLevelType());
        // And a child level that is a Chapter or Part
        Integer childLevelType = selectRandomListItem(asList(CommonConstants.CS_NODE_PART, CommonConstants.CS_NODE_CHAPTER));
        CSNodeWrapper levelNode = createValidLevelMock(childLevelType);
        setChildNodes(asList(levelNode));
        // And appropriate values for sorting
        given(levelNode.getPreviousNodeId()).willReturn(null);
        given(levelNode.getNextNodeId()).willReturn(null);

        // When transformLevel is called
        Level result = transformer.transformLevel(nodeWrapper, nodes, specTopicMap, targetTopics, relationshipFromNodes);

        // Then the level is transformed and added as a level child
        // And a textnode is added containing a newline character
        assertThat(result.getChildNodes().size(), is(2));
        assertThat(result.getChildNodes().get(0).getClass().equals(getLevelTypeMapping().get(childLevelType)), is(true));
        assertThat(result.getChildNodes().get(1).getText(), is("\n"));
    }

    @Test
    public void shouldSortAndAddChildNodes() throws Exception {
        // Given a node with a valid level type
        given(nodeWrapper.getNodeType()).willReturn(getRandomLevelType());
        // And a collection of child nodes of different types
        CSNodeWrapper topicChildNode = createValidTopicMock();
        CSNodeWrapper levelChildNode = createValidLevelMock(CommonConstants.CS_NODE_APPENDIX);
        CSNodeWrapper commentChildNode = createValidCommentMock(text);
        setChildNodes(asList(commentChildNode, levelChildNode, topicChildNode));
        // And that appropriate sorting values have been set
        given(topicChildNode.getPreviousNodeId()).willReturn(null);
        given(levelChildNode.getId()).willReturn(id);
        given(topicChildNode.getNextNodeId()).willReturn(id);
        given(commentChildNode.getId()).willReturn(anotherId);
        given(levelChildNode.getNextNodeId()).willReturn(anotherId);
        given(commentChildNode.getNextNodeId()).willReturn(null);

        // When transformLevel is called
        Level result = transformer.transformLevel(nodeWrapper, nodes, specTopicMap, targetTopics, relationshipFromNodes);

        // Then the nodes should have been added as children
        // And be in the order expected
        assertThat(result.getChildNodes().get(0).getClass().equals(SpecTopic.class), is(true));
        assertThat(result.getChildNodes().get(1).getClass().equals(Appendix.class), is(true));
        assertThat(result.getChildNodes().get(2).getClass().equals(Comment.class), is(true));
    }

    void setChildNodes(List<CSNodeWrapper> childItems) {
        given(nodeWrapper.getChildren()).willReturn(nodeChildren);
        given(nodeChildren.getItems()).willReturn(childItems);
    }
}
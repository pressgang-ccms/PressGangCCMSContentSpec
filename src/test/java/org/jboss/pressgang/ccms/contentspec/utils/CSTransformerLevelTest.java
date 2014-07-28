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

package org.jboss.pressgang.ccms.contentspec.utils;

import static com.google.common.collect.Maps.newHashMap;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.jboss.pressgang.ccms.contentspec.TestUtil.createValidCommentMock;
import static org.jboss.pressgang.ccms.contentspec.TestUtil.createValidInfoTopicMock;
import static org.jboss.pressgang.ccms.contentspec.TestUtil.createValidLevelMock;
import static org.jboss.pressgang.ccms.contentspec.TestUtil.createValidTopicMock;
import static org.jboss.pressgang.ccms.contentspec.TestUtil.getLevelTypeMapping;
import static org.jboss.pressgang.ccms.contentspec.TestUtil.getRandomLevelType;
import static org.jboss.pressgang.ccms.contentspec.TestUtil.selectRandomListItem;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.ipsedixit.annotation.Arbitrary;
import net.sf.ipsedixit.annotation.ArbitraryString;
import net.sf.ipsedixit.core.StringType;
import org.jboss.pressgang.ccms.contentspec.Appendix;
import org.jboss.pressgang.ccms.contentspec.Comment;
import org.jboss.pressgang.ccms.contentspec.Level;
import org.jboss.pressgang.ccms.contentspec.Node;
import org.jboss.pressgang.ccms.contentspec.Process;
import org.jboss.pressgang.ccms.contentspec.SpecTopic;
import org.jboss.pressgang.ccms.contentspec.TextNode;
import org.jboss.pressgang.ccms.utils.constants.CommonConstants;
import org.jboss.pressgang.ccms.wrapper.CSInfoNodeWrapper;
import org.jboss.pressgang.ccms.wrapper.CSNodeWrapper;
import org.jboss.pressgang.ccms.wrapper.collection.UpdateableCollectionWrapper;
import org.junit.Test;
import org.mockito.Mock;

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
    @Mock UpdateableCollectionWrapper<CSNodeWrapper> nodeChildren;
    List<CSNodeWrapper> relationshipFromNodes = new ArrayList<CSNodeWrapper>();
    Map<Integer, Node> nodes = newHashMap();
    Map<String, SpecTopic> targetTopics = newHashMap();
    List<Process> processes = new ArrayList<Process>();

    @Test
    public void shouldThrowExceptionIfNodeNotLevel() throws Exception {
        // Given a node that is not a level
        given(nodeWrapper.getNodeType()).willReturn(CommonConstants.CS_NODE_COMMENT);

        // When transformLevel is called
        try {
            CSTransformer.transformLevel(nodeWrapper, nodes, targetTopics, relationshipFromNodes, processes);

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
        Level result = CSTransformer.transformLevel(nodeWrapper, nodes, targetTopics, relationshipFromNodes, processes);

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
        Level result = CSTransformer.transformLevel(nodeWrapper, nodes, targetTopics, relationshipFromNodes, processes);

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
        Level result = CSTransformer.transformLevel(nodeWrapper, nodes, targetTopics, relationshipFromNodes, processes);

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
        given(topicNode.getNextNode()).willReturn(null);

        // When transformLevel is called
        Level result = CSTransformer.transformLevel(nodeWrapper, nodes, targetTopics, relationshipFromNodes, processes);

        // Then the topic is transformed and added as a level child
        assertThat(result.getChildNodes().size(), is(1));
        assertThat(result.getChildNodes().get(0).getClass().equals(SpecTopic.class), is(true));
    }

    @Test
    public void shouldAddInfoTopic() throws Exception {
        // Given a node with a valid level type
        given(nodeWrapper.getNodeType()).willReturn(getRandomLevelType());
        // And a child topic node
        CSInfoNodeWrapper topicNode = createValidInfoTopicMock();
        given(nodeWrapper.getInfoTopicNode()).willReturn(topicNode);

        // When transformLevel is called
        Level result = CSTransformer.transformLevel(nodeWrapper, nodes, targetTopics, relationshipFromNodes, processes);

        // Then the topic is transformed and set as the info topic
        assertNotNull(result.getInfoTopic());
    }

    @Test
    public void shouldAddChildComment() throws Exception {
        // Given a node with a valid level type
        given(nodeWrapper.getNodeType()).willReturn(getRandomLevelType());
        // And a child comment node
        CSNodeWrapper commentNode = createValidCommentMock(text);
        setChildNodes(asList(commentNode));
        // And appropriate values for sorting
        given(commentNode.getNextNode()).willReturn(null);

        // When transformLevel is called
        Level result = CSTransformer.transformLevel(nodeWrapper, nodes, targetTopics, relationshipFromNodes, processes);

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
        given(levelNode.getNextNode()).willReturn(null);

        // When transformLevel is called
        Level result = CSTransformer.transformLevel(nodeWrapper, nodes, targetTopics, relationshipFromNodes, processes);

        // Then the level is transformed and added as a level child
        assertThat(result.getChildNodes().size(), is(1));
        assertThat(result.getChildNodes().get(0).getClass().equals(Appendix.class), is(true));
    }

    @Test
    public void shouldAddNewLineToSeparateChaptersOrParts() throws Exception {
        // Given a node with a valid level type
        given(nodeWrapper.getNodeType()).willReturn(getRandomLevelType());
        // And a child level that is a Chapter or Part
        Integer childLevelType = selectRandomListItem(asList(CommonConstants.CS_NODE_PART, CommonConstants.CS_NODE_CHAPTER,
                CommonConstants.CS_NODE_APPENDIX, CommonConstants.CS_NODE_PREFACE));
        CSNodeWrapper levelNode = createValidLevelMock(childLevelType);
        setChildNodes(asList(levelNode));
        // And appropriate values for sorting
        given(levelNode.getNextNode()).willReturn(null);

        // When transformLevel is called
        Level result = CSTransformer.transformLevel(nodeWrapper, nodes, targetTopics, relationshipFromNodes, processes);

        // Then the level is transformed and added as a level child
        assertThat(result.getChildNodes().size(), is(1));
        assertThat(result.getChildNodes().get(0).getClass().equals(getLevelTypeMapping().get(childLevelType)), is(true));
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
        given(levelChildNode.getId()).willReturn(id);
        given(topicChildNode.getNextNode()).willReturn(levelChildNode);
        given(commentChildNode.getId()).willReturn(anotherId);
        given(levelChildNode.getNextNode()).willReturn(commentChildNode);
        given(commentChildNode.getNextNode()).willReturn(null);

        // When transformLevel is called
        Level result = CSTransformer.transformLevel(nodeWrapper, nodes, targetTopics, relationshipFromNodes, processes);

        // Then the nodes should have been added as children
        // And be in the order expected
        assertThat(result.getChildNodes().get(0).getClass().equals(SpecTopic.class), is(true));
        assertThat(result.getChildNodes().get(1).getClass().equals(Appendix.class), is(true));
        assertThat(result.getChildNodes().get(2).getClass().equals(TextNode.class), is(true));
        assertThat(result.getChildNodes().get(3).getClass().equals(Comment.class), is(true));
    }

    void setChildNodes(List<CSNodeWrapper> childItems) {
        given(nodeWrapper.getChildren()).willReturn(nodeChildren);
        given(nodeChildren.getItems()).willReturn(childItems);
    }
}

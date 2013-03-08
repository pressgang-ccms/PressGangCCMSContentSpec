package org.jboss.pressgang.ccms.contentspec.utils;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.jboss.pressgang.ccms.contentspec.TestUtil.createMetaDataMock;
import static org.jboss.pressgang.ccms.contentspec.TestUtil.createValidCommentMock;
import static org.jboss.pressgang.ccms.contentspec.TestUtil.createValidLevelMock;
import static org.jboss.pressgang.ccms.contentspec.TestUtil.createValidTopicMock;
import static org.jboss.pressgang.ccms.contentspec.TestUtil.getLevelTypeMapping;
import static org.jboss.pressgang.ccms.contentspec.TestUtil.selectRandomListItem;
import static org.jboss.pressgang.ccms.utils.constants.CommonConstants.CS_RELATIONSHIP_REFER_TO;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;

import net.sf.ipsedixit.annotation.Arbitrary;
import net.sf.ipsedixit.annotation.ArbitraryString;
import net.sf.ipsedixit.core.StringType;
import org.hamcrest.Matchers;
import org.jboss.pressgang.ccms.contentspec.Appendix;
import org.jboss.pressgang.ccms.contentspec.Comment;
import org.jboss.pressgang.ccms.contentspec.ContentSpec;
import org.jboss.pressgang.ccms.contentspec.KeyValueNode;
import org.jboss.pressgang.ccms.contentspec.Level;
import org.jboss.pressgang.ccms.contentspec.Node;
import org.jboss.pressgang.ccms.contentspec.Section;
import org.jboss.pressgang.ccms.contentspec.SpecTopic;
import org.jboss.pressgang.ccms.contentspec.TextNode;
import org.jboss.pressgang.ccms.contentspec.provider.DataProviderFactory;
import org.jboss.pressgang.ccms.contentspec.wrapper.CSNodeWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.CSRelatedNodeWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.ContentSpecWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TagWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.UpdateableCollectionWrapper;
import org.jboss.pressgang.ccms.utils.constants.CommonConstants;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * @author kamiller@redhat.com (Katie Miller)
 */
public class CSTransformerTransformTest extends CSTransformerTest {

    @ArbitraryString(type = StringType.ALPHANUMERIC) String title;
    @ArbitraryString(type = StringType.ALPHANUMERIC) String text;
    @ArbitraryString(type = StringType.ALPHANUMERIC) String product;
    @ArbitraryString(type = StringType.ALPHANUMERIC) String condition;
    @ArbitraryString(type = StringType.ALPHANUMERIC) String version;
    @ArbitraryString(type = StringType.ALPHANUMERIC) String tagName;
    @ArbitraryString(type = StringType.ALPHANUMERIC) String tagName2;
    @Arbitrary Integer id;
    @Arbitrary Integer anotherId;
    @Mock ContentSpecWrapper specWrapper;
    @Mock DataProviderFactory providerFactory;
    @Mock CollectionWrapper<TagWrapper> tagWrapper;
    @Mock TagWrapper tag;
    @Mock TagWrapper tag2;
    @Mock CollectionWrapper<CSNodeWrapper> nodeCollectionWrapper;
    @Mock UpdateableCollectionWrapper<CSRelatedNodeWrapper> collectionWrapper;
    @Mock CSRelatedNodeWrapper relatedNodeWrapper;

    @Test
    public void shouldSetBasicValues() throws Exception {
        // Given a contentSpecWrapper with some base values set
        given(specWrapper.getId()).willReturn(id);
        given(specWrapper.getCondition()).willReturn(condition);

        // When the spec is transformed
        ContentSpec result = transformer.transform(specWrapper, providerFactory);

        // Then those values should be set on the resulting spec
        assertThat(result.getId(), is(id));
        assertThat(result.getBaseLevel().getConditionStatement(), is(condition));
    }

    @Test
    public void shouldAddTextNodeToSeparateMetaData() throws Exception {
        // Given a contentSpecWrapper with a title
        CSNodeWrapper metaDataNode = createMetaDataMock("Title", text);
        CSNodeWrapper levelNode = createValidLevelMock(CommonConstants.CS_NODE_SECTION);
        setChildren(asList(metaDataNode, levelNode));
        // And appropriate values for sorting
        given(metaDataNode.getPreviousNodeId()).willReturn(null);
        given(metaDataNode.getNextNodeId()).willReturn(anotherId);
        given(metaDataNode.getId()).willReturn(id);
        given(levelNode.getPreviousNodeId()).willReturn(id);
        given(levelNode.getNextNodeId()).willReturn(null);
        given(levelNode.getId()).willReturn(anotherId);

        // When the spec is transformed
        ContentSpec result = transformer.transform(specWrapper, providerFactory);

        // Then a textNode should have been added containing just a newline
        assertThat(result.getNodes().size(), is(3)); // (the id, title plus the new node)
        assertThat(result.getNodes().get(2).getClass().equals(TextNode.class), is(true));
        assertThat(result.getNodes().get(2).getText().equals(("\n")), is(true));
    }

    @Test
    public void shouldAddTagsFromGivenSpec() throws Exception {
        // Given a spec that has some tags
        given(specWrapper.getTags()).willReturn(tagWrapper);
        given(tag.getName()).willReturn(tagName);
        given(tag2.getName()).willReturn(tagName2);
        List<TagWrapper> tags = asList(tag, tag2);
        given(tagWrapper.getItems()).willReturn(tags);

        // When the spec is transformed
        ContentSpec result = transformer.transform(specWrapper, providerFactory);

        // Then the tags should be set on the resulting spec
        assertThat(result.getTags(), Matchers.contains(tagName, tagName2));
    }

    @Test
    public void shouldAddTransformedChildTopic() throws Exception {
        // Given a spec with a child topic
        CSNodeWrapper topicNode = createValidTopicMock();
        setChildren(asList(topicNode));
        // And appropriate values for sorting
        given(topicNode.getPreviousNodeId()).willReturn(null);
        given(topicNode.getNextNodeId()).willReturn(null);

        // When the spec is transformed
        ContentSpec result = transformer.transform(specWrapper, providerFactory);

        // Then the transformed topic should be a child node on the resulting spec
        assertThat(result.getChildNodes().size(), is(1));
        assertThat(result.getChildNodes().get(0).getClass().equals(SpecTopic.class), is(true));
    }

    @Test
    public void shouldAddTransformedChildComment() throws Exception {
        // Given a spec with a child comment
        CSNodeWrapper commentNode = createValidCommentMock(text);
        setChildren(asList(commentNode));
        // And appropriate values for sorting
        given(commentNode.getPreviousNodeId()).willReturn(null);
        given(commentNode.getNextNodeId()).willReturn(null);

        // When the spec is transformed
        ContentSpec result = transformer.transform(specWrapper, providerFactory);

        // Then the transformed comment should be a child node on the resulting spec
        assertThat(result.getNodes().size(), is(2));
        assertThat(result.getNodes().get(1).getClass().equals(Comment.class), is(true));
    }

    @Test
    public void shouldAddTransformedMetaDataIfNotOnIgnoreList() throws Exception {
        // Given a spec with child metadata that does not have a title on the ignore list
        CSNodeWrapper metaDataNode = createMetaDataMock(title, text);
        setChildren(asList(metaDataNode));
        // And appropriate values for sorting
        given(metaDataNode.getPreviousNodeId()).willReturn(null);
        given(metaDataNode.getNextNodeId()).willReturn(null);

        // When the spec is transformed
        ContentSpec result = transformer.transform(specWrapper, providerFactory);

        // Then the transformed metadata should be a node on the resulting spec
        assertThat(result.getChildNodes().size(), is(0));
        assertThat(result.getNodes().size(), is(2));
        assertThat(result.getNodes().get(1).getClass().equals(KeyValueNode.class), is(true));
        assertThat(result.getNodes().get(1).getText(), containsString(title));
    }

    @Test
    public void shouldNotAddMetaDataIfOnIgnoreList() throws Exception {
        // Given a spec with child metadata that has a title on the ignore list
        CSNodeWrapper metaDataNode = createMetaDataMock("ID", text);
        setChildren(asList(metaDataNode));
        // And appropriate values for sorting
        given(metaDataNode.getPreviousNodeId()).willReturn(null);
        given(metaDataNode.getNextNodeId()).willReturn(null);

        // When the spec is transformed
        ContentSpec result = transformer.transform(specWrapper, providerFactory);

        // Then the transformed metadata should not be a node on the resulting spec
        assertThat(result.getChildNodes().size(), is(0));
        assertThat(result.getNodes().size(), is(1));
        for (Node n : result.getNodes()) {
            assertThat(n.getText().contains(title), is(false));
        }
    }

    @Test
    public void shouldAddTransformedChildLevel() throws Exception {
        // Given a spec with a child level
        CSNodeWrapper levelNode = createValidLevelMock(CommonConstants.CS_NODE_SECTION);
        setChildren(asList(levelNode));
        // And appropriate values for sorting
        given(levelNode.getPreviousNodeId()).willReturn(null);
        given(levelNode.getNextNodeId()).willReturn(null);

        // When the spec is transformed
        ContentSpec result = transformer.transform(specWrapper, providerFactory);

        // Then the transformed level should be a child node on the resulting spec
        assertThat(result.getChildNodes().size(), is(1));
        assertThat(result.getChildNodes().get(0).getClass().equals(Section.class), is(true));
    }

    @Test
    public void shouldSortAndAddChildNodes() throws Exception {
        // Given a spec with several child nodes of different types
        CSNodeWrapper topicNode = createValidTopicMock();
        CSNodeWrapper levelNode = createValidLevelMock(CommonConstants.CS_NODE_APPENDIX);
        CSNodeWrapper commentNode = createValidCommentMock(text);
        setChildren(asList(commentNode, levelNode, topicNode));
        // And that appropriate sorting values have been set
        given(commentNode.getPreviousNodeId()).willReturn(null);
        given(levelNode.getId()).willReturn(id);
        given(commentNode.getNextNodeId()).willReturn(id);
        given(topicNode.getId()).willReturn(anotherId);
        given(levelNode.getNextNodeId()).willReturn(anotherId);
        given(topicNode.getNextNodeId()).willReturn(null);

        // When the spec is transformed
        ContentSpec result = transformer.transform(specWrapper, providerFactory);

        // Then the nodes should have been added as children of the resulting spec
        // And be in the order expected
        assertThat(result.getNodes().get(1).getClass().equals(Comment.class), is(true));
        assertThat(result.getChildNodes().get(0).getClass().equals(Appendix.class), is(true));
        assertThat(result.getChildNodes().get(1).getClass().equals(SpecTopic.class), is(true));
    }

    @Test
    public void shouldAddTextNodeAfterChaptersOrParts() throws Exception {
        // Given a spec with a child level that is a Chapter or Part
        Integer childLevelType = selectRandomListItem(asList(CommonConstants.CS_NODE_PART, CommonConstants.CS_NODE_CHAPTER));
        CSNodeWrapper levelNode = createValidLevelMock(childLevelType);
        // And a comment child node
        CSNodeWrapper commentNode = createValidCommentMock(text);
        setChildren(asList(levelNode, commentNode));
        // And appropriate values for sorting
        given(levelNode.getPreviousNodeId()).willReturn(null);
        given(levelNode.getNextNodeId()).willReturn(id);
        given(commentNode.getId()).willReturn(id);
        given(commentNode.getNextNodeId()).willReturn(null);

        // When the spec is transformed
        ContentSpec result = transformer.transform(specWrapper, providerFactory);

        // Then the level and comment nodes are transformed and added as children
        // And a textnode is added between them, containing a newline character
        assertThat(result.getChildNodes().size(), is(3));
        assertThat(result.getChildNodes().get(0).getClass().equals(getLevelTypeMapping().get(childLevelType)), is(true));
        assertThat(result.getChildNodes().get(1).getText(), is("\n"));
        assertThat(result.getChildNodes().get(2).getClass().equals(Comment.class), is(true));
    }

    @Test
    public void shouldApplyRelationshipsToTransformedSpec() throws Exception {
        // Given a contentSpecWrapper with a spec topic child with a relationship
        CSNodeWrapper topicNode = createValidTopicMock();
        given(topicNode.getId()).willReturn(id);
        setChildren(asList(topicNode));
        given(topicNode.getRelatedToNodes()).willReturn(collectionWrapper);
        List<CSRelatedNodeWrapper> items = Arrays.asList(relatedNodeWrapper);
        given(collectionWrapper.getItems()).willReturn(items);
        given(relatedNodeWrapper.getId()).willReturn(id);
        given(relatedNodeWrapper.getRelationshipType()).willReturn(CS_RELATIONSHIP_REFER_TO);
        // And appropriate values for sorting
        given(topicNode.getPreviousNodeId()).willReturn(null);
        given(topicNode.getNextNodeId()).willReturn(null);

        // When the spec is transformed
        ContentSpec result = transformer.transform(specWrapper, providerFactory);

        // Then the relationship should be processed
        assertThat(result.getRelationships().size(), is(1));
    }

    @Test
    public void shouldAddProcessLevelToProcesses() throws Exception {
        // Given a spec with a child level that is a process
        CSNodeWrapper levelNode = createValidLevelMock(CommonConstants.CS_NODE_PROCESS);
        given(levelNode.getId()).willReturn(id);
        setChildren(asList(levelNode));
        // And appropriate values for sorting
        given(levelNode.getPreviousNodeId()).willReturn(null);
        given(levelNode.getNextNodeId()).willReturn(null);
        // And we are spying on our class under test (as there isn't another good way to check this)
        CSTransformer transformerSpy = Mockito.spy(transformer);

        // When the spec is transformed
        ContentSpec result = transformerSpy.transform(specWrapper, providerFactory);

        // Then the transformed level should be a child node on the resulting spec
        assertThat(result.getChildNodes().size(), is(1));
        // And the level should have been added to the spec's processes
        ArgumentCaptor<List> processesCaptor = ArgumentCaptor.forClass(List.class);
        verify(transformerSpy, times(1)).applyRelationships(any(ContentSpec.class), anyMap(), anyMap(), anyMap(), anyList(),
                processesCaptor.capture(), any(DataProviderFactory.class));
        assertThat(processesCaptor.getValue().size(), is(1));
        Level expectedLevel = (Level) processesCaptor.getValue().get(0);
        assertThat(expectedLevel.getUniqueId(), is(id.toString()));
    }

    void setChildren(List<CSNodeWrapper> nodeList) {
        given(specWrapper.getChildren()).willReturn(nodeCollectionWrapper);
        given(nodeCollectionWrapper.getItems()).willReturn(nodeList);
    }
}
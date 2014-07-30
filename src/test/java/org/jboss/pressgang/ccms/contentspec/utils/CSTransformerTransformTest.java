/*
  Copyright 2011-2014 Red Hat, Inc

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

package org.jboss.pressgang.ccms.contentspec.utils;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertTrue;
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
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import net.sf.ipsedixit.annotation.Arbitrary;
import net.sf.ipsedixit.annotation.ArbitraryString;
import net.sf.ipsedixit.core.StringType;
import org.jboss.pressgang.ccms.contentspec.Appendix;
import org.jboss.pressgang.ccms.contentspec.Comment;
import org.jboss.pressgang.ccms.contentspec.ContentSpec;
import org.jboss.pressgang.ccms.contentspec.KeyValueNode;
import org.jboss.pressgang.ccms.contentspec.Level;
import org.jboss.pressgang.ccms.contentspec.Node;
import org.jboss.pressgang.ccms.contentspec.Section;
import org.jboss.pressgang.ccms.contentspec.SpecTopic;
import org.jboss.pressgang.ccms.contentspec.TextNode;
import org.jboss.pressgang.ccms.provider.DataProviderFactory;
import org.jboss.pressgang.ccms.provider.ServerSettingsProvider;
import org.jboss.pressgang.ccms.utils.constants.CommonConstants;
import org.jboss.pressgang.ccms.wrapper.CSNodeWrapper;
import org.jboss.pressgang.ccms.wrapper.CSRelatedNodeWrapper;
import org.jboss.pressgang.ccms.wrapper.ContentSpecWrapper;
import org.jboss.pressgang.ccms.wrapper.ServerEntitiesWrapper;
import org.jboss.pressgang.ccms.wrapper.ServerSettingsWrapper;
import org.jboss.pressgang.ccms.wrapper.TagWrapper;
import org.jboss.pressgang.ccms.wrapper.collection.CollectionWrapper;
import org.jboss.pressgang.ccms.wrapper.collection.UpdateableCollectionWrapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

/**
 * @author kamiller@redhat.com (Katie Miller)
 */
@PrepareForTest(CSTransformer.class)
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
    @Mock ServerSettingsProvider serverSettingsProvider;
    @Mock ServerSettingsWrapper serverSettings;
    @Mock ServerEntitiesWrapper serverEntities;
    @Mock CollectionWrapper<TagWrapper> tagWrapper;
    @Mock TagWrapper tag;
    @Mock TagWrapper tag2;
    @Mock UpdateableCollectionWrapper<CSNodeWrapper> nodeCollectionWrapper;
    @Mock UpdateableCollectionWrapper<CSRelatedNodeWrapper> collectionWrapper;
    @Mock CSRelatedNodeWrapper relatedNodeWrapper;

    @Before
    public void setUp() {
        when(providerFactory.getProvider(ServerSettingsProvider.class)).thenReturn(serverSettingsProvider);
        when(serverSettingsProvider.getServerSettings()).thenReturn(serverSettings);
        when(serverSettings.getEntities()).thenReturn(serverEntities);
        when(serverEntities.getTaskTagId()).thenReturn(4);
    }

    @Test
    public void shouldSetBasicValues() throws Exception {
        // Given a contentSpecWrapper with some base values set
        given(specWrapper.getId()).willReturn(id);
        given(specWrapper.getCondition()).willReturn(condition);

        // When the spec is transformed
        ContentSpec result = CSTransformer.transform(specWrapper, providerFactory, true);

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
        given(metaDataNode.getNextNode()).willReturn(levelNode);
        given(metaDataNode.getId()).willReturn(id);
        given(levelNode.getNextNode()).willReturn(null);
        given(levelNode.getId()).willReturn(anotherId);

        // When the spec is transformed
        ContentSpec result = CSTransformer.transform(specWrapper, providerFactory, true);

        // Then a textNode should have been added containing just a newline
        assertThat(result.getNodes().size(), is(3)); // (the id, title plus the new node)
        assertThat(result.getNodes().get(2).getClass().equals(TextNode.class), is(true));
        assertThat(result.getNodes().get(2).getText().equals(("\n")), is(true));
    }

    @Test
    public void shouldAddTagsFromGivenSpec() throws Exception {
        // Given a spec that has some tags
        given(specWrapper.getBookTags()).willReturn(tagWrapper);
        given(tag.getName()).willReturn(tagName);
        given(tag2.getName()).willReturn(tagName2);
        List<TagWrapper> tags = asList(tag, tag2);
        given(tagWrapper.getItems()).willReturn(tags);

        // When the spec is transformed
        ContentSpec result = CSTransformer.transform(specWrapper, providerFactory, true);

        // Then the tags should be set on the resulting spec
        assertTrue(result.getTags().contains(tagName));
        assertTrue(result.getTags().contains(tagName2));
    }

    @Test
    public void shouldAddTransformedChildTopic() throws Exception {
        // Given a spec with a child topic
        CSNodeWrapper topicNode = createValidTopicMock();
        setChildren(asList(topicNode));
        // And appropriate values for sorting
        given(topicNode.getNextNode()).willReturn(null);

        // When the spec is transformed
        ContentSpec result = CSTransformer.transform(specWrapper, providerFactory, true);

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
        given(commentNode.getNextNode()).willReturn(null);

        // When the spec is transformed
        ContentSpec result = CSTransformer.transform(specWrapper, providerFactory, true);

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
        given(metaDataNode.getNextNode()).willReturn(null);

        // When the spec is transformed
        ContentSpec result = CSTransformer.transform(specWrapper, providerFactory, true);

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
        given(metaDataNode.getNextNode()).willReturn(null);

        // When the spec is transformed
        ContentSpec result = CSTransformer.transform(specWrapper, providerFactory, false);

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
        given(levelNode.getNextNode()).willReturn(null);

        // When the spec is transformed
        ContentSpec result = CSTransformer.transform(specWrapper, providerFactory, false);

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
        given(levelNode.getId()).willReturn(id);
        given(commentNode.getNextNode()).willReturn(levelNode);
        given(topicNode.getId()).willReturn(anotherId);
        given(levelNode.getNextNode()).willReturn(topicNode);
        given(topicNode.getNextNode()).willReturn(null);

        // When the spec is transformed
        ContentSpec result = CSTransformer.transform(specWrapper, providerFactory, false);

        // Then the nodes should have been added as children of the resulting spec
        // And be in the order expected
        assertThat(result.getNodes().get(1).getClass().equals(Comment.class), is(true));
        assertThat(result.getChildNodes().get(0).getClass().equals(Appendix.class), is(true));
        assertThat(result.getChildNodes().get(1).getClass().equals(TextNode.class), is(true));
        assertThat(result.getChildNodes().get(2).getClass().equals(SpecTopic.class), is(true));
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
        given(levelNode.getNextNode()).willReturn(commentNode);
        given(commentNode.getId()).willReturn(id);
        given(commentNode.getNextNode()).willReturn(null);

        // When the spec is transformed
        ContentSpec result = CSTransformer.transform(specWrapper, providerFactory, false);

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
        given(topicNode.getNextNode()).willReturn(null);

        // When the spec is transformed
        ContentSpec result = CSTransformer.transform(specWrapper, providerFactory, false);

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
        given(levelNode.getNextNode()).willReturn(null);
        // And we are spying on our class under test (as there isn't another good way to check this)
        PowerMockito.spy(CSTransformer.class);

        // When the spec is transformed
        ContentSpec result = CSTransformer.transform(specWrapper, providerFactory, true);

        // Then the transformed level should be a child node on the resulting spec
        assertThat(result.getChildNodes().size(), is(1));
        // And the level should have been added to the spec's processes
        ArgumentCaptor<List> processesCaptor = ArgumentCaptor.forClass(List.class);
        PowerMockito.verifyStatic(times(1));
        CSTransformer.applyRelationships(any(ContentSpec.class), anyMap(), anyMap(), anyList(), processesCaptor.capture(),
                any(DataProviderFactory.class));
        assertThat(processesCaptor.getValue().size(), is(1));
        Level expectedLevel = (Level) processesCaptor.getValue().get(0);
        assertThat(expectedLevel.getUniqueId(), is(id.toString()));
    }

    void setChildren(List<CSNodeWrapper> nodeList) {
        given(specWrapper.getChildren()).willReturn(nodeCollectionWrapper);
        given(nodeCollectionWrapper.getItems()).willReturn(nodeList);
    }
}

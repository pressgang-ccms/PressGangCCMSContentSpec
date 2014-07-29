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

package org.jboss.pressgang.ccms.contentspec.utils;

import static com.google.common.collect.Maps.newHashMap;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.jboss.pressgang.ccms.utils.constants.CommonConstants.CS_RELATIONSHIP_MODE_ID;
import static org.jboss.pressgang.ccms.utils.constants.CommonConstants.CS_RELATIONSHIP_MODE_TARGET;
import static org.jboss.pressgang.ccms.utils.constants.CommonConstants.CS_RELATIONSHIP_PREREQUISITE;
import static org.jboss.pressgang.ccms.utils.constants.CommonConstants.CS_RELATIONSHIP_REFER_TO;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.internal.matchers.StringContains.containsString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.sf.ipsedixit.annotation.Arbitrary;
import net.sf.ipsedixit.annotation.ArbitraryString;
import net.sf.ipsedixit.core.StringType;
import org.jboss.pressgang.ccms.contentspec.ContentSpec;
import org.jboss.pressgang.ccms.contentspec.Level;
import org.jboss.pressgang.ccms.contentspec.Node;
import org.jboss.pressgang.ccms.contentspec.Process;
import org.jboss.pressgang.ccms.contentspec.SpecTopic;
import org.jboss.pressgang.ccms.contentspec.enums.LevelType;
import org.jboss.pressgang.ccms.contentspec.enums.RelationshipType;
import org.jboss.pressgang.ccms.provider.DataProviderFactory;
import org.jboss.pressgang.ccms.provider.ServerSettingsProvider;
import org.jboss.pressgang.ccms.provider.TopicProvider;
import org.jboss.pressgang.ccms.wrapper.CSNodeWrapper;
import org.jboss.pressgang.ccms.wrapper.CSRelatedNodeWrapper;
import org.jboss.pressgang.ccms.wrapper.collection.UpdateableCollectionWrapper;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

/**
 * @author kamiller@redhat.com (Katie Miller)
 */
@PrepareForTest({ContentSpecUtilities.class})
public class CSTransformerRelationshipsTest extends CSTransformerTest {

    @ArbitraryString(type = StringType.ALPHANUMERIC) String title;
    @ArbitraryString(type = StringType.ALPHANUMERIC) String targetId;
    @Arbitrary Integer relationshipFromNodeId;
    @Arbitrary Integer relatedNodeWrapperId;
    @Arbitrary Integer relatedNodeWrapperId2;
    @Arbitrary Integer relatedNodeWrapperId3;
    @Mock ContentSpec contentSpec;
    @Mock DataProviderFactory providerFactory;
    @Mock Node node;
    @Mock Map<Integer, Node> nodes;
    @Mock CSNodeWrapper relationshipFromNode;
    @Mock UpdateableCollectionWrapper<CSRelatedNodeWrapper> relatedToNodeCollection;
    @Mock SpecTopic fromNodeSpecTopic;
    @Mock CSRelatedNodeWrapper relatedNodeWrapper;
    @Mock List<SpecTopic> toSpecTopics;
    List<CSNodeWrapper> relationshipFromNodes = new ArrayList<CSNodeWrapper>();
    Map<String, SpecTopic> targetTopics = newHashMap();
    List<Process> processes = new ArrayList<Process>();

    @Test
    public void shouldCreateUniqueIdMap() throws Exception {
        // Given a transformed content spec
        PowerMockito.mockStatic(ContentSpecUtilities.class);

        // When relationships are applied
        CSTransformer.applyRelationships(contentSpec, nodes, targetTopics, relationshipFromNodes, processes, providerFactory);

        // Then a unique id map should be created based on the spec
        PowerMockito.verifyStatic(Mockito.times(1));
        ContentSpecUtilities.getUniqueIdSpecTopicMap(contentSpec);
    }

    @Test
    public void shouldApplyProcessRelationships() throws Exception {
        // Given a transformed content spec that had a process
        Process process = mock(Process.class);
        List<Process> processList = asList(process);

        // When relationships are applied
        CSTransformer.applyRelationships(contentSpec, nodes, targetTopics, relationshipFromNodes, processList,
                providerFactory);

        // Then the process should have its relationships applied
        verify(process, times(1)).processTopics(anyMap(), anyMap(), any(TopicProvider.class), any(ServerSettingsProvider.class));
    }

    @Test
    public void shouldNotAddRelationshipsForNodeWithoutAny() throws Exception {
        // Given a transformed content spec with a single relationshipFromNode
        // And no relationships to process
        // And that relationships are sorted with Collections.sort()
        PowerMockito.mockStatic(Collections.class);
        SpecTopic specTopicFromNode = setUpSpecTopicMock();
        given(nodes.get(any(Integer.class))).willReturn(specTopicFromNode);

        // When the relationships are applied
        CSTransformer.applyRelationships(contentSpec, nodes, targetTopics, relationshipFromNodes, processes, providerFactory);

        // Then no relationships are added
        assertThat(specTopicFromNode.getRelationships().size() == 0, is(true));
    }

    @Test
    public void shouldThrowExceptionIfRelatedNodeDoesNotExistInSpec() throws Exception {
        // Given a transformed content spec and a related node that does not exist in the spec
        given(relationshipFromNode.getRelatedToNodes()).willReturn(relatedToNodeCollection);
        given(relatedToNodeCollection.getItems()).willReturn(asList(relatedNodeWrapper));
        List<CSNodeWrapper> fromNodes = asList(relationshipFromNode);

        // When the relationships are applied
        try {
            CSTransformer.applyRelationships(contentSpec, nodes, targetTopics, fromNodes, processes, providerFactory);

            // Then an exception is thrown with an appropriate error message
            fail("IllegalStateException expected but not thrown");
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), containsString("The related node does not exist in the content specification"));
        }
    }

    @Test
    public void shouldAddRelationshipToLevel() throws Exception {
        // Given a transformed content spec and a related level node with a target id
        List<CSNodeWrapper> fromNodes = setUpFromNodes();
        setUpRelatedToNodes(relatedNodeWrapper, CS_RELATIONSHIP_REFER_TO, CS_RELATIONSHIP_MODE_TARGET);
        given(nodes.get(relationshipFromNodeId)).willReturn(fromNodeSpecTopic);

        Level level = setUpLevelMock(title, LevelType.BASE, relatedNodeWrapperId);
        level.setTargetId(targetId);

        // When the relationships are applied
        CSTransformer.applyRelationships(contentSpec, nodes, targetTopics, fromNodes, processes, providerFactory);

        // Then the level is added
        verify(fromNodeSpecTopic, times(1)).addRelationshipToTarget(level, RelationshipType.REFER_TO, title);
    }

    @Test
    public void shouldEnsureRelatedLevelsHaveTargetId() throws Exception {
        // Given a transformed content spec and a related level node without a target id
        List<CSNodeWrapper> fromNodes = setUpFromNodes();
        setUpRelatedToNodes(relatedNodeWrapper, CS_RELATIONSHIP_PREREQUISITE, CS_RELATIONSHIP_MODE_TARGET);
        given(nodes.get(relationshipFromNodeId)).willReturn(fromNodeSpecTopic);

        Level level = setUpLevelMock(title, LevelType.BASE, relatedNodeWrapperId);

        // When the relationships are applied
        CSTransformer.applyRelationships(contentSpec, nodes, targetTopics, fromNodes, processes, providerFactory);

        // Then the level is given the target id expected
        assertThat(level.getTargetId(), is("T00" + relatedNodeWrapperId));
        // And the level is added
        verify(fromNodeSpecTopic, times(1)).addRelationshipToTarget(level, RelationshipType.PREREQUISITE, title);
    }


    @Test
    public void shouldAddRelationshipToUnduplicatedSpecTopic() throws Exception {
        // Given a transformed content spec and a related content spec node that is not duplicated
        List<CSNodeWrapper> fromNodes = setUpFromNodes();
        setUpRelatedToNodes(relatedNodeWrapper, CS_RELATIONSHIP_REFER_TO, CS_RELATIONSHIP_MODE_ID);
        given(nodes.get(relationshipFromNodeId)).willReturn(fromNodeSpecTopic);

        SpecTopic specTopic = setUpSpecTopicMock();

        // When the relationships are applied
        CSTransformer.applyRelationships(contentSpec, nodes, targetTopics, fromNodes, processes, providerFactory);

        // Then the spec topic is added, referenced directly
        verify(fromNodeSpecTopic, times(1)).addRelationshipToTopic(specTopic, RelationshipType.REFER_TO, title);
    }

    @Test
    public void shouldAddRelationshipToSpecTopicWithTarget() throws Exception {
        // Given a transformed content spec and a related content spec node that is duplicated and has a target
        List<CSNodeWrapper> fromNodes = setUpFromNodes();
        setUpRelatedToNodes(relatedNodeWrapper, CS_RELATIONSHIP_REFER_TO, CS_RELATIONSHIP_MODE_TARGET);
        given(nodes.get(relationshipFromNodeId)).willReturn(fromNodeSpecTopic);

        SpecTopic specTopic = setUpSpecTopicMock();
        given(specTopic.getTargetId()).willReturn(targetId);
        makeSpecTopicDuplicated();

        // When the relationships are applied
        CSTransformer.applyRelationships(contentSpec, nodes, targetTopics, fromNodes, processes, providerFactory);

        // Then the spec topic is added as a target
        verify(fromNodeSpecTopic, times(1)).addRelationshipToTarget(specTopic, RelationshipType.REFER_TO, title);
    }

    @Test
    public void shouldSortRelatedNodesAsExpected() throws Exception {
        // Given a node with relationships
        List<CSNodeWrapper> fromNodes = setUpFromNodes();

        given(relatedNodeWrapper.getId()).willReturn(relatedNodeWrapperId);
        given(relatedNodeWrapper.getRelationshipType()).willReturn(CS_RELATIONSHIP_REFER_TO);
        given(relatedNodeWrapper.getRelationshipId()).willReturn(null);

        CSRelatedNodeWrapper relatedNodeWrapper2 = mock(CSRelatedNodeWrapper.class);
        given(relatedNodeWrapper2.getId()).willReturn(relatedNodeWrapperId2);
        given(relatedNodeWrapper2.getRelationshipType()).willReturn(CS_RELATIONSHIP_REFER_TO);
        given(relatedNodeWrapper.getRelationshipId()).willReturn(1);

        CSRelatedNodeWrapper relatedNodeWrapper3 = mock(CSRelatedNodeWrapper.class);
        given(relatedNodeWrapper3.getId()).willReturn(relatedNodeWrapperId3);
        given(relatedNodeWrapper3.getRelationshipType()).willReturn(CS_RELATIONSHIP_REFER_TO);
        given(relatedNodeWrapper.getRelationshipId()).willReturn(-1);

        given(relatedToNodeCollection.getItems()).willReturn(asList(relatedNodeWrapper3, relatedNodeWrapper, relatedNodeWrapper2));

        SpecTopic specTopicFromNode = new SpecTopic(relationshipFromNodeId, title);
        given(nodes.get(relationshipFromNodeId)).willReturn(specTopicFromNode);

        setUpLevelMock(title, LevelType.SECTION, relatedNodeWrapperId);
        setUpLevelMock(title, LevelType.BASE, relatedNodeWrapperId2);
        setUpLevelMock(title, LevelType.APPENDIX, relatedNodeWrapperId3);

        // When the relationships are applied
        CSTransformer.applyRelationships(contentSpec, nodes, targetTopics, fromNodes, processes, providerFactory);

        // Then the nodes are sorted and added as expected
        assertThat(specTopicFromNode.getRelationships().size() == 3, is(true));
        assertThat(specTopicFromNode.getRelationships().get(0).getSecondaryRelationshipId().equals("T00" + relatedNodeWrapperId3),
                is(true));
        assertThat(specTopicFromNode.getRelationships().get(1).getSecondaryRelationshipId().equals("T00" + relatedNodeWrapperId),
                is(true));
        assertThat(specTopicFromNode.getRelationships().get(2).getSecondaryRelationshipId().equals("T00" + relatedNodeWrapperId2),
                is(true));
    }

    private void setUpRelatedToNodes(CSRelatedNodeWrapper relatedNodeWrapper, Integer relationshipType, Integer relationshipMode) {
        given(relatedNodeWrapper.getId()).willReturn(relatedNodeWrapperId);
        given(relatedNodeWrapper.getRelationshipType()).willReturn(relationshipType);
        given(relatedNodeWrapper.getRelationshipMode()).willReturn(relationshipMode);
        given(relatedToNodeCollection.getItems()).willReturn(asList(relatedNodeWrapper));
    }

    private List<CSNodeWrapper> setUpFromNodes() {
        given(relationshipFromNode.getId()).willReturn(relationshipFromNodeId);
        given(relationshipFromNode.getRelatedToNodes()).willReturn(relatedToNodeCollection);
        return asList(relationshipFromNode);
    }

    private Level setUpLevelMock(String title, LevelType levelType, Integer relatedNodeWrapperId) {
        Level level = new Level(title, levelType);
        given(nodes.get(relatedNodeWrapperId)).willReturn(level);
        return level;
    }

    private void makeSpecTopicDuplicated() {
        given(toSpecTopics.size()).willReturn(2);
    }

    private SpecTopic setUpSpecTopicMock() {
        SpecTopic specTopic = mock(SpecTopic.class);
        given(specTopic.getTitle()).willReturn(title);
        given(nodes.get(relatedNodeWrapperId)).willReturn(specTopic);
        return specTopic;
    }
}

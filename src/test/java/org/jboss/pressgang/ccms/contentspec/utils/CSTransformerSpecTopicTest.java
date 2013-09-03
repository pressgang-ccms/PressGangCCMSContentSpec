package org.jboss.pressgang.ccms.contentspec.utils;

import static com.google.common.collect.Maps.newHashMap;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.ipsedixit.annotation.Arbitrary;
import net.sf.ipsedixit.annotation.ArbitraryString;
import net.sf.ipsedixit.core.StringType;
import org.jboss.pressgang.ccms.contentspec.Node;
import org.jboss.pressgang.ccms.contentspec.SpecTopic;
import org.jboss.pressgang.ccms.utils.constants.CommonConstants;
import org.jboss.pressgang.ccms.wrapper.CSNodeWrapper;
import org.jboss.pressgang.ccms.wrapper.CSRelatedNodeWrapper;
import org.jboss.pressgang.ccms.wrapper.collection.UpdateableCollectionWrapper;
import org.junit.Test;
import org.mockito.Mock;

/**
 * @author kamiller@redhat.com (Katie Miller)
 */
public class CSTransformerSpecTopicTest extends CSTransformerTest {

    @Arbitrary Integer id;
    @Arbitrary Integer entityId;
    @Arbitrary Integer revision;
    @Arbitrary String condition;
    @Arbitrary String targetId;
    @ArbitraryString(type = StringType.ALPHANUMERIC) String title;
    @Mock CSNodeWrapper nodeWrapper;
    @Mock UpdateableCollectionWrapper<CSRelatedNodeWrapper> collectionWrapper;
    @Mock List<CSRelatedNodeWrapper> items;
    List<CSNodeWrapper> relationshipFromNodes = new ArrayList<CSNodeWrapper>();
    Map<Integer, Node> nodes = newHashMap();
    Map<String, SpecTopic> targetTopics = newHashMap();

    @Test
    public void shouldThrowExceptionIfNodeNotSpecTopic() throws Exception {
        // Given a node that is not a spec topic
        given(nodeWrapper.getNodeType()).willReturn(CommonConstants.CS_NODE_COMMENT);

        // When transformSpecTopic is called
        try {
            CSTransformer.transformSpecTopic(nodeWrapper, nodes, targetTopics, relationshipFromNodes);

            // Then an IllegalArgumentException is thrown
            fail(ILLEGAL_ARG_EX_MISSING);
        } catch (IllegalArgumentException e) {
            // And an appropriate error message is included
            assertThat(e.getMessage(), containsString("The passed node is not a Spec Topic"));
        }
    }

    @Test
    public void shouldTransformValidSpecTopic() throws Exception {
        // Given a valid spec topic with no related nodes and a target id
        mockValidSpecTopicWrapper();

        // When transformSpecTopic is called
        SpecTopic result = CSTransformer.transformSpecTopic(nodeWrapper, nodes, targetTopics, relationshipFromNodes);

        // Then all values are transformed as expected
        assertThat(result.getDBId(), is(nodeWrapper.getEntityId()));
        assertThat(result.getTitle(), is(nodeWrapper.getTitle()));
        assertThat(result.getRevision(), is(nodeWrapper.getEntityRevision()));
        assertThat(result.getConditionStatement(), is(nodeWrapper.getCondition()));
        assertThat(result.getTargetId(), is(nodeWrapper.getTargetId()));
        assertThat(result.getUniqueId(), is(nodeWrapper.getId().toString()));

        // And the target should be added to the target list
        assertThat(targetTopics.get(result.getTargetId()), is(result));

        // And the node should not have been added to relationshipFromNodes
        assertThat(relationshipFromNodes.contains(nodeWrapper), is(false));
    }

    @Test
    public void shouldAddNodeRelationship() throws Exception {
        // Given a valid spec topic with related nodes
        mockValidSpecTopicWrapper();
        given(nodeWrapper.getRelatedToNodes()).willReturn(collectionWrapper);
        given(collectionWrapper.getItems()).willReturn(items);
        given(items.isEmpty()).willReturn(false);

        // When transformSpecTopic is called
        CSTransformer.transformSpecTopic(nodeWrapper, nodes, targetTopics, relationshipFromNodes);

        // Then the node should be added to relationshipFromNodes
        assertThat(relationshipFromNodes.contains(nodeWrapper), is(true));
    }

    @Test
    public void shouldNotAddNodeRelationshipIfRelatedCollectionEmpty() throws Exception {
        // Given a valid spec topic with an empty related nodes collection
        mockValidSpecTopicWrapper();
        given(nodeWrapper.getRelatedToNodes()).willReturn(collectionWrapper);
        given(collectionWrapper.getItems()).willReturn(items);
        given(items.isEmpty()).willReturn(true);

        // When transformSpecTopic is called
        CSTransformer.transformSpecTopic(nodeWrapper, nodes, targetTopics, relationshipFromNodes);

        // Then the node should not be added to relationshipFromNodes
        assertThat(relationshipFromNodes.contains(nodeWrapper), is(false));
    }

    @Test
    public void shouldNotAddTargetsIfThereAreNone() throws Exception {
        // Given a valid spec topic with no target id
        given(nodeWrapper.getTargetId()).willReturn(null);
        // And an empty targetTopics list

        // When transformSpecTopic is called
        CSTransformer.transformSpecTopic(nodeWrapper, nodes, targetTopics, relationshipFromNodes);

        // Then no target should be added
        assertThat(targetTopics.size(), is(0));
    }

    @Test
    public void shouldTransformNullValuesAsNull() throws Exception {
        // Given a spec topic
        given(nodeWrapper.getNodeType()).willReturn(CommonConstants.CS_NODE_TOPIC);
        given(nodeWrapper.getEntityId()).willReturn(entityId);
        // With some null values
        given(nodeWrapper.getTitle()).willReturn(null);
        given(nodeWrapper.getEntityRevision()).willReturn(null);
        given(nodeWrapper.getCondition()).willReturn(null);
        given(nodeWrapper.getId()).willReturn(null);

        // When transformSpecTopic is called
        SpecTopic result = CSTransformer.transformSpecTopic(nodeWrapper, nodes, targetTopics, relationshipFromNodes);

        // Then the corresponding values should be null
        assertThat(result.getTitle() == null, is(true));
        assertThat(result.getRevision() == null, is(true));
        assertThat(result.getConditionStatement() == null, is(true));
        assertThat(result.getUniqueId() == null, is(true));
    }

    public void mockValidSpecTopicWrapper() {
        given(nodeWrapper.getNodeType()).willReturn(CommonConstants.CS_NODE_TOPIC);
        given(nodeWrapper.getEntityId()).willReturn(entityId);
        given(nodeWrapper.getTitle()).willReturn(title);
        given(nodeWrapper.getRevision()).willReturn(revision);
        given(nodeWrapper.getCondition()).willReturn(condition);
        given(nodeWrapper.getTargetId()).willReturn(targetId);
        given(nodeWrapper.getId()).willReturn(id);
    }
}

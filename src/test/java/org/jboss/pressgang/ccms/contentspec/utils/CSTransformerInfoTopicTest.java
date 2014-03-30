package org.jboss.pressgang.ccms.contentspec.utils;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import net.sf.ipsedixit.annotation.Arbitrary;
import org.jboss.pressgang.ccms.contentspec.InfoTopic;
import org.jboss.pressgang.ccms.wrapper.CSInfoNodeWrapper;
import org.junit.Test;
import org.mockito.Mock;

public class CSTransformerInfoTopicTest extends CSTransformerTest {

    @Arbitrary Integer id;
    @Arbitrary Integer entityId;
    @Arbitrary Integer revision;
    @Arbitrary String condition;
    @Arbitrary String targetId;
    @Mock CSInfoNodeWrapper nodeWrapper;

    @Test
    public void shouldTransformValidSpecTopic() throws Exception {
        // Given a valid info topic with no related nodes and a target id
        mockValidInfoTopicWrapper();

        // When transformInfoTopic is called
        InfoTopic result = CSTransformer.transformInfoTopic(nodeWrapper);

        // Then all values are transformed as expected
        assertThat(result.getDBId(), is(nodeWrapper.getTopicId()));
        assertThat(result.getRevision(), is(nodeWrapper.getTopicRevision()));
        assertThat(result.getConditionStatement(), is(nodeWrapper.getCondition()));
        assertThat(result.getUniqueId(), is(nodeWrapper.getId().toString()));
    }

    @Test
    public void shouldTransformNullValuesAsNull() throws Exception {
        // Given an info topic
        given(nodeWrapper.getTopicId()).willReturn(entityId);
        // With some null values
        given(nodeWrapper.getTopicRevision()).willReturn(null);
        given(nodeWrapper.getCondition()).willReturn(null);
        given(nodeWrapper.getId()).willReturn(null);

        // When transformInfoTopic is called
        InfoTopic result = CSTransformer.transformInfoTopic(nodeWrapper);

        // Then the corresponding values should be null
        assertThat(result.getRevision() == null, is(true));
        assertThat(result.getConditionStatement() == null, is(true));
        assertThat(result.getUniqueId() == null, is(true));
    }

    public void mockValidInfoTopicWrapper() {
        given(nodeWrapper.getTopicId()).willReturn(entityId);
        given(nodeWrapper.getTopicRevision()).willReturn(revision);
        given(nodeWrapper.getCondition()).willReturn(condition);
        given(nodeWrapper.getId()).willReturn(id);
    }
}

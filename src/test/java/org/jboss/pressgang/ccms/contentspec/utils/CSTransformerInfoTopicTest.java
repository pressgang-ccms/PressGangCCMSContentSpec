package org.jboss.pressgang.ccms.contentspec.utils;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import net.sf.ipsedixit.annotation.Arbitrary;
import org.jboss.pressgang.ccms.contentspec.InfoTopic;
import org.jboss.pressgang.ccms.wrapper.CSInfoNodeWrapper;
import org.jboss.pressgang.ccms.wrapper.CSNodeWrapper;
import org.junit.Test;
import org.mockito.Mock;

public class CSTransformerInfoTopicTest extends CSTransformerTest {

    @Arbitrary Integer id;
    @Arbitrary Integer id2;
    @Arbitrary Integer entityId;
    @Arbitrary Integer revision;
    @Arbitrary String condition;
    @Arbitrary String targetId;
    @Mock CSInfoNodeWrapper infoNodeWrapper;
    @Mock CSNodeWrapper nodeWrapper;

    @Test
    public void shouldTransformValidSpecTopic() throws Exception {
        // Given a valid info topic with no related nodes and a target id
        mockValidInfoTopicWrapper();

        // When transformInfoTopic is called
        InfoTopic result = CSTransformer.transformInfoTopic(nodeWrapper, infoNodeWrapper);

        // Then all values are transformed as expected
        assertThat(result.getDBId(), is(infoNodeWrapper.getTopicId()));
        assertThat(result.getRevision(), is(infoNodeWrapper.getTopicRevision()));
        assertThat(result.getConditionStatement(), is(infoNodeWrapper.getCondition()));
        assertThat(result.getUniqueId(), is(infoNodeWrapper.getId().toString()));
    }

    @Test
    public void shouldTransformNullValuesAsNull() throws Exception {
        // Given an info topic
        given(infoNodeWrapper.getTopicId()).willReturn(entityId);
        // With some null values
        given(infoNodeWrapper.getTopicRevision()).willReturn(null);
        given(infoNodeWrapper.getCondition()).willReturn(null);
        given(infoNodeWrapper.getId()).willReturn(null);
        given(nodeWrapper.getId()).willReturn(null);

        // When transformInfoTopic is called
        InfoTopic result = CSTransformer.transformInfoTopic(nodeWrapper, infoNodeWrapper);

        // Then the corresponding values should be null
        assertThat(result.getRevision() == null, is(true));
        assertThat(result.getConditionStatement() == null, is(true));
        assertThat(result.getUniqueId() == null, is(true));
    }

    public void mockValidInfoTopicWrapper() {
        given(nodeWrapper.getId()).willReturn(id2);
        given(infoNodeWrapper.getTopicId()).willReturn(entityId);
        given(infoNodeWrapper.getTopicRevision()).willReturn(revision);
        given(infoNodeWrapper.getCondition()).willReturn(condition);
        given(infoNodeWrapper.getId()).willReturn(id);
    }
}

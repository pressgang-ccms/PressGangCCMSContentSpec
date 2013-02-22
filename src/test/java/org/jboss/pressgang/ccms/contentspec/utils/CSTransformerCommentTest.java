package org.jboss.pressgang.ccms.contentspec.utils;

import org.jboss.pressgang.ccms.contentspec.wrapper.CSNodeWrapper;
import org.jboss.pressgang.ccms.utils.constants.CommonConstants;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.internal.matchers.StringContains.containsString;
import static org.mockito.BDDMockito.given;

/**
 * @author kamiller@redhat.com (Katie Miller)
 */
public class CSTransformerCommentTest extends CSTransformerTest {

    @Mock CSNodeWrapper nodeWrapper;

    @Test
    public void shouldThrowExceptionIfNodeNotComment() throws Exception {
        // Given a node that is not a comment
        given(nodeWrapper.getNodeType()).willReturn(CommonConstants.CS_NODE_TOPIC);

        // When tranformComment is called
        try {
            transformer.transformComment(nodeWrapper);

            // Then an exception is thrown with an appropriate error
            fail("IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), containsString("The passed node is not a Comment"));
        }
    }
}

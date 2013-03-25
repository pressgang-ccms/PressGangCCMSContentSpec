package org.jboss.pressgang.ccms.contentspec.utils;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.internal.matchers.StringContains.containsString;
import static org.mockito.BDDMockito.given;

import net.sf.ipsedixit.annotation.ArbitraryString;
import net.sf.ipsedixit.core.StringType;
import org.jboss.pressgang.ccms.contentspec.Comment;
import org.jboss.pressgang.ccms.utils.constants.CommonConstants;
import org.jboss.pressgang.ccms.wrapper.CSNodeWrapper;
import org.junit.Test;
import org.mockito.Mock;

/**
 * @author kamiller@redhat.com (Katie Miller)
 */
public class CSTransformerCommentTest extends CSTransformerTest {

    @ArbitraryString(type = StringType.ALPHANUMERIC) String text;
    @Mock CSNodeWrapper nodeWrapper;

    @Test
    public void shouldThrowExceptionIfNodeNotComment() throws Exception {
        // Given a node that is not a comment
        given(nodeWrapper.getNodeType()).willReturn(CommonConstants.CS_NODE_TOPIC);

        // When tranformComment is called
        try {
            CSTransformer.transformComment(nodeWrapper);

            // Then an exception is thrown with an appropriate error
            fail(ILLEGAL_ARG_EX_MISSING);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), containsString("The passed node is not a Comment"));
        }
    }

    @Test
    public void shouldAddTextToComment() throws Exception {
        // Given a node that is a comment
        given(nodeWrapper.getNodeType()).willReturn(CommonConstants.CS_NODE_COMMENT);
        // And that has additional text
        given(nodeWrapper.getAdditionalText()).willReturn(text);

        // When tranformComment is called
        Comment result = CSTransformer.transformComment(nodeWrapper);

        // Then the comment should be returned with the text
        assertThat(result.getText(), containsString(text));
    }
}

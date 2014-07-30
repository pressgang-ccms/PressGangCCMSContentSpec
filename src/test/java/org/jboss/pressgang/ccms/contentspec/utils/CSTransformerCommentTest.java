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
        // And that has a title
        given(nodeWrapper.getTitle()).willReturn(text);

        // When tranformComment is called
        Comment result = CSTransformer.transformComment(nodeWrapper);

        // Then the comment should be returned with the text
        assertThat(result.getText(), containsString(text));
    }
}

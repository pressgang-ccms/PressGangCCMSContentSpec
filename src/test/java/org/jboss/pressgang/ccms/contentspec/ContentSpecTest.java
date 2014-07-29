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

package org.jboss.pressgang.ccms.contentspec;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.jboss.pressgang.ccms.contentspec.enums.TopicType;
import org.junit.Before;
import org.junit.Test;

public class ContentSpecTest extends BaseUnitTest {
    private ContentSpec contentSpec;

    @Before
    public void setUp() {
        contentSpec = new ContentSpec();
    }

    @Test
    public void shouldSetRevisionHistoryWhenAppendingRevisionHistoryMetaData() {
        // Given a KeyValueNode that is for a spec topic
        SpecTopic specTopic = new SpecTopic(0, "Revision History");
        KeyValueNode <SpecTopic> revisionHistory = new KeyValueNode<SpecTopic>("Revision History", specTopic);

        // When appending a child node
        contentSpec.appendChild(revisionHistory);

        // Then check that the revision history is set
        assertThat(contentSpec.getRevisionHistory(), is(specTopic));
        // and the revision history topic type is set
        assertThat(contentSpec.getRevisionHistory().getTopicType(), is(TopicType.REVISION_HISTORY));
    }

    @Test
    public void shouldSetLegalNoticeWhenAppendingLegalNoticeMetaData() {
        // Given a KeyValueNode that is for a Legal Notice spec topic
        SpecTopic specTopic = new SpecTopic(0, "Legal Notice");
        KeyValueNode <SpecTopic> legalNotice = new KeyValueNode<SpecTopic>("Legal Notice", specTopic);

        // When appending a child node
        contentSpec.appendChild(legalNotice);

        // Then check that the legal notice is set
        assertThat(contentSpec.getLegalNotice(), is(specTopic));
        // and the legal notice topic type is set
        assertThat(contentSpec.getLegalNotice().getTopicType(), is(TopicType.LEGAL_NOTICE));
    }

    @Test
    public void shouldSetAuthorGroupWhenAppendingAuthorGroupMetaData() {
        // Given a KeyValueNode that is for a Author Group spec topic
        SpecTopic specTopic = new SpecTopic(0, "Author Group");
        KeyValueNode <SpecTopic> authorGroup = new KeyValueNode<SpecTopic>("Author Group", specTopic);

        // When appending a child node
        contentSpec.appendChild(authorGroup);

        // Then check that the author group is set
        assertThat(contentSpec.getAuthorGroup(), is(specTopic));
        // and the author group topic type is set
        assertThat(contentSpec.getAuthorGroup().getTopicType(), is(TopicType.AUTHOR_GROUP));
    }
}

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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;

import net.sf.ipsedixit.annotation.Arbitrary;
import net.sf.ipsedixit.annotation.ArbitraryString;
import net.sf.ipsedixit.core.StringType;
import org.jboss.pressgang.ccms.contentspec.BaseUnitTest;
import org.jboss.pressgang.ccms.wrapper.TopicWrapper;
import org.jboss.pressgang.ccms.wrapper.TranslatedCSNodeWrapper;
import org.jboss.pressgang.ccms.wrapper.TranslatedTopicWrapper;
import org.jboss.pressgang.ccms.wrapper.collection.CollectionWrapper;
import org.junit.Test;
import org.mockito.Mock;

public class EntityUtilitiesTest extends BaseUnitTest {
    @Arbitrary Integer id;
    @Arbitrary Integer id2;
    @Arbitrary Integer id3;
    @Arbitrary Integer revision;
    @ArbitraryString(type = StringType.ALPHA) String locale;
    @Mock TopicWrapper topicWrapper;
    @Mock TranslatedTopicWrapper translatedTopicWrapper;
    @Mock TranslatedTopicWrapper translatedTopicWrapper2;
    @Mock CollectionWrapper<TranslatedTopicWrapper> translatedTopicCollectionWrapper;
    @Mock TranslatedCSNodeWrapper translatedCSNodeWrapper;
    @Mock TranslatedCSNodeWrapper translatedCSNodeWrapper2;

    @Test
    public void shouldFindHighestWhenMatchingIdForPushedTranslatedTopic() {
        // Given
        setUpBaseTopic(topicWrapper);
        setUpBaseTranslatedTopic(translatedTopicWrapper);
        setUpBaseTranslatedTopic(translatedTopicWrapper2);
        given(translatedTopicCollectionWrapper.getItems()).willReturn(Arrays.asList(translatedTopicWrapper, translatedTopicWrapper2));
        given(translatedTopicWrapper2.getTopicRevision()).willReturn(revision - 1);
        given(translatedTopicWrapper2.getTopicRevision()).willReturn(revision);

        // When getting the pushed translated topic
        final TranslatedTopicWrapper result = EntityUtilities.returnPushedTranslatedTopic(topicWrapper);

        // Then the result should match the second entity, since it's revision matches.
        assertEquals(translatedTopicWrapper2, result);
    }

    @Test
    public void shouldFindWhenMatchingIdAndDifferentLocalesForPushedTranslatedTopic() {
        // Given
        setUpBaseTopic(topicWrapper);
        setUpBaseTranslatedTopic(translatedTopicWrapper);
        setUpBaseTranslatedTopic(translatedTopicWrapper2);
        given(translatedTopicCollectionWrapper.getItems()).willReturn(Arrays.asList(translatedTopicWrapper, translatedTopicWrapper2));
        given(translatedTopicWrapper2.getTopicRevision()).willReturn(revision);
        given(translatedTopicWrapper2.getTopicRevision()).willReturn(revision);
        given(translatedTopicWrapper.getLocale()).willReturn("en-US");

        // When getting the pushed translated topic
        final TranslatedTopicWrapper result = EntityUtilities.returnPushedTranslatedTopic(topicWrapper);

        // Then the result should match the second entity, since it's locale matches and the first doesn't
        assertEquals(translatedTopicWrapper2, result);
    }

    @Test
    public void shouldFindMatchingWhenDuplicateExistsWithTranslatedCSNodeForPushedTranslatedTopic() {
        // Given
        setUpBaseTopic(topicWrapper);
        setUpBaseTranslatedTopic(translatedTopicWrapper);
        setUpBaseTranslatedTopic(translatedTopicWrapper2);
        setUpBaseTranslatedCSNode(translatedCSNodeWrapper);
        given(translatedTopicCollectionWrapper.getItems()).willReturn(Arrays.asList(translatedTopicWrapper, translatedTopicWrapper2));
        given(translatedTopicWrapper2.getTopicRevision()).willReturn(revision);
        given(translatedTopicWrapper2.getTopicRevision()).willReturn(revision);
        given(translatedTopicWrapper.getTranslatedCSNode()).willReturn(translatedCSNodeWrapper);

        // When getting the pushed translated topic
        final TranslatedTopicWrapper result = EntityUtilities.returnPushedTranslatedTopic(topicWrapper, translatedCSNodeWrapper);
        final TranslatedTopicWrapper result2 = EntityUtilities.returnPushedTranslatedTopic(topicWrapper, null);

        // Then the one with the translated csnode should be returned
        assertEquals(translatedTopicWrapper, result);
        // and result2 should be the one without the translated csnode
        assertEquals(translatedTopicWrapper2, result2);
    }

    @Test
    public void shouldFindHighestMatchingWhenDuplicateExistsWithTranslatedCSNodeForPushedTranslatedTopic() {
        // Given a topic with two matching translated topics
        setUpBaseTopic(topicWrapper);
        setUpBaseTranslatedTopic(translatedTopicWrapper);
        setUpBaseTranslatedTopic(translatedTopicWrapper2);
        setUpBaseTranslatedCSNode(translatedCSNodeWrapper);
        given(translatedTopicCollectionWrapper.getItems()).willReturn(Arrays.asList(translatedTopicWrapper, translatedTopicWrapper2));
        given(translatedTopicWrapper.getTranslatedCSNode()).willReturn(translatedCSNodeWrapper);
        given(translatedTopicWrapper2.getTranslatedCSNode()).willReturn(translatedCSNodeWrapper);
        // and different revisions
        given(translatedTopicWrapper2.getTopicRevision()).willReturn(revision - 1);
        given(translatedTopicWrapper2.getTopicRevision()).willReturn(revision);

        // When getting the pushed translated topic
        final TranslatedTopicWrapper result = EntityUtilities.returnPushedTranslatedTopic(topicWrapper, translatedCSNodeWrapper);

        // Then the one with the highest revision should be returned
        assertEquals(translatedTopicWrapper2, result);
    }

    @Test
    public void shouldFindMatchingWhenDuplicateExistsWithDifferentTranslatedCSNodeForPushedTranslatedTopic() {
        // Given a topic with two matching translated topics
        setUpBaseTopic(topicWrapper);
        setUpBaseTranslatedTopic(translatedTopicWrapper);
        setUpBaseTranslatedTopic(translatedTopicWrapper2);
        setUpBaseTranslatedCSNode(translatedCSNodeWrapper);
        given(translatedTopicCollectionWrapper.getItems()).willReturn(Arrays.asList(translatedTopicWrapper, translatedTopicWrapper2));
        given(translatedTopicWrapper2.getTopicRevision()).willReturn(revision);
        given(translatedTopicWrapper2.getTopicRevision()).willReturn(revision);
        // and different translated csnodes
        given(translatedTopicWrapper2.getTranslatedCSNode()).willReturn(translatedCSNodeWrapper);
        given(translatedTopicWrapper.getTranslatedCSNode()).willReturn(translatedCSNodeWrapper2);
        given(translatedCSNodeWrapper2.getId()).willReturn(id3);

        // When getting the pushed translated topic
        final TranslatedTopicWrapper result = EntityUtilities.returnPushedTranslatedTopic(topicWrapper, translatedCSNodeWrapper);
        final TranslatedTopicWrapper result2 = EntityUtilities.returnPushedTranslatedTopic(topicWrapper, translatedCSNodeWrapper2);

        // Then the one with the translated csnode should be returned
        assertEquals(translatedTopicWrapper2, result);
        // and result2 should be the one with the other translated csnode
        assertEquals(translatedTopicWrapper, result2);
    }

    @Test
    public void shouldIgnoreHigherRevisionsForPushedTranslatedTopic() {
        // Given
        setUpBaseTopic(topicWrapper);
        setUpBaseTranslatedTopic(translatedTopicWrapper);
        given(translatedTopicCollectionWrapper.getItems()).willReturn(Arrays.asList(translatedTopicWrapper));
        given(translatedTopicWrapper.getTopicRevision()).willReturn(revision + 1);

        // When getting the pushed translated topic
        final TranslatedTopicWrapper result = EntityUtilities.returnPushedTranslatedTopic(topicWrapper, null);

        // Then the result should be null since all are too high
        assertNull(result);
    }

    @Test
    public void shouldReturnNullForTopicWithNoTranslations() {
        // Given a topic with no translations
        setUpBaseTopic(topicWrapper);
        given(topicWrapper.getTranslatedTopics()).willReturn(null);

        // When getting the pushed translated topic
        final TranslatedTopicWrapper result = EntityUtilities.returnPushedTranslatedTopic(topicWrapper, null);

        // Then the result should be null
        assertNull(result);
    }

    private void setUpBaseTopic(final TopicWrapper topicWrapper) {
        given(topicWrapper.getId()).willReturn(id);
        given(topicWrapper.getTopicId()).willReturn(id);
        given(topicWrapper.getRevision()).willReturn(revision);
        given(topicWrapper.getTopicRevision()).willReturn(revision);
        given(topicWrapper.getTranslatedTopics()).willReturn(translatedTopicCollectionWrapper);
        given(topicWrapper.getLocale()).willReturn(locale);
    }

    private void setUpBaseTranslatedTopic(final TranslatedTopicWrapper topicWrapper) {
        given(topicWrapper.getTopicId()).willReturn(id);
        given(topicWrapper.getLocale()).willReturn(locale);
    }

    private void setUpBaseTranslatedCSNode(final TranslatedCSNodeWrapper translatedCSNodeWrapper) {
        given(translatedCSNodeWrapper.getId()).willReturn(id2);
    }
}

package org.jboss.pressgang.ccms.contentspec.utils;

import static com.google.common.collect.Maps.newHashMap;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.jboss.pressgang.ccms.contentspec.TestUtil.createMetaDataMock;
import static org.jboss.pressgang.ccms.contentspec.TestUtil.selectRandomListItem;
import static org.jboss.pressgang.ccms.contentspec.constants.CSConstants.INLINE_INJECTION_TITLE;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.ipsedixit.annotation.Arbitrary;
import net.sf.ipsedixit.annotation.ArbitraryString;
import net.sf.ipsedixit.core.StringType;
import org.hamcrest.Matchers;
import org.jboss.pressgang.ccms.contentspec.KeyValueNode;
import org.jboss.pressgang.ccms.contentspec.Node;
import org.jboss.pressgang.ccms.contentspec.SpecTopic;
import org.jboss.pressgang.ccms.contentspec.constants.CSConstants;
import org.jboss.pressgang.ccms.contentspec.entities.InjectionOptions;
import org.jboss.pressgang.ccms.contentspec.enums.BookType;
import org.jboss.pressgang.ccms.wrapper.CSNodeWrapper;
import org.junit.Test;

/**
 * @author kamiller@redhat.com (Katie Miller)
 */
@SuppressWarnings("unchecked")
public class CSTransformerMetaDataTest extends CSTransformerTest {

    @Arbitrary Integer id;
    @Arbitrary Integer topicId;
    @Arbitrary Integer revision;
    @ArbitraryString(type = StringType.ALPHANUMERIC) String key;
    @ArbitraryString(type = StringType.ALPHANUMERIC) String value;
    List<CSNodeWrapper> relationshipFromNodes = new ArrayList<CSNodeWrapper>();
    Map<Integer, Node> nodes = newHashMap();
    Map<String, SpecTopic> targetTopics = newHashMap();
    Map<String, List<SpecTopic>> specTopicMap = newHashMap();

    @Test
    public void shouldTransformBookMetaData() throws Exception {
        // Given a node with a book type title
        // And a valid book type
        ArrayList<BookType> bookTypes = new ArrayList<BookType>(asList(BookType.values()));
        bookTypes.remove(BookType.INVALID);
        BookType bookType = selectRandomListItem(bookTypes);
        CSNodeWrapper nodeWrapper = createMetaDataMock(CSConstants.BOOK_TYPE_TITLE, bookType.toString());
        given(nodeWrapper.getId()).willReturn(id);

        // When the node metadata is transformed
        KeyValueNode<BookType> result = (KeyValueNode<BookType>) CSTransformer.transformMetaData(nodeWrapper, nodes, specTopicMap,
                targetTopics, relationshipFromNodes);

        // Then the result has a book type key
        assertThat(result.getKey(), is(CSConstants.BOOK_TYPE_TITLE));
        // And the type of book specified as its value
        assertThat(result.getValue(), is(bookType));
        // And the unique id was set
        assertThat(result.getUniqueId(), is(id.toString()));
    }

    @Test
    public void shouldTransFormBookMetaDataWithInvalidType() throws Exception {
        // Given a node with a book type title
        // And an invalid book type
        CSNodeWrapper nodeWrapper = createMetaDataMock(CSConstants.BOOK_TYPE_TITLE, "foo");

        // When the node metadata is transformed
        KeyValueNode<BookType> result = (KeyValueNode<BookType>) CSTransformer.transformMetaData(nodeWrapper, nodes, specTopicMap,
                targetTopics, relationshipFromNodes);

        // Then the result has a book type key
        assertThat(result.getKey(), is(CSConstants.BOOK_TYPE_TITLE));
        // And the type of book specified as its value is invalid
        assertThat(result.getValue(), is(BookType.INVALID));
    }

    @Test
    public void shouldTransformInjectionMetaData() throws Exception {
        // Given a node with an inline injection type title
        // And a valid injection
        CSNodeWrapper nodeWrapper = createMetaDataMock(INLINE_INJECTION_TITLE, "[topicType]");
        given(nodeWrapper.getId()).willReturn(id);

        // When the node metadata is transformed
        KeyValueNode<InjectionOptions> result = (KeyValueNode<InjectionOptions>) CSTransformer.transformMetaData(nodeWrapper, nodes,
                specTopicMap, targetTopics, relationshipFromNodes);

        // Then the result has an inline injection type key
        assertThat(result.getKey(), is(INLINE_INJECTION_TITLE));
        // And the value is the injection options
        assertThat(result.getValue().getStrictTopicTypes(), Matchers.contains("topicType"));
        // And the unique id was set
        assertThat(result.getUniqueId(), is(id.toString()));
    }

    @Test
    public void shouldTransformArbitraryMetaData() throws Exception {
        // Given a node with a title that is not an inline injection or book type
        // And some string value
        CSNodeWrapper nodeWrapper = createMetaDataMock(key, value);
        given(nodeWrapper.getId()).willReturn(id);

        // When the node metadata is transformed
        KeyValueNode<String> result = (KeyValueNode<String>) CSTransformer.transformMetaData(nodeWrapper, nodes, specTopicMap, targetTopics,
                relationshipFromNodes);

        // Then the result has the key given
        assertThat(result.getKey(), is(key));
        // And the value is the string given
        assertThat(result.getValue(), is(value));
        // And the unique id was set
        assertThat(result.getUniqueId(), is(id.toString()));
    }

    @Test
    public void shouldTransformSpecTopicMetaData() throws Exception {
        // Given a node with a revision history title
        // And a valid value
        CSNodeWrapper nodeWrapper = createMetaDataMock(CSConstants.REV_HISTORY_TITLE, "[" + topicId + ", rev: " + revision + ", " +
                "condition=" + value + "]");
        given(nodeWrapper.getId()).willReturn(id);
        given(nodeWrapper.getEntityId()).willReturn(topicId);
        given(nodeWrapper.getEntityRevision()).willReturn(revision);
        given(nodeWrapper.getCondition()).willReturn(value);

        // When the node metadata is transformed
        KeyValueNode<SpecTopic> result = (KeyValueNode<SpecTopic>) CSTransformer.transformMetaData(nodeWrapper, nodes, specTopicMap,
                targetTopics, relationshipFromNodes);

        // Then the result has a revision history key
        assertThat(result.getKey(), is(CSConstants.REV_HISTORY_TITLE));
        // And the value matches the spec topic
        assertThat(result.getValue().getId(), is(topicId.toString()));
        assertThat(result.getValue().getRevision(), is(revision));
        assertThat(result.getValue().getConditionStatement(false), is(value));
        // And the unique id was set
        assertThat(result.getUniqueId(), is(id.toString()));
    }
}

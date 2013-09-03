package org.jboss.pressgang.ccms.contentspec.utils;

import static com.google.common.collect.Maps.newHashMap;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.jboss.pressgang.ccms.contentspec.TestUtil.createMetaDataMock;
import static org.jboss.pressgang.ccms.contentspec.TestUtil.createValidFileMock;
import static org.jboss.pressgang.ccms.contentspec.TestUtil.selectRandomListItem;
import static org.jboss.pressgang.ccms.utils.constants.CommonConstants.CS_INLINE_INJECTION_TITLE;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.sf.ipsedixit.annotation.Arbitrary;
import net.sf.ipsedixit.annotation.ArbitraryString;
import net.sf.ipsedixit.core.StringType;
import org.hamcrest.Matchers;
import org.jboss.pressgang.ccms.contentspec.FileList;
import org.jboss.pressgang.ccms.contentspec.KeyValueNode;
import org.jboss.pressgang.ccms.contentspec.Node;
import org.jboss.pressgang.ccms.contentspec.SpecTopic;
import org.jboss.pressgang.ccms.contentspec.entities.InjectionOptions;
import org.jboss.pressgang.ccms.contentspec.enums.BookType;
import org.jboss.pressgang.ccms.utils.constants.CommonConstants;
import org.jboss.pressgang.ccms.wrapper.CSNodeWrapper;
import org.jboss.pressgang.ccms.wrapper.collection.UpdateableCollectionWrapper;
import org.junit.Test;

/**
 * @author kamiller@redhat.com (Katie Miller)
 */
@SuppressWarnings("unchecked")
public class CSTransformerMetaDataTest extends CSTransformerTest {

    @Arbitrary Integer id;
    @Arbitrary Integer id2;
    @Arbitrary Integer topicId;
    @Arbitrary Integer revision;
    @ArbitraryString(type = StringType.ALPHANUMERIC) String key;
    @ArbitraryString(type = StringType.ALPHANUMERIC) String value;
    @ArbitraryString String title;
    List<CSNodeWrapper> relationshipFromNodes = new ArrayList<CSNodeWrapper>();
    Map<Integer, Node> nodes = newHashMap();
    Map<String, SpecTopic> targetTopics = newHashMap();

    @Test
    public void shouldTransformBookMetaData() throws Exception {
        // Given a node with a book type title
        // And a valid book type
        ArrayList<BookType> bookTypes = new ArrayList<BookType>(asList(BookType.values()));
        bookTypes.remove(BookType.INVALID);
        BookType bookType = selectRandomListItem(bookTypes);
        CSNodeWrapper nodeWrapper = createMetaDataMock(CommonConstants.CS_BOOK_TYPE_TITLE, bookType.toString());
        given(nodeWrapper.getId()).willReturn(id);

        // When the node metadata is transformed
        KeyValueNode<BookType> result = (KeyValueNode<BookType>) CSTransformer.transformMetaData(nodeWrapper, nodes, targetTopics,
                relationshipFromNodes);

        // Then the result has a book type key
        assertThat(result.getKey(), is(CommonConstants.CS_BOOK_TYPE_TITLE));
        // And the type of book specified as its value
        assertThat(result.getValue(), is(bookType));
        // And the unique id was set
        assertThat(result.getUniqueId(), is(id.toString()));
    }

    @Test
    public void shouldTransFormBookMetaDataWithInvalidType() throws Exception {
        // Given a node with a book type title
        // And an invalid book type
        CSNodeWrapper nodeWrapper = createMetaDataMock(CommonConstants.CS_BOOK_TYPE_TITLE, "foo");

        // When the node metadata is transformed
        KeyValueNode<BookType> result = (KeyValueNode<BookType>) CSTransformer.transformMetaData(nodeWrapper, nodes, targetTopics,
                relationshipFromNodes);

        // Then the result has a book type key
        assertThat(result.getKey(), is(CommonConstants.CS_BOOK_TYPE_TITLE));
        // And the type of book specified as its value is invalid
        assertThat(result.getValue(), is(BookType.INVALID));
    }

    @Test
    public void shouldTransformInjectionMetaData() throws Exception {
        // Given a node with an inline injection type title
        // And a valid injection
        CSNodeWrapper nodeWrapper = createMetaDataMock(CS_INLINE_INJECTION_TITLE, "[topicType]");
        given(nodeWrapper.getId()).willReturn(id);

        // When the node metadata is transformed
        KeyValueNode<InjectionOptions> result = (KeyValueNode<InjectionOptions>) CSTransformer.transformMetaData(nodeWrapper, nodes,
                targetTopics, relationshipFromNodes);

        // Then the result has an inline injection type key
        assertThat(result.getKey(), is(CS_INLINE_INJECTION_TITLE));
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
        KeyValueNode<String> result = (KeyValueNode<String>) CSTransformer.transformMetaData(nodeWrapper, nodes, targetTopics,
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
        CSNodeWrapper nodeWrapper = createMetaDataMock(CommonConstants.CS_REV_HISTORY_TITLE, "[" + topicId + ", rev: " + revision + ", " +
                "condition=" + value + "]");
        given(nodeWrapper.getId()).willReturn(id);
        given(nodeWrapper.getEntityId()).willReturn(topicId);
        given(nodeWrapper.getEntityRevision()).willReturn(revision);
        given(nodeWrapper.getCondition()).willReturn(value);
        given(nodeWrapper.getNodeType()).willReturn(CommonConstants.CS_NODE_META_DATA_TOPIC);

        // When the node metadata is transformed
        KeyValueNode<SpecTopic> result = (KeyValueNode<SpecTopic>) CSTransformer.transformMetaData(nodeWrapper, nodes, targetTopics,
                relationshipFromNodes);

        // Then the result has a revision history key
        assertThat(result.getKey(), is(CommonConstants.CS_REV_HISTORY_TITLE));
        // And the value matches the spec topic
        assertThat(result.getValue().getId(), is(topicId.toString()));
        assertThat(result.getValue().getRevision(), is(revision));
        assertThat(result.getValue().getConditionStatement(false), is(value));
        // And the unique id was set
        assertThat(result.getUniqueId(), is(id.toString()));
    }

    @Test
    public void shouldTransformFileMetaDataWithoutChildren() throws Exception {
        // Given a node with a file title
        // And a valid value
        CSNodeWrapper nodeWrapper = createMetaDataMock(CommonConstants.CS_FILE_TITLE, null);
        given(nodeWrapper.getId()).willReturn(id);
        given(nodeWrapper.getEntityId()).willReturn(topicId);
        given(nodeWrapper.getEntityRevision()).willReturn(revision);
        given(nodeWrapper.getNodeType()).willReturn(CommonConstants.CS_NODE_META_DATA);

        // When the node metadata is transformed
        final FileList result = (FileList) CSTransformer.transformMetaData(nodeWrapper, nodes, targetTopics, relationshipFromNodes);

        // Then the result has a file key
        assertThat(result.getKey(), is(CommonConstants.CS_FILE_TITLE));
        // And the value is an empty list
        assertThat(result.getValue().size(), is(0));
        // And the unique id was set
        assertThat(result.getUniqueId(), is(id.toString()));
    }

    @Test
    public void shouldTransformFileMetaDataWithChildren() throws Exception {
        // Given a node with a file title
        // And a valid value
        CSNodeWrapper nodeWrapper = createMetaDataMock(CommonConstants.CS_FILE_TITLE, null);
        given(nodeWrapper.getId()).willReturn(id);
        given(nodeWrapper.getNodeType()).willReturn(CommonConstants.CS_NODE_META_DATA);
        // And a child file
        CSNodeWrapper fileWrapper = createValidFileMock();
        given(fileWrapper.getTitle()).willReturn(title);
        given(fileWrapper.getEntityId()).willReturn(topicId);
        given(fileWrapper.getEntityRevision()).willReturn(revision);
        given(fileWrapper.getId()).willReturn(id2);
        // and the children is mocked
        final UpdateableCollectionWrapper<CSNodeWrapper> children = mock(UpdateableCollectionWrapper.class);
        given(nodeWrapper.getChildren()).willReturn(children);
        given(children.getItems()).willReturn(Arrays.asList(fileWrapper));

        // When the node metadata is transformed
        final FileList result = (FileList) CSTransformer.transformMetaData(nodeWrapper, nodes, targetTopics, relationshipFromNodes);

        // Then the result has a file key
        assertThat(result.getKey(), is(CommonConstants.CS_FILE_TITLE));
        // And the value has one item in its list
        assertThat(result.getValue().size(), is(1));
        // And that value has the right properties
        assertThat(result.getValue().get(0).getTitle(), is(title));
        assertThat(result.getValue().get(0).getId(), is(topicId));
        assertThat(result.getValue().get(0).getRevision(), is(revision));
        assertThat(result.getValue().get(0).getUniqueId(), is(id2.toString()));
        // And the unique id was set
        assertThat(result.getUniqueId(), is(id.toString()));
    }
}

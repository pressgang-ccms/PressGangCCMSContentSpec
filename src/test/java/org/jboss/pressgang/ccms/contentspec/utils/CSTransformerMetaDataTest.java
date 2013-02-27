package org.jboss.pressgang.ccms.contentspec.utils;

import net.sf.ipsedixit.annotation.ArbitraryString;
import net.sf.ipsedixit.core.StringType;
import org.hamcrest.Matchers;
import org.jboss.pressgang.ccms.contentspec.KeyValueNode;
import org.jboss.pressgang.ccms.contentspec.constants.CSConstants;
import org.jboss.pressgang.ccms.contentspec.entities.InjectionOptions;
import org.jboss.pressgang.ccms.contentspec.enums.BookType;
import org.jboss.pressgang.ccms.contentspec.wrapper.CSNodeWrapper;
import org.junit.Test;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.jboss.pressgang.ccms.contentspec.TestUtil.createMetaDataMock;
import static org.jboss.pressgang.ccms.contentspec.TestUtil.selectRandomListItem;
import static org.jboss.pressgang.ccms.contentspec.constants.CSConstants.INLINE_INJECTION_TITLE;
import static org.junit.Assert.assertThat;

/**
 * @author kamiller@redhat.com (Katie Miller)
 */
@SuppressWarnings("unchecked")
public class CSTransformerMetaDataTest extends CSTransformerTest {

    @ArbitraryString(type = StringType.ALPHANUMERIC) String key;
    @ArbitraryString(type = StringType.ALPHANUMERIC) String value;

    @Test
    public void shouldTransformBookMetaData() throws Exception {
        // Given a node with a book type title
        // And a valid book type
        ArrayList<BookType> bookTypes = new ArrayList<BookType>(asList(BookType.values()));
        bookTypes.remove(BookType.INVALID);
        BookType bookType = selectRandomListItem(bookTypes);
        CSNodeWrapper nodeWrapper = createMetaDataMock(CSConstants.BOOK_TYPE_TITLE, bookType.toString());

        // When the node metadata is transformed
        KeyValueNode<BookType> result = (KeyValueNode<BookType>) transformer.transformMetaData(nodeWrapper);

        // Then the result has a book type key
        assertThat(result.getKey(), is(CSConstants.BOOK_TYPE_TITLE));
        // And the type of book specified as its value
        assertThat(result.getValue(), is(bookType));
    }

    @Test
    public void shouldTransFormBookMetaDataWithInvalidType() throws Exception {
        // Given a node with a book type title
        // And an invalid book type
        CSNodeWrapper nodeWrapper = createMetaDataMock(CSConstants.BOOK_TYPE_TITLE, "foo");

        // When the node metadata is transformed
        KeyValueNode<BookType> result = (KeyValueNode<BookType>) transformer.transformMetaData(nodeWrapper);

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

        // When the node metadata is transformed
        KeyValueNode<InjectionOptions> result = (KeyValueNode<InjectionOptions>) transformer.transformMetaData(nodeWrapper);

        // Then the result has an inline injection type key
        assertThat(result.getKey(), is(INLINE_INJECTION_TITLE));
        // And the value is the injection options
        assertThat(result.getValue().getStrictTopicTypes(), Matchers.contains("topicType"));
    }

    @Test
    public void shouldTransformArbitraryMetaData() throws Exception {
        // Given a node with a title that is not an inline injection or book type
        // And some string value
        CSNodeWrapper nodeWrapper = createMetaDataMock(key, value);

        // When the node metadata is transformed
        KeyValueNode<String> result = (KeyValueNode<String>) transformer.transformMetaData(nodeWrapper);

        // Then the result has the key given
        assertThat(result.getKey(), is(key));
        // And the value is the string given
        assertThat(result.getValue(), is(value));
    }
}

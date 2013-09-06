package org.jboss.pressgang.ccms.contentspec;

public class KeyValueNode<T> extends Node {
    private String key;
    private T value = null;
    private final char separator;

    public KeyValueNode(final String key, final T value, final char separator, final int lineNumber) {
        super(lineNumber, key + " " + separator + " " + value);
        this.key = key;
        this.value = value;
        this.separator = separator;

        if (value instanceof Node) {
            ((Node) value).setParent(this);
        }
    }

    public KeyValueNode(final String key, final T value, final char separator) {
        this(key, value, separator, 0);
    }

    public KeyValueNode(final String key, final T value, final int lineNumber) {
        this(key, value, '=', lineNumber);
    }

    public KeyValueNode(final String key, final T value) {
        this(key, value, '=');
    }

    @Override
    public Integer getStep() {
        if (getParent() == null) return null;

        // Get the position of the metadata in the content spec nodes and add one
        return getParent().getNodes().indexOf(this) + 1;
    }

    @Override
    protected void removeParent() {
        getParent().removeChild(this);
    }

    public String getKey() {
        return key;
    }

    protected void setKey(final String key) {
        this.key = key;
    }

    public T getValue() {
        return value;
    }

    public void setValue(final T value) {
        this.value = value;

        if (value instanceof Node) {
            ((Node) value).setParent(this);
        }
    }

    protected char getSeparator() {
        return separator;
    }

    @Override
    public ContentSpec getParent() {
        return (ContentSpec) parent;
    }

    /**
     * Sets the Parent node for the Comment.
     *
     * @param parent The parent node for the comment.
     */
    protected void setParent(final ContentSpec parent) {
        super.setParent(parent);
    }

    public String getText() {
        if (key.equals("publican.cfg")) {
            return key + " " + separator + " [" + (value == null ? "" : value.toString()) + "]";
        } else if (value instanceof Boolean) {
            return key + " " + separator + " " + (value == null ? "" : ((Boolean) value ? "ON" : "OFF"));
        } else if (value instanceof SpecTopic) {
            return key + " " + separator + " " + (value == null ? "" : ("[" + ((SpecTopic) value).getIdAndOptionsString()) + "]");
        } else {
            return key + " " + separator + " " + (value == null ? "" : value.toString());
        }
    }

    @Override
    public String toString() {
        return getText() + "\n";
    }
}

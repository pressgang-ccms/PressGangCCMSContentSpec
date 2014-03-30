package org.jboss.pressgang.ccms.contentspec;

import java.util.List;

public interface IOptionsNode {
    /**
     * Get the Assigned Writer for a topic. If useInherited is true then it will check for an inherited writer as well.
     *
     * @param useInherited If the function should check for an inherited writer
     * @return The Assigned Writers name as a String
     */
    String getAssignedWriter(boolean useInherited);
    /**
     * Sets the Assigned Writer for this set of options
     *
     * @param writer The writers name that matches to the assigned writer tag in the database
     */
    void setAssignedWriter(String writer);

    /**
     * Get the description for a node. If useInherited is true then it will check for an inherited description as well.
     *
     * @param useInherited If the function should check for an inherited description
     * @return The description as a String
     */
    String getDescription(boolean useInherited);
    /**
     * Sets the description for a node.
     *
     * @param description The description.
     */
    void setDescription(String description);

    /**
     * Adds a tag to the list of tags. If the tag starts with a - then its added to the remove tag list otherwise its added to
     * the normal tag mapping. Also strips off + & - from the start of tags.
     *
     * @param tagName The name of the Tag to be added.
     * @return True if the tag was added successfully otherwise false.
     */
    boolean addTag(String tagName);
    /**
     * Adds an array of tags to the list of tags for this node
     *
     * @param tagArray A list of tags by name that are to be added.
     * @return True if all the tags were added successfully otherwise false.
     */
    boolean addTags(List<String> tagArray);
    /**
     * Gets the set of tags for this set of options. If useInherited is true then it will check for inherited options as well.
     * <p/>
     * This function also removes the tags from the HashMap for any tag that has a - in front of its name.
     *
     * @param useInherited If the function should check for inherited tags
     * @return A list of tags to be added for the topic.
     */
    List<String> getTags(boolean useInherited);
    /**
     * Sets the set of tags for this set of options
     *
     * @param tags A HashMap of tags. The key in the map is the tags category name and the value is an ArrayList of tags for
     *             each category.
     */
    void setTags(List<String> tags);

    /**
     * Gets an ArrayList of tags that are to be removed for these options. If useInherited is true then it will also add all
     * inherited removeable tags.
     *
     * @param useInherited If the function should check for inherited removable tags
     * @return An ArrayList of tags
     */
    List<String> getRemoveTags(boolean useInherited);
    /**
     * Sets the list of tags that are to be removed in this set of options
     *
     * @param tags An ArrayList of tags to be removed
     */
    void setRemoveTags(List<String> tags);

    /**
     * Sets the conditional statement to be used when building
     *
     * @param condition The conditional statement for this node and it's sub nodes.
     */
    void setConditionStatement(final String condition);
    /**
     * Gets the conditional statement to be used when building
     *
     * @return The conditional statement for this node and it's sub nodes.
     */
    String getConditionStatement();
    /**
     * Gets the conditional statement to be used when building
     *
     * @param useInherited If the conditional statement should be pulled from its parent nodes.
     * @return The conditional statement for this node and it's sub nodes.
     */
    String getConditionStatement(final boolean useInherited);

    String getText();
    int getLineNumber();
}

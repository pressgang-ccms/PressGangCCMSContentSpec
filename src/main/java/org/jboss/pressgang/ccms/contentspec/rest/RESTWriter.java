package org.jboss.pressgang.ccms.contentspec.rest;

import static org.jboss.pressgang.ccms.rest.v1.collections.base.RESTBaseCollectionItemV1.ADD_STATE;
import static org.jboss.pressgang.ccms.rest.v1.collections.base.RESTBaseCollectionItemV1.REMOVE_STATE;

import java.util.Date;

import org.apache.log4j.Logger;
import org.jboss.pressgang.ccms.contentspec.constants.CSConstants;
import org.jboss.pressgang.ccms.contentspec.rest.utils.RESTCollectionCache;
import org.jboss.pressgang.ccms.contentspec.rest.utils.RESTEntityCache;
import org.jboss.pressgang.ccms.rest.v1.collections.RESTCategoryCollectionV1;
import org.jboss.pressgang.ccms.rest.v1.collections.RESTTagCollectionV1;
import org.jboss.pressgang.ccms.rest.v1.collections.RESTTopicCollectionV1;
import org.jboss.pressgang.ccms.rest.v1.collections.RESTTopicSourceUrlCollectionV1;
import org.jboss.pressgang.ccms.rest.v1.collections.items.RESTTagCollectionItemV1;
import org.jboss.pressgang.ccms.rest.v1.collections.items.RESTTopicCollectionItemV1;
import org.jboss.pressgang.ccms.rest.v1.collections.items.RESTTopicSourceUrlCollectionItemV1;
import org.jboss.pressgang.ccms.rest.v1.collections.items.join.RESTAssignedPropertyTagCollectionItemV1;
import org.jboss.pressgang.ccms.rest.v1.collections.join.RESTAssignedPropertyTagCollectionV1;
import org.jboss.pressgang.ccms.rest.v1.collections.join.RESTCategoryInTagCollectionV1;
import org.jboss.pressgang.ccms.rest.v1.entities.RESTCategoryV1;
import org.jboss.pressgang.ccms.rest.v1.entities.RESTTagV1;
import org.jboss.pressgang.ccms.rest.v1.entities.RESTTopicV1;
import org.jboss.pressgang.ccms.rest.v1.entities.base.RESTLogDetailsV1;
import org.jboss.pressgang.ccms.rest.v1.entities.enums.RESTXMLDoctype;
import org.jboss.pressgang.ccms.rest.v1.entities.join.RESTAssignedPropertyTagV1;
import org.jboss.pressgang.ccms.rest.v1.entities.join.RESTCategoryInTagV1;
import org.jboss.pressgang.ccms.rest.v1.jaxrsinterfaces.RESTInterfaceV1;

public class RESTWriter {

    private static final Logger log = Logger.getLogger(RESTWriter.class);

    private final RESTInterfaceV1 client;
    private final RESTReader reader;
    private final RESTEntityCache entityCache;
    private final RESTCollectionCache collectionsCache;

    public RESTWriter(final RESTReader reader, final RESTInterfaceV1 client, final RESTEntityCache cache,
            final RESTCollectionCache collectionsCache) {
        this.reader = reader;
        this.client = client;
        this.entityCache = cache;
        this.collectionsCache = collectionsCache;
    }

    /**
     * Writes a Category tuple to the database using the data provided.
     */
    public Integer createCategory(boolean mutuallyExclusive, String name) {
        Integer insertId = null;
        try {
            RESTCategoryV1 category = new RESTCategoryV1();
            category.explicitSetMutuallyExclusive(mutuallyExclusive);
            category.explicitSetName(name);

            category = client.createJSONCategory(null, category);
            insertId = category.getId();
            collectionsCache.expire(RESTCategoryV1.class);
        } catch (Exception e) {
            log.debug(e);
        }
        return insertId;
    }

    /**
     * Writes a Tag tuple to the database using the data provided.
     */
    public Integer createTag(final String name, final String description, final RESTCategoryCollectionV1 categories) {
        Integer insertId = null;
        try {
            RESTTagV1 tag = new RESTTagV1();
            tag.explicitSetName(name);
            tag.explicitSetDescription(description);
            final RESTCategoryInTagCollectionV1 newCategories = new RESTCategoryInTagCollectionV1();
            for (final RESTCategoryV1 category : categories.returnItems()) {
                final RESTCategoryInTagV1 newCategory = new RESTCategoryInTagV1(category);
                newCategories.addNewItem(newCategory);
            }
            tag.explicitSetCategories(newCategories);

            tag = client.createJSONTag(null, tag);
            insertId = tag.getId();
            collectionsCache.expire(RESTTagV1.class);
        } catch (Exception e) {
            log.error(e);
        }
        return insertId;
    }

    /**
     * Writes a Topic tuple to the database using the data provided.
     */
    public Integer createTopic(final String title, final String text, final String description, final Date timestamp) {
        return createTopic(title, text, description, timestamp, null, null, null, null, null);
    }

    /**
     * Writes a Topic tuple to the database using the data provided.
     */
    public Integer createTopic(final String title, final String text, final String description, final Date timestamp,
            final RESTTopicSourceUrlCollectionV1 sourceUrls, final RESTTopicCollectionV1 incomingRelationships,
            final RESTTopicCollectionV1 outgoingRelationships, final RESTTagCollectionV1 tags,
            final RESTAssignedPropertyTagCollectionV1 properties) {
        return createTopic(title, text, description, timestamp, sourceUrls, incomingRelationships, outgoingRelationships, tags, properties,
                null);
    }

    /**
     * Writes a Topic tuple to the database using the data provided.
     */
    public Integer createTopic(final String title, final String text, final String description, final Date timestamp,
            final RESTTopicSourceUrlCollectionV1 sourceUrls, final RESTTopicCollectionV1 incomingRelationships,
            final RESTTopicCollectionV1 outgoingRelationships, final RESTTagCollectionV1 tags,
            final RESTAssignedPropertyTagCollectionV1 properties, final RESTLogDetailsV1 logDetails) {
        Integer insertId = null;
        try {
            RESTTopicV1 topic = new RESTTopicV1();

            topic.explicitSetTitle(title);
            topic.explicitSetDescription(description);
            if (timestamp != null) {
                topic.setCreated(timestamp);
            }
            if (text != null) {
                topic.explicitSetXml(text);
            }
            if (!incomingRelationships.getItems().isEmpty()) {
                for (final RESTTopicCollectionItemV1 incomingRelationship : incomingRelationships.getItems()) {
                    incomingRelationship.setState(ADD_STATE);
                }
                topic.explicitSetIncomingRelationships(incomingRelationships);
            }
            if (!outgoingRelationships.getItems().isEmpty()) {
                for (final RESTTopicCollectionItemV1 outgoingRelationship : outgoingRelationships.getItems()) {
                    outgoingRelationship.setState(ADD_STATE);
                }
                topic.explicitSetOutgoingRelationships(outgoingRelationships);
            }
            if (!tags.getItems().isEmpty()) {
                for (final RESTTagCollectionItemV1 tag : tags.getItems()) {
                    tag.setState(ADD_STATE);
                }
                topic.explicitSetTags(tags);
            }
            if (!properties.getItems().isEmpty()) {
                for (final RESTAssignedPropertyTagCollectionItemV1 tag : properties.getItems()) {
                    tag.setState(ADD_STATE);
                }
                topic.explicitSetProperties(properties);
            }
            if (!sourceUrls.getItems().isEmpty()) {
                for (final RESTTopicSourceUrlCollectionItemV1 sourceUrl : sourceUrls.getItems()) {
                    sourceUrl.setState(ADD_STATE);
                }
                topic.explicitSetSourceUrls_OTM(sourceUrls);
            }

            if (logDetails == null) {
                topic = client.createJSONTopic(null, topic);
            } else {
                final Integer userId = logDetails.getUser() == null || logDetails.getUser().getId() == null ? CSConstants.UNKNOWN_USER_ID
                        : logDetails.getUser().getId();
                topic = client.createJSONTopic(null, topic, logDetails.getMessage(), logDetails.getFlag(), userId.toString());
            }
            insertId = topic.getId();
            collectionsCache.expire(RESTTopicV1.class);
        } catch (Exception e) {
            log.error(e);
        }
        return insertId;
    }

    /**
     * Updates a Topic tuple in the database using the data provided.
     */
    public Integer updateTopic(final Integer topicId, final String title, final String text, final String description,
            final Date timestamp) {
        return updateTopic(topicId, title, text, description, timestamp, null, null, null, null, null);
    }

    /**
     * Updates a Topic tuple in the database using the data provided.
     */
    public Integer updateTopic(final Integer topicId, final String title, final String text, final String description, final Date timestamp,
            final RESTTopicSourceUrlCollectionV1 sourceUrls, final RESTTopicCollectionV1 incomingRelationships,
            final RESTTopicCollectionV1 outgoingRelationships, final RESTTagCollectionV1 tags,
            final RESTAssignedPropertyTagCollectionV1 properties) {
        return updateTopic(topicId, title, text, description, timestamp, sourceUrls, incomingRelationships, outgoingRelationships, tags,
                properties, null);
    }

    /**
     * Updates a Topic tuple in the database using the data provided.
     */
    public Integer updateTopic(final Integer topicId, final String title, final String text, final String description, final Date timestamp,
            final RESTTopicSourceUrlCollectionV1 sourceUrls, final RESTTopicCollectionV1 incomingRelationships,
            final RESTTopicCollectionV1 outgoingRelationships, final RESTTagCollectionV1 tags,
            final RESTAssignedPropertyTagCollectionV1 properties, final RESTLogDetailsV1 logDetails) {
        Integer insertId = null;
        try {

            RESTTopicV1 topic = reader.getTopicById(topicId, null);

            if (title != null) {
                topic.explicitSetTitle(title);
            }
            if (description != null) {
                topic.explicitSetDescription(description);
            }
            if (timestamp != null) {
                topic.setCreated(timestamp);
            }
            if (text != null) {
                topic.explicitSetXml(text);
            }
            if (!incomingRelationships.getItems().isEmpty()) {
                for (final RESTTopicCollectionItemV1 incomingRelationship : incomingRelationships.getItems()) {
                    incomingRelationship.setState(ADD_STATE);
                }
                topic.explicitSetIncomingRelationships(incomingRelationships);
            }
            if (!outgoingRelationships.getItems().isEmpty()) {
                for (final RESTTopicCollectionItemV1 outgoingRelationship : outgoingRelationships.getItems()) {
                    outgoingRelationship.setState(ADD_STATE);
                }
                topic.explicitSetOutgoingRelationships(outgoingRelationships);
            }
            if (!tags.getItems().isEmpty()) {
                for (final RESTTagCollectionItemV1 tag : tags.getItems()) {
                    tag.setState(ADD_STATE);
                }
                topic.explicitSetTags(tags);
            }
            if (!properties.getItems().isEmpty()) {
                for (final RESTAssignedPropertyTagCollectionItemV1 tag : properties.getItems()) {
                    tag.setState(ADD_STATE);
                }
                topic.explicitSetProperties(properties);
            }
            if (!sourceUrls.getItems().isEmpty()) {
                for (final RESTTopicSourceUrlCollectionItemV1 sourceUrl : sourceUrls.getItems()) {
                    sourceUrl.setState(ADD_STATE);
                }
                topic.explicitSetSourceUrls_OTM(sourceUrls);
            }

            if (logDetails == null) {
                topic = client.updateJSONTopic(null, topic);
            } else {
                final Integer userId = logDetails.getUser() == null || logDetails.getUser().getId() == null ? CSConstants.UNKNOWN_USER_ID
                        : logDetails.getUser().getId();
                topic = client.updateJSONTopic(null, topic, logDetails.getMessage(), logDetails.getFlag(), userId.toString());
            }
            insertId = topic.getId();
            entityCache.expire(RESTTopicV1.class, insertId);
            collectionsCache.expire(RESTTopicV1.class);
        } catch (Exception e) {
            log.error(e);
        }
        return insertId;
    }

    /**
     * Writes a ContentSpecs tuple to the database using the data provided.
     */
    public Integer createContentSpec(final String title, final String preContentSpec, final String dtd, final String createdBy) {
        return createContentSpec(title, preContentSpec, dtd, createdBy, null);
    }

    /**
     * Writes a ContentSpecs tuple to the database using the data provided.
     */
    public Integer createContentSpec(final String title, final String preContentSpec, final String dtd, final String createdBy,
            final RESTLogDetailsV1 logDetails) {
        try {
            RESTTopicV1 contentSpec = new RESTTopicV1();
            contentSpec.explicitSetTitle(title);
            contentSpec.explicitSetXml(preContentSpec);
            contentSpec.explicitSetXmlDoctype(RESTXMLDoctype.DOCBOOK_45);

            // Create the Added By, Content Spec Type and DTD property tags
            final RESTAssignedPropertyTagCollectionV1 properties = new RESTAssignedPropertyTagCollectionV1();
            final RESTAssignedPropertyTagV1 addedBy = new RESTAssignedPropertyTagV1(
                    client.getJSONPropertyTag(CSConstants.ADDED_BY_PROPERTY_TAG_ID, null));
            addedBy.explicitSetValue(createdBy);

            final RESTAssignedPropertyTagV1 typePropertyTag = new RESTAssignedPropertyTagV1(
                    client.getJSONPropertyTag(CSConstants.CSP_TYPE_PROPERTY_TAG_ID, null));
            typePropertyTag.explicitSetValue(CSConstants.CSP_PRE_PROCESSED_STRING);

            properties.addNewItem(addedBy);
            properties.addNewItem(typePropertyTag);

            contentSpec.explicitSetProperties(properties);

            // Add the Content Specification Type Tag
            final RESTTagCollectionV1 tags = new RESTTagCollectionV1();
            final RESTTagV1 typeTag = client.getJSONTag(CSConstants.CONTENT_SPEC_TAG_ID, null);
            tags.addNewItem(typeTag);

            contentSpec.explicitSetTags(tags);

            if (logDetails == null) {
                contentSpec = client.createJSONTopic("", contentSpec);
            } else {
                final Integer userId = logDetails.getUser() == null || logDetails.getUser().getId() == null ? CSConstants.UNKNOWN_USER_ID
                        : logDetails.getUser().getId();
                contentSpec = client.createJSONTopic("", contentSpec, logDetails.getMessage(), logDetails.getFlag(), userId.toString());
            }
            if (contentSpec != null) return contentSpec.getId();
            collectionsCache.expire(RESTTopicV1.class);
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

    /**
     * Updates a ContentSpecs tuple from the database using the data provided.
     */
    public boolean updateContentSpec(final Integer id, final String title, final String preContentSpec, final String dtd) {
        return updateContentSpec(id, title, preContentSpec, dtd, null);
    }

    /**
     * Updates a ContentSpecs tuple from the database using the data provided.
     */
    public boolean updateContentSpec(final Integer id, final String title, final String preContentSpec, final String dtd,
            final RESTLogDetailsV1 logDetails) {
        try {
            RESTTopicV1 contentSpec = reader.getContentSpecById(id, null);

            if (contentSpec == null) return false;

            // Change the title if it's different
            if (!contentSpec.getTitle().equals(title)) {
                contentSpec.explicitSetTitle(title);
            }

            contentSpec.explicitSetXmlDoctype(RESTXMLDoctype.DOCBOOK_45);
            contentSpec.explicitSetXml(preContentSpec);

            // Update the Content Spec Type and DTD property tags
            final RESTAssignedPropertyTagCollectionV1 properties = contentSpec.getProperties();
            if (properties.getItems() != null && !properties.getItems().isEmpty()) {
                // The property tag should never match a pre tag
                final RESTAssignedPropertyTagV1 typePropertyTag = new RESTAssignedPropertyTagV1(
                        client.getJSONPropertyTag(CSConstants.CSP_TYPE_PROPERTY_TAG_ID, null));
                typePropertyTag.explicitSetValue(CSConstants.CSP_PRE_PROCESSED_STRING);

                properties.addNewItem(typePropertyTag);
            }

            contentSpec.explicitSetProperties(properties);

            if (logDetails == null) {
                contentSpec = client.updateJSONTopic("", contentSpec);
            } else {
                final Integer userId = logDetails.getUser() == null || logDetails.getUser().getId() == null ? CSConstants.UNKNOWN_USER_ID
                        : logDetails.getUser().getId();
                contentSpec = client.updateJSONTopic("", contentSpec, logDetails.getMessage(), logDetails.getFlag(), userId.toString());
            }
            if (contentSpec != null) {
                entityCache.expire(RESTTopicV1.class, id);
                collectionsCache.expire(RESTTopicV1.class);
                return true;
            }
        } catch (Exception e) {
            log.error(e);
        }
        return false;
    }

    /**
     * Writes a ContentSpecs tuple to the database using the data provided.
     */
    public boolean updatePostContentSpec(final Integer id, final String postContentSpec) {
        return updatePostContentSpec(id, postContentSpec, null);
    }

    /**
     * Writes a ContentSpecs tuple to the database using the data provided.
     */
    public boolean updatePostContentSpec(final Integer id, final String postContentSpec, final RESTLogDetailsV1 logDetails) {
        try {
            RESTTopicV1 contentSpec = reader.getContentSpecById(id, null);
            if (contentSpec == null) return false;

            contentSpec.explicitSetXml(postContentSpec);

            // Update Content Spec Type
            final RESTAssignedPropertyTagCollectionV1 properties = contentSpec.getProperties();
            if (properties.getItems() != null && !properties.getItems().isEmpty()) {
                // Loop through and remove the type
                for (final RESTAssignedPropertyTagCollectionItemV1 propertyItem : properties.getItems()) {
                    final RESTAssignedPropertyTagV1 property = propertyItem.getItem();

                    if (property.getId().equals(CSConstants.CSP_TYPE_PROPERTY_TAG_ID)) {
                        propertyItem.setState(REMOVE_STATE);
                    }
                }

                final RESTAssignedPropertyTagV1 typePropertyTag = new RESTAssignedPropertyTagV1(
                        client.getJSONPropertyTag(CSConstants.CSP_TYPE_PROPERTY_TAG_ID, null));
                typePropertyTag.explicitSetValue(CSConstants.CSP_POST_PROCESSED_STRING);

                properties.addNewItem(typePropertyTag);

                contentSpec.explicitSetProperties(properties);
            }

            if (logDetails == null) {
                contentSpec = client.updateJSONTopic("", contentSpec);
            } else {
                final Integer userId = logDetails.getUser() == null || logDetails.getUser().getId() == null ? CSConstants.UNKNOWN_USER_ID
                        : logDetails.getUser().getId();
                contentSpec = client.updateJSONTopic("", contentSpec, logDetails.getMessage(), logDetails.getFlag(), userId.toString());
            }
            if (contentSpec != null) {
                entityCache.expire(RESTTopicV1.class, id);
                collectionsCache.expire(RESTTopicV1.class);
                return true;
            }
        } catch (Exception e) {
            log.error(e);
        }
        return false;
    }

    /**
     * Delete a Content Specification from the database.
     */
    public boolean deleteContentSpec(final Integer id) {
        try {
            client.deleteJSONTopic(id, null);
            entityCache.expire(RESTTopicV1.class, id);
            collectionsCache.expire(RESTTopicV1.class);
            return true;
        } catch (Exception e) {
            log.error(e);
        }
        return false;
    }
}
package org.jboss.pressgang.ccms.docbook.messaging;

import org.jboss.pressgang.ccms.docbook.compiling.DocBookBuildingOptions;
import org.jboss.pressgang.ccms.zanata.ZanataDetails;

public class BuildDocbookMessage {
    private String query;
    private DocBookBuildingOptions docbookOptions;
    private int entityType;
    private ZanataDetails zanataDetails;

    public String getQuery() {
        return query;
    }

    public void setQuery(final String query) {
        this.query = query;
    }

    public DocBookBuildingOptions getDocbookOptions() {
        return docbookOptions;
    }

    public void setDocbookOptions(final DocBookBuildingOptions docbookOptions) {
        this.docbookOptions = docbookOptions;
    }

    public int getEntityType() {
        return entityType;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
    }

    public ZanataDetails getZanataDetails() {
        return zanataDetails;
    }

    public void setZanataDetails(final ZanataDetails zanataDetails) {
        this.zanataDetails = zanataDetails;
    }
}

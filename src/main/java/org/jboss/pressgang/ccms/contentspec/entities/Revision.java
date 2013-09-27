package org.jboss.pressgang.ccms.contentspec.entities;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Revision {

    private Integer id = 0;
    private Integer revision = null;
    private Date date = null;

    public Revision(final Integer id, final Integer revision, final Date date) {
        this.id = id;
        this.revision = revision;
        this.date = date;
    }

    public Revision() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public String toString() {
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, MMM dd, yyyy 'at' hh:mm:ss a");
        return String.format("-> ID: %6d on %s", revision, dateFormatter.format(date));
    }
}

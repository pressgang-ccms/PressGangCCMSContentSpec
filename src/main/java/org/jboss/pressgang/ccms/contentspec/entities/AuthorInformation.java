package org.jboss.pressgang.ccms.contentspec.entities;

public class AuthorInformation {

    private Integer authorTagId = null;
    private String firstName = null;
    private String lastName = null;
    private String email = null;
    private String organization = null;
    private String orgDivision = null;

    public AuthorInformation() {
    }

    public AuthorInformation(final Integer authorId, final String firstName, final String lastName, final String email) {
        authorTagId = authorId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public int getAuthorId() {
        return authorTagId;
    }

    public void setAuthorId(final int authorId) {
        authorTagId = authorId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(final String organization) {
        this.organization = organization;
    }

    public String getOrgDivision() {
        return orgDivision;
    }

    public void setOrgDivision(final String orgDivision) {
        this.orgDivision = orgDivision;
    }

}

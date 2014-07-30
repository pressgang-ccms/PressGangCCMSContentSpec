/*
  Copyright 2011-2014 Red Hat, Inc

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

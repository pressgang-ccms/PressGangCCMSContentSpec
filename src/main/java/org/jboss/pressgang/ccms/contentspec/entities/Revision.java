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

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

package org.jboss.pressgang.ccms.contentspec.structures;

public class RevNumber implements Comparable<RevNumber> {
    private final Version version;
    private final Version release;

    public RevNumber(final String revnumber) {
        final String[] split = revnumber.split("-", 2);
        version = new Version(split[0]);
        if (split.length > 1) {
            release = new Version(split[1]);
        } else {
            release = null;
        }
    }

    public RevNumber(final Version version, final Version release) {
        this.version = version;
        this.release = release;
    }

    @Override
    public String toString() {
        if (release == null) {
            return version.toString();
        } else {
            return version.toString() + "-" + release.toString();
        }
    }

    @Override
    public int compareTo(final RevNumber revnumber) {
        if (this == revnumber) {
            return 0;
        }

        if (revnumber == null) {
            return 1;
        }

        if (version == null && revnumber.version == null) {
            return 0;
        }

        if (version == revnumber.version) {
            return 0;
        }

        if (version == null) {
            return -1;
        }

        if (revnumber.version == null) {
            return 1;
        }

        if (version.equals(revnumber.version)) {
            if (release == null && revnumber.release == null) {
                return 0;
            }

            if (release == revnumber.release) {
                return 0;
            }

            if (release == null) {
                return -1;
            }

            if (revnumber.release == null) {
                return 1;
            }

            if (release.equals(revnumber.release)) {
                return 0;
            } else {
                return release.compareTo(revnumber.release);
            }
        } else {
            return version.compareTo(revnumber.version);
        }
    }

    public Version getVersion() {
        return version;
    }

    public Version getRelease() {
        return release;
    }
}

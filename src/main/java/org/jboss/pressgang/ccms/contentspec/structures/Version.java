/*
  Copyright 2011-2014 Red Hat

  This file is part of PresGang CCMS.

  PresGang CCMS is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  PresGang CCMS is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with PresGang CCMS.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.jboss.pressgang.ccms.contentspec.structures;

/*
 * This class implements the compareTo method based on http://stackoverflow.com/a/6702175/1330640
 */
public class Version implements Comparable<Version> {
    private final Integer major;
    private final int majorLeadingZeros;
    private final Integer minor;
    private final int minorLeadingZeros;
    private final Integer revision;
    private final int revisionLeadingZeros;
    private final String other;
    private final String version;

    private Version(final int majorLeadingZeros, final Integer major, final int minorLeadingZeros, final Integer minor,
            final int revisionLeadingZeros, final Integer revision, final String other) {
        assert major != null;

        this.majorLeadingZeros = majorLeadingZeros;
        this.major = major;
        this.minorLeadingZeros = minorLeadingZeros;
        this.minor = minor;
        this.revisionLeadingZeros = revisionLeadingZeros;
        this.revision = revision;
        this.other = other;

        // Build the version
        final StringBuilder version = new StringBuilder(10);
        addLeadingZeros(version, majorLeadingZeros);
        version.append(major);
        if (minor != null) {
            version.append(".");
            addLeadingZeros(version, minorLeadingZeros);
            version.append(minor);
            if (revision != null) {
                version.append(".");
                addLeadingZeros(version, revisionLeadingZeros);
                version.append(revision);
            }
        }

        this.version = version.toString();
    }

    public Version(final String version) throws NumberFormatException {
        String[] val = version.split("-", 2);

        // Get the major/minor/revision values
        this.version = val[0];
        String[] vals = this.version.split("\\.");
        majorLeadingZeros = numLeadingZeros(vals[0]);
        major = Integer.parseInt(vals[0]);
        if (vals.length > 1) {
            minorLeadingZeros = numLeadingZeros(vals[1]);
            minor = Integer.valueOf(vals[1]);
            if (vals.length > 2) {
                revisionLeadingZeros = numLeadingZeros(vals[2]);
                revision = Integer.valueOf(vals[2]);
            } else {
                revisionLeadingZeros = 0;
                revision = null;
            }
        } else {
            minorLeadingZeros = 0;
            minor = null;
            revisionLeadingZeros = 0;
            revision = null;
        }

        if (val.length > 1) {
            other = val[1];
        } else {
            other = null;
        }
    }

    public Version(final Integer major, final Integer minor, final Integer revision, final String other) {
        this(0, major, 0, minor, 0, revision, other);
    }

    private int numLeadingZeros(final String num) {
        // Ignore the last value to account for a '0' value
        int i;
        for (i = 0; i < num.length() - 1 && num.charAt(i) == '0'; i++) {
        }

        return i;
    }

    private void addLeadingZeros(final StringBuilder builder, int numZeros) {
        for (int i = 0; i < numZeros; i++) {
            builder.append('0');
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        } else if (version == null) {
            return o instanceof Version && ((Version) o).version == null;
        } else {
            return o instanceof Version && version.equals(((Version) o).version);
        }
    }

    @Override
    public int hashCode() {
        return version == null ? super.hashCode() : version.hashCode();
    }

    @Override
    public String toString() {
        if (version == null) {
            return super.toString();
        } else if (other == null) {
            return version;
        } else {
            return version + "-" + other;
        }
    }

    @Override
    public int compareTo(final Version version) {
        if (this == version) {
            return 0;
        }

        if (version == null) {
            return 1;
        }

        if (this.version == null && version.version == null) {
            return 0;
        }

        if (this.version == version.version) {
            return 0;
        }

        if (this.version == null) {
            return -1;
        }

        if (version.version == null) {
            return 1;
        }

        if (this.version.equals(version.version)) return 0; // Short circuit when you shoot for efficiency

        String[] vals1 = this.version.split("\\.");
        String[] vals2 = version.version.split("\\.");

        int i = 0;

        // Most efficient way to skip past equal version subparts
        while (i < vals1.length && i < vals2.length && Integer.valueOf(vals1[i]).equals(Integer.valueOf(vals2[i]))) i++;

        try {
            // If we didn't reach the end,
            if (i < vals1.length && i < vals2.length) {
                // Have to use integer comparison to avoid the "10" < "1" problem
                return Integer.valueOf(vals2[i]).compareTo(Integer.valueOf(vals1[i]));
            }

            if (i < vals1.length) {
                return -1;
            }

            if (i < vals2.length) {
                return 1;
            }
        } catch (NumberFormatException e) {
            // If a number can't be parsed then ignore it and assume it's in the right position
            return 0;
        }

        return 0; // Should never happen (identical strings.)
    }

    public Integer getMajor() {
        return major;
    }

    public Integer getMinor() {
        return minor;
    }

    public Integer getRevision() {
        return revision;
    }

    public String getOther() {
        return other;
    }

    public Version adjustMajor(int increment) {
        return new Version(majorLeadingZeros, major + increment, minorLeadingZeros, minor, revisionLeadingZeros, revision, other);
    }

    public Version adjustMinor(int increment) {
        int newMinor = minor == null ? increment : (minor + increment);
        return new Version(majorLeadingZeros, major, minorLeadingZeros, newMinor, revisionLeadingZeros, revision, other);
    }

    public Version adjustRevision(int increment) {
        int newRevision = revision == null ? increment : (revision + increment);
        return new Version(majorLeadingZeros, major, minorLeadingZeros, minor, revisionLeadingZeros, newRevision, other);
    }
}

package org.jboss.pressgang.ccms.contentspec.structures;

/*
 * This class implements the compareTo method based on http://stackoverflow.com/a/6702175/1330640
 */
public class Version implements Comparable<Version> {
    private final Integer major;
    private final Integer minor;
    private final Integer revision;
    private final String other;
    private final String version;

    public Version(final String version) throws NumberFormatException {
        String[] val = version.split("-", 2);

        // Get the major/minor/revision values
        this.version = val[0];
        String[] vals = this.version.split("\\.");
        this.major = Integer.parseInt(vals[0]);
        if (vals.length > 1) {
            minor = Integer.valueOf(vals[1]);
            if (vals.length > 2) {
                revision = Integer.valueOf(vals[2]);
            } else {
                revision = null;
            }
        } else {
            minor = null;
            revision = null;
        }

        if (val.length > 1) {
            other = val[1];
        } else {
            other = null;
        }
    }

    public Version(final Integer major, final Integer minor, final Integer revision, final String other) {
        this.major = major;
        this.minor = minor;
        this.revision = revision;
        this.other = other;

        // Build the version
        final StringBuilder version = new StringBuilder(major);
        if (minor != null) {
            version.append(".").append(minor);
            if (revision != null) {
                version.append(".").append(revision);
            }
        }

        this.version = version.toString();
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
        while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) i++;

        try {
            // If we didn't reach the end,
            if (i < vals1.length && i < vals2.length) {
                // Have to use integer comparison to avoid the "10" < "1" problem
                return Integer.valueOf(vals2[i]).compareTo(Integer.valueOf(vals1[i]));
            }

            if (i < vals1.length) {
                // end of version.version, check if this.version is all 0's
                boolean allZeros = true;
                for (int j = i; allZeros & (j < vals1.length); j++)
                    allZeros &= (Integer.parseInt(vals1[j]) == 0);
                return allZeros ? 0 : -1;
            }

            if (i < vals2.length) {
                // end of this.version, check if version.version is all 0's
                boolean allZeros = true;
                for (int j = i; allZeros & (j < vals2.length); j++)
                    allZeros &= (Integer.parseInt(vals2[j]) == 0);
                return allZeros ? 0 : 1;
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
}
package org.codehaus.mojo.webstart.manifestmodification;

/**
 * Created with IntelliJ IDEA.
 * User: CPL
 * Date: 25.10.13
 * Time: 09:41
 * To change this template use File | Settings | File Templates.
 */
public class ManifestEntry {

    private final static String SEPARATOR = ":";
    private final static String SPACE = " ";
    private final String header;
    private final String value;

    public ManifestEntry(String header, String value) {
        this.header = header;
        this.value = value;
    }

    public static ManifestEntry parseLine(final String line) {

        if (line == null) {
            throw new IllegalArgumentException("NULL value not allowed");
        }

        int colonPos = line.indexOf(":");
        final String header;
        final String value;
        if (colonPos < 0) {
            header = line;
            value = null;
        } else {
            header = line.substring(0, colonPos);
            value = line.substring(colonPos + 1).trim();
        }

        return new ManifestEntry(header, value);
    }

    public String toString() {
        if (value == null) {
            return header;
        } else {
            return header + SEPARATOR + SPACE + value;
        }
    }

    public String getHeader() {
        return header;
    }

    public String getValue() {
        return value;
    }
}

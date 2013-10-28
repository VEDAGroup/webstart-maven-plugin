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
        this.header = formatHeader(header, value != null);
        this.value = value;
    }

    private String formatHeader(String header, boolean valueAvailable) {
        if (!valueAvailable) {
            return header;
        }

        // Due to the original configuration is XML based the header is always lowercase.
        final String firstCharacterInUpperCase = header.substring(0, 1).toUpperCase();
        return firstCharacterInUpperCase + header.substring(1);
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
}

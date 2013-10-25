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
        if (colonPos < 0) {
            throw new IllegalArgumentException("Given line '" + line
                    + "' is invalid because it does not contain expected separator.");
        }

        final String header = line.substring(0, colonPos + 1);
        final String value = line.substring(colonPos + 1);

        return new ManifestEntry(header, value);
    }

    public String toString() {
        return header + SEPARATOR + SPACE + value;
    }
}

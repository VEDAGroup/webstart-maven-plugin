package org.codehaus.mojo.webstart.manifestmodification;

import java.util.*;

/**
 * User: CPL
 * Date: 25.10.13
 * Time: 09:39
 * To change this template use File | Settings | File Templates.
 */
public class ManifestFile {

    private final Map<String, ManifestEntry> entries = new LinkedHashMap<String, ManifestEntry>();

    public void addEntry(final ManifestEntry entry) {
        entries.put(entry.getHeader(), entry);
    }

    public Collection<ManifestEntry> getEntries() {
        return entries.values();
    }

    public boolean hasEntry(String header) {
        return entries.containsKey(header);
    }

    public ManifestEntry getEntry(String header) {
        return entries.get(header);
    }
}

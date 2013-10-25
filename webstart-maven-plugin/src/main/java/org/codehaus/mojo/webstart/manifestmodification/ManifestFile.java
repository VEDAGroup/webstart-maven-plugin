package org.codehaus.mojo.webstart.manifestmodification;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * User: CPL
 * Date: 25.10.13
 * Time: 09:39
 * To change this template use File | Settings | File Templates.
 */
public class ManifestFile {
    private final List<ManifestEntry> entries = new ArrayList<ManifestEntry>();

    public void addEntry(final ManifestEntry entry){
        entries.add(entry);
    }

    public List<ManifestEntry> getEntries(){
        return entries;
    }
}

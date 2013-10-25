package org.codehaus.mojo.webstart.manifestmodification;

import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileReader;
import de.schlichtherle.truezip.file.TFileWriter;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Created with IntelliJ IDEA.
 * User: CPL
 * Date: 25.10.13
 * Time: 10:01
 * To change this template use File | Settings | File Templates.
 */
public class DefaultManifestTool implements ManifestTool {

    private static final String MANIFEST_FILE_NAME = "MANIFEST.MF";
    private static final String META_INF_DIR = "META-INF";
    private static final String MANIFEST_PATH_IN_ZIP = META_INF_DIR + "/"
            + MANIFEST_FILE_NAME;

    public ManifestFile readManifestFromJar(File jarFile) throws MojoExecutionException {

        // Check if JAR has a MANIFEST file
        checkIfManifestIsAvailable(jarFile);

        final File entry = getManifestPathInJar(jarFile);

        // Read the MANIFEST file from the JAR
        return internalRead(entry);
    }

    /**
     * Returns the default path to the Manifest file inside the JAR file.
     *
     * @param jarFile The path the the jar file.
     * @return Returns the default path to the Manifest file inside the JAR file.
     */
    private File getManifestPathInJar(File jarFile) {
        return new TFile(jarFile.getName() + "/" + MANIFEST_PATH_IN_ZIP);
    }

    private void checkIfManifestIsAvailable(File jarFile) throws MojoExecutionException {

        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(jarFile);
            final ZipEntry manifestEntry = zipFile.getEntry(MANIFEST_PATH_IN_ZIP);
            if (manifestEntry == null) {
                throw new MojoExecutionException("No Manifest found in JAR");
            }
        } catch (ZipException e) {
            throw new MojoExecutionException("JAR file has wrong format: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to read from JAR file: " + e.getMessage(), e);
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    // ignore on close
                }
            }
        }
    }

    private static ManifestFile internalRead(final File entry) throws MojoExecutionException {

        final ManifestFile manifest = new ManifestFile();

        // Read existing MANIFEST
        Reader reader = null;
        BufferedReader buffer = null;
        try {
            reader = new TFileReader(entry);
            buffer = new BufferedReader(reader);

            String line = buffer.readLine();
            while (line != null) {
                // Skip empty lines (e.g the last line is always empty)
                if (line.isEmpty()) {
                    continue;
                }
                manifest.addEntry(ManifestEntry.parseLine(line));
                line = buffer.readLine();
            }

        } catch (IOException e) {
            throw new MojoExecutionException("Failed to read Manifest from JAR: " + e.getMessage(), e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (buffer != null) {
                    buffer.close();
                }
            } catch (IOException e) {
                // Ignore on close
            }
        }

        return manifest;
    }

    public void writeManifestToJar(ManifestFile manifest, File jarFile) throws MojoExecutionException {

        Writer writer = null;
        BufferedWriter buffer = null;
        try {
            writer = new TFileWriter(getManifestPathInJar(jarFile), false);
            buffer = new BufferedWriter(writer);
            for (ManifestEntry entry : manifest.getEntries()) {
                buffer.write(entry.toString());
                buffer.newLine();

            }
        } catch (FileNotFoundException e) {
            // Should not happen due to previous check. But we handle it anyway.
            throw new MojoExecutionException("JAR File not found: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to write Manifest file to JAR: " + e.getMessage(), e);
        } finally {
            try {
                if (buffer != null) {
                    buffer.close();
                }
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                // Ignore on close
            }
        }
    }
}
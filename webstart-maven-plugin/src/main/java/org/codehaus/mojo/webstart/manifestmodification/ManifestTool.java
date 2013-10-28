package org.codehaus.mojo.webstart.manifestmodification;

import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: CPL
 * Date: 25.10.13
 * Time: 09:59
 * To change this template use File | Settings | File Templates.
 */
public interface ManifestTool {
    /**
     * Reads the existing Manifest file from a given JAR.
     *
     * @param jarFile The path of the JAR file
     * @return The read Manifest file
     * @throws org.apache.maven.plugin.MojoExecutionException
     *          if something wrong occurs
     */
    public ManifestFile readManifestFromJar(File jarFile) throws MojoExecutionException;

    /**
     * Writes the given Manifest file to given JAR file.
     *
     * @param manifest The Manifest to write
     * @param jarFile  The JAR file to write to
     * @throws org.apache.maven.plugin.MojoExecutionException
     *          if something wrong occurs
     */
    public void writeManifestToJar(ManifestFile manifest, File jarFile) throws MojoExecutionException;


    /**
     * This method has to be called at the end of any operations done with the ManifestTool. <br/>
     * Explanation:<br/> Depending on the underlying implementation it may be necessary to clean-up virtual file
     * systems or temporary files. Otherwise the following operations on the jar files may fail.
     */
    public void finalizeOperations() throws MojoExecutionException;
}

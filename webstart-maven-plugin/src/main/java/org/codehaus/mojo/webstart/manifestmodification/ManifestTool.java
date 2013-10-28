package org.codehaus.mojo.webstart.manifestmodification;
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;

/**
 * Tool to handle modify Manifest files in existing jar files.
 *
 * @author Christian Plätzinger <christian@plaetzinger.de>
 * @author $LastChangedBy$
 * @version $Revision$
 * @since 25 October 2013
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

    /**
     * Adds a JNLP file to a jar file.
     * @param jarFile jar file
     * @param jnlpFile JNLP file to add
     */
    public void addJnlpToJar(File jarFile, File jnlpFile) throws MojoExecutionException;

}

package org.codehaus.mojo.webstart.jarmodification;
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

import java.io.File;

/**
 * Utility class.
 *
 * @author Christian Plätzinger <christian@plaetzinger.de>
 * @author $LastChangedBy$
 * @version $Revision$
 * @since 30 October 2013
 */
public class JarUtil {

    public static String getApplicationNameFromJar(File jarFile) {
        return getApplicationNameFromJar(jarFile.getName());
    }

    public static String getApplicationNameFromJar(final String jarFileName) {

        String applicationName = jarFileName;
        // Remove .jar suffix
        applicationName = jarFileName.substring(0, jarFileName.toLowerCase().indexOf(".jar"));
        // Remove -SNAPSHOT suffix if present
        int snapshotSuffixPos = applicationName.indexOf("-SNAPSHOT");
        if (snapshotSuffixPos > 0) {
            applicationName = applicationName.substring(0, snapshotSuffixPos);
        }
        // Remove a version number if present
        applicationName = applicationName.replaceAll("(-)(\\d.*)", "");

        return applicationName;
    }
}

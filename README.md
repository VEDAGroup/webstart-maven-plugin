Webstart Maven Plugin (VEDA Fork)
=================================

This is a fork of the Codehaus Webstart Maven Plugin - http://mojo.codehaus.org/webstart/webstart-maven-plugin/

We need some improvements to the original plugin within a short timeframe and a possibility to properly release a new version. For that reason we decided to fork the original project, release our changes and then try to get our changes applied back to the original project.

This project, as the original, is licensed under Apache License 2.0
http://www.apache.org/licenses/LICENSE-2.0.html

How to build a release
======================
The following how-to is specific to VEDA releases. The releases are deployed the the VEDA internal Nexus.

Preconditions
-------------
Make sure all required changes are committed and pushed to the remote repository. Currently the branch
**master-distribution-veda** is used and contains all settings necessary to build a release.

The Maven build will fail (to be more precise: one of the integration tests) using Maven version 3.1.x. So
make sure you use Maven 3.0.x to build. On Windows this can be done for the current shell by the following commands:
    set M2_HOME=<your path to Maven 3.0.x>
    set PATH=%M2_HOME%bin;%PATH%

To be able to deploy to VEDA internal Nexus server you have to make sure your settings.xml contains
valid credentials:
    <servers>
        <server>
            <id>veda-releases</id>
            <username>Username</username>
            <password>Password</password>
        </server>
    </servers>

During the release process the maven-release-plugin will push changes to the remote repository on GitHub.
If not using ssh based authentication it might be required to store your GitHub credentials in the settings.xml
file as well.
    <profiles>
        <profile>
            <id>github-release</id>
            <properties>
                <username>...</username>
                <password>...</password>
            </properties>
        </profile>
    </profiles>
    <activeProfiles>
        <activeProfile>github-release</activeProfile>
    </activeProfiles>


Build the release
---------------
- Clone the repository into an empty directory `git clone https://github.com/VEDAGroup/webstart-maven-plugin.git -b master-distribution-veda .`
- Make a dry run `mvn release:prepare -DdryRun=true`
- Prepare the release `mvn release:prepare`
- Perform the release `mvn release:perform`

If something went wrong here are some useful commands to get back to a clean state
    mvn release:clean
    mvn release:rollback
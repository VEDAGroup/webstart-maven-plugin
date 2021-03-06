package org.codehaus.mojo.webstart.generator;

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

import org.apache.commons.lang.BooleanUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.log.NullLogSystem;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.WriterFactory;

import java.io.File;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.TimeZone;

/**
 * The abstract superclass for classes that generate the JNLP files produced by the
 * various MOJOs available in the plugin.
 *
 * @author Kevin Stembridge
 * @version $Revision$
 * @since 30 Aug 2007
 */
public abstract class AbstractGenerator
{

    private VelocityEngine engine;

    private final MavenProject mavenProject;

    private Template velocityTemplate;

    private final File outputFile;

    private final String mainClass;

    private final String encoding;

    private GeneratorExtraConfig extraConfig;

    private Log log;


    /**
     * Creates a new {@code AbstractGenerator}.
     *
     * @param resourceLoaderPath    used to find the template in conjunction to inputFileTemplatePath
     * @param outputFile            The location of the file to be generated.
     * @param inputFileTemplatePath relative to resourceLoaderPath
     * @param mainClass             The text that should replace the $mainClass placeholder in the JNLP template.
     * @throws IllegalArgumentException if any argument is null.
     */
    protected AbstractGenerator( Log log, MavenProject mavenProject, File resourceLoaderPath,
                                 String defaultTemplateResourceName, File outputFile, String inputFileTemplatePath,
                                 String mainClass, String webstartJarURL, String encoding )
    {

        this.log = log;
        if ( mavenProject == null )
        {
            throw new IllegalArgumentException( "mavenProject must not be null" );
        }

        if ( resourceLoaderPath == null )
        {
            throw new IllegalArgumentException( "resourceLoaderPath must not be null" );
        }

        if ( outputFile == null )
        {
            throw new IllegalArgumentException( "outputFile must not be null" );
        }

        if ( mainClass == null )
        {
            throw new IllegalArgumentException( "mainClass must not be null" );
        }

        this.outputFile = outputFile;
        this.mainClass = mainClass;
        this.mavenProject = mavenProject;
        this.encoding = encoding;

        Properties props = new Properties();

        if ( inputFileTemplatePath != null )
        {
            props.setProperty( VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS,
                               "org.apache.velocity.runtime.log.NullLogSystem" );
            props.setProperty( "file.resource.loader.path", resourceLoaderPath.getAbsolutePath() );

            initVelocity( props );

            if ( !engine.templateExists( inputFileTemplatePath ) )
            {
                log.warn( "Warning, template not found. Will probably fail." );
            }
        }
        else
        {
            log.info( "No template specified Using default one." );

            inputFileTemplatePath = defaultTemplateResourceName;

            log.debug( "***** Webstart JAR URL: " + webstartJarURL );

            props = new Properties();
            props.setProperty( "resource.loader", "jar" );
            props.setProperty( "jar.resource.loader.description",
                               "Jar resource loader for default webstart templates" );
            props.setProperty( "jar.resource.loader.class",
                               "org.apache.velocity.runtime.resource.loader.JarResourceLoader" );
            props.setProperty( "jar.resource.loader.path", webstartJarURL );

            initVelocity( props );

            if ( !engine.templateExists( inputFileTemplatePath ) )
            {
                log.error( "Inbuilt template not found!! " + defaultTemplateResourceName + " Will probably fail." );
            }
        }

        try
        {
            this.velocityTemplate = engine.getTemplate( inputFileTemplatePath, encoding );
        }
        catch ( Exception e )
        {
            IllegalArgumentException iae =
                new IllegalArgumentException( "Could not load the template file from '" + inputFileTemplatePath + "'" );
            iae.initCause( e );
            throw iae;
        }
    }

	/**
	 * Create a proper JNLP resource element text for jar or nativelib resources.
	 *
	 * @param resourceName       the name of the resource to add
	 * @param resourcePathPrefix optional prefix to prepend to the resource name, separated with "/"; may be <tt>null</tt>
	 * @param version            optional version String; omitted if <tt>null</tt> or empty
	 * @param isNative           <tt>false</tt> if this is a jar-resource; <tt>true</tt> if this is a nativelib resource
	 * @param containsMainClass  whether the resource contains the main class for this Webstart project; if <tt>true</tt>,
	 *                           the main attribute is added with a value of <tt>true</tt>
	 *
	 * @return the resource element to be added to a JNLP resources block
	 */
	protected static String createReferenceText( String resourceName, String resourcePathPrefix, String version,
												 boolean isNative,
												 boolean containsMainClass ) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(isNative ? "<nativelib href=\"" : "<jar href=\"");
		if (StringUtils.isNotEmpty(resourcePathPrefix)) {
			buffer.append(resourcePathPrefix);
			buffer.append('/');
		}
		buffer.append(resourceName);
		buffer.append("\"");

		if (StringUtils.isNotEmpty(version)) {
			buffer.append(" version=\"").append(version).append("\"");
		}

		if (containsMainClass) {
			buffer.append(" main=\"true\"");
		}

		buffer.append("/>");
		return buffer.toString();
	}

	/**
	 * Check if the given qualifier indicates that this is a native resource.
	 *
	 * @param classifier the classifier to check
	 *
	 * @return <tt>true</tt> if the classifier is not null and starts with the term "native"
	 */
	protected static boolean isNativeClassifier( String classifier ) {
		return classifier != null && classifier.startsWith("native");
	}

	private void initVelocity( Properties props )
    {
        try
        {
            engine = new VelocityEngine();
            engine.setProperty( "runtime.log.logsystem", new NullLogSystem() );
            engine.init( props );
        }
        catch ( Exception e )
        {
            IllegalArgumentException iae = new IllegalArgumentException( "Could not initialise Velocity" );
            iae.initCause( e );
            throw iae;
        }
    }

    public void setExtraConfig( GeneratorExtraConfig extraConfig )
    {
        this.extraConfig = extraConfig;
    }

    /**
     * Generate the JNLP file.
     *
     * @throws Exception
     */
    public final void generate()
        throws Exception
    {
        VelocityContext context = createAndPopulateContext();

        Writer writer = WriterFactory.newWriter( outputFile, encoding );

        try
        {
            velocityTemplate.merge( context, writer );
            writer.flush();
        }
        catch ( Exception e )
        {
            throw new Exception(
                "Could not generate the template " + velocityTemplate.getName() + ": " + e.getMessage(), e );
        }
        finally
        {
            writer.close();
        }

    }

    /**
     * Subclasses must implement this method to return the text that should
     * replace the $dependencies placeholder in the JNLP template.
     *
     * @return The dependencies text, never null.
     */
    protected abstract String getDependenciesText();

    /**
     * Creates a Velocity context and populates it with replacement values
     * for our pre-defined placeholders.
     *
     * @return Returns a velocity context with system and maven properties added
     */
    protected VelocityContext createAndPopulateContext()
    {
        VelocityContext context = new VelocityContext();

        context.put( "dependencies", getDependenciesText() );

        // Note: properties that contain dots will not be properly parsed by Velocity. 
        // Should we replace dots with underscores ?        
        addPropertiesToContext( System.getProperties(), context );
        addPropertiesToContext( mavenProject.getProperties(), context );

        context.put( "project", mavenProject.getModel() );
        context.put( "jnlpCodebase", extraConfig.getJnlpCodeBase() );

        // aliases named after the JNLP file structure
        context.put( "informationTitle", mavenProject.getModel().getName() );
        context.put( "informationDescription", mavenProject.getModel().getDescription() );
        if ( mavenProject.getModel().getOrganization() != null )
        {
            context.put( "informationVendor", mavenProject.getModel().getOrganization().getName() );
            context.put( "informationHomepage", mavenProject.getModel().getOrganization().getUrl() );
        }

        // explicit timestamps in local and and UTC time zones
        Date timestamp = new Date();
        context.put( "explicitTimestamp", dateToExplicitTimestamp( timestamp ) );
        context.put( "explicitTimestampUTC", dateToExplicitTimestampUTC( timestamp ) );

        context.put( "outputFile", outputFile.getName() );
        context.put( "mainClass", this.mainClass );
        context.put( "encoding", encoding );
        context.put( "input.encoding", encoding );
        context.put( "output.encoding", encoding );

        // TODO make this more extensible
        context.put( "allPermissions", Boolean.valueOf( BooleanUtils.toBoolean( extraConfig.getAllPermissions() ) ) );
        context.put( "offlineAllowed", Boolean.valueOf( BooleanUtils.toBoolean( extraConfig.getOfflineAllowed() ) ) );
        context.put( "jnlpspec", extraConfig.getJnlpSpec() );
        context.put( "j2seVersion", extraConfig.getJ2seVersion() );

        return context;
    }

    private void addPropertiesToContext( Properties properties, VelocityContext context )
    {
        for ( Iterator iter = properties.keySet().iterator(); iter.hasNext(); )
        {
            String nextKey = (String) iter.next();
            String nextValue = properties.getProperty( nextKey );
            context.put( nextKey, nextValue );
        }
    }

    /**
     * Converts a given date to an explicit timestamp string in local time zone.
     *
     * @param date a timestamp to convert.
     * @return a string representing a timestamp.
     */
    private String dateToExplicitTimestamp( Date date )
    {
        DateFormat df = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ssZ" );
        return "TS: " + df.format( date );
    }

    /**
     * Converts a given date to an explicit timestamp string in UTC time zone.
     *
     * @param date a timestamp to convert.
     * @return a string representing a timestamp.
     */
    private String dateToExplicitTimestampUTC( Date date )
    {
        DateFormat df = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        df.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
        return "TS: " + df.format( date ) + "Z";
    }

    /**
     * Add {@code level} space caracteres at the begin of each lines of the
     * given {@code text}.
     *
     * @param level the number of space caracteres to add
     * @param text  the text to prefix
     * @return the indented text
     */
    protected String indentText( int level, String text )
    {
        StringBuffer buffer = new StringBuffer();
        String[] lines = text.split( "\n" );
        String prefix = "";
        for ( int i = 0; i < level; i++ )
        {
            prefix += " ";
        }
        for ( int i = 0, j = lines.length; i < j; i++ )
        {
            buffer.append( prefix ).append( lines[i] ).append( "\n" );
        }
        return buffer.toString();
    }
}

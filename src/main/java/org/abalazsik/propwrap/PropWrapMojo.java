
package org.abalazsik.propwrap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Properties;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 *
 * @author ador
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES, executionStrategy = "always")
public class PropWrapMojo extends AbstractMojo {

	/**
	 * Package where to generate the Wrapper classes
	 */
	@Parameter(name = "pkg", required = true)
	private String pkg;
	
	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	private MavenProject project;
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		
		this.getLog().debug("Wrapping those .properties files");
		
		File resourcesDir = new File(project.getBasedir().getAbsolutePath() + "/src/main/resources");
		
		File generatedSourcesDir = new File(project.getBasedir().getAbsolutePath() + "/target/generated-sources/wrappers" + pkg.replaceAll(".", "/"));
		
		generatedSourcesDir.mkdirs();
		
		boolean fail = false;
		
		Properties props = new Properties();
		
		String[] propFiles = resourcesDir.list((File arg0, String fileName) -> {
			return fileName.endsWith(".properties");
		});
		
		if (propFiles == null) {
			this.getLog().debug("No properties files in the " + resourcesDir.getAbsolutePath() + " directory");
			return;
		}
		
		project.addCompileSourceRoot(generatedSourcesDir.getAbsolutePath());
		
		for (String propFile : propFiles) {
			String name = propFile.substring(0, propFile.length() - ".properties".length());

			File outputFile = new File(generatedSourcesDir.getAbsolutePath() + "/" + WrapperGenerator.normalize(name) + ".java");
			
			props.clear();
			
			try (
					FileInputStream inputStream = new FileInputStream(resourcesDir.getAbsolutePath() + "/" +propFile);
					FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
					PrintWriter printWriter = new PrintWriter(fileOutputStream)){ //heh
				
				props.load(inputStream);
				
				WrapperGenerator.writeWrapper(printWriter, pkg, name, props);
				
			} catch (Exception e) {
				fail = true;
				if (outputFile.exists()) {
					outputFile.delete();//do not produce partial files
				}
				this.getLog().error("Failed to generate the file: " + outputFile.getAbsolutePath(), e);
			}
		}

		if (fail) {
			throw new RuntimeException("Failed to construct wrapper files. See above");
		}
		
	}
	
}

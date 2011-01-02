package com.soebes.maven.plugins.mlv;

import java.io.File;
import java.net.URL;

/**
 * This is a class which exposes helper methods to do Unit testing
 * in a Maven/Eclipse environment.
 * @author Karl Heinz Marbaise
 */
public class TestBase {
	/**
	 * This method will give you back
	 * the filename incl. the absolute path name
	 * to the resource. 
	 * If the resource does not exist it will give
	 * you back the resource name incl. the path.
	 * 
	 * It will give you back an absolute path
	 * incl. the name which is in the same directory 
	 * as the the class you've called it from.
	 * 
	 * @param name
	 * @return
	 */
	public String getFileResource(String name) {
		URL url = this.getClass().getResource(name);
		if (url != null) {
			return url.getFile();
		} else {
			//We have a file which does not exists
			//We got the path
			url = this.getClass().getResource(".");
			return url.getFile() + name;
		}
	}

	/**
	 * Return the base directory of the project.
	 * @return The base folder.
	 */
	public String getMavenBaseDir() {
		//basedir is defined by Maven 
		//but the above will not work under Eclipse.
		//So there I'M using user.dir 
		return System.getProperty("basedir", System.getProperty("user.dir", "."));
	}

	/**
	 * Return the <code>target</code> directory of the current project.
	 * @return The target folder.
	 */
	public String getTargetDir() {
		return getMavenBaseDir() + File.separatorChar + "target" + File.separator;
	}
	
	/**
	 * This will give you the <code>src</code> folder.
	 * @return The string
	 */
	public String getSrcDirectory () {
		return getMavenBaseDir() + File.separator + "src" ;
	}

	/**
	 * This will give you the <code>src/test</code> folder.
	 * @return String representing the folder.
	 */

	public String getTestDirectory () {
		return getSrcDirectory() + File.separator + "test";
	}

	/**
	 * This will give you the <code>src/test/resources</code> folder.
	 * @return The string representing the folder.
	 */
	public String getTestResourcesDirectory() {
		return getTestDirectory() + File.separator + "resources" + File.separator;
	}

}

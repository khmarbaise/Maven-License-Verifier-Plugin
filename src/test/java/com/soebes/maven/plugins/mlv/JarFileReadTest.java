package com.soebes.maven.plugins.mlv;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.testng.annotations.Test;

public class JarFileReadTest extends TestBase {

	public void readJarFileTest() throws IOException {
		JarFile jarFile = new JarFile(new File(getTestResourcesDirectory() + File.separator + "maven-eclipse-plugin-2.7.jar"));
		Manifest manifest = jarFile.getManifest();
		Attributes attributes = manifest.getMainAttributes();
		for (Entry<Object, Object> item : attributes.entrySet()) {
			System.out.println("key:" + item.getKey());
		}
		Map<String, Attributes> result = manifest.getEntries();
		for (Map.Entry<String, Attributes> item : result.entrySet()) {
			Attributes attrib = item.getValue();
			System.out.println(" ****=> " + item.getKey() );
		}
		
		for (Enumeration<JarEntry> item = jarFile.entries(); item.hasMoreElements(); ) {
			JarEntry jarEntry = item.nextElement();
			System.out.println("Name:" + jarEntry.getName());
			String name = jarEntry.getName().toUpperCase();
			File f = new File(name);
			String fn = f.getName();
			String fp = f.getPath();
			System.out.println(fn + " ::: " + fp);
			if (fn.matches("^LICENSE.*")) {
				System.out.println(" --> Possible License file found!");
			}
		}
		
		jarFile.close();
	}
}

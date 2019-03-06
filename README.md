# Maven License Verifier Plugin

# Overview

If you are working with Maven it often happens that someone adds a new library to 
a project cause its needed somehow (or whatevery the reason is). The problem with 
such cases is: What kind of license does this library have? Is it one of the 
allowed licenses in the company?  Or is it a critical license(for example GPL in 
commerical environment)?

This plugin can help you in the above situations. It will check the licenses of
all artifacts during every build against a list of valid licenses or licenses which should
be checked in more detail (warning) or unknown licenses or invalid licenses.

## License

[Apache License, Version 2.0, January 2004](http://www.apache.org/licenses/)

## Issue Tracker

[The Issue Tracker](https://github.com/khmarbaise/Maven-Licenses-Verifier-Plugin/issues)

## Status

 * Reports are created.
 * Currently no fail of a build independant of the category.
 ** Later the default will be: failOnInvalid and failOnUnknown.


## TODOs

 * Make the reports better.
 * http://www.sonatype.com/people/2011/01/how-to-use-aether-in-maven-plugins/
 * http://maven.apache.org/guides/mini/guide-maven-classloading.html (Check this!!)

## Usage

The first and simplest usage is to configure the Maven Licenses Verifier Plugin

    <plugin>
      <groupId>com.soebes.maven.plugins</groupId>
      <artifactId>license-verifier-maven-plugin</artifactId>
      <version>0.5</version>
      <executions>
        <execution>
          <phase>test</phase>
          <goals>
            <goal>check</goal>
          </goals>
        </execution>
      </executions>
    </plugin>

## Settings Configuration

If you like you can configure an appropriate plugin group in your
settings.xml file to make life a little bit easier.

    <settings>
      ...
      <pluginGroups>
        <pluginGroup>com.soebes.maven.plugins</pluginGroup>
      </pluginGroups>
      ...
    </settings>

The above setting makes it possible to call the plugin on command 
line simply:

  mvn license-verifier:check 


## Reporting about Licenses



Examples
--------

A real example can be looked at [Licenses Verifier Maven Plugin Example](http://khmarbaise.github.com/mlvp-example/licenseverifierreport.html)


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
 * Currently only working with Maven 2.2.1 (may be below but not checked!)
 * Currently no fail of a build indenpendant of the category.
 ** Later the default will be: failOnInvalid and failOnUnknown.


## TODOs

 * Make the reports better.
 * Get it run on Maven 3 as well.

## Usage

The first and simplest usage is to configure the Maven Licenses Verifier Plugin

    <plugin>
      <groupId>com.soebes.maven.plugins.mlv</groupId>
      <artifactId>maven-licenses-verifier-plugin</artifactId>
      <version>0.2-SNAPSHOT</version>
      <executions>
        <execution>
          <phase>test</phase>
          <goals>
            <goal>check</goal>
          </goals>
        </execution>
      </executions>
    </plugin>

## Reporting about Licenses



Examples
--------

You can see examples of the current state of the reports as [part I](http://blog.soebes.de/uploads/Screenshot-Artifact-License-Categories.png)
 and [Part II](http://blog.soebes.de/uploads/Screenshot-Artifact-Categories.png) and of course
of the resulting report the [configuration which is used](http://blog.soebes.de/uploads/Screenshot-License-Configuration.png).


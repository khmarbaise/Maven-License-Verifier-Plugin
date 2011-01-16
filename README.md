Maven License Verifier Plugin
=============================

Overview
========
If you are working with Maven it often happens that someone adds a new library to 
a project cause its needed somehow. The problem with such cases is: What kind
of license does this library have? Is it one of the allowed licenses in the company?
Or is it a critical license?

This plugin can help you in the above situations. It will check during every build
the licenses and check it against a list of valid licenses or licenses which should
be checked in more detail (warning) or unknown licenses.

License
-------
[Apache License, Version 2.0, January 2004](http://www.apache.org/licenses/)

Issue Tracker
-------------
[The Issue Tracker](http://supose.org/projects/show/mlv)

Status
------

TODOs
-----

Usage
-----

The first and simplest usage is to configure the Maven Licenses Verifier Plugin


    <plugin>
      <groupId>com.soebes.maven.plugins.mlv</groupId>
      <artifactId>maven-licenses-verifier-plugin</artifactId>
      <version>0.2-SNAPSHOT</version>
      <configuration>
        <verbose>true</verbose>
      </configuration>
      <executions>
        <execution>
          <phase>test</phase>
          <goals>
            <goal>check</goal>
          </goals>
        </execution>
      </executions>
    </plugin>


Examples
--------

In My Blog you can see [example outputs](http://blog.soebes.de/index.php?/archives/320-Maven-Licenses-Verifier-Plugin.html)


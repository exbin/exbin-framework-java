ExBin Framework Libraries
=========================

Framework provides various libraries for swing desktop applications support.

Homepage: https://exbin.org  

Structure
---------

Project is constructed from multiple repositories.

  * modules - Sources split in separate modules
  * src - Sources related to building distribution packages
  * deps - Folder for downloading libraries for dependency resolution
  * lib - Additional libraries
  * gradle - Gradle wrapper

Compiling
---------

Build commands: "gradle build" and "gradle distZip"

Java Development Kit (JDK) version 8 or later is required to build this project.

For project compiling Gradle 7.1 build system is used: https://gradle.org

You can either download and install gradle or use gradlew or gradlew.bat scripts to download separate copy of gradle to perform the project build.

On the first build there will be an attempt to download all required dependecy modules and currently it's necessary to execute build twice.

Alternative is to have all dependecy modules stored in local maven repository - Manually download all dependencies from GitHub (clone repositories from github.com/exbin - see. deps directory for names) and run "gradle publish" on each of them.

License
-------

Project uses various libraries with specific licenses and some tools are licensed with multiple licenses with exceptions for specific modules to cover license requirements for used libraries.

Primary license: Apache License, Version 2.0 - see LICENSE.txt

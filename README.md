ExBin Framework Libraries
=========================

Framework provides various libraries for swing desktop applications support.

Homepage: http://exbin.org  

Structure
---------

Project is constructed from multiple repositories.

  * modules - Sources split in separate modules
  * src - Sources related to building distribution packages
  * deps - Folder for downloading libraries for dependency resolution
  * doc - Documentation + related presentations
  * gradle - Gradle wrapper

Compiling
---------

Java Development Kit (JDK) version 8 or later is required to build this project.

For project compiling Gradle 6.0 build system is used: http://gradle.org

You can either download and install gradle or use gradlew or gradlew.bat scripts to download separate copy of gradle to perform the project build.

Build commands: "gradle build" and "gradle distZip"

Dependecies are either downloaded or loaded from local maven repository. 

License
-------

Project uses various libraries with specific licenses and some tools are licensed with multiple licenses with exceptions for specific modules to cover license requirements for used libraries.

Main license is: GNU/LGPL (see gpl-3.0.txt AND lgpl-3.0.txt)  
License for documentation: GNU/FDL (see doc/fdl-1.3.txt)  

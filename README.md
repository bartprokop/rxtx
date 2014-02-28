rxtx
====

Serial Port rxtx library

This is an attemp to revive the old but very good rxtx library that allows for using serial ports in Java. I was using rxtx 2.1.7 for many years. But as my projects have evolved from IDE build through ant to Maven and JDK from 1.1 to 1.8 this was aparent that rxtx is unmaintained. So i tried to get the sources (now not easilly avaiable) of latest stable version (stable in terms of used in many production system) and continue that great piece of software on github.

The intention is to release version 2.2.0 with very minimal changes to 2.1.7r2 codebase. Just make it clean Maven project and add digital signature for all classes (because of a need to use it in applets). Version 2.2.1 will undergo some refactoring to meet contemporary Java good practices.

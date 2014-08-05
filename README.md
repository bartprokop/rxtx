rxtx
====

Serial Port rxtx library

Version 2.1.8 is:
 - source code taken from rxtx-2.1-7r2.zip
 - added Maven POM
 - digitally signed with recognized certificate
 - compiled against Java 1.3 (as original rxtx)

 Version 2.2.0.pre2 is:
 - source code taken from rxtx-2.2pre2-src.zip
 - digitally signed with recognized certificate and timestamped
 - compiled against Java 1.3

 Version 2.2.0.20110203 is:
 - latest avaiable in CVS, snapshot from 2011.02.03
 - digitally signed with recognized certificate and timestamped
 - compiled against Java 1.3
 
Currently I'm working on version 2.3.0:
 - added Findbugs build checks (107 deficiencies reported on Low threshold, 21 on Default, all fixed !!!)
 - Java 1.7
 - some new features....

 
This is an attemp to revive the old but very good rxtx library that allows for using serial ports in Java. I was using rxtx 2.1.7 for many years. But as my projects have evolved from IDE build through ant to Maven and JDK from 1.1 to 1.8 this was aparent that rxtx is unmaintained. So i tried to get the sources (now not easilly avaiable) of latest stable version (stable in terms of used in many production system) and continue that great piece of software on github.

The intention is to release version 2.2.0 with very minimal changes to 2.1.7r2 codebase. Just make it clean Maven project and add digital signature for all classes (because of a need to use it in applets). Version 2.2.1 will undergo some refactoring to meet contemporary Java good practices.

Resources:
 * https://code.google.com/p/create-lab-commons/source/browse/trunk/java/lib/rxtx/
 
> RXTX binary builds provided as a courtesy of Mfizz Inc. (http://mfizz.com/).
> Please see http://mfizz.com/oss/rxtx-for-java for more information.

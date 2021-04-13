# Alice 2

[Alice](https://www.alice.org) is an innovative block-based programming environment that makes it easy to create animations, build interactive narratives, or program simple games in 3D.

### Latest Released Build: [![](https://img.shields.io/badge/2.5.4.1-green.svg)](http://www.alice.org/get-alice/alice-2/)

## Building Alice 2 from Source

Use maven to compile the `src` directory or build and install a jar into the `target` directory.

`mvn compile`

`mvn install`

When launching include the compiled files or jar and the files in `lib`.
Launch `edu.cmu.cs.stage3.alice.authoringtool.JAlice` from the Required folder, which holds the supporting files.

Include the following VM arguments:

`-Dpython.home=jython-2.1 -Dpython.path=jython-2.1/Lib/alice -Xmx1024m -Dfile.encoding=UTF-8`

On Windows, to include the Dynamic-Link Libraries (DLLs), add:

`-Djava.library.path=lib/win32;externalLib/win32`

On Mac add:

`-Dapple.laf.useScreenMenuBar=true`

## Release Builds

Official builds of Alice are built using Install4J 7.

The file `installer/alice2.install4j` can be built using Install4J or `mvn install`. Maven will automatically use Install4J if it is installed.

It will create ten files, for each combination of gallery (2) and OS installer(5):

Gallery:
* English
* Spanish

OS
* Windows
* Mac
* Linux (tar, sh, deb)


## Additional documentation

Some historical documents reside in the `docs` directory.

Not all information there is up to date or accurate, but it may be useful.

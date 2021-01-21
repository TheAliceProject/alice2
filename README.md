# Alice 2

[Alice](https://www.alice.org) is an innovative block-based programming environment that makes it easy to create animations, build interactive narratives, or program simple games in 3D.

### Latest Released Build:

[![](https://img.shields.io/badge/master-2.5-green.svg)](http://www.alice.org/get-alice/alice-2/)

## Building Alice 2 from the source

Requires the latest version of Alice 2 (Required folder).
Add all the jar files from Required/externalLib to your project.
Include the following VM arguments when debugging/running the code.
  -Dpython.home=jython-2.1 -Dpython.path=jython-2.1/Lib/alice -Djava.library.path=lib/win32;externalLib/win32
java.library.path only required for Windows to include the Dynamic-Link Libraries (dll).

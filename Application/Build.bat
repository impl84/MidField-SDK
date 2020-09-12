@echo off

set RTE=..\Runtime\
set PATH=%RTE%;%RTE%\jre\bin;%PATH%
set CLASSPATH=%RTE%\MfsCore.jar;%RTE%\MfsGui.jar;%RTE%gson-2.8.2.jar;.\bin;%CLASSPATH%

@echo on

javac --release 11 -d .\bin .\src\com\midfield_system\app\util\*.java
javac --release 11 -d .\bin .\src\com\midfield_system\app\performer\ex0\*.java
javac --release 11 -d .\bin .\src\com\midfield_system\app\performer\ex1\*.java
javac --release 11 -d .\bin .\src\com\midfield_system\app\selector\*.java
javac --release 11 -d .\bin .\src\com\midfield_system\app\videochat\*.java

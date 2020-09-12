@echo off

set RTE=..\Runtime\
set PATH=%RTE%;%RTE%\jre\bin;%PATH%
set CLASSPATH=%RTE%\MfsCore.jar;%RTE%\MfsGui.jar;%RTE%gson-2.8.2.jar;.\bin;%CLASSPATH%

@echo on

java com.midfield_system.app.performer.ex0.StreamToStreamEx0

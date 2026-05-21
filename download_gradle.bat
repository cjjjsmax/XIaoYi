@echo off
set JAVA_HOME=D:\jdk
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

rem Download Gradle 8.13 wrapper
%JAVA_EXE% -Dgradle.wrapper.distribution.url=https://services.gradle.org/distributions/gradle-8.13-bin.zip -jar "%~dp0gradle\wrapper\gradle-wrapper.jar" build

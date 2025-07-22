@echo off
rem ###################################
rem Batch file for running callite report
rem ###################################
setlocal
set callite_home=%~dp0
rem echo %callite_home%
if exist "%callite_home%/lib/callite-report-tool.jar" goto :valid

:notfound

echo ############################################################
echo   Error: CALLITE files not found
echo ############################################################
PAUSE
goto :end

:valid
rem ###############
rem Set path to location of dll
rem ###############
set path=%callite_home%/lib;%path%

rem ###############
rem starting callite
rem ###############
:start
set classpath=%callite_home%/lib/callite-report-tool.jar
set classpath=%classpath%;%callite_home%/lib/crimson.jar
set classpath=%classpath%;%callite_home%/jython/dom4j-1.6.1.jar
set classpath=%classpath%;%callite_home%/lib/dsm2-input-model.jar
set classpath=%classpath%;%callite_home%/lib/hec-monolith-5.3.5.jar
set classpath=%classpath%;%callite_home%/lib/flogger-0.5.1.jar
set classpath=%classpath%;%callite_home%/lib/flogger-system-backend-0.5.1.jar
set classpath=%classpath%;%callite_home%/lib/itext-hyph-xml.jar
set classpath=%classpath%;%callite_home%/lib/iText.jar
set classpath=%classpath%;%callite_home%/lib/jfreechart.jar
set classpath=%classpath%;%callite_home%/lib/pdf-renderer.jar

"%callite_home%/jre1.8.0_212/bin/java" -mx512m  -Djava.library.path="%callite_home%/lib" -classpath "%classpath%" gov.ca.dwr.callite.Batch %1%
:end
endlocal 
rem 

REM ********************************************************************
REM *** vtymkiv. All rights reserved.
REM ********************************************************************
REM ***  $file:   run.bat  $
REM ********************************************************************

@echo on


set "JAVA_HOME=C:\Users\aquan\.eclipse\oracleJdk-24"
set "PATH=%PATH%;%JAVA_HOME%\bin"
set "JAVA_OPTIONS="
set "JAVA_FX_HOME="
java -add-modules javafx.controls,javafx.fxml --module-path %JAVA_FX_HOME%\lib -jar %~dp0w2e-ui.jar


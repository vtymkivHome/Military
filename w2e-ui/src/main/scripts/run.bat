REM ********************************************************************
REM *** vtymkiv. All rights reserved.
REM ********************************************************************
REM ***  $file:   run.bat  $
REM ********************************************************************

@echo on

rem JAVA_OPTIONS="-DpathToExcel=army_state_on_2022_05_24.xlsx  -DpathToDoc=generated_from_template.docx"

set "JAVA_HOME=C:\Users\aquan\.eclipse\oracleJdk-24"
set "PATH=%PATH%;%JAVA_HOME%\bin"
set "JAVA_OPTIONS=-Dw2e.config.path=w2e/config/w2e.yml"
set EXEC_JAR="wd2excel.jar --pathToDoc=%1 --pathToExcel=%2"
echo -e "\n JAVA_HOME=%JAVA_HOME%"
echo -e "\n --pathToDoc=%1, --pathToExcel=%2"
java  %JAVA_OPTIONS% -jar %EXEC_JAR%


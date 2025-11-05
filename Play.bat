@echo off
echo ====================================
echo Endangered Animals Wordle - Launcher
echo ====================================
echo.

echo Current directory: %CD%
echo.

echo Looking for JAR files...
dir /b *.jar
echo.

echo Cleaning old class files...
del /Q *.class 2>nul

echo Compiling Java files...
javac -encoding UTF-8 -cp ".;slf4j-api-1.7.36.jar;slf4j-nop-1.7.36.jar;sqlite-jdbc-3.45.0.0.jar" Database.java GameLogic.java WordleGUI.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ========================================
    echo ERROR: Compilation failed!
    echo ========================================
    pause
    exit /b %ERRORLEVEL%
)

echo Compilation successful!
echo.
echo Launching Endangered Animals Wordle...
echo ========================================
echo.

java -cp ".;slf4j-api-1.7.36.jar;slf4j-nop-1.7.36.jar;sqlite-jdbc-3.45.0.0.jar" WordleGUI

echo.
echo ========================================
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Program exited with error code %ERRORLEVEL%
) else (
    echo Program closed normally
)
echo ========================================
echo.
pause
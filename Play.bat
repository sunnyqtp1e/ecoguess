@echo off
echo ====================================
echo Endangered Animals Wordle - Launcher
echo ====================================
echo.

REM Display current directory
echo Current directory: %CD%
echo.

REM List all JAR files
echo Looking for JAR files...
dir /b *.jar
echo.

REM Clean old class files
echo Cleaning old class files...
del /Q *.class 2>nul

REM Compile WITHOUT JSON library (using built-in parsing)
echo Compiling Java files...
javac -encoding UTF-8 -cp ".;slf4j-api-1.7.36.jar;slf4j-nop-1.7.36.jar;sqlite-jdbc-3.45.0.0.jar" Database.java GameLogic.java AnimalAPIService.java WordleGUI.java

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

REM Run WITHOUT JSON library
java -cp ".;slf4j-api-1.7.36.jar;slf4j-nop-1.7.36.jar;sqlite-jdbc-3.45.0.0.jar" WordleGUI

echo.
echo ========================================
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Program exited with error code %ERRORLEVEL%
) else (
    echo Program closed normally)
echo ========================================
echo.
pause
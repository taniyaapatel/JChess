@echo off
echo ============================================
echo   Chess Engine - Build and Run
echo ============================================
echo.

:: Clean previous build
if exist out rmdir /s /q out
mkdir out

echo [1/2] Compiling Java sources...
javac -d out -sourcepath src src\com\chess\Main.java

if %ERRORLEVEL% neq 0 (
    echo.
    echo [ERROR] Compilation failed!
    pause
    exit /b 1
)

echo [2/2] Starting Chess Server...
echo.
java -cp out com.chess.Main

pause

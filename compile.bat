@echo off
setlocal

echo.
echo =========================
echo BUILD START
echo =========================

if exist out (
	rmdir /s /q out
)
mkdir out

echo * Compiling Java source files...
javac -cp "lib\jl1.0.1.jar;." -d out ^
src\main\java\com\example\pong\*.java ^
src\main\java\com\example\pong\screen\*.java

if %errorlevel% neq 0 (
    echo.
    echo ❌ COMPILATION FAILED
    pause
    exit /b
)

echo * Copying resources...
xcopy src\main\resources out\ /E /I /Y > nul

if errorlevel 1 exit /b 1

echo * Building JAR...
jar cfm pong.jar manifest.txt -C out .

if %errorlevel% neq 0 (
    echo.
    echo ❌ JAR BUILD FAILED
    pause
    exit /b
)

echo.
echo =========================
echo BUILD SUCCESS
echo =========================
echo.
echo Starting with:
echo java -jar pong.jar
echo.

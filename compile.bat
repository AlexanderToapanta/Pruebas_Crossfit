@echo off
echo Compilando IroncladBox CrossFit...
echo ==================================
if exist bin rmdir /s /q bin
mkdir bin
echo Compilando todas las clases...
dir /s /B src\*.java > sources.txt
javac -encoding UTF-8 -d bin -cp "lib\*" @sources.txt
del sources.txt
if %errorlevel% neq 0 (
    echo ERROR en compilacion
    pause
    exit /b 1
)
echo Copiando imagenes y recursos...
if exist src\com\ironcladbox\images (
    xcopy /Y /I src\com\ironcladbox\images\* bin\com\ironcladbox\images\ 2>nul
)
if exist resources (
    xcopy /Y /E resources\* bin\ 2>nul
)
echo ==================================
echo Compilacion exitosa!
echo Para ejecutar:
echo java -cp "bin;lib\*" com.ironcladbox.view.LoginView
pause

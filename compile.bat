@echo off
echo Compilando IroncladBox CrossFit...
echo ==================================

REM Limpiar compilación anterior
if exist bin rmdir /s /q bin
mkdir bin

REM Compilar todos los .java de una vez
echo Compilando todas las clases...
dir /s /B src\*.java > sources.txt
javac -encoding UTF-8 -d bin -cp "lib\*" @sources.txt
del sources.txt

if %errorlevel% neq 0 (
    echo ERROR en compilacion
    pause
    exit /b 1
)

REM Copiar recursos (imágenes)
echo Copiando imagenes y recursos...
xcopy /Y /I src\com\ironcladbox\images\* bin\com\ironcladbox\images\
if exist resources xcopy /Y /E resources\* bin\

echo.
echo ==================================
echo Compilacion exitosa!
echo ==================================
echo.
echo Para ejecutar:
echo java -cp "bin;lib\*" com.ironcladbox.view.LoginView
echo.
pause
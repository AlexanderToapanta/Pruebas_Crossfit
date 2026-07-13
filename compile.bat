@echo off
echo Compilando IroncladBox CrossFit...
echo ==================================
if exist bin rmdir /s /q bin
mkdir bin

echo Compilando todas las clases...
powershell -Command "$files = Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName }; & javac -encoding UTF-8 -d bin -cp 'lib\*' $files"

if %errorlevel% neq 0 (
    echo ERROR en compilacion
    pause
    exit /b 1
)

echo Copiando imagenes...
if exist src\com\ironcladbox\images (
    xcopy /Y /I "src\com\ironcladbox\images\*" "bin\com\ironcladbox\images\" 2>nul
)
echo ==================================
echo Compilacion exitosa!
echo Para ejecutar:
echo java -cp "bin;lib\*" com.ironcladbox.view.LandingView
pause

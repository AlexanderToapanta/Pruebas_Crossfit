@echo off
echo Compilando IroncladBox CrossFit...
echo ==================================

if not exist bin mkdir bin

cd src
echo Compilando model...
javac -encoding UTF-8 -d ..\bin com\ironcladbox\model\*.java

echo Compilando dao...
javac -encoding UTF-8 -d ..\bin -cp ..\bin com\ironcladbox\dao\*.java

echo Compilando util...
javac -encoding UTF-8 -d ..\bin -cp ..\bin com\ironcladbox\util\*.java

echo Compilando controller...
javac -encoding UTF-8 -d ..\bin -cp ..\bin com\ironcladbox\controller\*.java

echo Compilando view...
javac -encoding UTF-8 -d ..\bin -cp ..\bin com\ironcladbox\view\*.java

cd ..

echo.
echo Compilacion completada!
echo.
echo Para ejecutar:
echo java -cp bin com.ironcladbox.view.LoginView

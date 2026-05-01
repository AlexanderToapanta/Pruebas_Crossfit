#!/bin/bash

echo "Compilando IroncladBox CrossFit..."
echo "=================================="

# Crear directorio bin si no existe
mkdir -p bin

# Compilar
cd src
javac -encoding UTF-8 -d ../bin com/ironcladbox/model/*.java
javac -encoding UTF-8 -d ../bin -cp ../bin com/ironcladbox/dao/*.java
javac -encoding UTF-8 -d ../bin -cp ../bin com/ironcladbox/util/*.java
javac -encoding UTF-8 -d ../bin -cp ../bin com/ironcladbox/controller/*.java
javac -encoding UTF-8 -d ../bin -cp ../bin com/ironcladbox/view/*.java
cd ..

echo "✓ Compilación completada"
echo ""
echo "Para ejecutar:"
echo "java -cp bin com.ironcladbox.view.LoginView"

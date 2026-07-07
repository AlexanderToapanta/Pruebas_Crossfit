#!/bin/bash
echo "Compilando IroncladBox CrossFit..."
rm -rf bin
mkdir -p bin
find src -name "*.java" > sources.txt
javac -encoding UTF-8 -d bin -cp "lib/*" @sources.txt
rm sources.txt
if [ $? -ne 0 ]; then
    echo "ERROR en compilacion"
    exit 1
fi
if [ -d "src/com/ironcladbox/images" ]; then
    cp -r src/com/ironcladbox/images bin/com/ironcladbox/images
fi
if [ -d "resources" ]; then
    cp -r resources/* bin/
fi
echo ""
echo "Compilacion completada!"
echo ""
echo "Para ejecutar:"
echo "java -cp 'bin:lib/*' com.ironcladbox.view.LoginView"

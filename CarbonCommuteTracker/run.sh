#!/bin/bash

echo "Compiling Carbon Commute Tracker..."
mkdir -p out data
find src -name "*.java" | xargs javac -d out -sourcepath src

if [ $? -ne 0 ]; then
    echo "Compilation failed. Make sure Java JDK is installed."
    exit 1
fi

echo "Compiled successfully."
echo
echo "Choose mode:"
echo "1) Console"
echo "2) GUI"
read -p "Enter 1 or 2: " mode

if [ "$mode" = "2" ]; then
    java -cp out carboncommute.ui.CarbonCommuteGUI
else
    java -cp out carboncommute.Main
fi

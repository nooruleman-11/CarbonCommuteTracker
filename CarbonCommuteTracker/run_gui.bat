@echo off
echo Compiling Carbon Commute Tracker...

if not exist out mkdir out
if not exist data mkdir data

javac -d out -sourcepath src src\carboncommute\Main.java src\carboncommute\model\*.java src\carboncommute\transport\*.java src\carboncommute\report\*.java src\carboncommute\system\*.java src\carboncommute\ui\*.java

if %errorlevel% neq 0 (
    echo Compilation failed. Make sure Java JDK is installed.
    pause
    exit /b 1
)

echo Starting GUI version...
java -cp out carboncommute.ui.CarbonCommuteGUI

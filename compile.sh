#!/bin/bash
# for nerds :nerd:
rm *.class -f
javac -cp "lib/jline-4.4.3.jar;." .\Main.java
java -cp "lib/jline-4.4.3.jar;." Main
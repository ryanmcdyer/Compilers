#!/bin/bash
mv STC.java STC.j
rm *.java
mv STC.j STC.java
rm *.class
#cp ../Parserjava/STC.java .
jjtree CCALRyanMcDyer.jjt
echo ""
javacc CCALRyanMcDyer.jj
echo ""
javac *.java
echo "Built"

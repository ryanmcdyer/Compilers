#!/bin/bash
rm *.class

FILE="SymbolTable"
if [ -f $FILE.java ]
then
  echo "Backing up $FILE"
  mv $FILE.java $FILE.j
fi
FILE="DataType"
if [ -f $FILE.java ]
then
  echo "Backing up $FILE"
  mv $FILE.java $FILE.j
fi
FILE="SCVisitor"
if [ -f $FILE.java ]
then
  echo "Backing up $FILE"
  mv $FILE.java $FILE.j
fi
FILE="Quad"
if [ -f $FILE.java ]
then
  echo "Backing up $FILE"
  mv $FILE.java $FILE.j
fi
FILE="IRVisitor"
if [ -f $FILE.java ]
then
  echo "Backing up $FILE"
  mv $FILE.java $FILE.j
fi

rm *.java

FILE="SymbolTable"
if [ -f $FILE.j ]
then
  echo "Restoring $FILE"
  mv $FILE.j $FILE.java
fi
FILE="DataType"
if [ -f $FILE.j ]
then
  echo "Restoring $FILE"
  mv $FILE.j $FILE.java
fi
FILE="SCVisitor"
if [ -f $FILE.j ]
then
  echo "Restoring $FILE"
  mv $FILE.j $FILE.java
fi
FILE="Quad"
if [ -f $FILE.j ]
then
  echo "Restoring $FILE"
  mv $FILE.j $FILE.java
fi
FILE="IRVisitor"
if [ -f $FILE.j ]
then
  echo "Restoring $FILE"
  mv $FILE.j $FILE.java
fi

jjtree CCALRyanMcDyer.jjt
echo ""
javacc CCALRyanMcDyer.jj
echo ""
javac *.java
echo "Built"

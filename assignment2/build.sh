rm *.java
rm *.class
cp ../Parserjava/STC.java .
#cp ../Parserjava/SimpleNode.java .
jjtree CCALRyanMcDyer.jjt
echo ""
javacc CCALRyanMcDyer.jj
echo ""
javac *.java

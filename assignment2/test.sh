#!/bin/bash
FILES="../CCAL/*"
for f in $FILES
do
  echo "File name: $f"
  java CCAL $f
done

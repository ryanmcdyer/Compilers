#!/bin/bash
FILES="../CCAL/*"
for f in $FILES
do
  echo "$f"
  java CCAL $f
done

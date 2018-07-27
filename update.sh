#! /bin/sh

SOURCE="./database/app/build/outputs/aar/database-debug.aar"

DESTS="./SimpleBARTInfo/database-debug/database-debug.aar ./ui_libs/database-debug/database-debug.aar ./MinnowRSS/database-debug/database-debug.aar"

for S in $DESTS; do 
   cp "$SOURCE" "$S"
done

SOURCE="./ui_libs/app/build/outputs/aar/ui-libs-debug.aar"
DESTS="./SimpleBARTInfo/ui-libs-debug/ui-libs-debug.aar ./MinnowRSS/ui-libs-debug/ui-libs-debug.aar"

for S in $DESTS; do 
   cp "$SOURCE" "$S"
done



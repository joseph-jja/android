#! /bin/sh

BUILD_DIRS="database ui_libs SimpleBARTInfo MinnowRSS"
for S in $BUILD_DIRS; do 
   cd $S && ./gradlew clean && ./gradlew build && cd ..
done


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



#!/bin/bash

# = NOTE =
# Use at root of repository: ./scripts/inject-license.sh
SRC=$(realpath app/src/main/java)
STRING="Copyright © 2015 Infrared5"
LICENSE=$(realpath scripts/LICENSE_INJECT)
WAS_UPDATED=0

# check to see if already has license...
echo "Traversing ${SRC}..."
while IFS= read -r -d '' file; do
        if grep -q "$STRING" "$file"; then
                echo "$file"
                echo "Already has license..."
        else
                cat "$LICENSE" "$file" > $$.tmp && mv $$.tmp "$file"
                WAS_UPDATED=1
        fi
done < <(find "${SRC}/" -type f -name "*.java" -print0)

echo "Exit $WAS_UPDATED"
exit 0
# $WAS_UPDATED

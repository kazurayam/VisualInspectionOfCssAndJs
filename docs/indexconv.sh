#!/usr/bin/env bash

# convert all the files in the current directory with name ending with `*.adoc` into `*.md`.
# except ones with `_` as prefix.
# E.g, `_.adoc` will be converted into `readme_.md`

find . -iname "*.adoc" -type f -maxdepth 1 -not -name "_*.adoc" | while read fname; do
  target=${fname//adoc/md}
  xml=${fname//adoc/xml}
  echo "converting $fname into $target"
  asciidoctor -b docbook -a leveloffset=+1 -o - "$fname" | pandoc  --markdown-headings=atx --wrap=preserve -t markdown_strict -f docbook - > "$target"
  echo deleting $xml
  rm -f "$xml"
done

# if we find a index*.md (or INDEX*.md),
# we rename all of them to a single index.md while overwriting,
# effectively the last wins.
# E.g, `index_.adoc` will overwrite into `index.md`
find . -iname "index*.md" -not -name "index.md" -type f -maxdepth 1 | while read fname; do
  echo Renaming $fname to index.md
  mv $fname index.md
done

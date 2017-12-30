#!/usr/bin/env bash

trap 'exit' INT TERM
trap 'kill 0' EXIT

hugo server &

fswatch -0 "themes/radoszewski-blog/static/css" -i "\\.scss$" -e ".*" --event Updated | while IFS= read -r -d "" path
do
	target="${path%.scss}.css"
	echo "Rebuilding CSS: $path..."
	sassc $path $target
done &

wait
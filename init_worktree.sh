#!/usr/bin/env bash

rm -rf public
git worktree add -B gh-pages public origin/gh-pages

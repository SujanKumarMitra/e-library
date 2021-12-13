#!/bin/sh

# loop through all subdirectories,
# if build file exists, then execute it,

build_filename="build.sh"

echo "Starting build process"

for sub_directory in *; do
  if [ -d "$sub_directory" ]; then
    cd "$sub_directory" || exit
    if [ -e "$build_filename" ]; then
      build_cmd="sh $build_filename"
      echo "Executing '$build_cmd' inside '$sub_directory'"
      $build_cmd
    else
      echo "No $build_filename file found inside '$sub_directory', skipping"
    fi
    cd ../
  fi
done

echo "Build finished"

#!/usr/bin/env bash
# Compile the sources and launch the Scientific Calculator GUI.
set -e
DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$DIR"

# Make sure a working javac is reachable. On macOS the /usr/bin/javac stub
# exists even with no JDK installed, so we test whether it actually runs
# (Homebrew's OpenJDK is keg-only and not on PATH by default).
if ! javac -version >/dev/null 2>&1; then
  for cand in /opt/homebrew/opt/openjdk/bin /usr/local/opt/openjdk/bin "$(/usr/libexec/java_home 2>/dev/null)/bin"; do
    if [ -x "$cand/javac" ]; then
      export PATH="$cand:$PATH"
      break
    fi
  done
fi

mkdir -p out
find src -name '*.java' > out/sources.txt
javac -encoding UTF-8 -d out @out/sources.txt
java -cp out com.calculator.Main

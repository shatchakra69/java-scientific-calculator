#!/usr/bin/env bash
# Compile everything and run the headless engine self-test.
set -e
DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$DIR"

# On macOS the /usr/bin/javac stub exists even with no JDK, so test whether
# javac actually runs before falling back to a known install location.
if ! javac -version >/dev/null 2>&1; then
  for cand in /opt/homebrew/opt/openjdk/bin /usr/local/opt/openjdk/bin "$(/usr/libexec/java_home 2>/dev/null)/bin"; do
    if [ -x "$cand/javac" ]; then
      export PATH="$cand:$PATH"
      break
    fi
  done
fi

mkdir -p out
find src test -name '*.java' > out/sources.txt
javac -encoding UTF-8 -d out @out/sources.txt
java -cp out com.calculator.CalculatorEngineTest

#include <assert.h>

#include "common.h"
#include "chunk.h"
#include "debug.h"

static void testGetLine(void) {
  Chunk chunk;
  initChunk(&chunk);

  // Instructions 0–1 are on line 10.
  writeChunk(&chunk, OP_RETURN, 10);
  writeChunk(&chunk, OP_RETURN, 10);

  // Instructions 2–4 are on line 20.
  writeChunk(&chunk, OP_RETURN, 20);
  writeChunk(&chunk, OP_RETURN, 20);
  writeChunk(&chunk, OP_RETURN, 20);

  // Instruction 5 is on line 30.
  writeChunk(&chunk, OP_RETURN, 30);

  assert(getLine(&chunk, 0) == 10);
  assert(getLine(&chunk, 1) == 10);
  assert(getLine(&chunk, 2) == 20);
  assert(getLine(&chunk, 3) == 20);
  assert(getLine(&chunk, 4) == 20);
  assert(getLine(&chunk, 5) == 30);

  assert(chunk.lineCount == 3);

  freeChunk(&chunk);
}

int main(int argc, const char* argv[]) {
  testGetLine();

  return 0;
}
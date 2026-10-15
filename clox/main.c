#include <stdio.h>
#include <time.h>

#include "common.h"
#include "chunk.h"
#include "debug.h"
#include "vm.h"

int main(int argc, const char* argv[]) {
  initVM();

  Chunk chunk;
  initChunk(&chunk);

  // Stack-heavy chunk dominated by OP_NEGATE.
  // Build: -(-(-(-(-(-(-(-1.2)))))))
  int constant = addConstant(&chunk, 1.2);
  writeChunk(&chunk, OP_CONSTANT, 123);
  writeChunk(&chunk, constant, 123);

  for (int i = 0; i < 8; i++) {
    writeChunk(&chunk, OP_NEGATE, 123);
  }

  writeChunk(&chunk, OP_RETURN, 123);

  const int iterations = 100000;
  struct timespec start, end;

  // Warm-up
  for (int i = 0; i < 10000; i++) interpret(&chunk);

  clock_gettime(CLOCK_MONOTONIC, &start);

  for (int i = 0; i < iterations; i++) {
    interpret(&chunk);
  }

  clock_gettime(CLOCK_MONOTONIC, &end);

  double elapsed =
      (end.tv_sec - start.tv_sec) +
      (end.tv_nsec - start.tv_nsec) / 1e9;

  printf("Elapsed:    %.6f seconds\n", elapsed);
  printf("Per iter:   %.2f ns\n", elapsed * 1e9 / iterations);
  printf("Negates:    %d\n", iterations * 8);

  freeVM();
  freeChunk(&chunk);
  return 0;
}
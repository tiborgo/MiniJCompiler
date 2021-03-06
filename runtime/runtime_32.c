/*
    Runtime library, for use in compiled MiniJ programs
 */

#include <stdio.h>
#include <stdlib.h>
#include <inttypes.h>

int32_t lmain(int32_t);

// Allocate <size> bytes of memory space and initialise it with zeroes
int32_t halloc(int32_t size) {
  return (int32_t)calloc(size, 1);
}

// Print an integer to the standard output
int32_t println_int(int32_t n)
{
  printf("%" PRId32 "\n",n);
  fflush(stdout);
  return 0;
}

// Print character to the standard output
int32_t print_char(int32_t n)
{
  printf("%c", n);
  fflush(stdout);
  return 0;
}

// Abort the execution with an error code
int32_t raise(int32_t rc)
{
  fprintf(stderr, "Program terminated with error code %" PRId32 ,rc);
  exit(rc);
  return 0;
}

// Actual entry point: wrapper around the compiled main method
// of the main class of the MiniJ program
int main()
{
  lmain(0);   // call main method with dummy argument for (unused) string array
  return 0;
}

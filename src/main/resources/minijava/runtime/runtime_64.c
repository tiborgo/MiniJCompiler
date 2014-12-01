/*
    Runtime library, for use in compiled MiniJava programs
 */

#include <stdio.h>
#include <stdlib.h>
#include <inttypes.h>

int64_t Lmain(int64_t);

// Allocate <size> bytes of memory space and initialise it with zeroes
int64_t L_halloc(int64_t size) {
  return (int64_t)calloc(size, 1);
}

// Print an integer to the standard output
int64_t L_println_int(int64_t n)
{
  printf("%" PRId64 "\n",n);
  return 0;
}

// Print character to the standard output
int64_t L_print_char(int64_t n)
{
  printf("%c", n);
  return 0;
}

// Abort the execution with an error code
int64_t L_raise(int64_t rc)
{
  fprintf(stderr, "Program terminated with error code %" PRId64 ,rc);
  exit(rc);
  return 0;
}

// Actual entry point: wrapper around the compiled main method
// of the main class of the MiniJava program
int main()
{
  Lmain(0);   // call main method with dummy argument for (unused) string array
  return 0;
}

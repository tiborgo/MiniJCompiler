# MiniJCompiler

Created by [Tibor Goldschwendt](https://github.com/tiborgo) <goldschwendt@cip.ifi.lmu.de> and [Michael Seifert](https://github.com/seifertm) <mseifert@error-reports.org>. This project was realised in the course of the *[Praktikum Compilerbau](http://www.tcs.ifi.lmu.de/lehre/ws-2014-15/compilerbau)* at LMU Munich under the supervision of Dr. Ulrich Schöpp.

The MiniJCompiler is a compiler system developed to translate MiniJ source code (simplified Java) to Intel assembler. For that matter, the MiniJCompiler chain comprises the following steps:

- Lexing
- Parsing
- Semantic Analysis (type checking)
- Translation
- Canonicalization
- Instruction Selection (assembler instruction)
- Control Flow Analysis

The MiniJCompiler is implemented in Java.

## Building

MiniJCompiler uses the Gradle build system. For use with Eclipse:

1. Install Eclipse
2. Install Gradle Eclipse plugin
3. Import project:
	- File > Import > Gradle > Gradle Project
	- Choose path to project
	- Click *Build Model*
	- Select *MiniJCompiler* 
	- Click *Finish*
4. Install dependencies:
	- Right click on `MiniJCompiler`
	- Gradle > Refresh All
5. Generate parser and lexer
	- Right click on *MiniJCompiler*
	- Gradle > Tasks Quick Launcher
	- Type in `generateGrammarSource`
	- Enter
6. Run Test
	- Right click on `MiniJCompiler/src/test/java/MiniJCompilerTest`
	- Run As > JUnit Test
7. Export JAR
	- Right click on `MiniJCompiler`
	- Export ...
	- Java > Runnable JAR file
	- Select *MiniJCompiler* from drop-down list
	- Select export destination, e.g. `<JAR path>/MiniJCompiler.jar`
	- Select *Package required libraries into generated JAR*
	- Finish
8. Make runtime available
	- Copy `runtime/runtime_32.c` to `<JAR path>/runtime/runtime_32.c`

The compiler is now ready to go.

## Launching

To compile a MiniJ file run in console:

```
$ cd <JAR path>
$ java -jar MiniJCompiler.jar <MiniJ file>
```

See `src/test/resources/working/` for MiniJ examples.
Further examples are available at the [MiniJava project page](http://www.cambridge.org/us/features/052182060X/) or 
the [course website](http://www.tcs.ifi.lmu.de/lehre/ws-2014-15/compilerbau).

### Command Line Options

| Command | Effect |
|-|-|
| `--assembler`, `-a` | compile until assembler and linker step (default: false) |
| `--canonicalize`, `-c`| compile until canonicalize step (default: false) |
| `--code-emission`, `-ce`| compile until code emission step (default: false) |
| `--debug`, `-d`| Print information in case of exceptions (default: false) |
| `--instruction-selection`, `-se` | compile until instruction selection step (default: false) |
| `--no-coalesce`, `-nc` | Skips coalescing optimisation (slows down register allocation)(default: false) |
| `--output`, `-o` `<file>`  |  Output file |
| `--parse`, `-p`  | compile until parse step (default: false) |
| `--print-assembly`, `-pa` | Prints the final assembly (default: false) |
| `--print-canonicalized-intermediate`, `-pci` | Pretty print the canonicalized intermediate code (default: false) |
| `--print-coalescing-details`, `-pcd`  | Prints details of the coalescing algorithm (default: false) |
| `--print-control-flow-graphs`, `-pcfg` | Prints the control flow graph (default: false) |
| `--print-flow-analysis-details`, `-pfad` | Prints details of the flow analysis (default: false) |
| `--print-interference-graphs`, `-pig` | Prints the interference graphs (default: false) |
| `--print-intermediate`, `-pi` | Pretty print the intermediate code (default: false) |
| `--print-pre-assembly`, `-ppa` | Prints the assembly with temporiaries and unspecified frame size (default: false) |
| `--print-pre-colored-graphs`, `-ppg` | Prints the pre colored graphs (default: false) |
| `--print-register-allocation-details`, `-prad` | Prints details of the register allocation (default: false) |
| `--print-source-code`, `-psc` | Pretty print the input source code (default: false) |
| `--register-allocation`, `-re`  | compile until register allocation step (default: false) |
| `--run-executable`, `-e` | Runs the compiled executable (default: false) |
| `--semantic-analysis`, `-sa` | compile until semantic analysis step (default: false) |
| `--silent`, `-s` | Print no output at all (default: false) |
| `--translate`, `-t` | compile until translate step (default: false) |
| `--verbose`, `-v` | Print additional information (default: false) |

## License

The compiler itself is distributed under the terms of the GPLv3 license.
The .minij example/test files are taken by courtesy of Ulrich Schöpp.
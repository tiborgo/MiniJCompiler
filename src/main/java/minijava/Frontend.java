package minijava;

import java.io.IOException;

import minijava.ast.rules.Prg;

public interface Frontend {
	Prg getAbstractSyntaxTree(String filePath) throws IOException;
}

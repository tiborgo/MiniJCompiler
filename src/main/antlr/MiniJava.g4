grammar MiniJava;

// Lexer

INT: 'int';
WS: [ \t\r\n] -> skip;
COMMENT: '//'.*?'\n' -> skip;
LSBRACKET: '[';
RSBRACKET: ']';
CLASS: 'class';
PUBLIC: 'public';
STATIC: 'static';
VOID: 'void';
LBRACKET: '(';
RBRACKET: ')';
MAIN: 'main';
STRING: 'String';
LCBRACKET: '{';
RCBRACKET: '}';
EXTENDS: 'extends';
SEMICOLON: ';';
COMMA: ',';
RETURN: 'return';
BOOLEAN: 'boolean';
IF: 'if';
ELSE: 'else';
WHILE: 'while';
NEW: 'new';
EXCLAMATION_MARK: '!';
FALSE: 'false';
TRUE: 'true';
THIS: 'this';
DOT: '.';
LENGTH: 'length';
STAR: '*';
SLASH: '/';
PLUS: '+';
MINUS: '-';
SMALLER: '<';
DOUBLE_AMPERSAND: '&&';
EQUAL_SIGN: '=';
CHAR: 'char';
SYSTEM_OUT_PRINT: 'System.out.print';
SYSTEM_OUT_PRINTLN: 'System.out.println';
INTEGER_LITERAL: [0-9]+;
IDENTIFIER: [a-zA-Z][a-zA-Z0-9_]*;

// Parser

prog: mainClass ( classDeclaration )* EOF;
mainClass: CLASS identifier LCBRACKET PUBLIC STATIC VOID MAIN LBRACKET STRING LSBRACKET RSBRACKET identifier RBRACKET LCBRACKET statement RCBRACKET RCBRACKET;

classDeclaration: CLASS className=identifier (EXTENDS identifier)? LCBRACKET ( varDeclaration )* ( methodDeclaration )* RCBRACKET;

varDeclaration: type identifier SEMICOLON;

methodDeclaration: PUBLIC returnType=type methodName=identifier LBRACKET ( type identifier ( COMMA type identifier )* )? RBRACKET LCBRACKET (varDeclaration )* ( statement )* RETURN expression SEMICOLON RCBRACKET;

type: INT LSBRACKET RSBRACKET # intArrayType
	|  BOOLEAN            # booleanType
	|  INT                # intType
	|  identifier         # otherType
	;

statement: LCBRACKET ( statement )* RCBRACKET							# bracketStatement
	|  IF LBRACKET expression RBRACKET statement ELSE statement				# ifStatement
	|  WHILE LBRACKET expression RBRACKET statement						# whileStatement
	|  SYSTEM_OUT_PRINTLN LBRACKET expression RBRACKET SEMICOLON				# systemOutPrintlnStatement
	|  SYSTEM_OUT_PRINT LBRACKET LBRACKET CHAR RBRACKET expression RBRACKET SEMICOLON	# systemOutPrintStatement
	|  identifier EQUAL_SIGN expression SEMICOLON						# assignStatement
	|  identifier LSBRACKET expression RSBRACKET EQUAL_SIGN expression SEMICOLON		# arrayAssignStatement
	;

expression: expression ( STAR | SLASH ) expression
	|  expression ( PLUS | MINUS ) expression
	|  expression ( SMALLER ) expression
	|  expression ( DOUBLE_AMPERSAND ) expression
	|  expression LSBRACKET expression RSBRACKET
	|  expression DOT LENGTH
	|  expression DOT identifier LBRACKET ( expression ( COMMA expression )* )? RBRACKET
	|  INTEGER_LITERAL
	|  identifier
	|  NEW INT LSBRACKET expression RSBRACKET
	|  NEW identifier LBRACKET RBRACKET
	|  EXCLAMATION_MARK expression
	|  LBRACKET expression RBRACKET
	|  FALSE
	|  TRUE
	|  THIS
	;

identifier: IDENTIFIER;

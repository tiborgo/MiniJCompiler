grammar MiniJava;

// Lexer

INT: 'int';
WS: [ \t\r\n] -> skip;
COMMENT: '//'.*?'\n' -> skip;
MULTILINE_COMMENT: '/*'.*?'*/' -> skip;
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

classDeclaration: CLASS className=identifier (EXTENDS superClassName=identifier)? LCBRACKET ( varDeclaration )* ( methodDeclaration )* RCBRACKET;

varDeclaration: type identifier SEMICOLON;

methodDeclaration: PUBLIC returnType=type methodName=identifier LBRACKET ( firstParameterType=type firstParameterName=identifier ( COMMA type identifier )* )? RBRACKET LCBRACKET (varDeclaration )* ( statement )* RETURN returnExpression=expression SEMICOLON RCBRACKET;

type: INT LSBRACKET RSBRACKET # intArrayType
	|  BOOLEAN            # booleanType
	|  INT                # intType
	|  identifier         # otherType
	;

statement: LCBRACKET ( statement )* RCBRACKET							# bracketStatement
	|  IF LBRACKET condition=expression RBRACKET trueStatement=statement ELSE falseStatement=statement # ifStatement
	|  WHILE LBRACKET expression RBRACKET statement						# whileStatement
	|  SYSTEM_OUT_PRINTLN LBRACKET expression RBRACKET SEMICOLON				# systemOutPrintlnStatement
	|  SYSTEM_OUT_PRINT LBRACKET LBRACKET CHAR RBRACKET expression RBRACKET SEMICOLON	# systemOutPrintStatement
	|  identifier EQUAL_SIGN expression SEMICOLON						# assignStatement
	|  identifier LSBRACKET index=expression RSBRACKET EQUAL_SIGN rhs=expression SEMICOLON		# arrayAssignStatement
	;

expression: expression ( STAR | SLASH ) expression						# binOpExpression
	|  expression ( PLUS | MINUS ) expression							# binOpExpression
	|  expression ( SMALLER ) expression								# binOpExpression
	|  expression ( DOUBLE_AMPERSAND ) expression						# binOpExpression
	|  expression LSBRACKET expression RSBRACKET						# arrayAccessExpression
	|  expression DOT LENGTH											# arrayLengthExpression
	|  expression DOT identifier LBRACKET ( expression ( COMMA expression )* )? RBRACKET # invokeExpression
	|  INTEGER_LITERAL													# integerLiteralExpression
	|  identifier														# identifierExpression
	|  NEW INT LSBRACKET expression RSBRACKET							# newIntArrayExpression
	|  NEW identifier LBRACKET RBRACKET									# newExpression
	|  EXCLAMATION_MARK expression										# notExpression
	|  LBRACKET expression RBRACKET										# bracketExpression
	|  FALSE															# falseExpression
	|  TRUE																# trueExpression
	|  THIS																# thisExpression
	;

identifier: IDENTIFIER;

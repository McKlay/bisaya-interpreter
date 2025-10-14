package com.bisayapp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Recursive Descent Parser for Bisaya++ Programming Language
 * 
 * Implements a top-down parser that converts a sequence of tokens into an Abstract Syntax Tree (AST).
 * The parser follows the Bisaya++ grammar rules and creates appropriate AST nodes for statements and expressions.
 * 
 * Grammar Flow (top to bottom):
 * program → statement → {printStmt | varDecl | exprStmt} → assignment → concatenation → primaryExpr
 */
public class Parser {
    // ========================================================================================
    // FIELDS AND CONSTRUCTOR
    // ========================================================================================
    
    /** The list of tokens to parse, provided by the lexer */
    private final List<Token> tokens;
    
    /** Current position in the token stream */
    private int current = 0;
    
    /** Tracks variables declared during parsing to catch undeclared variable usage early */
    private final Set<String> declaredVariables = new HashSet<>();

    /**
     * Constructs a new Parser with the given token list
     * @param tokens List of tokens from the lexer to parse
     */
    public Parser(List<Token> tokens) { 
        this.tokens = tokens; 
    }

    // ========================================================================================
    // ENTRY POINT - PROGRAM PARSING
    // ========================================================================================
    
    /**
     * Parses the entire Bisaya++ program
     * 
     * Grammar: SUGOD statement* KATAPUSAN EOF
     * 
     * A valid program must:
     * 1. Start with SUGOD keyword
     * 2. Contain zero or more statements
     * 3. End with KATAPUSAN keyword
     * 4. Have nothing significant after KATAPUSAN (only whitespace/comments allowed)
     * 
     * @return List of statement AST nodes representing the program
     * @throws ParseError if program structure is invalid
     */
    public List<Stmt> parseProgram() {
        skipNewlines();
        consume(TokenType.SUGOD, "Program must start with SUGOD.");
        skipNewlines();

        List<Stmt> stmts = new ArrayList<>();
        
        // Parse all statements until we reach KATAPUSAN or EOF
        while (!check(TokenType.KATAPUSAN) && !isAtEnd()) {
            skipNewlines();
            if (!check(TokenType.KATAPUSAN) && !isAtEnd()) {
                stmts.add(statement());
                skipNewlines();
            }
        }

        consume(TokenType.KATAPUSAN, "Program must end with KATAPUSAN.");
        skipNewlines(); // Skip any trailing newlines

        // Ensure no unexpected tokens remain after program end
        // Allow trailing whitespace/comments but not actual code
        if (!isAtEnd() && peek().type != TokenType.EOF) {
            throw error(peek(), "Unexpected tokens after KATAPUSAN.");
        }

        return stmts;
    }

    // ========================================================================================
    // STATEMENT PARSING (Level 1)
    // ========================================================================================
    
    /**
     * Parses a single statement
     * 
     * Grammar: printStmt | inputStmt | varDecl | exprStmt
     * 
     * Determines the type of statement by looking at the first token:
     * - IPAKITA → print statement
     * - DAWAT → input statement
     * - MUGNA → variable declaration
     * - Other → expression statement (assignments, etc.)
     * 
     * @return Statement AST node
     * @throws ParseError if statement is invalid
     */
    private Stmt statement() {
        if (match(TokenType.IPAKITA)) return printStmt();
        if (match(TokenType.DAWAT))   return inputStmt();
        if (match(TokenType.MUGNA))   return varDecl();
        return exprStmt(); // Default: treat as expression statement (assignments, etc.)
    }

    /**
     * Parses IPAKITA (print) statements
     * 
     * Grammar: IPAKITA ":" expression ("&" expression)*
     * 
     * Example: IPAKITA: "Hello" & " " & "World" & $
     * 
     * @return Print statement AST node containing list of expressions to concatenate
     * @throws ParseError if print statement syntax is invalid
     */
    private Stmt printStmt() {
        consume(TokenType.COLON, "Expect ':' after IPAKITA.");
        
        List<Expr> parts = new ArrayList<>();
        parts.add(assignment()); // First expression is required - allow full expressions

        // Parse additional expressions separated by & (concatenation)
        while (match(TokenType.AMPERSAND)) {
            parts.add(assignment()); // Allow full expressions in concatenation too
        }

        // Check if there are unexpected tokens after the expression(s)
        // This catches cases like "IPAKITA: a b c" where & is missing between variables
        if (!check(TokenType.NEWLINE) && !isAtEnd() && !check(TokenType.EOF)) {
            throw error(peek(), "Expected newline or end of program after IPAKITA statement.");
        }

        return new Stmt.Print(parts);
    }

    /**
     * Parses DAWAT (input) statements
     * 
     * Grammar: DAWAT ":" identifier ("," identifier)*
     * 
     * Example: DAWAT: x, y, z
     * 
     * @return Input statement AST node containing list of variable names
     * @throws ParseError if input statement syntax is invalid
     */
    private Stmt inputStmt() {
        consume(TokenType.COLON, "Expect ':' after DAWAT.");
        
        List<String> varNames = new ArrayList<>();
        Token firstVar = consume(TokenType.IDENTIFIER, "Expect variable name.");
        
        // Validate that the variable is declared
        if (!declaredVariables.contains(firstVar.lexeme)) {
            throw error(firstVar, "Undefined variable '" + firstVar.lexeme + 
                "'. Variables must be declared with MUGNA before using in DAWAT.");
        }
        varNames.add(firstVar.lexeme);

        // Parse additional variables separated by comma
        while (match(TokenType.COMMA)) {
            Token varToken = consume(TokenType.IDENTIFIER, "Expect variable name after ','.");
            
            // Validate that the variable is declared
            if (!declaredVariables.contains(varToken.lexeme)) {
                throw error(varToken, "Undefined variable '" + varToken.lexeme + 
                    "'. Variables must be declared with MUGNA before using in DAWAT.");
            }
            varNames.add(varToken.lexeme);
        }

        // Check for unexpected tokens after the variable list
        if (!check(TokenType.NEWLINE) && !isAtEnd() && !check(TokenType.EOF)) {
            throw error(peek(), "Expected newline or end of program after DAWAT statement.");
        }

        return new Stmt.Input(varNames);
    }

    /**
     * Parses MUGNA (variable declaration) statements
     * 
     * Grammar: MUGNA type identifier ("=" expression)? ("," identifier ("=" expression)?)* ";"?
     * 
     * Examples:
     * - MUGNA NUMERO x, y, z=5
     * - MUGNA LETRA ch='c'
     * - MUGNA TINUOD flag="OO"
     * 
     * @return Variable declaration AST node with type and list of variables
     * @throws ParseError if declaration syntax is invalid
     */
    private Stmt varDecl() {
        // Parse the type keyword (NUMERO, LETRA, TINUOD, or TIPIK)
        Token typeTok = consumeOneOf("Expect a type after MUGNA.", 
            TokenType.NUMERO, TokenType.LETRA, TokenType.TINUOD, TokenType.TIPIK);

        List<Stmt.VarDecl.Item> items = new ArrayList<>();
        List<String> declaredNames = new ArrayList<>(); // Track declared names in this statement
        
        // Parse comma-separated list of variable declarations
        do {
            String name = consume(TokenType.IDENTIFIER, "Expect variable name.").lexeme;
            
            // Check for duplicate declaration in same statement
            if (declaredNames.contains(name)) {
                throw error(previous(), "Cannot declare variable '" + name + "' twice in the same statement.");
            }
            declaredNames.add(name);
            
            // Add to global declared variables tracking
            declaredVariables.add(name);
            
            Expr init = null;
            
            // Optional initializer with = expression
            if (match(TokenType.EQUAL)) {
                init = concatenation(); // Allow concatenation in initializers
            }
            
            items.add(new Stmt.VarDecl.Item(name, init));
        } while (match(TokenType.COMMA)); // Continue if comma found

        // Check for unexpected tokens after variable declaration
        if (check(TokenType.SEMICOLON)) {
            throw error(peek(), "Semicolons are not allowed after statements in Bisaya++.");
        }

        return new Stmt.VarDecl(typeTok.type, items);
    }

    /**
     * Parses expression statements (assignments and bare expressions)
     * 
     * Grammar: assignment
     * 
     * Examples:
     * - x = 5
     * - x = y = z = 10  (right-associative)
     * 
     * @return Expression statement AST node wrapping the expression
     * @throws ParseError if expression is invalid
     */
    private Stmt exprStmt() {
        Expr e = assignment();
        
        // Check for semicolons and throw error if found
        if (check(TokenType.SEMICOLON)) {
            throw error(peek(), "Semicolons are not allowed after statements in Bisaya++.");
        }
        
        return new Stmt.ExprStmt(e);
    }

    // ========================================================================================
    // EXPRESSION PARSING (Level 2 - Precedence Order)
    // ========================================================================================
    
    /**
     * Parses assignment expressions (highest precedence, right-associative)
     * 
     * Grammar: IDENTIFIER "=" assignment | logical
     * 
     * Right-associativity means x=y=4 parses as x=(y=4)
     * 
     * @return Assignment expression or lower-precedence expression
     * @throws ParseError if assignment target is invalid
     */
    private Expr assignment() {
        Expr expr = logical(); // Try to parse as logical first
        
        if (match(TokenType.EQUAL)) {
            Expr value = assignment(); // Right-associative: recurse on assignment
            
            // Ensure left-hand side is a valid assignment target (variable)
            if (expr instanceof Expr.Variable var) {
                return new Expr.Assign(var.name, value);
            }
            
            throw error(previous(), "Invalid assignment target.");
        }
        
        return expr;
    }

    /**
     * Parses logical OR expressions (left-associative)
     * 
     * Grammar: logicalAnd ("O" logicalAnd)*
     * 
     * @return Binary expression for OR or lower-precedence expression
     */
    private Expr logical() {
        Expr expr = logicalAnd();

        while (match(TokenType.O)) {
            Token operator = previous();
            Expr right = logicalAnd();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    /**
     * Parses logical AND expressions (left-associative)
     * 
     * Grammar: equality ("UG" equality)*
     * 
     * @return Binary expression for AND or lower-precedence expression
     */
    private Expr logicalAnd() {
        Expr expr = equality();

        while (match(TokenType.UG)) {
            Token operator = previous();
            Expr right = equality();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    /**
     * Parses equality expressions (left-associative)
     * 
     * Grammar: comparison (("==" | "<>") comparison)*
     * 
     * @return Binary expression for equality/inequality or lower-precedence expression
     */
    private Expr equality() {
        Expr expr = comparison();

        while (match(TokenType.EQUAL_EQUAL) || match(TokenType.LT_GT)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    /**
     * Parses comparison expressions (left-associative)
     * 
     * Grammar: term ((">" | ">=" | "<" | "<=") term)*
     * 
     * @return Binary expression for comparison or lower-precedence expression
     */
    private Expr comparison() {
        Expr expr = concatenation();

        while (match(TokenType.GREATER) || match(TokenType.GREATER_EQUAL) 
            || match(TokenType.LESS) || match(TokenType.LESS_EQUAL)) {
            Token operator = previous();
            Expr right = concatenation();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    /**
     * Parses concatenation expressions (left-associative)
     * 
     * Grammar: term ("&" term)*
     * 
     * Left-associativity means "A"&"B"&"C" parses as ("A"&"B")&"C"
     * 
     * @return Binary expression for concatenation or lower-precedence expression
     */
    private Expr concatenation() {
        Expr expr = term(); // Start with term expression

        // Parse left-associative concatenation chain
        while (match(TokenType.AMPERSAND)) {
            Token operator = previous(); // The & operator token
            Expr right = term();  // Right operand
            expr = new Expr.Binary(expr, operator, right); // Build left-associative tree
        }

        return expr;
    }

    /**
     * Parses term expressions (addition/subtraction)
     * 
     * Grammar: factor (("+" | "-") factor)*
     * 
     * @return Binary expression for addition/subtraction or lower-precedence expression
     */
    private Expr term() {
        Expr expr = factor();

        while (match(TokenType.PLUS) || match(TokenType.MINUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    /**
     * Parses factor expressions (multiplication/division/modulo)
     * 
     * Grammar: unary (("*" | "/" | "%") unary)*
     * 
     * @return Binary expression for multiplication/division/modulo or lower-precedence expression
     */
    private Expr factor() {
        Expr expr = unary();

        while (match(TokenType.STAR) || match(TokenType.SLASH) || match(TokenType.PERCENT)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    /**
     * Parses unary expressions (prefix operators)
     * 
     * Grammar: ("+" | "-" | "++" | "--" | "DILI") unary | postfix
     * 
     * @return Unary expression or postfix expression
     */
    private Expr unary() {
        if (match(TokenType.PLUS) || match(TokenType.MINUS) || match(TokenType.DILI) 
            || match(TokenType.PLUS_PLUS) || match(TokenType.MINUS_MINUS)) {
            Token operator = previous();
            Expr right = unary(); // Right-associative
            return new Expr.Unary(operator, right);
        }

        return postfix();
    }

    /**
     * Parses postfix expressions (postfix operators)
     * 
     * Grammar: primary ("++" | "--")*
     * 
     * @return Postfix expression or primary expression
     */
    private Expr postfix() {
        Expr expr = primary();
        
        while (match(TokenType.PLUS_PLUS) || match(TokenType.MINUS_MINUS)) {
            Token operator = previous();
            expr = new Expr.Postfix(expr, operator);
        }
        
        return expr;
    }

    /**
     * Parses primary expressions (lowest precedence - literals, variables, and grouping)
     * 
     * Grammar: STRING | NUMBER | CHAR | "$" | "(" expression ")" | IDENTIFIER
     * 
     * Primary expressions are the basic building blocks:
     * - String literals: "Hello World"
     * - Number literals: 42, 3.14
     * - Character literals: 'a'
     * - Dollar sign: $ (represents newline)
     * - Parenthesized expressions: (a + b)
     * - Variable references: x, variable_name
     * 
     * @return Literal, Variable, or Grouping expression
     * @throws ParseError if no valid primary expression found
     */
    private Expr primary() {
        if (match(TokenType.STRING))     return new Expr.Literal(previous().literal);
        if (match(TokenType.NUMBER))     return new Expr.Literal(previous().literal);
        if (match(TokenType.CHAR))       return new Expr.Literal(previous().literal);
        if (match(TokenType.DOLLAR))     return new Expr.Literal("\n"); // $ becomes newline
        
        // Parenthesized expressions
        if (match(TokenType.LEFT_PAREN)) {
            Expr expr = assignment(); // Allow full expressions inside parens
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }
        
        if (match(TokenType.IDENTIFIER)) return new Expr.Variable(previous().lexeme);
        
        throw error(peek(), "Expect expression.");
    }

    /**
     * Helper to peek ahead n tokens
     */
    private Token peekAhead(int n) {
        int idx = current + n;
        if (idx >= tokens.size()) return null;
        return tokens.get(idx);
    }

    // ========================================================================================
    // UTILITY METHODS - TOKEN CONSUMPTION AND CHECKING
    // ========================================================================================
    
    /**
     * Consumes a token of the expected type or throws an error
     * 
     * @param type Expected token type
     * @param msg Error message if token type doesn't match
     * @return The consumed token
     * @throws ParseError if token type doesn't match expected
     */
    private Token consume(TokenType type, String msg) {
        if (check(type)) return advance();
        throw error(peek(), msg);
    }

    /**
     * Consumes one of several possible token types or throws an error
     * 
     * @param msg Error message if none of the types match
     * @param types Array of acceptable token types
     * @return The consumed token
     * @throws ParseError if no token type matches
     */
    private Token consumeOneOf(String msg, TokenType... types) {
        for (TokenType t : types) {
            if (check(t)) return advance();
        }
        throw error(peek(), msg);
    }

    /**
     * Checks if current token matches type and consumes it if so
     * 
     * @param t Token type to match
     * @return true if token was matched and consumed, false otherwise
     */
    private boolean match(TokenType t) {
        if (check(t)) { 
            advance(); 
            return true; 
        }
        return false;
    }

    /**
     * Checks if current token is of the specified type without consuming it
     * 
     * @param t Token type to check
     * @return true if current token matches type, false otherwise
     */
    private boolean check(TokenType t) {
        if (isAtEnd()) return false;
        return peek().type == t;
    }

    /**
     * Checks if the next token (lookahead) is of the specified type
     * 
     * @param t Token type to check
     * @return true if next token matches type, false otherwise
     */
    private boolean checkNext(TokenType t) {
        if (isAtEnd()) return false;
        if (current + 1 >= tokens.size()) return false;
        return tokens.get(current + 1).type == t;
    }

    // ========================================================================================
    // UTILITY METHODS - TOKEN NAVIGATION
    // ========================================================================================
    
    /**
     * Advances to the next token in the stream
     * 
     * @return The token that was current before advancing
     */
    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    /**
     * Checks if we've reached the end of the token stream
     * 
     * @return true if current token is EOF, false otherwise
     */
    private boolean isAtEnd() { 
        return peek().type == TokenType.EOF; 
    }

    /**
     * Returns the current token without advancing
     * 
     * @return Current token
     */
    private Token peek() { 
        return tokens.get(current); 
    }

    /**
     * Returns the previously consumed token
     * 
     * @return Previous token
     */
    private Token previous() { 
        return tokens.get(current - 1); 
    }

    /**
     * Skips any newline tokens in the stream
     * 
     * Newlines are used for formatting but don't affect program logic,
     * so we skip them during parsing while preserving them in the token stream
     * for error reporting purposes.
     */
    private void skipNewlines() {
        while (match(TokenType.NEWLINE)) {
            // Just consume newlines - they're syntactic sugar
        }
    }

    // ========================================================================================
    // ERROR HANDLING
    // ========================================================================================
    
    /**
     * Creates a parse error with the given token and message
     * 
     * Reports the error to ErrorReporter with line and column information
     * 
     * @param token Token where error occurred
     * @param message Descriptive error message
     * @return ParseError exception ready to be thrown
     */
    ParseError error(Token token, String message) {
        ErrorReporter.error(token.line, token.col, message);
        return new ParseError();
    }

    /**
     * Custom exception for parse errors
     * 
     * Extends RuntimeException to allow throwing without explicit declaration
     * Used for panic-mode error recovery in recursive descent parsing
     */
    static class ParseError extends RuntimeException {}
}
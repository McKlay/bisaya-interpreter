package com.bisayapp;

import java.util.ArrayList;
import java.util.List;

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
        skipNewlines(); // Skip any leading newlines before SUGOD
        consume(TokenType.SUGOD, "Program must start with SUGOD.");
        skipNewlines(); // Skip any newlines after SUGOD

        List<Stmt> stmts = new ArrayList<>();
        
        // Parse all statements until we reach KATAPUSAN or EOF
        while (!check(TokenType.KATAPUSAN) && !isAtEnd()) {
            skipNewlines(); // Skip newlines before each statement
            if (!check(TokenType.KATAPUSAN) && !isAtEnd()) {
                stmts.add(statement());
                skipNewlines(); // Skip newlines after each statement
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
     * Grammar: printStmt | varDecl | exprStmt
     * 
     * Determines the type of statement by looking at the first token:
     * - IPAKITA → print statement
     * - MUGNA → variable declaration
     * - Other → expression statement (assignments, etc.)
     * 
     * @return Statement AST node
     * @throws ParseError if statement is invalid
     */
    private Stmt statement() {
        if (match(TokenType.IPAKITA)) return printStmt();
        if (match(TokenType.MUGNA))   return varDecl();
        return exprStmt(); // Default: treat as expression statement (assignments, etc.)
    }

    /**
     * Parses IPAKITA (print) statements
     * 
     * Grammar: IPAKITA ":" expression ("&" expression)* ";"?
     * 
     * Example: IPAKITA: "Hello" & " " & "World" & $
     * 
     * @return Print statement AST node containing list of expressions to concatenate
     * @throws ParseError if print statement syntax is invalid
     */
    private Stmt printStmt() {
        consume(TokenType.COLON, "Expect ':' after IPAKITA.");
        
        List<Expr> parts = new ArrayList<>();
        parts.add(primaryExpr()); // First expression is required

        // Parse additional expressions separated by & (concatenation)
        while (match(TokenType.AMPERSAND)) {
            parts.add(primaryExpr());
        }

        match(TokenType.SEMICOLON); // Optional semicolon
        return new Stmt.Print(parts);
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
        List<String> declaredNames = new ArrayList<>(); // TODO: Fixed - Track declared names in this statement
        
        // Parse comma-separated list of variable declarations
        do {
            String name = consume(TokenType.IDENTIFIER, "Expect variable name.").lexeme;
            
            // TODO: Fixed - Check for duplicate declaration in same statement
            if (declaredNames.contains(name)) {
                throw error(previous(), "Cannot declare variable '" + name + "' twice in the same statement.");
            }
            declaredNames.add(name);
            
            Expr init = null;
            
            // Optional initializer with = expression
            if (match(TokenType.EQUAL)) {
                init = concatenation(); // Allow concatenation in initializers
            }
            
            items.add(new Stmt.VarDecl.Item(name, init));
        } while (match(TokenType.COMMA)); // Continue if comma found

        match(TokenType.SEMICOLON); // Optional semicolon
        return new Stmt.VarDecl(typeTok.type, items);
    }

    /**
     * Parses expression statements (assignments and bare expressions)
     * 
     * Grammar: assignment ";"?
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
        match(TokenType.SEMICOLON); // Optional semicolon
        return new Stmt.ExprStmt(e);
    }

    // ========================================================================================
    // EXPRESSION PARSING (Level 2 - Precedence Order)
    // ========================================================================================
    
    /**
     * Parses assignment expressions (highest precedence, right-associative)
     * 
     * Grammar: IDENTIFIER "=" assignment | concatenation
     * 
     * Right-associativity means x=y=4 parses as x=(y=4)
     * 
     * @return Assignment expression or lower-precedence expression
     * @throws ParseError if assignment target is invalid
     */
    private Expr assignment() {
        Expr expr = concatenation(); // Try to parse as concatenation first
        
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
     * Parses concatenation expressions (left-associative)
     * 
     * Grammar: primary ("&" primary)*
     * 
     * Left-associativity means "A"&"B"&"C" parses as ("A"&"B")&"C"
     * 
     * @return Binary expression for concatenation or primary expression
     */
    private Expr concatenation() {
        Expr expr = primaryExpr(); // Start with primary expression

        // Parse left-associative concatenation chain
        while (match(TokenType.AMPERSAND)) {
            Token operator = previous(); // The & operator token
            Expr right = primaryExpr();  // Right operand
            expr = new Expr.Binary(expr, operator, right); // Build left-associative tree
        }

        return expr;
    }

    /**
     * Parses primary expressions (lowest precedence - literals and variables)
     * 
     * Grammar: STRING | NUMBER | CHAR | "$" | IDENTIFIER
     * 
     * Primary expressions are the basic building blocks:
     * - String literals: "Hello World"
     * - Number literals: 42, 3.14
     * - Character literals: 'a'
     * - Dollar sign: $ (represents newline)
     * - Variable references: x, variable_name
     * 
     * @return Literal or Variable expression
     * @throws ParseError if no valid primary expression found
     */
    private Expr primaryExpr() {
        if (match(TokenType.STRING))     return new Expr.Literal(previous().literal);
        if (match(TokenType.NUMBER))     return new Expr.Literal(previous().literal);
        if (match(TokenType.CHAR))       return new Expr.Literal(previous().literal);
        if (match(TokenType.DOLLAR))     return new Expr.Literal("\n"); // $ becomes newline
        if (match(TokenType.IDENTIFIER)) return new Expr.Variable(previous().lexeme);
        
        throw error(peek(), "Expect string/number/char literal, $, or identifier.");
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
package com.bisayapp;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) { this.tokens = tokens; }

    // SUGOD <statements>* KATAPUSAN [EOF/whitespace]
    public List<Stmt> parseProgram() {
        consume(TokenType.SUGOD, "Program must start with SUGOD.");

        List<Stmt> stmts = new ArrayList<>();
        while (!check(TokenType.KATAPUSAN) && !isAtEnd()) {
            stmts.add(statement());
        }

        consume(TokenType.KATAPUSAN, "Program must end with KATAPUSAN.");

        // Your earlier fix: don't force-consume EOF; allow trailing whitespace/comments.
        // If something non-EOF remains (e.g., stray tokens), report it nicely.
        if (!isAtEnd() && peek().type != TokenType.EOF) {
            throw error(peek(), "Unexpected tokens after KATAPUSAN.");
        }

        return stmts;
    }

    // -------- statements --------
    private Stmt statement() {
        if (match(TokenType.IPAKITA)) return printStmt();
        if (match(TokenType.MUGNA))   return varDecl();
        return exprStmt(); // allow assignments / bare expressions as statements
    }

    // IPAKITA: <part> (& <part>)* [;]
    private Stmt printStmt() {
        consume(TokenType.COLON, "Expect ':' after IPAKITA.");
        List<Expr> parts = new ArrayList<>();
        parts.add(primaryExpr());

        while (match(TokenType.AMPERSAND)) {
            parts.add(primaryExpr());
        }

        // optional semicolon
        match(TokenType.SEMICOLON);
        return new Stmt.Print(parts);
    }

    // variable declarations MUGNA NUMERO|LETRA|TINUOD|TIPIK IDENT (= <expr>)? (, IDENT (= <expr>)?)* [;]
    private Stmt varDecl() {
        // type keyword
        Token typeTok = consumeOneOf("Expect a type after MUGNA.", TokenType.NUMERO, TokenType.LETRA, TokenType.TINUOD, TokenType.TIPIK);

        List<Stmt.VarDecl.Item> items = new ArrayList<>();
        do {
            String name = consume(TokenType.IDENTIFIER, "Expect variable name.").lexeme;
            Expr init = null;
            if (match(TokenType.EQUAL)) {
                // allow either a single primary (e.g., 5, 'c', "OO") or concatenation for strings
                init = concatenation();
            }
            items.add(new Stmt.VarDecl.Item(name, init));
        } while (match(TokenType.COMMA));

        // optional semicolon (keeps your style)
        match(TokenType.SEMICOLON);
        return new Stmt.VarDecl(typeTok.type, items);
    }

    // <assignment-or-primary> [;]
    private Stmt exprStmt() {
        Expr e = assignment();
        match(TokenType.SEMICOLON); // optional
        return new Stmt.ExprStmt(e);
    }

    // IDENT '=' assignment  |  concatenation
    private Expr assignment() {
        Expr expr = concatenation();
        if (match(TokenType.EQUAL)) {
            Expr value = assignment();
            if (expr instanceof Expr.Variable var) {
                return new Expr.Assign(var.name, value);
            }
            throw error(previous(), "Invalid assignment target.");
        }
        return expr;
    }

    // primary (& primary)*
    private Expr concatenation() {
        Expr expr = primaryExpr();

        while (match(TokenType.AMPERSAND)) {
            Token operator = previous();
            Expr right = primaryExpr();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    // STRING | $ | IDENT
    private Expr primaryExpr() {
        if (match(TokenType.STRING))   return new Expr.Literal(previous().literal);
        if (match(TokenType.DOLLAR))   return new Expr.Literal("\n");
        if (match(TokenType.NUMBER))     return new Expr.Literal(previous().literal);
        if (match(TokenType.CHAR))       return new Expr.Literal(previous().literal);
        if (match(TokenType.IDENTIFIER)) return new Expr.Variable(previous().lexeme);
        throw error(peek(), "Expect string/number/char literal, $, or identifier.");
    }

    // -------- helpers --------
    private Token consume(TokenType type, String msg) {
        if (check(type)) return advance();
        throw error(peek(), msg);
    }

    // consume one of the specified token types
    private Token consumeOneOf(String msg, TokenType... types) {
        for (TokenType t : types) if (check(t)) return advance();
        throw error(peek(), msg);
    }

    private boolean match(TokenType t) {
        if (check(t)) { advance(); return true; }
        return false;
    }

    private boolean check(TokenType t) {
        if (isAtEnd()) return false;
        return peek().type == t;
    }

    private boolean checkNext(TokenType t) {
        if (isAtEnd()) return false;
        if (current + 1 >= tokens.size()) return false;
        return tokens.get(current + 1).type == t;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() { return peek().type == TokenType.EOF; }
    private Token peek()      { return tokens.get(current); }
    private Token previous()  { return tokens.get(current - 1); }

    ParseError error(Token token, String message) {
        ErrorReporter.error(token.line, token.col, message);
        return new ParseError();
    }

    static class ParseError extends RuntimeException {}
}
package com.bisayapp;

import java.util.*;

public class Lexer {
  private final String src;
  private final List<Token> tokens = new ArrayList<>();
  private int start = 0, current = 0, line = 1, col = 1;

  private static final Map<String, TokenType> KEYWORDS = new HashMap<>();
  static {
    // Core structure
    KEYWORDS.put("SUGOD", TokenType.SUGOD);
    KEYWORDS.put("KATAPUSAN", TokenType.KATAPUSAN);

    // I/O
    KEYWORDS.put("IPAKITA", TokenType.IPAKITA);
    KEYWORDS.put("DAWAT", TokenType.DAWAT);

    // Decls & types
    KEYWORDS.put("MUGNA", TokenType.MUGNA);
    KEYWORDS.put("NUMERO", TokenType.NUMERO);
    KEYWORDS.put("LETRA", TokenType.LETRA);
    KEYWORDS.put("TINUOD", TokenType.TINUOD);
    KEYWORDS.put("TIPIK", TokenType.TIPIK);

    // Control & logic per spec
    KEYWORDS.put("KUNG", TokenType.KUNG);
    KEYWORDS.put("WALA", TokenType.WALA);
    KEYWORDS.put("DILI", TokenType.DILI);
    KEYWORDS.put("UG", TokenType.UG);
    KEYWORDS.put("O", TokenType.O);
    KEYWORDS.put("PUNDOK", TokenType.PUNDOK);
  }

  public Lexer(String source) { this.src = source; }

  public List<Token> scanTokens() {
    while (!isAtEnd()) {
      start = current;
      scanToken();
    }
    tokens.add(new Token(TokenType.EOF, "", null, line, col));
    return tokens;
  }

  // --- core scanning ---
  private void scanToken() {
    char c = advance();
    switch (c) {
      case '(' -> add(TokenType.LEFT_PAREN);
      case ')' -> add(TokenType.RIGHT_PAREN);
      case '{' -> add(TokenType.LEFT_BRACE);
      case '}' -> add(TokenType.RIGHT_BRACE);
      case '[' -> {
        if (match('[')) {
          // [[ is escape for literal [, need to consume the closing ]
          if (match(']')) {
            tokens.add(new Token(TokenType.STRING, "[[]", "[", line, col));
          } else {
            ErrorReporter.error(line, col, "Expected ']' after '[['.");
          }
        } else {
          escapeCode();
        }
      }
      case ']' -> add(TokenType.RIGHT_BRACKET);
      case ',' -> add(TokenType.COMMA);
      case '.' -> add(TokenType.DOT);
      case ':' -> add(TokenType.COLON);
      case ';' -> add(TokenType.SEMICOLON);
      case '+' -> add(TokenType.PLUS);
      case '-' -> {
        if (match('-')) lineComment(); else add(TokenType.MINUS);
      }
      case '*' -> add(TokenType.STAR);
      case '/' -> add(TokenType.SLASH);
      case '%' -> add(TokenType.PERCENT);
      case '&' -> add(TokenType.AMPERSAND);
      case '$' -> add(TokenType.DOLLAR);
      case '!' -> add(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
      case '=' -> add(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
      case '<' -> {
        if (match('>')) add(TokenType.LT_GT);
        else add(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
      }
      case '>' -> add(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
      case '"' -> string();
      case '\'' -> character();
      case ' ', '\r', '\t' -> {} // ignore whitespace
      case '\n' -> { line++; col = 0; }
      default -> {
        if (isDigit(c)) number();
        else if (isAlpha(c)) identifier();
        else ErrorReporter.error(line, col, "Unexpected character: " + c);
      }
    }
    col++;
  }

  // --- helpers ---
  private boolean isAtEnd() { return current >= src.length(); }
  private char advance() { return src.charAt(current++); }
  private boolean match(char expected) {
    if (isAtEnd()) return false;
    if (src.charAt(current) != expected) return false;
    current++; return true;
  }
  private char peek() { return isAtEnd() ? '\0' : src.charAt(current); }

  private void add(TokenType type) {
    String text = src.substring(start, current);
    tokens.add(new Token(type, text, null, line, col));
  }

  private void lineComment() { while (!isAtEnd() && peek() != '\n') advance(); }

  private void escapeCode() {
    // Already consumed the '['
    // Special case: if we see ']' followed by ']', it's []]
    if (peek() == ']' && peekNext() == ']') {
      advance(); // consume first ']'
      advance(); // consume second ']'
      tokens.add(new Token(TokenType.STRING, "[]]", "]", line, col));
      return;
    }
    
    // Read until ']'
    StringBuilder sb = new StringBuilder();
    while (!isAtEnd() && peek() != ']') {
      sb.append(advance());
    }
    if (isAtEnd()) {
      ErrorReporter.error(line, col, "Unterminated escape code.");
      return;
    }
    advance(); // consume the ']'
    
    String code = sb.toString();
    String escaped = switch (code) {
      case "&" -> "&";
      case "n" -> "\n";
      case "t" -> "\t";
      case "\"" -> "\"";
      case "'" -> "'";
      case "[" -> "[";
      case "]" -> "]";
      case "" -> "";  // empty escape code for []
      default -> {
        ErrorReporter.error(line, col, "Unknown escape code: [" + code + "]");
        yield "";
      }
    };
    
    // Create a string token with the escaped character
    String lexeme = "[" + code + "]";
    tokens.add(new Token(TokenType.STRING, lexeme, escaped, line, col));
  }

  private void string() {
    StringBuilder sb = new StringBuilder();
    while (!isAtEnd() && peek() != '"') {
      char ch = advance();
      if (ch == '\n') { line++; col = 0; }
      sb.append(ch);
    }
    if (isAtEnd()) ErrorReporter.error(line, col, "Unterminated string.");
    advance(); // closing "
    tokens.add(new Token(TokenType.STRING, src.substring(start, current), sb.toString(), line, col));
  }

  private void character() {
    // Simple char literal: 'x'
    if (isAtEnd() || peek() == '\n') ErrorReporter.error(line, col, "Unterminated char.");
    char value = advance();
    if (isAtEnd() || advance() != '\'') ErrorReporter.error(line, col, "Invalid char literal.");
    tokens.add(new Token(TokenType.CHAR, src.substring(start, current), value, line, col));
  }

  private void number() {
    while (isDigit(peek())) advance();
    // Optional fractional part
    if (peek() == '.' && isDigit(peekNext())) {
      advance(); while (isDigit(peek())) advance();
    }
    String text = src.substring(start, current);
    tokens.add(new Token(TokenType.NUMBER, text, Double.parseDouble(text), line, col));
  }
  private char peekNext() { return (current + 1 >= src.length()) ? '\0' : src.charAt(current + 1); }

  private void identifier() {
    while (isAlphaNum(peek())) advance();
    String text = src.substring(start, current);
    TokenType type = KEYWORDS.getOrDefault(text, TokenType.IDENTIFIER);
    tokens.add(new Token(type, text, null, line, col));
  }

  private boolean isDigit(char c) { return c >= '0' && c <= '9'; }
  private boolean isAlpha(char c) { return Character.isLetter(c) || c == '_'; }
  private boolean isAlphaNum(char c) { return isAlpha(c) || isDigit(c); }
}

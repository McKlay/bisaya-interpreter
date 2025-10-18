package com.bisayapp;

import java.util.*;

public class Lexer {
  // Source code string and token collection
  private final String src;
  private final List<Token> tokens = new ArrayList<>();
  
  // Position tracking: start/current for current token, line/col for error reporting
  private int start = 0, current = 0, line = 1, col = 1;

  // Keyword mapping: Bisaya keywords to their token types
  private static final Map<String, TokenType> KEYWORDS = new HashMap<>();
  static {
    // Core structure keywords
    KEYWORDS.put("SUGOD", TokenType.SUGOD);       // START/BEGIN
    KEYWORDS.put("KATAPUSAN", TokenType.KATAPUSAN); // END

    // I/O operations
    KEYWORDS.put("IPAKITA", TokenType.IPAKITA);   // PRINT/SHOW
    KEYWORDS.put("DAWAT", TokenType.DAWAT);       // INPUT/RECEIVE

    // Variable declarations and data types
    KEYWORDS.put("MUGNA", TokenType.MUGNA);       // CREATE/DECLARE
    KEYWORDS.put("NUMERO", TokenType.NUMERO);     // NUMBER type
    KEYWORDS.put("LETRA", TokenType.LETRA);       // LETTER/CHARACTER type
    KEYWORDS.put("TINUOD", TokenType.TINUOD);     // BOOLEAN type
    KEYWORDS.put("TIPIK", TokenType.TIPIK);       // STRING type

    // Control flow and logical operators
    KEYWORDS.put("KUNG", TokenType.KUNG);         // IF
    KEYWORDS.put("WALA", TokenType.WALA);         // ELSE/NOTHING
    KEYWORDS.put("DILI", TokenType.DILI);         // NOT
    KEYWORDS.put("UG", TokenType.UG);             // AND
    KEYWORDS.put("O", TokenType.O);               // OR
    KEYWORDS.put("PUNDOK", TokenType.PUNDOK);     // GROUP/BLOCK
    
    // Loop keywords
    KEYWORDS.put("ALANG", TokenType.ALANG);       // FOR
    KEYWORDS.put("SA", TokenType.SA);             // (part of FOR loop)
    KEYWORDS.put("SAMTANG", TokenType.SAMTANG);   // WHILE
  }

  /**
   * Constructor - Initialize lexer with source code
   * @param source The Bisaya++ source code string to tokenize
   */
  public Lexer(String source) { this.src = source; }

  /**
   * MAIN TOKENIZATION DRIVER
   * 
   * Scans the entire source code string and produces a list of tokens.
   * This is the primary entry point for lexical analysis.
   * 
   * Algorithm:
   * 1. Loop until end of source is reached
   * 2. Mark start of each token, then call scanToken()
   * 3. Add EOF token at the end for parser convenience
   * 
   * @return Complete list of tokens including final EOF token
   * 
   * Usage: Called by Parser or CLI to begin lexical analysis
   */
  public List<Token> scanTokens() {
    while (!isAtEnd()) {
      start = current;  // Mark start of next token
      scanToken();
    }
    // Add EOF token to mark end of input
    tokens.add(new Token(TokenType.EOF, "", null, line, col));
    return tokens;
  }

  /**
   * CORE TOKEN SCANNER - State machine for character-by-character processing
   * 
   * Reads one character via advance() and determines what type of token to create
   * based on that character. Handles both single-character tokens (like '+', '(')
   * and multi-character tokens (like '==', '<=', '--', escape sequences).
   * 
   * Key responsibilities:
   * - Dispatch to specialized handlers for complex tokens (strings, numbers, identifiers)
   * - Handle multi-character operators using lookahead (match() function)
   * - Process special Bisaya++ syntax like [escape] sequences
   * - Manage position tracking (line/column numbers)
   * 
   * Side effects:
   * - Updates current position and possibly line/col counters
   * - Adds tokens to the tokens list
   * - May call ErrorReporter for invalid characters
   * 
   * Called by: scanTokens() repeatedly until end of source
   */
  private void scanToken() {
    char c = advance();
    switch (c) {
      // Single-character tokens
      case '(' -> add(TokenType.LEFT_PAREN);
      case ')' -> add(TokenType.RIGHT_PAREN);
      case '{' -> add(TokenType.LEFT_BRACE);
      case '}' -> add(TokenType.RIGHT_BRACE);
      case '[' -> {
        // Special handling for escape sequences and literal brackets
        if (match('[')) {
          // [[ is escape for literal [, need to consume the closing ]
          if (match(']')) {
            tokens.add(new Token(TokenType.STRING, "[[]", "[", line, col));
          } else {
            ErrorReporter.error(line, col, "Expected ']' after '[['.");
          }
        } else {
          escapeCode();  // Handle escape sequences like [n], [t], etc.
        }
      }
      case ']' -> add(TokenType.RIGHT_BRACKET);
      case ',' -> add(TokenType.COMMA);
      case '.' -> add(TokenType.DOT);
      case ':' -> add(TokenType.COLON);
      case ';' -> add(TokenType.SEMICOLON);
      
      // Arithmetic operators
      case '+' -> add(match('+') ? TokenType.PLUS_PLUS : TokenType.PLUS);
      case '-' -> {
        if (match('-')) {
          // This is --
          // Rule: At start of line, determine if comment or decrement operator
          if (isAtStartOfLine()) {
            char next = isAtEnd() ? '\0' : peek();
            
            // If followed by whitespace or end, it's definitely a comment
            if (isAtEnd() || next == ' ' || next == '\n' || next == '\r' || next == '\t') {
              lineComment();
            }
            // If followed by '(', it's a decrement operator on an expression (even if invalid)
            else if (next == '(') {
              add(TokenType.MINUS_MINUS);
            }
            // If followed by non-identifier character (like punctuation), it's a comment
            else if (!Character.isJavaIdentifierStart(next) && !Character.isDigit(next)) {
              lineComment();
            }
            // If followed by identifier/digit, check if there's a space later in the line
            // Comments typically have text: "--this is a comment"
            // Decrement statements are usually just: "--x"
            else if (hasSpaceAheadInLine()) {
              // Has space later = likely comment text
              lineComment();
            }
            else {
              // No space ahead, likely expression statement like --x
              add(TokenType.MINUS_MINUS);
            }
          } else {
            // In expression context, always decrement operator
            add(TokenType.MINUS_MINUS);
          }
        } else {
          add(TokenType.MINUS);
        }
      }
      case '*' -> add(TokenType.STAR);
      case '/' -> add(TokenType.SLASH);
      case '%' -> add(TokenType.PERCENT);
      
      // Special symbols
      case '&' -> add(TokenType.AMPERSAND);
      case '$' -> add(TokenType.DOLLAR);
      
      // Comparison operators - supports lookahead for multi-character operators
      case '!' -> add(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
      case '=' -> add(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
      case '<' -> {
        if (match('>')) add(TokenType.LT_GT);       // <> not equal
        else add(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
      }
      case '>' -> add(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
      
      // String and character literals
      case '"' -> string();
      case '\'' -> character();
      
      // Whitespace handling
      case ' ', '\r', '\t' -> {} // ignore whitespace
      case '\n' -> { 
        add(TokenType.NEWLINE);  // generate newline token
        line++; 
        col = 0; 
      }
      
      default -> {
        // Numbers, identifiers, or error
        if (isDigit(c)) number();
        else if (isAlpha(c)) identifier();
        else ErrorReporter.error(line, col, "Unexpected character: " + c);
      }
    }
    col++;  // Track column position
  }

  // ============================================================================
  // POSITION AND CHARACTER UTILITIES
  // ============================================================================

  /**
   * Check if we've reached the end of the source code
   * @return true if no more characters to scan, false otherwise
   */
  private boolean isAtEnd() { return current >= src.length(); }

  /**
   * CONSUME NEXT CHARACTER
   * 
   * Returns the character at the current position and advances the scanner.
   * This is the primary way characters are consumed during tokenization.
   * 
   * @return The character at the current position
   * 
   * Side effects: Increments current position by 1
   * 
   * Usage: Called whenever we need to consume a character we know we want
   */
  private char advance() { return src.charAt(current++); }
  
  /**
   * CONDITIONAL CHARACTER CONSUMPTION - Lookahead for multi-character tokens
   * 
   * Checks if the current character matches expected. If yes, consumes it and returns true.
   * If no, leaves the scanner position unchanged and returns false.
   * 
   * @param expected The character we're looking for
   * @return true if character matched and consumed, false otherwise
   * 
   * Usage: Building multi-character operators like '==', '<=', '--', '<>'
   * Essential for distinguishing '=' from '==' or '-' from '--'
   */
  private boolean match(char expected) {
    if (isAtEnd()) return false;
    if (src.charAt(current) != expected) return false;
    current++; return true;
  }

  /**
   * SAFE LOOKAHEAD - Examine current character without consuming it
   * 
   * @return Current character, or '\0' if at end of source
   * 
   * Usage: Safe for loops that scan until a terminator (strings, numbers, identifiers)
   */
  private char peek() { return isAtEnd() ? '\0' : src.charAt(current); }

  /**
   * TOKEN CREATION HELPER
   * 
   * Creates a token from the current lexeme (substring from start to current position)
   * and adds it to the tokens list with position information.
   * 
   * @param type The TokenType for this token
   * 
   * Side effects: Adds token to tokens list using current start/current positions
   * 
   * Usage: Called by simple single-character token cases
   */
  private void add(TokenType type) {
    String text = src.substring(start, current);
    tokens.add(new Token(type, text, null, line, col));
  }

  /**
   * START OF LINE CHECKER - Determines if we're at the start of a line
   * 
   * Comments in Bisaya++ only exist at the start of lines.
   * A line starts either at the beginning of the file or after a NEWLINE token.
   * 
   * @return true if we're at the start of a line (comment context)
   */
  private boolean isAtStartOfLine() {
    // If no tokens generated yet, we're at start of file (start of line)
    if (tokens.isEmpty()) return true;
    
    // Get the last token
    Token lastToken = tokens.get(tokens.size() - 1);
    
    // If the last token is a NEWLINE, we're at the start of a new line
    return lastToken.type == TokenType.NEWLINE;
  }

  /**
   * SPACE AHEAD CHECKER - Looks ahead in current line for spaces
   * 
   * Used to disambiguate comments from decrement operators at line start.
   * Comments typically contain text with spaces: "--this is a comment"
   * Decrement statements are usually just: "--x"
   * 
   * @return true if there's a space character before the next newline
   */
  private boolean hasSpaceAheadInLine() {
    int lookahead = current;
    while (lookahead < src.length()) {
      char c = src.charAt(lookahead);
      if (c == '\n' || c == '\r') return false; // End of line reached, no space found
      if (c == ' ' || c == '\t') return true;   // Space found
      lookahead++;
    }
    return false; // EOF reached, no space found
  }

  /**
   * COMMENT PROCESSOR - Consume line comment until newline
   * 
   * Handles '--' style comments by consuming all characters until end of line.
   * Does not generate any tokens (comments are ignored in Bisaya++).
   * 
   * Side effects: Advances current position to end of line
   * 
   * Usage: Called when scanToken() encounters '--' sequence at start of line
   */
  private void lineComment() { while (!isAtEnd() && peek() != '\n') advance(); }

  // ============================================================================
  // SPECIALIZED TOKEN HANDLERS
  // ============================================================================

  /**
   * ESCAPE SEQUENCE PROCESSOR - Handle [code] patterns
   * 
   * Processes Bisaya++ escape sequences enclosed in square brackets.
   * Maps escape codes to their actual character values and creates STRING tokens.
   * 
   * Supported escape codes:
   * - [&] -> literal ampersand
   * - [n] -> newline character
   * - [t] -> tab character  
   * - ["] -> literal double quote
   * - ['] -> literal single quote
   * - [[] -> literal left bracket
   * - []] -> literal right bracket (special case handled separately)
   * - [] -> empty string
   * 
   * Special cases:
   * - []] is detected by lookahead and handled before general processing
   * - Unknown codes generate error and produce empty string
   * 
   * @precondition Opening '[' has been consumed by scanToken()
   * 
   * Side effects: 
   * - Consumes characters until ']'
   * - Adds STRING token with escape sequence as lexeme and translated value
   * - May call ErrorReporter for invalid sequences
   */
  private void escapeCode() {
    // Already consumed the '['
    // Special case: if we see ']' followed by ']', it's []]
    if (peek() == ']' && peekNext() == ']') {
      advance(); // consume first ']'
      advance(); // consume second ']'
      tokens.add(new Token(TokenType.STRING, "[]]", "]", line, col));
      return;
    }
    
    // Read escape code content until ']'
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
    // TODO: Fixed - Only allow specific escape sequences as per specification
    String escaped;
    switch (code) {
      case "&" -> escaped = "&";      // literal ampersand
      case "[" -> escaped = "[";      // literal left bracket  
      case "]" -> escaped = "]";      // literal right bracket
      case "" -> escaped = "";        // empty escape code for []
      default -> {
        // For Increment 1, only [[, ]], and [&] are allowed per specification
        ErrorReporter.error(line, col, "Invalid escape sequence: [" + code + "]. Only [[, ]], and [&] are supported.");
        return; // Don't create a token for invalid sequences
      }
    }
    
    // Create string token with escaped value
    String lexeme = "[" + code + "]";
    tokens.add(new Token(TokenType.STRING, lexeme, escaped, line, col));
  }

  /**
   * STRING LITERAL PROCESSOR - Parse double-quoted strings
   * 
   * Handles string literals enclosed in double quotes, supporting multi-line strings.
   * Collects all characters until closing quote and creates STRING token with
   * both the original lexeme and the parsed string value.
   * 
   * Features:
   * - Multi-line support (tracks line numbers within strings)
   * - Raw character collection (no escape processing inside strings)
   * - Proper error reporting for unterminated strings
   * 
   * Note: Escape sequences like [n] are NOT processed inside double-quoted strings.
   * They are only processed when [ appears as a standalone token.
   * 
   * @precondition Opening '"' has been consumed by scanToken()
   * 
   * Side effects:
   * - Consumes characters until closing '"'
   * - Updates line/col counters for embedded newlines
   * - Adds STRING token with full lexeme and parsed content
   * - Calls ErrorReporter if string is not terminated
   */
  private void string() {
    StringBuilder sb = new StringBuilder();
    while (!isAtEnd() && peek() != '"') {
      char ch = advance();
      if (ch == '\n') { line++; col = 0; }  // track newlines in strings
      sb.append(ch);
    }
    if (isAtEnd()) ErrorReporter.error(line, col, "Unterminated string.");
    advance(); // closing "
    tokens.add(new Token(TokenType.STRING, src.substring(start, current), sb.toString(), line, col));
  }

  /**
   * CHARACTER LITERAL PROCESSOR - Parse single-quoted characters
   * 
   * Handles character literals like 'a', 'Z', '1' enclosed in single quotes.
   * Validates that exactly one character exists between the quotes.
   * 
   * Validation:
   * - Must have exactly one character between quotes
   * - Cannot contain newlines
   * - Must be properly terminated with closing quote
   * 
   * @precondition Opening "'" has been consumed by scanToken()
   * 
   * Side effects:
   * - Consumes the character and closing quote
   * - Adds CHAR token with lexeme and character value
   * - Calls ErrorReporter for malformed character literals
   */
  private void character() {
    // TODO: Fixed - Better detection of unclosed character literals
    if (isAtEnd() || peek() == '\n') {
      ErrorReporter.error(line, col, "Unterminated character literal - missing closing quote.");
      return;
    }
    char value = advance();
    if (isAtEnd()) {
      ErrorReporter.error(line, col, "Unterminated character literal - missing closing quote.");
      return;
    }
    if (peek() != '\'') {
      ErrorReporter.error(line, col, "Invalid character literal - expected closing quote after character.");
      return;
    }
    advance(); // consume the closing '
    tokens.add(new Token(TokenType.CHAR, src.substring(start, current), value, line, col));
  }

  /**
   * NUMERIC LITERAL PROCESSOR - Parse integer and decimal numbers
   * 
   * Handles numeric literals in both integer and decimal formats.
   * Uses greedy parsing to consume all consecutive digits, optionally
   * followed by a decimal point and fractional digits.
   * 
   * Supported formats:
   * - Integers: 42, 0, 123
   * - Decimals: 3.14, 0.5, 100.0
   * 
   * Parsing rules:
   * - Requires digit before decimal point (no leading dots like .5)
   * - Requires digit after decimal point if dot is consumed
   * - Uses peekNext() to validate decimal point usage
   * 
   * Storage: All numbers stored as Double regardless of integer/decimal appearance
   * (Parser/Interpreter must handle NUMERO vs TIPIK type distinctions)
   * 
   * @precondition First digit character has been detected by scanToken()
   * 
   * Side effects:
   * - Consumes all consecutive digits and optional decimal portion
   * - Adds NUMBER token with lexeme and parsed Double value
   */
  private void number() {
    while (isDigit(peek())) advance();
    // Optional fractional part
    if (peek() == '.' && isDigit(peekNext())) {
      advance(); // consume the '.'
      while (isDigit(peek())) advance();
    }
    // Optional scientific notation (e.g., 1.5E10, 2e-5)
    if (peek() == 'E' || peek() == 'e') {
      advance(); // consume 'E' or 'e'
      // Optional sign
      if (peek() == '+' || peek() == '-') {
        advance();
      }
      // Exponent digits (required)
      if (!isDigit(peek())) {
        ErrorReporter.error(line, col, "Invalid scientific notation: expected digits after 'E'");
        return;
      }
      while (isDigit(peek())) advance();
    }
    String text = src.substring(start, current);
    tokens.add(new Token(TokenType.NUMBER, text, Double.parseDouble(text), line, col));
  }
  
  /**
   * TWO-CHARACTER LOOKAHEAD UTILITY
   * 
   * Examines the character after the current position without consuming it.
   * Used for multi-character pattern detection like decimal points and escape sequences.
   * 
   * @return Character at current+1 position, or '\0' if beyond end of source
   * 
   * Usage: Validating patterns like "1.5" (ensuring digit after dot)
   */
  private char peekNext() { return (current + 1 >= src.length()) ? '\0' : src.charAt(current + 1); }

  /**
   * IDENTIFIER AND KEYWORD PROCESSOR
   * 
   * Handles identifiers (variable names) and reserved keywords.
   * Uses greedy parsing to consume all alphanumeric characters, then
   * checks against KEYWORDS map to determine final token type.
   * 
   * Identifier rules (Bisaya++):
   * - Start with letter or underscore
   * - Continue with letters, underscores, or digits
   * - Case-sensitive
   * 
   * Keyword detection:
   * - All keywords are uppercase (MUGNA, NUMERO, etc.)
   * - Lowercase versions treated as identifiers
   * - Uses HashMap lookup for O(1) keyword recognition
   * 
   * @precondition First alphabetic character detected by scanToken()
   * 
   * Side effects:
   * - Consumes all consecutive alphanumeric characters
   * - Adds token with either keyword TokenType or IDENTIFIER
   * - Token value is null (lexeme contains the text)
   */
  private void identifier() {
    while (isAlphaNum(peek())) advance();
    String text = src.substring(start, current);
    // Check if identifier is a reserved keyword, default to IDENTIFIER
    TokenType type = KEYWORDS.getOrDefault(text, TokenType.IDENTIFIER);
    tokens.add(new Token(type, text, null, line, col));
  }

  // ============================================================================
  // CHARACTER CLASSIFICATION UTILITIES
  // ============================================================================

  /**
   * Digit character test - checks if character is 0-9
   * @param c Character to test
   * @return true if c is a decimal digit
   */
  private boolean isDigit(char c) { return c >= '0' && c <= '9'; }

  /**
   * Alphabetic character test - letters or underscore
   * @param c Character to test  
   * @return true if c is letter (including Unicode) or underscore
   */
  private boolean isAlpha(char c) { return Character.isLetter(c) || c == '_'; }

  /**
   * Alphanumeric character test - combination of isAlpha and isDigit
   * @param c Character to test
   * @return true if c is letter, underscore, or digit
   */
  private boolean isAlphaNum(char c) { return isAlpha(c) || isDigit(c); }
}

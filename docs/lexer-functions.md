# Lexer.java — Function Reference & Debugging Notes

Purpose: a concise, revisit-friendly reference for every main and helper function in `Lexer.java`. Use this to quickly trace the logical flow and find which functions to inspect for a given input type or bug.

---

## Quick call-graph (top-level)

scanTokens -> scanToken -> (advance, match, add, lineComment, escapeCode, string, character, number, identifier, ErrorReporter)

Helper lookaheads: peek, peekNext

State mutated/shared across functions: `start`, `current`, `line`, `col`, `tokens`, `src` (read-only)

---

## Function list (alphabetical) with details

1) public List<Token> scanTokens()
- Signature: public List<Token> scanTokens()
- Purpose: top-level driver that scans the entire source string and returns the produced token list.
- Inputs: none (uses `src` field)
- Outputs: `List<Token>` (appends an EOF token before returning)
- Side-effects:
  - Repeatedly sets `start = current` and calls `scanToken()` until `isAtEnd()`.
  - Adds EOF token to `tokens`.
- Callers: external (CLI/tests/Parser)
- Reasons to inspect: if tokens are missing at end of input or EOF token incorrect.
- Quick trace for bug: check `isAtEnd()` behavior and whether `current` is advanced correctly by `scanToken()` functions.

2) private void scanToken()
- Signature: private void scanToken()
- Purpose: reads the next character (via `advance()`) and handles token logic via a switch statement.
- Inputs: consumes one or more characters from `src` (via `advance()`, `match()`)
- Outputs: adds zero or more tokens to `tokens` (usually exactly one), or calls error reporters.
- Side-effects: updates `current`, possibly `line`, `col`, `tokens`, and may call sub-handlers that further mutate state.
- Called by: `scanTokens()` repeatedly
- Key branches to inspect when debugging:
  - `'['` branch -> either `tokens.add` for literal `[[ ]` or `escapeCode()`
  - `'-'` branch -> comment detection via `match('-')` and `lineComment()`
  - comparison operators ("=", "<", ">", "!") -> uses `match()` for multi-char operators
  - string/char cases -> delegates to `string()`/`character()`
  - default -> `number()` or `identifier()` or error
- Quick trace steps: print `start/current/line/col` at method entry and exit, and log the read char from `advance()` to see which branch is taken.

3) private boolean isAtEnd()
- Signature: private boolean isAtEnd()
- Purpose: tells whether there are no more characters to scan
- Inputs: none
- Outputs: boolean (true if `current >= src.length()`)
- Side-effects: none
- Debug tip: if scanning hangs or misses EOF, verify `src.length()` and `current` progression.

4) private char advance()
- Signature: private char advance()
- Purpose: return current character then increment `current` (consumes character)
- Inputs: none
- Outputs: char (the character at old `current`)
- Side-effects: increments `current`
- Debug tip: `advance()` is the single place that moves `current` forward one char; ensure every path that consumes text calls `advance()` appropriately. Off-by-one bugs usually originate here or in use of `start` when building lexemes.

5) private boolean match(char expected)
- Signature: private boolean match(char expected)
- Purpose: conditional single-character lookahead: if next char equals `expected`, consume it and return true; else leave scanner unchanged and return false.
- Inputs: `expected` char
- Outputs: boolean
- Side-effects: may increment `current` when a match occurs
- Call sites: operator recognition in `scanToken()` (for `==`, `<=`, `>=`, `<>`, `--`), and the `[[`/`[]` handling
- Debug tip: when multi-character tokens are mis-tokenized, print `peek()` and `current` before/after `match()` to ensure lookahead sees the expected char.

6) private char peek()
- Signature: private char peek()
- Purpose: look at the current (not yet consumed) character; returns NUL ('\0') if at end
- Inputs: none
- Outputs: char
- Side-effects: none
- Debug tip: `peek()` is safe for loops that scan until a terminator (e.g., string, identifier, number). If it returns '\0' prematurely, confirm `isAtEnd()` semantics and that `current` hasn't been advanced too far.

7) private char peekNext()
- Signature: private char peekNext()
- Purpose: look at the character after `current` (two-character lookahead). Returns '\0' if beyond end.
- Inputs: none
- Outputs: char
- Side-effects: none
- Callers: `escapeCode()` special-case (checking `]]`), `number()` to check fractional part, other potential multi-char rules
- Debug tip: use when diagnosing multi-character patterns like `[]]` or decimal points.

8) private void add(TokenType type)
- Signature: private void add(TokenType type)
- Purpose: create a `Token` using the source substring from `start` to `current` and append it to `tokens`.
- Inputs: `TokenType` enum
- Outputs: none (mutates `tokens`)
- Side-effects: reads `src.substring(start, current)` and constructs a `Token` with position (`line`, `col`)
- Important note: `col` passed here is the column at the time `add()` is called (method increments `col` after `scanToken()`), so column in token reflects last update pattern in code; if column seems off, inspect where `col` is adjusted.
- Debug tip: if lexeme text doesn't match expected characters, log `start`/`current` and the substring returned by `src.substring(start,current)`.

9) private void lineComment()
- Signature: private void lineComment()
- Purpose: consume characters until newline without generating tokens (implements `--` comments)
- Inputs: none
- Outputs: none
- Side-effects: repeatedly calls `advance()` until `peek() == '\n'` or `isAtEnd()`; does not add tokens
- Debug tip: if trailing text after `--` is still tokenized, confirm that `lineComment()` is invoked and that it consumes the expected characters.

10) private void escapeCode()
- Signature: private void escapeCode()
- Purpose: handle special bracket escape sequences that start with `[` and end with `]` and map some codes (like `n`, `t`, `&`, `"`, `'`, `[` , `]`, or empty) to string tokens.
- Inputs: assumes `[` has already been consumed (called from `scanToken()`)
- Outputs: adds one Token (TokenType.STRING) containing the lexeme and a `value` with the translated character(s)
- Side-effects: advances `current` to consume escape contents and trailing `]`; may call `ErrorReporter.error(...)` on unknown or unterminated codes
- Key logic and gotchas:
  - Special-case check for `peek() == ']' && peekNext() == ']'` to create the `[]]` token by consuming two `]` characters. This seems to correspond to a specific syntax handling.
  - Supports empty code `[]` producing empty string
  - Maps `n` -> newline, `t` -> tab, `&` to `&`, `"` and `'` to quotes, `[` and `]` to literal brackets
  - Unknown codes trigger `ErrorReporter.error` and insert empty string as value
- Debug tip: to trace bracket-related bugs, run a small input containing `[` and print `peek()`/`peekNext()` values and the `code` string read from `sb`.

11) private void string()
- Signature: private void string()
- Purpose: parse double-quoted string literal: read until matching `"`, support multi-line strings, collect raw content and produce a TokenType.STRING token with lexeme and parsed value
- Inputs: assumes opening `"` has been consumed
- Outputs: adds Token(TokenType.STRING, lexeme, value)
- Side-effects: advances `current`, may increment `line` and reset `col` on embedded newlines, calls `ErrorReporter.error` if EOF reached before closing quote
- Gotchas: if a string contains `"` as content, the lexer does not currently support escape via `\"` — the language uses bracketed escapes instead (`["]`) so `string()` intentionally treats `"` as terminator. Ensure tests use bracket escapes for quotes.
- Debug tip: when unterminated string errors appear, inspect the loop condition `peek() != '"'` and whether `peek()` returns `\0`.

12) private void character()
- Signature: private void character()
- Purpose: parse single-quoted character literal like `'a'` and create a CHAR token
- Inputs: assumes opening `'` consumed
- Outputs: adds Token(TokenType.CHAR, lexeme, value (char))
- Side-effects: consumes the character and the closing `'`, calls `ErrorReporter.error` on unterminated or invalid char literal
- Gotchas: multi-character contents are not valid; method verifies the closing `'` via `advance()` result and errors if mismatched
- Debug tip: if char tokens are empty or incorrect, log `peek()` and the two `advance()` reads used to build the char

13) private void number()
- Signature: private void number()
- Purpose: parse numeric literals (integers and decimals). It reads digits, optionally a '.' and fractional digits, then constructs a NUMBER token with parsed Double value
- Inputs: called when `scanToken()` sees a digit as the first char
- Outputs: adds Token(TokenType.NUMBER, text, Double.parseDouble(text))
- Side-effects: consumes digits via `advance()` while `isDigit(peek())` is true; may consume a '.' plus fractional digits
- Gotchas:
  - Accepts decimals and stores as Double regardless of language type distinctions for NUMERO vs TIPIK (the parser/interpreter must enforce integer vs decimal semantics if required)
  - Uses `peekNext()` to ensure '.' is followed by a digit before consuming the '.'
- Debug tip: if parsing `123.` or `.5` produces unexpected tokens, note that this lexer requires digits before '.' (no leading dot numbers) and requires a digit after '.' to treat it as fractional.

14) private void identifier()
- Signature: private void identifier()
- Purpose: read an identifier (letters/underscore followed by alphanumerics), then check `KEYWORDS` map to decide whether the lexeme is a reserved keyword or user identifier
- Inputs: first character already consumed and known to be alpha; continues to consume while `isAlphaNum(peek())`
- Outputs: adds Token with either the token type from `KEYWORDS` or `TokenType.IDENTIFIER`
- Side-effects: mutates `current`, `tokens`
- Gotchas: keywords are matched case-sensitively; reserved words appear in `KEYWORDS` in uppercase. If source uses lowercase keywords, they get treated as identifiers.
- Debug tip: for mistaken keyword recognition, print the lexeme `text` and confirm the keywords map contains the expected key.

15) private boolean isDigit(char c)
- Signature: private boolean isDigit(char c)
- Purpose: return true if `c` is between `'0'` and `'9'`
- Inputs: `char c`
- Outputs: boolean
- Debug tip: used in loops that consume numeric characters; ensure characters coming from `peek()` are the expected code points.

16) private boolean isAlpha(char c)
- Signature: private boolean isAlpha(char c)
- Purpose: return true if `c` is a letter (via `Character.isLetter`) or underscore `_`
- Inputs: `char c`
- Outputs: boolean
- Debug tip: this allows unicode letters; if you want only ASCII letters, change to range checks.

17) private boolean isAlphaNum(char c)
- Signature: private boolean isAlphaNum(char c)
- Purpose: combine `isAlpha` or `isDigit` check
- Inputs: `char c`
- Outputs: boolean
- Debug tip: used to continue identifier scanning

---

## Common debugging recipes (how to trace a bug fast)

- If a specific input (e.g., `"Hello[n]"` or `MUGNA x=5`) produces wrong tokens:
  1. Add temporary logs at `scanToken()` entry: print `start`, `current`, `line`, `col`, and the `char c = src.charAt(current)` before calling `advance()` (or log the result of `advance()`).
  2. Follow the branch taken in `scanToken()` and instrument the sub-function (e.g., `string()` or `escapeCode()`) similarly.
  3. When tokens are created incorrectly, inspect `add()` caller: log `src.substring(start, current)` and the `TokenType` passed.

- Off-by-one lexeme errors (missing first/last char): verify correct use of `start` prior to `advance()` and `src.substring(start, current)` bounds.

- Wrong line/column numbers: `line` is incremented in two places (newline in `scanToken()` and newline inside `string()`/escape handling). Check that `col` is reset/updated consistently; note the code sets `col = 0` on newline then increments `col` at `scanToken()` end.

- Multi-character operator mis-detection: trace `match()` calls and confirm `current` position after match; print `peek()` where useful.

- Escape sequence failures: log the `code` built by `escapeCode()` and inspect `peek()/peekNext()` values used by the special-case branch.

---

## Quick-trace examples (where to step through code)

1) Input: `MUGNA NUMERO x = 5` (keyword & identifier)
- Expected path: `scanTokens()` -> `scanToken`() reads 'M', default branch -> `isAlpha` true -> `identifier()` consumes "MUGNA" -> maps to TokenType.MUGNA via `KEYWORDS`
- Files/functions to inspect: `scanToken()`, `identifier()`, `KEYWORDS` map, `add()`

2) Input: `-- comment here\n` (comment)
- Expected path: `scanToken()` sees '-' -> `match('-')` true -> `lineComment()` consumes until '\n'
- Inspect: `match()`, `lineComment()` and ensure no tokens added

3) Input: `"Hello[n]"` (string with bracket escape)
- Expected path: `scanToken()` sees '"' -> `string()` reads content until '"', includes the substring `[n]` as characters; bracket escapes only processed when a `[` token-handling path is used (escapeCode() is used when `[` is encountered as a top-level token, not inside `string()`)
- Note: bracket escapes inside double quotes are not processed by `escapeCode()` in current lexer; the lexer collects raw `[n]` inside `string()` value. If the language expects bracket escapes inside strings to be interpreted, the lexer must be changed.
- Inspect: `string()` to see how it treats `[` inside strings; adjust design if needed.

4) Input: `[n]` (escape sequence outside quotes)
- Expected path: `scanToken()` sees '[' -> `escapeCode()` reads 'n' and ']' -> maps to newline token value and creates a STRING token containing lexeme `[n]` and value `"\n"`
- Inspect: `scanToken()`, `escapeCode()`, `tokens` entry created by `tokens.add(new Token(...))`

---

## Notes & potential improvements (quick checklist)
- Column (`col`) handling is unusual: `col` is reset to 0 on newline, and incremented once at end of `scanToken()`; consider revising to maintain exact column of token start.
- Decide whether bracket escapes should be interpreted inside double-quoted `string()` contents; current behavior interprets escapes only when `[` is seen as a token.
- `number()` always produces `Double` values: if the language distinguishes integer (`NUMERO`) vs decimal (`TIPIK`), enforce in parser or change lexer to differentiate.
- `KEYWORDS` is case-sensitive; if language keywords should be case-insensitive, normalize input before lookup.

---

File created: `docs/lexer-functions.md` — open this file when you need a quick map from an input case to the function(s) to step through while debugging.


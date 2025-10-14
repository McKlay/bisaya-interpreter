package com.bisayapp;

public enum TokenType {
  // Single-char symbols
  LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
  LEFT_BRACKET, RIGHT_BRACKET,
  COMMA, DOT, COLON, SEMICOLON,
  PLUS, MINUS, STAR, SLASH, PERCENT,
  AMPERSAND, DOLLAR, EQUAL,

  // Two-char operators
  BANG, BANG_EQUAL, EQUAL_EQUAL,
  GREATER, GREATER_EQUAL, LESS, LESS_EQUAL,
  LT_GT, // "<>" not equal (per spec)
  PLUS_PLUS, MINUS_MINUS, // ++ and --

  // Literals
  IDENTIFIER, STRING, NUMBER, CHAR,

  // Keywords (Bisaya++)
  SUGOD, KATAPUSAN,
  IPAKITA, DAWAT,
  MUGNA,
  NUMERO, LETRA, TINUOD, TIPIK,
  KUNG, WALA, DILI, UG, O, PUNDOK,

  // Special tokens
  NEWLINE,
  EOF
}

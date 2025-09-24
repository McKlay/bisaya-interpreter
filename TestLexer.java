import java.util.*;

public class TestLexer {
    public static void main(String[] args) {
        // Test [[ escape
        String src1 = "[[";
        System.out.println("Testing: " + src1);
        List<Token> tokens1 = new Lexer(src1).scanTokens();
        for (Token token : tokens1) {
            if (token.type != TokenType.EOF) {
                System.out.println("  Type: " + token.type + ", Lexeme: \"" + token.lexeme + "\", Literal: \"" + token.literal + "\"");
            }
        }
        
        // Test ]] escape  
        String src2 = "]]";
        System.out.println("\nTesting: " + src2);
        List<Token> tokens2 = new Lexer(src2).scanTokens();
        for (Token token : tokens2) {
            if (token.type != TokenType.EOF) {
                System.out.println("  Type: " + token.type + ", Lexeme: \"" + token.lexeme + "\", Literal: \"" + token.literal + "\"");
            }
        }
        
        // Test [&] escape
        String src3 = "[&]";
        System.out.println("\nTesting: " + src3);
        List<Token> tokens3 = new Lexer(src3).scanTokens();
        for (Token token : tokens3) {
            if (token.type != TokenType.EOF) {
                System.out.println("  Type: " + token.type + ", Lexeme: \"" + token.lexeme + "\", Literal: \"" + token.literal + "\"");
            }
        }
    }
}

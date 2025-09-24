import java.util.List;
import com.bisayapp.*;

public class TestLexerDebug {
    public static void main(String[] args) {
        String src = """
            SUGOD
            IPAKITA: [[ & $
            IPAKITA: []]
            IPAKITA: [&]
            KATAPUSAN
            """;
        
        List<Token> tokens = new Lexer(src).scanTokens();
        for (Token tok : tokens) {
            System.out.println("Token: " + tok.type + " | Lexeme: '" + tok.lexeme + "' | Literal: '" + tok.literal + "'");
        }
    }
}

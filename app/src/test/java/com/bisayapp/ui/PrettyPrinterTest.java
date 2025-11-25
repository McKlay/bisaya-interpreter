package com.bisayapp.ui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PrettyPrinter
 */
class PrettyPrinterTest {
    
    @Test
    void testBasicIndentation() {
        String input = "SUGOD\nMUGNA NUMERO x\nKATAPUSAN";
        String expected = "SUGOD\n    MUGNA NUMERO x\nKATAPUSAN";
        assertEquals(expected, PrettyPrinter.format(input));
    }
    
    @Test
    void testNestedBlocks() {
        String input = "SUGOD\nKUNG (x>0)\nPUNDOK{\nIPAKITA: x\n}\nKATAPUSAN";
        String expected = "SUGOD\n    KUNG (x > 0)\n    PUNDOK{\n        IPAKITA: x\n    }\nKATAPUSAN";
        assertEquals(expected, PrettyPrinter.format(input));
    }
    
    @Test
    void testOperatorSpacing() {
        String input = "SUGOD\nx=5\ny=x+2*3\nKATAPUSAN";
        String expected = "SUGOD\n    x = 5\n    y = x + 2 * 3\nKATAPUSAN";
        assertEquals(expected, PrettyPrinter.format(input));
    }
    
    @Test
    void testCommaSpacing() {
        String input = "SUGOD\nMUGNA NUMERO x,y,z\nKATAPUSAN";
        String expected = "SUGOD\n    MUGNA NUMERO x, y, z\nKATAPUSAN";
        assertEquals(expected, PrettyPrinter.format(input));
    }
    
    @Test
    void testComparisonOperators() {
        String input = "SUGOD\nKUNG (x>5 UG y<10)\nPUNDOK{\nIPAKITA: x\n}\nKATAPUSAN";
        String expected = "SUGOD\n    KUNG (x > 5 UG y < 10)\n    PUNDOK{\n        IPAKITA: x\n    }\nKATAPUSAN";
        assertEquals(expected, PrettyPrinter.format(input));
    }
    
    @Test
    void testStringPreservation() {
        String input = "SUGOD\nIPAKITA: \"Hello  World\"\nKATAPUSAN";
        String expected = "SUGOD\n    IPAKITA: \"Hello  World\"\nKATAPUSAN";
        assertEquals(expected, PrettyPrinter.format(input));
    }
    
    @Test
    void testCommentPreservation() {
        String input = "SUGOD\n@@ This is a comment\nMUGNA NUMERO x\nKATAPUSAN";
        String expected = "SUGOD\n    @@ This is a comment\n    MUGNA NUMERO x\nKATAPUSAN";
        assertEquals(expected, PrettyPrinter.format(input));
    }
    
    @Test
    void testBlankLinePreservation() {
        String input = "SUGOD\n\nMUGNA NUMERO x\n\nKATAPUSAN";
        String expected = "SUGOD\n\n    MUGNA NUMERO x\n\nKATAPUSAN";
        assertEquals(expected, PrettyPrinter.format(input));
    }
    
    @Test
    void testEmptyCode() {
        String input = "";
        assertEquals("", PrettyPrinter.format(input));
    }
    
    @Test
    void testNullCode() {
        assertDoesNotThrow(() -> PrettyPrinter.format(null));
    }
    
    @Test
    void testMultiLevelNesting() {
        String input = "SUGOD\nKUNG (x>0)\nPUNDOK{\nALANG SA (i=1,i<=5,i++)\nPUNDOK{\nIPAKITA: i\n}\n}\nKATAPUSAN";
        String expected = "SUGOD\n    KUNG (x > 0)\n    PUNDOK{\n        ALANG SA (i = 1, i <= 5, i++)\n        PUNDOK{\n            IPAKITA: i\n        }\n    }\nKATAPUSAN";
        assertEquals(expected, PrettyPrinter.format(input));
    }
    
    @Test
    void testConcatenation() {
        String input = "SUGOD\nIPAKITA: \"Hello\"&name&\"!\"\nKATAPUSAN";
        String expected = "SUGOD\n    IPAKITA: \"Hello\" & name & \"!\"\nKATAPUSAN";
        assertEquals(expected, PrettyPrinter.format(input));
    }
    
    @Test
    void testForLoopSpacing() {
        String input = "SUGOD\nALANG SA (i=1,i<=10,i++)\nPUNDOK{\nIPAKITA: i\n}\nKATAPUSAN";
        String expected = "SUGOD\n    ALANG SA (i = 1, i <= 10, i++)\n    PUNDOK{\n        IPAKITA: i\n    }\nKATAPUSAN";
        assertEquals(expected, PrettyPrinter.format(input));
    }
    
    @Test
    void testWhileLoop() {
        String input = "SUGOD\nSAMTANG (x<10)\nPUNDOK{\nx++\n}\nKATAPUSAN";
        String expected = "SUGOD\n    SAMTANG (x < 10)\n    PUNDOK{\n        x++\n    }\nKATAPUSAN";
        assertEquals(expected, PrettyPrinter.format(input));
    }
    
    @Test
    void testChainedAssignment() {
        String input = "SUGOD\nx=y=z=5\nKATAPUSAN";
        String expected = "SUGOD\n    x = y = z = 5\nKATAPUSAN";
        assertEquals(expected, PrettyPrinter.format(input));
    }
    
    @Test
    void testAlreadyFormatted() {
        String input = "SUGOD\n    MUGNA NUMERO x\n    x = 5\nKATAPUSAN";
        String result = PrettyPrinter.format(input);
        // Should maintain proper formatting
        assertTrue(result.contains("    MUGNA NUMERO x"));
        assertTrue(result.contains("    x = 5"));
    }
    
    @Test
    void testInlineComment() {
        String input = "SUGOD\nx=5  @@ Set value\nKATAPUSAN";
        // Inline comments should be preserved (implementation may vary)
        String result = PrettyPrinter.format(input);
        assertTrue(result.contains("@@"));
    }
    
    @Test
    void testComplexExpression() {
        String input = "SUGOD\nresult=(a+b)*(c-d)/e\nKATAPUSAN";
        String expected = "SUGOD\n    result = (a + b) * (c - d) / e\nKATAPUSAN";
        assertEquals(expected, PrettyPrinter.format(input));
    }
}

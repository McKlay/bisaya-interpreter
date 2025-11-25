package com.bisayapp.ui;

public class ForLoopDebug {
    public static void main(String[] args) {
        String input = "SUGOD\nALANG SA (i=1,i<=10,i++)\nPUNDOK{\nIPAKITA: i\n}\nKATAPUSAN";
        String result = PrettyPrinter.format(input);
        
        System.out.println("INPUT:");
        System.out.println(input);
        System.out.println("\nRESULT:");
        System.out.println(result);
        System.out.println("\nEXPECTED:");
        System.out.println("SUGOD\n    ALANG SA (i=1, i<=10, i++)\n    PUNDOK{\n        IPAKITA: i\n    }\nKATAPUSAN");
        
        System.out.println("\n\nCHARACTER-BY-CHARACTER COMPARISON:");
        String expected = "SUGOD\n    ALANG SA (i=1, i<=10, i++)\n    PUNDOK{\n        IPAKITA: i\n    }\nKATAPUSAN";
        String[] resultLines = result.split("\n", -1);
        String[] expectedLines = expected.split("\n", -1);
        
        for (int i = 0; i < Math.max(resultLines.length, expectedLines.length); i++) {
            String r = i < resultLines.length ? resultLines[i] : "[MISSING]";
            String e = i < expectedLines.length ? expectedLines[i] : "[MISSING]";
            boolean match = r.equals(e);
            System.out.println("Line " + i + ": " + (match ? "✓" : "✗"));
            System.out.println("  Result  : '" + r + "'");
            System.out.println("  Expected: '" + e + "'");
        }
    }
}

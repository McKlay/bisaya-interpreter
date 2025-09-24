package com.bisayapp;

public class ErrorReporter {
    private static boolean hadError = false;
    
    public static void error(int line, int column, String message) {
        report(line, column, "", message);
    }
    
    public static void error(int line, String where, String message) {
        report(line, 0, where, message);
    }
    
    private static void report(int line, int column, String where, String message) {
        System.err.println("[line " + line + " col " + column + "] Error" + where + ": " + message);
        hadError = true;
    }
    
    public static boolean hadError() {
        return hadError;
    }
    
    public static void reset() {
        hadError = false;
    }
}

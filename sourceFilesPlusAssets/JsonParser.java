import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonParser {
    
    // Extract a primitive value (String, int, boolean) by key
    public static String getValue(String json, String key) {
        // For strings: "key": "value"
        Pattern stringPattern = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*\"([^\"]+)\"");
        Matcher stringMatcher = stringPattern.matcher(json);
        if (stringMatcher.find()) {
            return stringMatcher.group(1);
        }
        
        // For numbers/booleans: "key": value
        Pattern valuePattern = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*(-?\\d+\\.?\\d*|true|false|null)");
        Matcher valueMatcher = valuePattern.matcher(json);
        if (valueMatcher.find()) {
            return valueMatcher.group(1).trim();
        }
        
        return "";
    }

    // Extract content inside { } for a specific key
    public static String getObjectContent(String json, String key) {
        String search = "\"" + key + "\"";
        int start = json.indexOf(search);
        if (start == -1) return "";
        
        // Move past the key to find the colon
        start = json.indexOf(":", start);
        if (start == -1) return "";
        
        // Skip whitespace after colon
        start++;
        while (start < json.length() && java.lang.Character.isWhitespace(json.charAt(start))) {
            start++;
        }
        
        // Now we should be at the opening brace
        if (start >= json.length() || json.charAt(start) != '{') return "";

        int end = findClosing(json, start, '{', '}');
        if (end == -1) return "";

        return json.substring(start, end + 1);
    }

    // Extract content inside [ ] for a specific key
    public static String getArrayContent(String json, String key) {
        String search = "\"" + key + "\"";
        int start = json.indexOf(search);
        if (start == -1) return "";
        
        // Move past the key to find the colon
        start = json.indexOf(":", start);
        if (start == -1) return "";
        
        // Skip whitespace after colon
        start++;
        while (start < json.length() && java.lang.Character.isWhitespace(json.charAt(start))) {
            start++;
        }
        
        // Now we should be at the opening bracket
        if (start >= json.length() || json.charAt(start) != '[') return "";

        int end = findClosing(json, start, '[', ']');
        if (end == -1) return "";

        return json.substring(start + 1, end); // Return content inside brackets
    }

    // Helper to find matching brace
    private static int findClosing(String str, int start, char open, char close) {
        int balance = 1;
        boolean inString = false;
        
        for (int i = start + 1; i < str.length(); i++) {
            char c = str.charAt(i);
            
            // Handle escaped quotes
            if (c == '\\' && i + 1 < str.length()) {
                i++; // Skip next character
                continue;
            }
            
            // Toggle string state
            if (c == '"') {
                inString = !inString;
                continue;
            }
            
            // Only count brackets outside of strings
            if (!inString) {
                if (c == open) balance++;
                else if (c == close) balance--;
                
                if (balance == 0) return i;
            }
        }
        return -1;
    }
}
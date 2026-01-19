package badWordScanner.helper;

import java.util.Base64;

public class JsonHelper {
    public static String extractTextFromJSON(String jsonResponse) {
        // 1. Suche nach dem Schlüssel "content"
        int keyIndex = jsonResponse.indexOf("\"content\"");

        if (keyIndex != -1) {
            // 2. Ab hier suchen wir den Doppelpunkt
            int colonIndex = jsonResponse.indexOf(":", keyIndex);

            // 3. Ab dem Doppelpunkt suchen wir das erste Gänsefüßchen (Start des Textes)
            int valueStartIndex = jsonResponse.indexOf("\"", colonIndex);

            if (colonIndex != -1 && valueStartIndex != -1) {
                // Der eigentliche Text beginnt NACH dem Gänsefüßchen
                int startIndex = valueStartIndex + 1;

                // 4. Wir suchen das Ende (deine Logik war hier super, ich habe sie übernommen!)
                int endIndex = -1;
                boolean isEscaped = false;

                for (int i = startIndex; i < jsonResponse.length(); i++) {
                    char c = jsonResponse.charAt(i);

                    if (c == '\\') {
                        // Das nächste Zeichen ist escaped
                        isEscaped = !isEscaped;
                    } else if (c == '"' && !isEscaped) {
                        // Wir haben das ENDE-Gänsefüßchen gefunden
                        endIndex = i;
                        break;
                    } else {
                        isEscaped = false;
                    }
                }

                if (endIndex != -1) {
                    String result = jsonResponse.substring(startIndex, endIndex);

                    return cleanUpAnswer(result);
                }
            }
        }
        System.out.println("DEBUG - Konnte nicht parsen. JSON war: " + jsonResponse);
        return "[Error] Konnte Antwort nicht lesen.";
    }

    private static String cleanUpAnswer(String jsonResponse) {
        return jsonResponse
                .replace("\\n", "  ")
                .replace("\\\"", "\"")
                .replace("\\u00e4", "ä")
                .replace("\\u00c4", "Ä")
                .replace("\\u00f6", "ö")
                .replace("\\u00d6", "Ö")
                .replace("\\u00fc", "ü")
                .replace("\\u00dc", "Ü")
                .replace("\\u00df", "ß");
    }

    public static String makeSafeForJson(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "  ")
                .replace("\r", "")
                .replace("\t", "\\t");
    }

    public static String makeContextForJson(String[][] examples) {
        String output = "";

        for (String[] example : examples) {
            output +=
                    "    {\"role\": \"user\", \"content\": \"" + decodeAndMakesafe(example[0]) + "\"},\n" +
                    "    {\"role\": \"assistant\", \"content\": \"" + decodeAndMakesafe(example[1]) + "\"},\n";
        }
        return output;
    }
    private static String decodeAndMakesafe(String text) {
        return makeSafeForJson(new String(Base64.getDecoder().decode(text)));
    }

}



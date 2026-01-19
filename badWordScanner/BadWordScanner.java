package badWordScanner;

import badWordScanner.helper.HttpHelper;

import java.net.http.HttpResponse;

import static badWordScanner.helper.JsonHelper.*;

public class BadWordScanner {
    private Sensetivity sensetivity;
    private String apiUrl;
    private String aiModel;

    private double temperature = 0.01;
    private double top_p = 0.1;
    private int max_tokens = 30;
    private String user = "BadWordScanner";


    public BadWordScanner(Sensetivity sensetivity, String api_url,  String ai_model) {
        this.sensetivity = sensetivity;
        this.apiUrl = api_url;
        this.aiModel = ai_model;
    }

    public Response Check(String text) {
        if (sensetivity == Sensetivity.NOFILTER) {
            return new Response(true, "");
        } else {
            String response = Checkmessage(text);
            return CreateChecked(response);
        }
    }


    public String Checkmessage(String text) {
        try {
            String safeMessage = makeSafeForJson(text);

            String systemprompt = "You are a moderator. Check the following German text. " +
                    "**Rules: **" +
                    "1. If the text does NOT meet the conditions: Respond ONLY with: [false] " +
                    "2. If the text meets the conditions (including hidden ones such as Leetspeak, e.g., ‘3’ instead of ‘e’): Respond with: [true] - followed by the words that meet the conditions and a brief explanation of why you recognized a word (max. 1 sentence). " +
                    "3. Send [true] or [false] first, nothing else!!! The first thing you send must be one of these!" +
                    "4. This also applies if the word is hidden, i.e. letters have been swapped or reversed. " +

                    "Conditions that should be [true]:" +
                    sensetivity.getConditions() +

                    "Exceptions that should be [false]:" +
                    sensetivity.getExceptions();

            String saveSystempromt = makeSafeForJson(systemprompt);

            String jsonBody = "{\n" +
                    "  \"model\": \"" + aiModel + "\",\n" +
                    "  \"messages\": [\n" +
                    "    {\"role\": \"system\", \"content\": \"" + saveSystempromt + "\"},\n" +

                    makeContextForJson(sensetivity.getExample())  +

                    "    {\"role\": \"user\", \"content\": \"Analyze this text: " + safeMessage + "\"}\n" +
                    "  ],\n" +
                    "  \"temperature\": " + temperature + ",\n" +
                    "  \"top_p\": " + top_p + ",\n" +
                    "  \"max_tokens\": " + max_tokens + ",\n" +
                    "  \"user\": \"" + user + "\"" +
                    "}";


            HttpResponse<String> response = HttpHelper.sendHttpRequest(jsonBody, apiUrl);

            if (response.statusCode() == 200) {
                return extractTextFromJSON(response.body());
            } else {
                return "[Error] Server response with Code: " + response.statusCode();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "[Error] connection problem";
        }
    }

    private Response CreateChecked(String response) {
        String stripedResponse = response.strip();
        String lowerResponse = stripedResponse.toLowerCase();

        if (lowerResponse.contains("[false]") || lowerResponse.contains("false")) {
            return new Response(true, "");
        } else if (lowerResponse.contains("[true]") || lowerResponse.contains("true")) {
            return new Response(false, response.replace("[true]", ""));
        } else if (lowerResponse.startsWith("[Error]")) {
            System.out.println("There is an Issue" + response);
            return new Response(false, "");
        } else {
            System.out.println("AI has Problem: " + response);
            return new Response(false, response);
        }
    }

    public Sensetivity getSensetivity() {
        return sensetivity;
    }
    public void setSensetivity(Sensetivity sensetivity) {
        this.sensetivity = sensetivity;
    }
    public String getApiUrl() {
        return apiUrl;
    }
    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
    public String getAiModel() {
        return aiModel;
    }
    public void setAiModel(String aiModel) {
        this.aiModel = aiModel;
    }


    //You Probably won't need this
    public double getTemperature() {
        return temperature;
    }
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
    public double getTop_p() {
        return top_p;
    }
    public void setTop_p(double top_p) {
        this.top_p = top_p;
    }
    public int getMax_tokens() {
        return max_tokens;
    }
    public void setMax_tokens(int max_tokens) {
        this.max_tokens = max_tokens;
    }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
}

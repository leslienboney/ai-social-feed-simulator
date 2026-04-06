package com.example.a433assn4;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Simple REST helper for Gemini using the HTTP API.
 * Uses a current model ("gemini-2.0-flash-lite") on the v1beta endpoint.
 *
 * CALL THIS FROM YOUR APP AS:
 *   String reply = GeminiApi.generateText(promptString);
 */
public class GeminiApi {

    // 🔑 PUT YOUR REAL GEMINI API KEY HERE
    // (same key you used before – don’t check this into git)
    private static final String API_KEY = "AIzaSyAK9iePuQplc3AHhjwcd3KeX8zwp3GZ4Ow";

    // A *supported* model id – 1.x and plain "gemini-pro" are retired.
    // You can also try: "gemini-2.0-flash" or "gemini-2.5-flash-lite"
    private static final String MODEL = "gemini-2.0-flash-lite";

    // v1beta HTTP endpoint for generateContent
    // https://generativelanguage.googleapis.com/v1beta/models/{MODEL}:generateContent
    private static final String ENDPOINT =
            "https://generativelanguage.googleapis.com/v1beta/models/"
                    + MODEL + ":generateContent?key=" + API_KEY;

    private static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    private static final OkHttpClient client = new OkHttpClient();

    /**
     * Sends a plain-text prompt to Gemini and returns a plain-text response.
     * On any error, returns a string starting with "Gemini error:" or
     * "Gemini parse error:" so you can display it in the UI.
     */
    public static String generateText(String prompt) {
        try {
            // ---------- Build request JSON ----------
            JSONObject root = new JSONObject();
            JSONArray contents = new JSONArray();
            JSONObject content = new JSONObject();
            JSONArray parts = new JSONArray();
            JSONObject part = new JSONObject();

            part.put("text", prompt);
            parts.put(part);
            content.put("parts", parts);
            contents.put(content);
            root.put("contents", contents);

            RequestBody body = RequestBody.create(root.toString(), JSON);

            Request request = new Request.Builder()
                    .url(ENDPOINT)
                    .post(body)
                    .build();

            // ---------- Call HTTP API ----------
            try (Response response = client.newCall(request).execute()) {
                String bodyStr = (response.body() != null)
                        ? response.body().string()
                        : "";

                // Non-200 => surface any error message from JSON if possible
                if (!response.isSuccessful()) {
                    try {
                        JSONObject errObj = new JSONObject(bodyStr);
                        if (errObj.has("error")) {
                            JSONObject err = errObj.getJSONObject("error");
                            String msg = err.optString("message", bodyStr);
                            return "Gemini error: " + msg;
                        }
                    } catch (Exception ignored) {
                        // fall through to generic message
                    }
                    return "Gemini error: HTTP " + response.code() + " " + bodyStr;
                }

                // ---------- Parse response ----------
                try {
                    JSONObject respJson = new JSONObject(bodyStr);

                    if (!respJson.has("candidates")) {
                        return "Gemini parse error: no candidates field";
                    }

                    JSONArray candidates = respJson.getJSONArray("candidates");
                    if (candidates.length() == 0) {
                        return "Gemini parse error: empty candidates";
                    }

                    JSONObject first = candidates.getJSONObject(0);
                    if (!first.has("content")) {
                        return "Gemini parse error: no content";
                    }

                    JSONObject respContent = first.getJSONObject("content");
                    if (!respContent.has("parts")) {
                        return "Gemini parse error: no parts";
                    }

                    JSONArray respParts = respContent.getJSONArray("parts");
                    if (respParts.length() == 0) {
                        return "Gemini parse error: empty parts";
                    }

                    JSONObject firstPart = respParts.getJSONObject(0);
                    String text = firstPart.optString("text", "").trim();
                    if (text.isEmpty()) {
                        return "Gemini parse error: empty text";
                    }

                    return text;

                } catch (Exception je) {
                    return "Gemini parse error: " + je.getMessage();
                }
            }

        } catch (Exception e) {
            return "Gemini error: " + e.getMessage();
        }
    }
}
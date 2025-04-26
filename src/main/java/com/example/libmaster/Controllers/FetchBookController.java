package com.example.libmaster.Controllers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

public class FetchBookController {
    public static final String api = "https://www.googleapis.com/books/v1/volumes?q=";

    public void fetchBooks(String keyword) {
        String apiUrl = api + keyword.replace(" ", "+");

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            conn.disconnect();

            JSONObject json = new JSONObject(response.toString());
            JSONArray items = json.getJSONArray("items");

            System.out.println("📚 Top 5 Books for: " + keyword);
            for (int i = 0; i < Math.min(5, items.length()); i++) {
                JSONObject book = items.getJSONObject(i).getJSONObject("volumeInfo");

                String title = book.optString("title", "No Title");
                JSONArray authors = book.optJSONArray("authors");
                String authorList = (authors != null) ? authors.join(", ").replace("\"", "") : "Unknown";

                System.out.printf("%d. %s — by %s\n", i + 1, title, authorList);
            }

        } catch (Exception e) {
            System.out.println("❌ Error fetching data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

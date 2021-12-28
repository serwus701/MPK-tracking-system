package com.mycompany.app;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

public class GetMpkData {

    private static HttpURLConnection con;


    public static String getMpkData(LinkedList<String> requestedLines) throws IOException {
        var url = "https://mpk.wroc.pl/bus_position";

        var urlParameters = new StringBuilder();

        for (String param : requestedLines) {
            urlParameters.append("busList[][]=").append(param).append("&");
        }

        byte[] postBody = urlParameters.toString().getBytes(StandardCharsets.UTF_8);

        try {
            var myUrl = new URL(url);

            con = (HttpURLConnection) myUrl.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");


            try (DataOutputStream body = new DataOutputStream(con.getOutputStream())) {
                body.write(postBody);
            }

            StringBuilder results;
            String currLine;

            try (var reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {

                results = new StringBuilder();

                while ((currLine = reader.readLine()) != null) {
                    results.append(currLine);
                }
            }
            return results.toString();

        } finally {
            con.disconnect();
        }


    }

}

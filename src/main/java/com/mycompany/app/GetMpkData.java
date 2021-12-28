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

    private static HttpURLConnection connection;

    public static String getMpkData(LinkedList<String> requestedLines) throws IOException {
        var urlAddress = "https://mpk.wroc.pl/bus_position";

        var urlParameters = new StringBuilder();

        for (String param : requestedLines) {
            urlParameters.append("busList[][]=").append(param).append("&");
        }

        byte[] postBody = urlParameters.toString().getBytes(StandardCharsets.UTF_8);

        try {
            var myUrl = new URL(urlAddress);

            connection = (HttpURLConnection) myUrl.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            try (DataOutputStream body = new DataOutputStream(connection.getOutputStream())) {
                body.write(postBody);
            }

            StringBuilder output;
            String currentLine;

            try (var myReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {

                output = new StringBuilder();

                while ((currentLine = myReader.readLine()) != null) {
                    output.append(currentLine);
                }
            }
            return output.toString();

        } finally {
            connection.disconnect();
        }


    }
}

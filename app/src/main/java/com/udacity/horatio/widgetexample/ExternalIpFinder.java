package com.udacity.horatio.widgetexample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ExternalIpFinder {

    private static final String USER_AGENT = "Mozilla/5.0";

    private static final String GET_URL = "http://ipecho.net/plain";

	private static String sendGET() throws IOException {
		URL obj = new URL(GET_URL);
		HttpURLConnection con = (HttpURLConnection)obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);
		int responseCode = con.getResponseCode();
		//System.out.println("GET Response Code :: " + responseCode);
		if (responseCode == HttpURLConnection.HTTP_OK) {
            
            // success
			BufferedReader in = new BufferedReader(new InputStreamReader( con.getInputStream()) );
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			return response.toString();
		} else {
			return "#.#.#.#";
		}

	}

    public static String getExternalIpAddress() throws IOException {
        return sendGET();
        //return "x.x.x.x";
    }
}

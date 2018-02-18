package com.udacity.horatio.widgetexample;

import android.os.AsyncTask;//<Params, Progress, Result>;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.udacity.horatio.widgetexample.RemoteViewChanger;

/**
 * Note that calling getIpAddress() diretly from the main thread throws android.os.NetworkOnMainThreadException
 * https://stackoverflow.com/questions/40749664/android-studio-httpurlconnection-app-crash
 *

 */
public class ExternalIpFinder extends AsyncTask<Void, Void, String>{

	private RemoteViewChanger remoteViewChanger;

    private static final String USER_AGENT = "Mozilla/5.0";

    private static final String GET_URL = "http://ipecho.net/plain";

	ExternalIpFinder(RemoteViewChanger rvc) {
		remoteViewChanger = rvc;
	}

	private static String getIpAddress() throws IOException {
		URL obj = new URL(GET_URL);
		HttpURLConnection con = (HttpURLConnection)obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);
		int responseCode = con.getResponseCode();

		// //System.out.println("GET Response Code :: " + responseCode);
		if(responseCode == HttpURLConnection.HTTP_OK) {
            
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

    public static String getExternalIpAddress() {

        String ipAddress = "";
		try {
			ipAddress = getIpAddress();
		} catch(IOException ioe) {
			ipAddress = "IO exception";
		}

        return ipAddress;
    }

    @Override
	protected String doInBackground(Void...v) {
		return getExternalIpAddress();
	}

    @Override
    protected void onPostExecute(String result) {
		remoteViewChanger.setText(R.id.public_ip_address,result);
    }
}

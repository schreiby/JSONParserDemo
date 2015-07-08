package com.example.ettie.jsonparserdemo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

    private EditText etLocation;
    private TextView tvOutput;
    private String base_url = "http://api.openweathermap.org/data/2.5/weather?q=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etLocation = (EditText)findViewById(R.id.etLocation);
        tvOutput = (TextView)findViewById(R.id.tvOutput);
    }

    public void getWeather(View view) {
        String location = etLocation.getText().toString();
        String urlString = base_url + location;

        new JSONParserTask().execute(urlString);
    }

    private String[] parseJSON(String in) {
        String[] dataArr = {null, null, null};

        try{
            JSONObject reader = new JSONObject(in);
            JSONObject main = reader.getJSONObject("main");

            dataArr[0] = main.getString("temp");
            dataArr[1] = main.getString("pressure");
            dataArr[2] = main.getString("humidity");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataArr;
    }

    private String fetchWeatherData(String urlString) {
        String weatherData = null;

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.connect();

            InputStream stream = conn.getInputStream();

            String data = convertStreamToString(stream);

            String[] dataArr = parseJSON(data);

            if (dataArr[0] != null && dataArr[1] != null && dataArr[2] != null) {
                weatherData = "Temperature: " + dataArr[0] +
                        "\nPressure" + dataArr[1] +
                        "\nHumidity" + dataArr[2];
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return weatherData;
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner scanner = new java.util.Scanner(is).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next(): "";
    }

    private class JSONParserTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String result = fetchWeatherData(params[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                tvOutput.setText(result);
            } else {
                tvOutput.setText("Can't fetch weather data!");
            }
        }
    }
}

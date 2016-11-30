package com.example.janitha.myapplication.async_tasks;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.example.janitha.myapplication.AppData;
import com.example.janitha.myapplication.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Siri on 11/30/2016.
 */

public class RetrieveLocWeatherTask extends AsyncTask<Void, Void, String> {
    //sample url: http://api.openweathermap.org/data/2.5/weather?lat=35&lon=139&appid=9fc5df99167e35be92d976d89c19f89f
    private String API_KEY = "9fc5df99167e35be92d976d89c19f89f";
    private String API_URL = "http://api.openweathermap.org/data/2.5/weather";
    private Location home_loc = AppData.HOME_LOCATION;
    private Location work_loc = AppData.WORK_LOCATION;
    private Double latitude = -0.0;
    private Double longitude= -0.0;

    private int locationType = -1; // 1 = Home Location,  2 = Work Location
    private String result;
    private String weatherType = "Empty weather type";
    private String weatherDescription = "Empty weather description";

    //case 1 = home location weather
    //case 2 = work location weather
    public RetrieveLocWeatherTask(int type){
        switch (type){
            case 1:
                latitude = home_loc.getLatitude();
                longitude = home_loc.getLongitude();
                locationType= 1;
                break;

            case 2:
                latitude = work_loc.getLatitude();
                longitude = work_loc.getLongitude();
                locationType = 2;
                break;
        }
    }

    private Exception exception;

    protected void onPreExecute() {
//        progressBar.setVisibility(View.VISIBLE);
//        responseView.setText("");
    }

    protected String doInBackground(Void... urls) {

        try {
            URL url = new URL(API_URL +"?"+ "lat=" + latitude +"&"+"lon="+longitude+"&units=metric"+"&appid=" + API_KEY);
            Log.i("URL",url.toString());

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                return stringBuilder.toString();
            }
            finally{
                urlConnection.disconnect();
            }
        }
        catch(Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            return null;
        }
    }

    protected void onPostExecute(String response) {
        if(response == null) {
            response = "THERE WAS AN ERROR";
        }

        Log.i("Web Response", response);

        // TODO: check this.exception
        // TODO: do something with the feed

        result = response;
        Log.i("Result-after-response", result);
        Double temperature = getTemperature();

//        MainActivity.getInstace().updateEditText_message(""+temperature);
//
//
//        String weatherType = getWeatherType();
//        String str = MainActivity.getInstace().editText.getText().toString();
//        MainActivity.getInstace().updateEditText_message(str+ " | " +weatherType);


    }



    private double getTemperature(){

        Double temperature = -0.0;

        try{
            if (result != null) {
                Log.i("Result-in-temp", result);
                JSONObject object = new JSONObject(result);
                JSONObject main = object.getJSONObject("main");
                temperature = main.getDouble("temp");
                Log.i("Current Temp-in-temp", result);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return temperature;
    }

    @SuppressLint("LongLogTag")
    private String getWeatherType(){
        String weatherType = "empty";

        try{
            if (result != null) {
                Log.i("Result-in-temp", result);
                JSONObject object = new JSONObject(result);
                JSONArray weather = object.getJSONArray("weather");
                weatherType = weather.getJSONObject(0).getString("main");
                Log.i("Current WeatherType-in-temp", weatherType);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return weatherType;
    }

    @SuppressLint("LongLogTag")
    private String getWeatherDescription(){
        String weatherDescription = "empty";

        try{
            if (result != null) {
                Log.i("Result-in-temp", result);
                JSONObject object = new JSONObject(result);
                JSONArray weather = object.getJSONArray("weather");
                weatherDescription = weather.getJSONObject(0).getString("description");
                Log.i("Current WeatherDes-in-temp", weatherDescription);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return weatherDescription;
    }
}

//Sample Api response to lat and lon
//{
//    "coord":{"lon":139,"lat":35},
//    "sys":{"country":"JP","sunrise":1369769524,"sunset":1369821049},
//    "weather":[{"id":804,"main":"clouds","description":"overcast clouds","icon":"04n"}],
//    "main":{"temp":289.5,"humidity":89,"pressure":1013,"temp_min":287.04,"temp_max":292.04},
//    "wind":{"speed":7.31,"deg":187.002},
//    "rain":{"3h":0},
//    "clouds":{"all":92},
//    "dt":1369824698,
//    "id":1851632,
//    "name":"Shuzenji",
//    "cod":200
//}

/*      Weather Types

    2xx : Thunderstorm
    3xx : Drizzle
    5xx : Rain
    6xx : Snow
    7xx : Atmosphere
    8xx : Clouds
    90x : Extreme (tornado)
    9xx : Additional (Windy)

 */
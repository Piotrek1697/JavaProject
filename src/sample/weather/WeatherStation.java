package sample.weather;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class WeatherStation {


    public static Weather getWeatherFromCity(String City, String units) throws IOException {

        String httpCity = City.replace(' ','+');

        StringBuffer response = new StringBuffer();
        String urlString = "http://api.openweathermap.org/data/2.5/weather?q="+ httpCity +"&units="+units+"&APPID=48ee905cfea07e1a3e313ac4091d723e";


            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();

            System.out.println("Response: " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine())!= null){
                response.append(inputLine);
            }

            in.close();


        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map map = gson.fromJson(response.toString(), Map.class);
        Weather weather = gson.fromJson(map.get("main").toString(),Weather.class);

        return weather;
    }

}

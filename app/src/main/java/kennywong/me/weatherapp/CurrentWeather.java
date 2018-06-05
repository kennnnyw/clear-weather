package kennywong.me.weatherapp;

import java.util.List;

/**
 * This class represents the current weather data for a specified location. The data is
 * retrieved via a GET request to the OpenWeatherMap Current Weather API. Only a selection of
 * the retrieved data is made use of in this app.
 *
 * Example API call: api.openweathermap.org/data/2.5/weather?q=London,uk
 *
 * Created by kennywong on 04/06/2018.
 */

public class CurrentWeather {
    private List<Weather> weather;
    private Main main;
    private String name;
    private long dt;

    class Main {
        public double temp;
        public int pressure;
        public int humidity;
        public int temp_min;
        public int temp_max;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public Main getMain() {
        return main;
    }

    public String getName() {
        return name;
    }

    public long getDt() {
        return dt;
    }
}


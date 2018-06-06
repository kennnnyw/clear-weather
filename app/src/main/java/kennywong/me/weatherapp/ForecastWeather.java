package kennywong.me.weatherapp;

import java.io.Serializable;
import java.util.List;

/**
 * Created by kennywong on 05/06/2018.
 */

public class ForecastWeather implements Serializable{
    private long dt;
    private Main main;
    private List<Weather> weather;

    class Main implements Serializable{
        public float temp;
        public float temp_min;
        public float temp_max;
        public int humidity;
    }

    public long getDt() {
        return dt;
    }

    public Main getMain() {
        return main;
    }

    public List<Weather> getWeather() {
        return weather;
    }
}
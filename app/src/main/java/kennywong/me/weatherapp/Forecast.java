package kennywong.me.weatherapp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kennywong on 05/06/2018.
 */

public class Forecast {
    private List<ForecastWeather> list;
    private City city;

    class City {
        public String name;
        public String country;
    }

    public List<ForecastWeather> getList() {
        return list;
    }

    public City getCity() {
        return city;
    }

    public List<ForecastWeather> getPartialList(int n){
        ArrayList<ForecastWeather> partialForecast = new ArrayList<ForecastWeather>(list.subList(0, n));
        //return list.subList(0, n);
        return partialForecast;
    }
}

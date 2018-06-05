package kennywong.me.weatherapp;

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


}

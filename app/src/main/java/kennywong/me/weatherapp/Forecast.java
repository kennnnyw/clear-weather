package kennywong.me.weatherapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kennywong on 05/06/2018.
 */

public class Forecast implements Serializable{
    private List<ForecastWeather> list;
//    private City city;

//    class City {
//        public String name;
//        public String country;
//    }

    public List<ForecastWeather> getList() {
        return list;
    }

//    public City getCity() {
//        return city;
//    }

    public List<ForecastWeather> getPartialList(int n){
        ArrayList<ForecastWeather> partialForecast = new ArrayList<ForecastWeather>(list.subList(0, n));
        //return list.subList(0, n);
        return partialForecast;
    }

    public int getDailyAverageTemp(int d){
        float aveTemp = 0;
        int count = 0;
        Calendar c = Calendar.getInstance();

        // set the calendar to "d" days in advance from the current date.
        c.add(Calendar.DATE, d);
        // get the day of the month
        int dayOfInterest = c.get(Calendar.DAY_OF_MONTH);

        // select the temperature information from the list about the dayOfInterest only.
        for (ForecastWeather f : list){
            c.setTime(new Date(f.getDt() * 1000));
            if (c.get(Calendar.DAY_OF_MONTH) == dayOfInterest){
                aveTemp += f.getMain().temp;
                count++;
            }
        }

//        getDailyConditions(d);
        aveTemp /= count;
        return Math.round(aveTemp);
    }

    public String getDailyConditions(int d){
        HashMap<String, Integer> occurrences = new HashMap<>();
        Calendar c = Calendar.getInstance();

        // set the calendar to "d" days in advance from the current date.
        c.add(Calendar.DATE, d);
        // get the day of the month
        int dayOfInterest = c.get(Calendar.DAY_OF_MONTH);

        for (ForecastWeather f : list){
            c.setTime(new Date(f.getDt() * 1000));
            if (c.get(Calendar.DAY_OF_MONTH) == dayOfInterest){
                String weather = f.getWeather().get(0).getMain();
                if (!occurrences.containsKey(weather)){
                    occurrences.put(weather, 1);
                } else {
                    occurrences.put(weather, occurrences.get(weather) + 1);
                }
            }
        }

        // determine which condition occurs the most
        String avg = "";
        int max = 0;
        for (String key : occurrences.keySet()){
            if (occurrences.get(key) > max){
                max = occurrences.get(key);
                avg = key;
            }
        }

        return avg.toLowerCase();
    }
}

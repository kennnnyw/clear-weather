package kennywong.me.weatherapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    public int getDailyAverage(int d){
        float aveTemp = 0;
        int count = 0;
        Calendar c = Calendar.getInstance();

        // set the calendar to "d" days in advance from the current date.
        c.add(Calendar.DATE, d);
        // get the day of the month
        int dayOfInterest = c.get(Calendar.DAY_OF_MONTH);

        // select the weather information from the list about the dayOfInterest only.
        for (ForecastWeather f : list){
            c.setTime(new Date(f.getDt() * 1000));
            if (c.get(Calendar.DAY_OF_MONTH) == dayOfInterest){
                // DEBUG printing
                System.out.println(c.get(Calendar.DAY_OF_MONTH) + " " + c.get(Calendar.MONTH)
                        + " " + c.get(Calendar.HOUR_OF_DAY) + ":00" + " " + f.getMain().temp + "º "
                        + f.getWeather().get(0).getMain() + " " + f.getWeather().get(0).getIcon());

                aveTemp += f.getMain().temp;
                count++;
            }
        }

        aveTemp /= count;

        System.out.println("DEBUG: Avg Temp = " + Math.round(aveTemp) + ".");
        return Math.round(aveTemp);
    }
}

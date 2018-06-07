package kennywong.me.weatherapp;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by kennywong on 05/06/2018.
 */

public class ForecastFragment extends Fragment {
    private List<ForecastWeather> forecastWeatherList;
    private Forecast forecast;

    public static ForecastFragment newInstance(List<ForecastWeather> forecastWeatherList){
        ForecastFragment forecastFragment = new ForecastFragment();
        Bundle args = new Bundle();
        // put list into args
        args.putSerializable("list", (Serializable) forecastWeatherList);
        forecastFragment.setArguments(args);
        return forecastFragment;
    }

    public static ForecastFragment newInstance(Forecast forecast){
        ForecastFragment forecastFragment = new ForecastFragment();
        Bundle args = new Bundle();
        // put list into args
        args.putSerializable("forecast", forecast);
        forecastFragment.setArguments(args);
        return forecastFragment;
    }

    public ForecastFragment(){
        forecastWeatherList = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            forecastWeatherList = (List<ForecastWeather>) getArguments().getSerializable("list");
            forecast = (Forecast) getArguments().getSerializable("forecast");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.forecasts, container, false);
        if (forecastWeatherList != null) {
            populateThreeHourly(view);
        }
        if (forecast != null) {
            populateFiveDays(view);
        }
        return view;
    }

    public void populateThreeHourly(View v) {
        Calendar c = Calendar.getInstance();

        // first entry
        c.setTime(new Date(forecastWeatherList.get(0).getDt() * 1000));
        TextView time = v.findViewById(R.id.entryOneTime);
        TextView temp = v.findViewById(R.id.entryOneTemperature);
        ImageView icon = v.findViewById(R.id.entryOneIcon);

        time.setText(getResources().getString(R.string.time, c.get(Calendar.HOUR_OF_DAY)));
        temp.setText(getResources().getString(R.string.current_temperature, (int) Math.round(forecastWeatherList.get(0).getMain().temp)));
        icon.setImageDrawable(selectIcon(forecastWeatherList.get(0).getWeather().get(0).getIcon()));

        // second entry
        c.setTime(new Date(forecastWeatherList.get(1).getDt() * 1000));
        time = v.findViewById(R.id.entryTwoTime);
        temp = v.findViewById(R.id.entryTwoTemperature);
        icon = v.findViewById(R.id.entryTwoIcon);

        time.setText(getResources().getString(R.string.time, c.get(Calendar.HOUR_OF_DAY)));
        temp.setText(getResources().getString(R.string.current_temperature, (int) Math.round(forecastWeatherList.get(1).getMain().temp)));
        icon.setImageDrawable(selectIcon(forecastWeatherList.get(1).getWeather().get(0).getIcon()));

        // third entry
        c.setTime(new Date(forecastWeatherList.get(2).getDt() * 1000));
        time = v.findViewById(R.id.entryThreeTime);
        temp = v.findViewById(R.id.entryThreeTemperature);
        icon = v.findViewById(R.id.entryThreeIcon);

        time.setText(getResources().getString(R.string.time, c.get(Calendar.HOUR_OF_DAY)));
        temp.setText(getResources().getString(R.string.current_temperature, (int) Math.round(forecastWeatherList.get(2).getMain().temp)));
        icon.setImageDrawable(selectIcon(forecastWeatherList.get(2).getWeather().get(0).getIcon()));

        // fourth entry
        c.setTime(new Date(forecastWeatherList.get(3).getDt() * 1000));
        time = v.findViewById(R.id.entryFourTime);
        temp = v.findViewById(R.id.entryFourTemperature);
        icon = v.findViewById(R.id.entryFourIcon);

        time.setText(getResources().getString(R.string.time, c.get(Calendar.HOUR_OF_DAY)));
        temp.setText(getResources().getString(R.string.current_temperature, (int) Math.round(forecastWeatherList.get(3).getMain().temp)));
        icon.setImageDrawable(selectIcon(forecastWeatherList.get(3).getWeather().get(0).getIcon()));

        // five entry
        c.setTime(new Date(forecastWeatherList.get(4).getDt() * 1000));
        time = v.findViewById(R.id.entryFiveTime);
        temp = v.findViewById(R.id.entryFiveTemperature);
        icon = v.findViewById(R.id.entryFiveIcon);

        time.setText(getResources().getString(R.string.time, c.get(Calendar.HOUR_OF_DAY)));
        temp.setText(getResources().getString(R.string.current_temperature, (int) Math.round(forecastWeatherList.get(4).getMain().temp)));
        icon.setImageDrawable(selectIcon(forecastWeatherList.get(4).getWeather().get(0).getIcon()));
    }

    public void populateFiveDays(View v){

    }

//    public void populateEntry(View v, String position){
//        TextView time, temp;
//        ImageView icon;
//    }


    public Drawable selectIcon(String icon){
        Resources r = getResources();
        switch (icon) {
            case "01d":
                return r.getDrawable(R.drawable.clear_sky);
            case "01n":
                return r.getDrawable(R.drawable.clear_sky_night);
            case "02d":
                return r.getDrawable(R.drawable.few_clouds);
            case "02n":
                return r.getDrawable(R.drawable.few_clouds_night);
            case "03d":
            case "03n":
                return r.getDrawable(R.drawable.scattered_clouds);
            case "04d":
            case "04n":
                return r.getDrawable(R.drawable.broken_clouds);
            case "09d":
            case "09n":
                return r.getDrawable(R.drawable.showers);
            case "10d":
            case "10n":
                return r.getDrawable(R.drawable.rain);
            case "11d":
            case "11n":
                return r.getDrawable(R.drawable.thunderstorm);
            case "13d":
            case "13n":
                return r.getDrawable(R.drawable.snow);
            case "50d":
            case "50n":
                return r.getDrawable(R.drawable.mist);
            default:
                return r.getDrawable(R.drawable.unavailable);
        }
    }
}

package kennywong.me.weatherapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by kennywong on 05/06/2018.
 */

public class ForecastAdapter extends FragmentStatePagerAdapter {
    private static final int NUM_PAGES = 2;
    private Forecast forecast;

    public ForecastAdapter(FragmentManager fm, Forecast forecast) {
        super(fm);
        this.forecast = forecast;
    }

    @Override
    public Fragment getItem(int position) {
        if (forecast == null) {
            return new ForecastFragment();
        } else {
            switch (position) {
                case 0:
                    return ForecastFragment.newInstance(forecast.getList());
                case 1:
                    return ForecastFragment.newInstance(forecast);
                default:
                    return null;
            }
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}


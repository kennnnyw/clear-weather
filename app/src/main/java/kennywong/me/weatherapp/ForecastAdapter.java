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
    private List<ForecastWeather> forecasted;

    public ForecastAdapter(FragmentManager fm, List<ForecastWeather> forecasted) {
        super(fm);
        this.forecasted = forecasted;
    }

    @Override
    public Fragment getItem(int position) {
        if (forecasted == null) {
            return new ForecastFragment();
        } else {
            switch (position) {
                case 0:
                    return ForecastFragment.newInstance(forecasted);
                case 1:
                    return new ForecastFragment();
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


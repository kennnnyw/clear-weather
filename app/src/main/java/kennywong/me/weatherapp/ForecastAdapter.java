package kennywong.me.weatherapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by kennywong on 05/06/2018.
 */

public class ForecastAdapter extends FragmentStatePagerAdapter {
    private static final int NUM_PAGES = 2;

    public ForecastAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new ForecastFragment();
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}


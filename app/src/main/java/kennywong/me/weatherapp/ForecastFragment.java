package kennywong.me.weatherapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by kennywong on 05/06/2018.
 */

public class ForecastFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
           ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.forecasts, container, false);

        return rootView;
    }

}

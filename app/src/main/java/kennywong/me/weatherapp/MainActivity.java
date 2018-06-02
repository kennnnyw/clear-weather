package kennywong.me.weatherapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.DayTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}

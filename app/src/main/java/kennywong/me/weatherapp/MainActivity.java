package kennywong.me.weatherapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        updateTheme();
        String today = getCurrentDate();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((TextView)findViewById(R.id.dateText)).setText(today);
    }

    /**
     * Change the colours of the app depending on the time of day.
     * 4:00 - 9:59 = Morning
     * 10:00 - 19:59 = Day
     * 20:00 - 3:59 = Night
     */
    public void updateTheme(){
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);

        if (hour < 10 && hour >= 4) {
            // morning
            setTheme(R.style.MorningTheme);
        } else if (hour >= 10  && hour < 20) {
            // day
            setTheme(R.style.DayTheme);
        } else if (hour >= 20 || hour < 4) {
            // night
            setTheme(R.style.NightTheme);
        }
    }

    /**
     * Retrieves and returns the current date.
     * @return Today's date in the SimpleDateFormat "EEEE, MMM d." (e.g. "Saturday, Jun 2.")
     */
    public String getCurrentDate(){
        Calendar now = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM d.");
        System.out.println("DEBUG " + dateFormat.format(now.getTime()));
        return dateFormat.format(now.getTime());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTheme();
        String today = getCurrentDate();
        setContentView(R.layout.activity_main);
        ((TextView)findViewById(R.id.dateText)).setText(today);
    }
}

package kennywong.me.weatherapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItem;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
//import android.support.v7.widget.SearchView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static kennywong.me.weatherapp.R.id.action_settings;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private FusedLocationProviderClient fusedLocationClient;
    protected Location lastLocation;

    private TextView locationText;

    private final String baseURL = "http://api.openweathermap.org/data/2.5/";
    private final String celsius = "&units=metric";

    private ViewPager forecastsView;
    private PagerAdapter pagerAdapter;
    private ConstraintLayout mainContainer;
    private SearchView searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        updateTheme();
        String today = getCurrentDate();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mainContainer = findViewById(R.id.mainContainer);
        mainContainer.requestFocus();

        ((TextView) findViewById(R.id.dateText)).setText(today);
        locationText = findViewById(R.id.locationText);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // handle search bar behaviour
        searchBar = findViewById(R.id.action_search);

        searchBar.setOnClickListener(new View.OnClickListener() {
            /**
             * This event fires when any part of the search view is clicked/touched.
             * It applies focus to the search view and brings up the keyboard.
             * @param view the component that has been clicked/touched (the search view)
             */
            @Override
            public void onClick(View view) {
                searchBar.onActionViewExpanded();
            }
        });

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            /**
             * This event fires whenever the query in the search view is submitted. The search term
             * is used as a parameter to fetch weather and forecast data. This method also removes
             * focus from the search view once the submit button has been pressed.
             * @param s the current string in the search view, represents a location that the user
             *          wishes to find weather data about.
             * @return true if the submit button has been pressed.
             */
            @Override
            public boolean onQueryTextSubmit(String s) {
                System.out.println("DEBUG: Search term... " + s);
                getCurrentWeatherTask weatherTask = new getCurrentWeatherTask();
                weatherTask.execute(s);

                getForecastTask forecastTask = new getForecastTask();
                forecastTask.execute(s);

                searchBar.clearFocus();
                searchBar.onActionViewCollapsed();
                searchBar.setIconified(true);
                searchBar.setActivated(false);
                mainContainer.requestFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    /**
     * Responsible for creating the options menu in the toolbar.
     * @param menu the menu that is being created
     * @return true if the menu has been created successfully.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions, menu);

        // set the colours of the icons in the toolbar
        for(int i = 0; i < menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if(drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.accent), PorterDuff.Mode.SRC_ATOP);
            }
        }
        return true;
    }

    /**
     * Responsible for handling the button actions for menu items.
     * @param item the menu item that has been pressed by the user.
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                // User chose the "refresh" item, refresh weather data for the current location
                refreshWeatherData();
                Toast toast = Toast.makeText(getApplicationContext(), "Refreshing...", Toast.LENGTH_SHORT);
                toast.show();
                return true;

            case R.id.action_location:
                // User chose the "location" action, update the current location
                if (!checkPermissions()) {
                    requestPermissions();
                } else {
                    getLastLocation();
                    toast = Toast.makeText(getApplicationContext(), "Updating Location...", Toast.LENGTH_SHORT);
                    toast.show();
                }
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            refreshWeatherData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTheme();
        View screen = findViewById(R.id.mainContainer);
        screen.invalidate();
        String today = getCurrentDate();
        refreshWeatherData();
        ((TextView) findViewById(R.id.dateText)).setText(today);
        mainContainer.requestFocus();
    }

    /**
     * This click listener is attached to various components in the activity. The main purpose
     * of this is to allow for the ability to collapse the search bar and hide the keyboard when
     * the user clicks "out" of these two components.
     * @param v
     */
    public void genericClickListener(View v){
        if (v.getId() != R.id.action_search){
            searchBar.clearFocus();
            searchBar.onActionViewCollapsed();
            searchBar.setIconified(true);
            searchBar.setActivated(false);
            mainContainer.requestFocus();
        }
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
     * Connects to the Open Weather API and performs a GET request for the current weather data
     * for the specified location.
     * @param location the location that the weather data should be about.
     * @return JSON string containing weather data for the specified location.
     */
    public String getCurrentWeather(String location){
        try {
            location = URLEncoder.encode(location, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String fullURL = baseURL + "weather?q=" + location + celsius + "&appid=" + getString(R.string.open_weather_api_key);
        System.out.println("Sending data to: " + fullURL);
        String line;
        String result = "";
        try {
            url = new URL(fullURL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //returns weather data as a json string
        System.out.println("DEBUG: " + result);
        return result;
    }

    /**
     * Performs the getCurrentWeather request in a separate thread.
     */
    private class getCurrentWeatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            return getCurrentWeather(strings[0]);
        }

        @Override
        protected void onPostExecute(String weatherJson) {
            // check if the http request returned any weather data
            // if data was returned, then refresh the UI with updated data
            if (weatherJson != "") {
                // Update the UI to show correct temperature values.
                Gson gson = new Gson();
                CurrentWeather w = gson.fromJson(weatherJson, CurrentWeather.class);
                int currentTemp = (int) Math.round(w.getMain().temp);
                int high = w.getMain().temp_max;
                int low = w.getMain().temp_min;

                TextView currentTempText = findViewById(R.id.currentTempText);
                TextView highLowText = findViewById(R.id.highLowText);

                currentTempText.setText(getResources().getString(R.string.current_temperature, currentTemp));
                highLowText.setText(getResources().getString(R.string.high_low_temperature, low, high));

                // Display a description of current weather conditions
                TextView weather = findViewById(R.id.weatherText);
                String[] words = w.getWeather().get(0).getDescription().split(" ");
                for (int i = 0; i < words.length; i++) {
                    words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1).toLowerCase();
                }
                weather.setText(TextUtils.join(" ", words));
            } else {
                // no data was returned, so leave the UI as it is.
                System.out.println("DEBUG: no weather data");
            }
        }
    }

    /**
     * Connects to the Open Weather API and performs a GET request for the 5 day / 3 hour
     * forecast for the specified location
     * @param location the location that the forecast data should be about.
     * @return JSON string containing forecast data for the specified location.
     */
    public String getForecast(String location){
        try {
            location = URLEncoder.encode(location, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String fullURL = baseURL + "forecast?q=" + location + celsius + "&appid=" + getString(R.string.open_weather_api_key);
        System.out.println("Sending data to: " + fullURL);
        String line;
        String result = "";
        try {
            url = new URL(fullURL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //returns forecast data as a json string
        System.out.println("DEBUG: " + result);
        return result;
    }

    /**
     * Performs the getForecast request in a separate thread.
     */
    private class getForecastTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            return getForecast(strings[0]);
        }

        @Override
        protected void onPostExecute(String forecastJson) {
            // check if the http request returned any forecast data
            // if data was returned, then refresh the UI with updated data
            if (forecastJson != "") {
                Gson gson = new Gson();
                Forecast f = gson.fromJson(forecastJson, Forecast.class);
                System.out.println(gson.toJson(f));

                SharedPreferences locations = getSharedPreferences("locations", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = locations.edit();
                editor.putString("lastLocation", f.getCity().name);
                editor.putString("lastCountry", f.getCity().country);
                editor.apply();

                TextView location = findViewById(R.id.locationText);
                location.setText(f.getCity().name);
                setupForecasts(f);
            } else {
                // no data was returned, so leave the UI as it is.
                System.out.println("DEBUG: no forecast data");
            }
        }
    }

    /**
     * Sets up the view pager and populates it with forecast information about the currently
     * specified location
     * @param forecast contains forecast information about the current location.
     */
    public void setupForecasts(Forecast forecast){
        forecastsView = findViewById(R.id.forecastsView);
        pagerAdapter = new ForecastAdapter(getSupportFragmentManager(), forecast);
        forecastsView.setAdapter(pagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(forecastsView, true);
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

    /**
     * Retrieves the last known location of the user's device, and then displays the location
     * in the appropriate TextView.
     */
    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Geocoder geocoder = new Geocoder(getApplicationContext());
                            List<Address> addresses = null;
                            lastLocation = task.getResult();
                            try {
                                addresses = geocoder.getFromLocation(lastLocation.getLatitude(), lastLocation.getLongitude(), 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (addresses != null || addresses.size() > 0) {
                                for (int i = 0; i < addresses.size(); i++) {
                                    String locality = addresses.get(i).getLocality();
                                    String country = addresses.get(i).getCountryCode();

                                    System.out.println(locality);
                                    locationText.setText(locality);
                                    // Save the location into a SharedPreferences file.
                                    // This location will be displayed in the UI if location is services
                                    // are disabled on next launch.
                                    SharedPreferences locations = getSharedPreferences("locations", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = locations.edit();
                                    editor.putString("lastLocation", locality);
                                    editor.putString("lastCountry", country);
                                    editor.apply();

                                    getCurrentWeatherTask weatherTask = new getCurrentWeatherTask();
                                    weatherTask.execute(locality+","+country);

                                    getForecastTask forecastTask = new getForecastTask();
                                    forecastTask.execute(locality+","+country);
                                }
                            }
                        } else {
                            Log.w(TAG, "getLastLocation:exception", task.getException());
                            showSnackbar((String) getText(R.string.location_disabled));
                            refreshWeatherData();
                        }
                    }
                });
    }

    public void refreshWeatherData(){
        SharedPreferences locations = getSharedPreferences("locations", Context.MODE_PRIVATE);
        String lastLocation = locations.getString("lastLocation", "London");
        String lastCountry = locations.getString("lastCountry", "GB");
        locationText.setText(lastLocation);

        getCurrentWeatherTask weatherTask = new getCurrentWeatherTask();
        weatherTask.execute(lastLocation+","+lastCountry);

        getForecastTask forecastTask = new getForecastTask();
        forecastTask.execute(lastLocation+","+lastCountry);
    }

    /**
     * Shows a {@link Snackbar} using {@code text}.
     *
     * @param text The Snackbar text.
     */
    private void showSnackbar(final String text) {
        View container = findViewById(R.id.mainContainer);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Check whether or not the necessary permissions have been granted by the user.
     * @return
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Requests the permissions that are necessary for the location features of the app.
     */
    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    /**
     * Request any permissions that are necessary certain features of the activity to function.
     */
    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            startLocationPermissionRequest();
                        }
                    });

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest();
        }
    }

    /**
     * Handles behaviour of the app after the permission requests have been completed.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation();
            } else {
                // Permission denied.
                // Since we cannot obtain the current location, the app will use the last location
                // saved in the SharedPreferences file instead.
                SharedPreferences locations = getSharedPreferences("locations", Context.MODE_PRIVATE);
                String lastLocation = locations.getString("lastLocation", "London");
                locationText.setText(lastLocation);

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }
}

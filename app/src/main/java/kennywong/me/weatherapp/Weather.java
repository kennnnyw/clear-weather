package kennywong.me.weatherapp;

import java.io.Serializable;

/**
 * Created by kennywong on 05/06/2018.
 */

public class Weather implements Serializable{
    private int id;
    private String main;
    private String description;
    private String icon;

    public int getId() {
        return id;
    }

    public String getMain() {
        return main;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }
}

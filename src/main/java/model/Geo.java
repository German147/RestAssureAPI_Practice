package model;

public class Geo {
    private String lat;
    private String lng;

    // Constructor, Getters, Setters
    public Geo(String lat, String lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public String getLat() { return lat; }
    public String getLng() { return lng; }
}

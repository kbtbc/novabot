package com.github.novskey.novabot.core;

import com.github.novskey.novabot.Util.UtilityFunctions;
import com.github.novskey.novabot.data.SpawnLocation;
import com.github.novskey.novabot.maps.GeocodedLocation;
import com.github.novskey.novabot.maps.GeofenceIdentifier;
import net.dv8tion.jda.core.entities.Message;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class Spawn {
    protected static final DateTimeFormatter printFormat24hr = DateTimeFormatter.ofPattern("HH:mm:ss");
    protected static final DateTimeFormatter printFormat12hr = DateTimeFormatter.ofPattern("hh:mm:ss a");
    public static NovaBot novaBot = null;
    private static int lastKey;

    private static int requests = 0;

    static {
        lastKey = 0;
    }

    public final HashMap<String, String> properties = new HashMap<>();
    protected final HashMap<String, Message> builtMessages = new HashMap<>();
    public Integer move_1;
    public Integer move_2;
    protected double lat;
    protected double lon;
    protected String formatKey = "pokemon";
    protected ArrayList<GeofenceIdentifier> geofenceIdentifiers = new ArrayList<>();
    private String imageUrl;
    public ZoneId timeZone;
    public SpawnLocation spawnLocation;
    public GeocodedLocation geocodedLocation = null;

    private synchronized static void incRequests(){
        requests++;
    }

    protected static String getWeather(int weather) {
        switch(weather){
            case 0:
                return novaBot.getLocalString("None");
            case 1:
                return novaBot.getLocalString("Clear");
            case 2:
                return novaBot.getLocalString("Rain");
            case 3:
                return novaBot.getLocalString("PartlyCloudy");
            case 4:
                return novaBot.getLocalString("Cloudy");
            case 5:
                return novaBot.getLocalString("Windy");
            case 6:
                return novaBot.getLocalString("Snow");
            case 7:
                return novaBot.getLocalString("Fog");
            default:
                return "unkn";
        }
    }

    public static int getRequests() {
        return requests;
    }

    public ArrayList<GeofenceIdentifier> getGeofences() {
        return geofenceIdentifiers;
    }

    public String getImage(String formatFile) {
        if (this.imageUrl == null) {
            if(novaBot.config.getStaticMapKeys().size() == 0){
                return "https://raw.githubusercontent.com/novskey/novabot/dev/static/no-api-keys-remaining.png";
            }else {
                incRequests();
                return this.imageUrl = "https://maps.googleapis.com/maps/api/staticmap?" + String.format("zoom=%s&size=%sx%s&markers=color:red|%s,%s&key=%s", novaBot.config.getMapZoom(formatFile, formatKey), novaBot.config.getMapWidth(formatFile, formatKey), novaBot.config.getMapHeight(formatFile, formatKey), this.lat, this.lon, getNextKey());
            }
        }
        return this.imageUrl;
    }

    public String getSuburb() {
        return properties.get("city");
    }

    public static void main(String[] args) {
        System.out.println(printFormat24hr.format(UtilityFunctions.getCurrentTime(UtilityFunctions.UTC)));
    }

    protected String getAppleMapsLink() {
        return String.format("http://maps.apple.com/maps?daddr=%s,%s&z=10&t=s&dirflg=w", this.lat, this.lon);
    }

    protected String getGmapsLink() {
        return String.format("https://www.google.com/maps?q=%s,%s", this.lat, this.lon);
    }

    public static void setNovaBot(NovaBot novaBot) {
        Spawn.novaBot = novaBot;
    }

    private synchronized static String getNextKey() {
        if (lastKey >= novaBot.config.getStaticMapKeys().size() - 1) {
            lastKey = 0;
            return novaBot.config.getStaticMapKeys().get(lastKey);
        }
        ++lastKey;
        return novaBot.config.getStaticMapKeys().get(lastKey);
    }

    public SpawnLocation getLocation() {
        return spawnLocation;
    }
}
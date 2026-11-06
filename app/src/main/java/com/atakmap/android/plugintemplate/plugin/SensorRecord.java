package com.atakmap.android.plugintemplate.plugin;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.UUID;

/** A portable sensor annotation. Coordinates are WGS84 decimal degrees. */
public final class SensorRecord {
    public final String id;
    public String type, label, color;
    public double latitude, longitude, bearing;

    public SensorRecord(String type, String label, String color, double latitude, double longitude, double bearing) {
        this(UUID.randomUUID().toString(), type, label, color, latitude, longitude, bearing);
    }
    private SensorRecord(String id, String type, String label, String color, double latitude, double longitude, double bearing) {
        if (!Double.isFinite(latitude) || latitude < -90 || latitude > 90 || !Double.isFinite(longitude) || longitude < -180 || longitude > 180)
            throw new IllegalArgumentException("Invalid WGS84 coordinate");
        this.id=id; this.type=type; this.label=label; this.color=color;
        this.latitude=latitude; this.longitude=longitude;
        this.bearing=((bearing%360)+360)%360;
    }
    public JSONObject toJson() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("id",id); obj.put("type",type); obj.put("label",label); obj.put("color",color);
        obj.put("lat",latitude); obj.put("lon",longitude); obj.put("bearing",bearing);
        return obj;
    }
    public static SensorRecord fromJson(JSONObject obj) throws JSONException {
        return new SensorRecord(obj.getString("id"),obj.getString("type"),obj.optString("label"),obj.getString("color"),
                obj.getDouble("lat"),obj.getDouble("lon"),obj.optDouble("bearing",0));
    }
    public int bearingMils() { return (int)Math.round(bearing * 6400 / 360) % 6400; }
}

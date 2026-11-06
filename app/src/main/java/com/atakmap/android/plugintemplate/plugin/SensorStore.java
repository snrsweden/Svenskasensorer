package com.atakmap.android.plugintemplate.plugin;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;

/** Offline persistence for user-created sensor annotations. */
public final class SensorStore {
    private static final String PREFS="svenska_sensorer_records";
    private static final String KEY="records_v1";
    private final Context context;
    public SensorStore(Context context) { this.context=context.getApplicationContext(); }
    public synchronized List<SensorRecord> load() throws JSONException {
        JSONArray array = new JSONArray(context.getSharedPreferences(PREFS,Context.MODE_PRIVATE).getString(KEY,"[]"));
        List<SensorRecord> result=new ArrayList<>();
        for(int i=0;i<array.length();i++) result.add(SensorRecord.fromJson(array.getJSONObject(i)));
        return result;
    }
    public synchronized boolean save(List<SensorRecord> records) throws JSONException {
        JSONArray array=new JSONArray();
        for(SensorRecord record:records) array.put(record.toJson());
        return context.getSharedPreferences(PREFS,Context.MODE_PRIVATE).edit().putString(KEY,array.toString()).commit();
    }
    public synchronized boolean upsert(SensorRecord record) throws JSONException {
        List<SensorRecord> records=load();
        for(int i=0;i<records.size();i++) if(records.get(i).id.equals(record.id)) { records.set(i,record); return save(records); }
        records.add(record); return save(records);
    }
    public synchronized boolean delete(String id) throws JSONException {
        List<SensorRecord> records=load();
        boolean removed=false;
        for(int i=records.size()-1;i>=0;i--) if(records.get(i).id.equals(id)) { records.remove(i); removed=true; }
        return removed && save(records);
    }
}

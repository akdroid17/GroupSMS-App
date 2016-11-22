package github.amarjeet.com.smsapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Map;

/**
 * Created by Dharmraj on 7/26/2016.
 */
public class SessionManager {
    private Context context;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context){
        this.context=context;
        pref = this.context.getSharedPreferences(Statics.PreferenceName,Context.MODE_PRIVATE);
        editor=pref.edit();
    }

    public boolean contains(String Key) {
        try {
            return pref.contains(Key);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public boolean isLogged() {
        try {
            if (this.contains(StorageVars._LOGGED))
                return pref.getBoolean(StorageVars._LOGGED, false);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public void insertStringPref(String key, String value) {
        try {
            editor.putString(key, value);
            editor.commit();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void insertFloatPref(String key, float value) {
        try {
            editor.putFloat(key, value);
            editor.commit();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void RemoveStringPref(String key) {
        try {
            editor.remove(key);
            editor.commit();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getStringPref(String key) {
        try {
            if (this.contains(key))
                return pref.getString(key, "");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    public float getFloatPref(String key) {
        try {
            if (this.contains(key))
                return pref.getFloat(key, 0);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

    public int getIntPref(String key) {
        try {
            if (this.contains(key))
                return pref.getInt(key, 0);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

    public int getIntPref(String key,int def) {
        try {
            if (this.contains(key))
                return pref.getInt(key, def);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return def;
    }

    public long getLongPref(String key) {
        try {
            if (this.contains(key))
                return pref.getLong(key, 0);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

    public void insertIntPref(String key, int value) {
        try {
            editor.putInt(key, value);
            editor.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void insertLongPref(String key, long value) {
        try {
            editor.putLong(key, value);
            editor.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public boolean getBooleanPref(String key, boolean def) {
        try {
            if (this.contains(key))
                return pref.getBoolean(key, def);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return def;
    }

    public void insertBooleanPref(String key, boolean val) {
        try {
            editor.putBoolean(key, val);
            editor.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void all() {
        Map<String, ?> keys = pref.getAll();

        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            Log.d("map values", entry.getKey() + ": "
                    + entry.getValue().toString());
        }
    }

    /*public  String getAndroidId() {
        String key = StorageVars._DEVICEID;
        if (SessionManager.contains(key))
            return pref.getString(key, "null");
        return "null";
    }*/

    public  int getNotificationId() {
        try {
            String key = StorageVars._NOTIFICATIONID;
            int id;
            if (this.contains(key)) {
                id = pref.getInt(key, 0);
                id++;
                if (id == 100)
                    id = 0;
                editor.putInt(key, id);
                editor.commit();
                return id;
            }
            editor.putInt(key, 1);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;

    }

    public void clearSF() {
        try{
            editor.clear().commit();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

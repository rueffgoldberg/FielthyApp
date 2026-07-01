package example.com.fielthyapps.Database;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor editor;
    private Context context;

    private static final String PREF_NAME = "fielthyapps_session";
    private static final String IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_UID = "uid";

    public SessionManager(Context context) {
        this.context = context;
        sharedPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPrefs.edit();
    }

    public void createLoginSession(String uid) {
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString(KEY_UID, uid);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return sharedPrefs.getBoolean(IS_LOGGED_IN, false);
    }

    public String getCurrentUserUid() {
        return sharedPrefs.getString(KEY_UID, null);
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
    }
}

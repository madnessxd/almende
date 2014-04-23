package alm.motiv.AlmendeMotivator;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

/**
 * Created by AsterLaptop on 4/23/14.
 */
public class FirstUseActivity extends Activity {
    private static String PREFS_NAME = "sportopiaprefs";
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termsofuse);
        settings = getSharedPreferences(PREFS_NAME, 0);
        editor = settings.edit();
    }
}

package alm.motiv.AlmendeMotivator;

import alm.motiv.AlmendeMotivator.models.Baseline;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by AsterLaptop on 4/23/14.
 */
public class FirstUseActivity extends Activity {
    private static String PREFS_NAME = "sportopiaprefs";
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private Baseline baseline = new Baseline();

    //views
    private EditText hours;
    private Spinner period;
    private EditText motivation1;
    private EditText motivation2;
    private EditText motivation3;
    private EditText motivation4;
    private Spinner partOfTheDay;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termsofuse);
        settings = getSharedPreferences(PREFS_NAME, 0);
        editor = settings.edit();

        //views

        hours = (EditText)findViewById(R.id.hoursInput);
        period = (Spinner)findViewById(R.id.spPeriod);
        motivation1 = (EditText)findViewById(R.id.motivationReason1);
        motivation2 = (EditText)findViewById(R.id.motivationReason2);
        motivation3 = (EditText)findViewById(R.id.motivationReason3);
        motivation4 = (EditText)findViewById(R.id.motivationReason4);
        partOfTheDay = (Spinner)findViewById(R.id.spPartOfTheDay);
    }

    public void onSubmitFirstUsePressed(View v){
        validation();
    }

    //for validation
    private boolean validation(){
        boolean succes = true;
        if(!Validation.hasText(hours))succes=false;
        return succes;
    }

}

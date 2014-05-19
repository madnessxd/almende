package alm.motiv.AlmendeMotivator;

/**
 * Created by Gebruiker on 19-5-14.
 */

import android.app.Activity;
import android.os.Bundle;

public class CheckThread extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        new Thread(new Runnable() {
            public void run(){

                System.out.println("test");

            }
        }).start();

    }

}
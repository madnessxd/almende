package alm.motiv.AlmendeMotivator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Motivation extends Activity{
    Intent home;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.motivation);
    }
    @Override
    public void onBackPressed() {
        home = new Intent(Motivation.this, Main.class);
        startActivity(home);
        return;
    }
}

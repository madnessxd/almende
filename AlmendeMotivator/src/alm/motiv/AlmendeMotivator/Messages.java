package alm.motiv.AlmendeMotivator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Messages extends Activity{
    Intent home;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages);
    }
    @Override
    public void onBackPressed() {
        finish();
        home = new Intent(Messages.this, Main.class);
        startActivity(home);
        return;
    }
}

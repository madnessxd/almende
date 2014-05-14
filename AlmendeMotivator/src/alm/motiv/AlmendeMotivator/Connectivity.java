package alm.motiv.AlmendeMotivator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.zip.Inflater;

/**
 * Created by AsterLaptop on 5/11/14.
 */
public class Connectivity {
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public static void showError(final Activity activity){
      // LayoutInflater inflater = activity.getLayoutInflater();


        /*View view = View.inflate(activity,R.layout.popup_connectivity,null);

        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(activity);
        final AlertDialog helpDialog = helpBuilder.create();
        helpDialog.setView(view, 0,0,0,0);
        helpDialog.show();

        Button btnContinue = (Button)view.findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helpDialog.dismiss();
                //android.os.Process.killProcess(android.os.Process.myPid());
            }
        });*/

        Toast.makeText(activity.getApplicationContext(),"You don't have internet", Toast.LENGTH_LONG).show();


    }
}

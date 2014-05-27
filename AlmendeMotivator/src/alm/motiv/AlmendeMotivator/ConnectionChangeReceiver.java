package alm.motiv.AlmendeMotivator;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by AsterLaptop on 5/14/14.
 */
public class ConnectionChangeReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive( Context context, Intent intent )
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(     ConnectivityManager.TYPE_MOBILE );

        if(!(activeNetInfo != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting())){
            Cookie.getInstance().internet=false;
            Toast.makeText( context, "You don't have internet. Please connect and reload." , Toast.LENGTH_LONG ).show();
//            showError(context);
        }else{
            Cookie.getInstance().internet=true;
        }
    }

    public static void showError(final Context context){
        View view = View.inflate(context,R.layout.popup_connectivity,null);

        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(context);
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
        });
    }
}
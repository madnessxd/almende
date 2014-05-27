package alm.motiv.AlmendeMotivator;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

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
        Toast.makeText(activity.getApplicationContext(),"You don't have internet", Toast.LENGTH_LONG).show();
    }
}

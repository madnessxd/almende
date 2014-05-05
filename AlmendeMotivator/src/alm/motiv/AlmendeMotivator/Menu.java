package alm.motiv.AlmendeMotivator;

import alm.motiv.AlmendeMotivator.facebook.FacebookMainActivity;
import alm.motiv.AlmendeMotivator.facebook.FacebookManager;
import android.app.Activity;
import android.content.Intent;

/**
 * Created by AsterLaptop on 5/5/14.
 */
public class Menu {

    public static void selectItem(int pos, Activity activity){
        Intent goTo = null;
        switch (pos) {
            case 0:
                goTo = new Intent(activity, ProfileActivity.class);
                break;
            case 1:
                goTo = new Intent(activity, MessageActivity.class);
                break;
            case 2:
                goTo = new Intent(activity, ChallengeOverviewActivity.class);
                break;
            case 3:
                goTo = new Intent(activity, FriendActivity.class);
                break;
            case 4:
                goTo = new Intent(activity, AboutActivity.class);
                break;
            case 5:
                FacebookManager.logout();
                goTo = new Intent(activity, FacebookMainActivity.class);
                break;
        }
        activity.finish();
        activity.startActivity(goTo);
    }
}

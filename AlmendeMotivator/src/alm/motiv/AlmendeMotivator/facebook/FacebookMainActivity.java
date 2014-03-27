package alm.motiv.AlmendeMotivator.facebook;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 10/14/13
 * Time: 9:30 AM
 * To change this template use File | SettingsHelper | File Templates.
 */
public class FacebookMainActivity extends FragmentActivity {
    private FacebookMainFragment mainFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            mainFragment = new FacebookMainFragment();
            getSupportFragmentManager()
            .beginTransaction()
            .add(android.R.id.content, mainFragment)
            .commit();
        } else {
            // Or set the fragment from restored state info
            mainFragment = (FacebookMainFragment) getSupportFragmentManager()
            .findFragmentById(android.R.id.content);
        }
    }
}

package alm.motiv.AlmendeMotivator;

import alm.motiv.AlmendeMotivator.adapters.MessagesAdapter;
import alm.motiv.AlmendeMotivator.facebook.FacebookMainActivity;
import alm.motiv.AlmendeMotivator.facebook.FacebookManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

public class MessageActivity extends Activity{
    Intent home;
    Intent k;
    private String[] mMenuOptions;
    private ListView mDrawerList;
    //private ListView messageList;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messagesmenu);

        mMenuOptions = getResources().getStringArray(R.array.profile_array);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_menu, mMenuOptions));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        //messageList = (ListView) findViewById(R.id.messagesGrid);
        //messageList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_message, mMenuOptions));
        //messageList.setOnItemClickListener(new DrawerItemClickListener());

        //GridView messageListView = (GridView) findViewById(R.id.messagesGrid);
        //messageListView.setAdapter(new MessagesAdapter(this));

    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    public void createMessage(View v){
        System.out.println("Er was eens een sout");
        k = new Intent(MessageActivity.this, NewMessageActivity.class);
        finish();
        startActivity(k);
    }
    public void selectItem(int pos){
        switch (pos){
            case 0:
                k = new Intent(MessageActivity.this, ProfileActivity.class);
                break;
            case 1:
                k = new Intent(MessageActivity.this, MessageActivity.class);
                break;
            case 2:
                k = new Intent(MessageActivity.this, ChallengesMenuActivity.class);
                break;
            case 3:
                k = new Intent(MessageActivity.this, FriendActivity.class);
                break;
            case 4:
                FacebookManager.logout();
                k = new Intent(MessageActivity.this, FacebookMainActivity.class);
                break;
        }
        finish();
        startActivity(k);
    }
    @Override
    public void onBackPressed() {
        finish();
        home = new Intent(MessageActivity.this, MainMenuActivity.class);
        startActivity(home);
        return;
    }

}

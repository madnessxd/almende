package alm.motiv.AlmendeMotivator.adapters;

/**
 * Created by AsterLaptop on 4/13/14.
 */

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import alm.motiv.AlmendeMotivator.Cookie;
import alm.motiv.AlmendeMotivator.Database;
import alm.motiv.AlmendeMotivator.R;
import alm.motiv.AlmendeMotivator.models.ChallengeHeader;
import alm.motiv.AlmendeMotivator.models.Challenge;
import alm.motiv.AlmendeMotivator.models.User;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class EntryAdapter extends ArrayAdapter<Item> {

    private Context context;
    private ArrayList<Item> items;
    private LayoutInflater vi;

    public EntryAdapter(Context context, ArrayList<Item> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
        vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        boolean sentChallenge = false;
        View v = convertView;
        final Item i = items.get(position);
        if (i != null) {
            if (i.isSection()) {
                ChallengeHeader si = (ChallengeHeader) i;
                v = vi.inflate(R.layout.list_item_section, null);

                v.setOnClickListener(null);
                v.setOnLongClickListener(null);
                v.setLongClickable(false);

                final TextView sectionView = (TextView) v.findViewById(R.id.list_item_section_text);
                sectionView.setText(si.getTitle());

            } else {
                Challenge ei = (Challenge) i;
                if (Cookie.getInstance().userEntryId.equals(ei.getChallenger())) {
                    sentChallenge = true;
                }
                v = vi.inflate(R.layout.list_item_entry, null);
                final TextView title = (TextView) v.findViewById(R.id.list_item_entry_title);
                final TextView challengee = (TextView) v.findViewById(R.id.list_item_entry_summary);
                final TextView status = (TextView) v.findViewById(R.id.list_item_entry_status);
                if (title != null)
                    title.setText(ei.getTitle());

                if (challengee != null)
                    if (sentChallenge) {
                        challengee.setText(ei.getChallengeeName());
                    } else {
                        challengee.setText(ei.getChallengerName());
                    }
                if (status != null)
                    status.setText("Status: " + ei.getStatus());
            }
        }
        return v;
    }
}




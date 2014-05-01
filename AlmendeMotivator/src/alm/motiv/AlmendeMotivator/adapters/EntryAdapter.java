package alm.motiv.AlmendeMotivator.adapters;

/**
 * Created by AsterLaptop on 4/13/14.
 */
import java.util.ArrayList;

import alm.motiv.AlmendeMotivator.R;
import alm.motiv.AlmendeMotivator.adapters.Item;
import alm.motiv.AlmendeMotivator.models.ChallengeHeader;
import alm.motiv.AlmendeMotivator.models.Challenge;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class EntryAdapter extends ArrayAdapter<Item> {

    private Context context;
    private ArrayList<Item> items;
    private LayoutInflater vi;

    public EntryAdapter(Context context,ArrayList<Item> items) {
        super(context,0, items);
        this.context = context;
        this.items = items;
        vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        final Item i = items.get(position);
        if (i != null) {
            if(i.isSection()){
                ChallengeHeader si = (ChallengeHeader)i;
                v = vi.inflate(R.layout.list_item_section, null);

                v.setOnClickListener(null);
                v.setOnLongClickListener(null);
                v.setLongClickable(false);

                final TextView sectionView = (TextView) v.findViewById(R.id.list_item_section_text);
                sectionView.setText(si.getTitle());

            }else{
                Challenge ei = (Challenge)i;
                v = vi.inflate(R.layout.list_item_entry, null);
                final TextView title = (TextView)v.findViewById(R.id.list_item_entry_title);
                final TextView challengee = (TextView)v.findViewById(R.id.list_item_entry_summary);
                final TextView status = (TextView)v.findViewById(R.id.list_item_entry_status);


                if (title != null)
                    title.setText(ei.getTitle());
                if(challengee != null)
                    challengee.setText(ei.getChallengee());
                if(status != null)
                    status.setText(ei.getStatus());
            }
        }
        return v;
    }

}

package alm.motiv.AlmendeMotivator.adapters;

/**
 * Created by AsterLaptop on 4/13/14.
 */

import java.util.ArrayList;

import alm.motiv.AlmendeMotivator.R;
import alm.motiv.AlmendeMotivator.models.Message;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.mongodb.BasicDBObject;

public class MessageAdapter extends ArrayAdapter<BasicDBObject> {

    private Context context;
    private ArrayList<BasicDBObject> messages;
    private LayoutInflater vi;

    public MessageAdapter(Context context, ArrayList<BasicDBObject> messages) {
        super(context, 0, messages);
        this.context = context;
        this.messages = messages;
        vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        final BasicDBObject message = messages.get(position);
        if (message != null) {
            v = vi.inflate(R.layout.list_item_challengemessage, null);
            final TextView author = (TextView) v.findViewById(R.id.messegeAuthor);
            final TextView date = (TextView) v.findViewById(R.id.messageDate);
            final TextView content = (TextView) v.findViewById(R.id.messageContent);


            if (author != null)
                author.setText(message.get("Author").toString());
            if (date != null)
                date.setText(message.get("Date").toString());
            if (content != null)
                content.setText(message.get("Content").toString());
        }
        return v;
    }

}


package alm.motiv.AlmendeMotivator.adapters;

/**
 * Created by AsterLaptop on 4/13/14.
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
            if (date != null){
                String string = message.get("Date").toString();
                //we parse the date from the message object
                Date theDate =null;
                try {
                   theDate = new SimpleDateFormat("EEE MMM d hh:mm:ss z yyyy").parse(string);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //we use this parsed date that now is a date object
                if(theDate!=null){
                    String dateString = new SimpleDateFormat("MMM d yyyy").format(theDate);
                    date.setText("on " +dateString);
                }else{
                    date.setText("");
                }

            }

            if (content != null)
                content.setText(message.get("Content").toString());
        }
        return v;
    }

}


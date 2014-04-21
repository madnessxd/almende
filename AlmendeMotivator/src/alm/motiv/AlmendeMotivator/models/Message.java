package alm.motiv.AlmendeMotivator.models;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by AsterLaptop on 4/1/14.
 */
public class Message extends BasicDBObject {

    //private ArrayList<String> receivedMessages = new ArrayList<String>();

    public Message(){

    }

    public Message(String receiver, String author, String title, ArrayList<String> receivedMessages, String date, String category, String liked){
        put("Receiver", receiver);
        put("Author", author);
        put("Title", title);
        put("Content", receivedMessages);
        put("Date", date);
        put("Category", category);
        put("Liked", liked);
    }

    public void setLiked(String liked){
        put("Liked", liked);
    }

    public String getLiked(){
        return this.get("Liked").toString();
    }

    public void setDate(Date date){
        put("Date", date);
    }

    public Date getDate(){
        return (Date) this.get("Date");
    }

    public void setTitle(String title){
        put("Title", title);
    }

    public String getTitle(){
        return this.get("Title").toString();
    }

    public void setContent(String content){
        this.put("Content", content);
    }

    public String getContent(){
        return this.get("content").toString();
    }
    public void setAuthor(String author){
        put("Author", author);
    }

    public String getAuthor(){
        return this.get("Author").toString();
    }

    public void setReceiver(String receiver){
        put("Receiver", receiver);
    }

    public String getReceiver(){
        return this.get("Receiver").toString();
    }

    public void setCategory(String category){
        put("Category", category);
    }

    public String getCatgeory(){
        return this.get("Category").toString();
    }
}

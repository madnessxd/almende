package alm.motiv.AlmendeMotivator.models;

import alm.motiv.AlmendeMotivator.adapters.Item;
import android.os.Parcelable;
import com.mongodb.BasicDBObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by AsterLaptop on 3/31/14.
 */
public class Challenge extends BasicDBObject implements Item {
    private static final long serialVersionUID = 1L;

    public Challenge() {}

    //TODO add likeAmount to challenge. Different way then it is now. Solution below causes DBThreadExceptions
    public Challenge(String title, String challenger, String challengee, String content, int evidence_amount, String evidence_type, String reward, String status, String gps, String amountHours, int XPreward) {
        put("title", title);
        put("challenger", challenger);
        put("challengee", challengee);
        put("content", content);
        put("evidence_amount", evidence_amount);
        put("evidence_type", evidence_type);
        put("reward", reward);
        put("status", status);
        put("gps", gps);
        put("amountHours", amountHours);
        put("XPreward", XPreward);
        long time= System.currentTimeMillis();
        this.put("Date", time);
    }

    public void updateLoginDate(){
        long time= System.currentTimeMillis();
        this.put("Date", time);
    }

    public void setStatus(String status) {
        put("status", status);
    }

    public Object getID() {
        return this.get("_id");
    }

    public String getTitle() {
        return this.getString("title");
    }

    public String getChallenger() {
        return this.getString("challenger");
    }

    public String getChallengee() {
        return this.getString("challengee");
    }

    public String getContent() {
        return this.getString("content");
    }

    public int getEvidenceAmount() {
        return this.getInt("evidence_amount");
    }

    public String getEvidenceType() {
        return this.getString("evidence_type");
    }

    public String getReward() {
        return this.getString("reward");
    }

    public String getStatus() {
        return this.getString("status");
    }

    public ArrayList<BasicDBObject> getEvidence() {
        return (ArrayList<BasicDBObject>) this.get("evidence");
    }

    public void setComments(Message message){
        this.put("$push", new BasicDBObject("comments", message));
    }

    public ArrayList<BasicDBObject> getComments(){
        return (ArrayList<BasicDBObject>) this.get("comments");
    }

    public void setRated(String rated){
        this.put("rated",rated);
    }

    public String getRated(){
        return this.get("rated").toString();
    }

    public void setRatedMessage(String ratedMessage){
        this.put("ratedMessage", ratedMessage);
    }

    public String getRatedMessage(){
        return this.get("ratedMessage").toString();
    }

    public void setStartDate(Date date){
        this.put("startDate", date);
    }

    public int getXPreward(){
        return this.getInt("XPreward");
    }

    @Override
    public boolean isSection() {
        return false;
    }

    public String getChallengeeName() {
        return this.getString("challengeeName");
    }

    public void setChallengeeName(String challengeeName) {
        this.put("challengeeName",challengeeName);
    }

    public String getChallengerName() {
        return this.getString("challengerName");
    }

    public void setChallengerName(String challengerName) {
        this.put("challengerName",challengerName);
    }
}

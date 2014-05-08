package alm.motiv.AlmendeMotivator.models;

import alm.motiv.AlmendeMotivator.adapters.Item;
import android.os.Parcelable;
import com.mongodb.BasicDBObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by AsterLaptop on 3/31/14.
 */
public class Challenge extends BasicDBObject implements Item {
    private static final long serialVersionUID = 1L;

    private String challengeeName;
    private String challengerName;

    public Challenge() {}

    //TODO add likeAmount to challenge. Different way then it is now. Solution below causes DBThreadExceptions
    public Challenge(String title, String challenger, String challengee, String content, int evidence_amount, String evidence_type, String reward, String status, String gps, String amountHours) {
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

    @Override
    public boolean isSection() {
        return false;
    }

    public String getChallengeeName() {
        return challengeeName;
    }

    public void setChallengeeName(String challengeeName) {
        this.challengeeName = challengeeName;
    }

    public String getChallengerName() {
        return challengerName;
    }

    public void setChallengerName(String challengerName) {
        this.challengerName = challengerName;
    }
}

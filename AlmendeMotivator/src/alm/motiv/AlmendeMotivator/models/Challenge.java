package alm.motiv.AlmendeMotivator.models;

import android.os.Parcelable;
import com.mongodb.BasicDBObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by AsterLaptop on 3/31/14.
 */
public class Challenge extends BasicDBObject implements Serializable{
    private static final long serialVersionUID = 1L;

    public Challenge() {

    }

    public Challenge(String title, String challenger, String challengee, String content, int evidence_amount, String evidence_type, String reward, String status) {
        put("title", title);
        put("challenger", challenger);
        put("challengee", challengee);
        put("content", content);
        put("evidence_amount", evidence_amount);
        put("evidence_type", evidence_type);
        put("reward", reward);
        put("status", status);
    }

    public void setStatus(String status){
        put("status", status);
    }

    public Object getID(){
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

    public ArrayList<BasicDBObject> getEvidence(){
        return (ArrayList<BasicDBObject>) this.get("evidence");
    }
}

package alm.motiv.AlmendeMotivator.models;

import com.mongodb.BasicDBObject;

/**
 * Created by AsterLaptop on 3/31/14.
 */
public class Challenge extends BasicDBObject {
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
}

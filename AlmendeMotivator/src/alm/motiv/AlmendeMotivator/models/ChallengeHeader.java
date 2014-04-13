package alm.motiv.AlmendeMotivator.models;

import alm.motiv.AlmendeMotivator.adapters.Item;

/**
 * Created by AsterLaptop on 4/13/14.
 */

public class ChallengeHeader implements Item {

    private final String title;

    public ChallengeHeader(String title) {
        this.title = title;
    }

    public String getTitle(){
        return title;
    }

    @Override
    public boolean isSection() {
        return true;
    }

}

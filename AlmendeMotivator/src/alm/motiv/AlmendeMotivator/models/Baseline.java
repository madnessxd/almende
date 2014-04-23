package alm.motiv.AlmendeMotivator.models;

import com.mongodb.BasicDBObject;

import java.util.ArrayList;

/**
 * Created by AsterLaptop on 4/23/14.
 */
public class Baseline extends BasicDBObject {

    public Baseline(){}

    public Baseline(String hours, String period, String partOfTheDay){
        put("numberOfHours", hours);
        put("period", period);
        put("partOfTheDay", partOfTheDay);
    }

    public void setHours(String hours){
        this.put("hours", hours);
    }

    public void setPeriod(String period){
        this.put("period", period);
    }

    public void setPartOfTheDay(String partOfTheDay){
        this.put("partOfTheDay", partOfTheDay);
    }
}

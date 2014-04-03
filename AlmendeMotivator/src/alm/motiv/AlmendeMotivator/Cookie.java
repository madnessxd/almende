package alm.motiv.AlmendeMotivator;

/**
 * Created by AsterLaptop on 4/2/14.
 */
public class Cookie {

    //a singleton for creating an instance of mongo, because we only need one
    public String userEntryId;

    private static Cookie instance =null;

    private Cookie(){}

    public static synchronized Cookie getInstance() {
        if(instance==null){
            instance=new Cookie();
        }
        return instance;
    }

}

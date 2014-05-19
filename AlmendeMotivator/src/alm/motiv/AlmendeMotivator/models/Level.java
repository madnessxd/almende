package alm.motiv.AlmendeMotivator.models;

/**
 * Created by AsterLaptop on 4/22/14.
 */
public enum Level {
    BEGINNER(10000),
    NOVICE(50000),
    ATHLETE(1000000),
    MASTER(5000000),
    CHAMPION(10000000);

    private int maxXP;

    private Level(int maxpXP){
        this.maxXP=maxpXP;
    }

    public int getMaxXP(){
        return maxXP;
    }


}

package alm.motiv.AlmendeMotivator.models;

/**
 * Created by AsterLaptop on 4/22/14.
 */
public enum Level {
    BEGINNER(1000),
    NOVICE(3000),
    ATHLETE(10000),
    MASTER(100000),
    CHAMPION(100000);

    private int maxXP;

    private Level(int maxpXP){
        this.maxXP=maxpXP;
    }

    public int getMaxXP(){
        return maxXP;
    }


}

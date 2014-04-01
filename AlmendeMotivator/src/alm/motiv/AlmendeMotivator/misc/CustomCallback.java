package alm.motiv.AlmendeMotivator.misc;
/**
 * Created by Thijs on 19-3-14.
 */
import java.io.Serializable;

public interface CustomCallback extends Serializable {
    public Object callback(Object object);
}
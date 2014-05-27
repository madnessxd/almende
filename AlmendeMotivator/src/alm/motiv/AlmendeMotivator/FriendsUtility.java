package alm.motiv.AlmendeMotivator;

import alm.motiv.AlmendeMotivator.adapters.FriendsAdapter;
import alm.motiv.AlmendeMotivator.models.User;
import android.widget.GridView;
import com.facebook.model.GraphUser;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.*;

/**
 * Created by AsterLaptop on 5/27/14.
 */
public class FriendsUtility {
    private FriendsAdapter adapter;
    private GraphUser friend;
    private User user = null;
    private GridView friendListView;
    private List<DBObject> allUsers = null;
    private boolean initializedFriends = false;
    private int positionSelectedFriend = 0;

    public FriendsUtility(User user, List<DBObject> allUsers) {
        this.user = user;
        this.allUsers = allUsers;
    }

    public ArrayList compareFriends(boolean manageFriends) {
        ArrayList<GraphUser> facebookFriends = Cookie.getInstance().facebookFriends;
        ArrayList<BasicDBObject> currentFriends = new ArrayList<BasicDBObject>();
        ArrayList<GraphUser> result = new ArrayList<GraphUser>();
        Set<String> compareWith = new HashSet<String>();

        //if this fails, this means that the user is new and does not yet have any friends he follows
        try {
            currentFriends = user.getFriends();
        } catch (Exception e) {
            System.out.println(e);
        }

        if (currentFriends == null ) {
            if( manageFriends == false){
                //if we don't want to manage friends, it's ok to return al the facebookfriends
                return facebookFriends;
            }
            return result;
        }

        //fill our set, we are going to compare strings
        for (BasicDBObject aFriend : currentFriends) {
            compareWith.add((String) aFriend.get("facebookID"));
        }

        //we want only to show the facebookfriends that the user follows so that he can manage it
        //when manageFriends is true
        if (manageFriends) {
            return returnFriendsToManage(compareWith,facebookFriends);
        }

        //compare strings and put the facebook user that's not yet followed by the user in result
        for (GraphUser facebookFriend : facebookFriends) {
            if (!compareWith.contains(facebookFriend.getId())) {
                result.add(facebookFriend);
            }
        }
        Collections.sort(result, sortUsers);
        return result;


    }

    private ArrayList returnFriendsToManage(Set<String> compareWith, ArrayList<GraphUser> facebookFriends) {
        ArrayList<GraphUser> result = new ArrayList<GraphUser>();

        for (GraphUser facebookFriend : facebookFriends) {
            if (compareWith.contains(facebookFriend.getId())) {
                result.add(facebookFriend);
            }
        }
        Collections.sort(result, sortUsers);
        System.out.println(result);
        return result;
    }

    //we want to know which facebookfriends also have the app
    public ArrayList<GraphUser> hasSportopiaAccount(ArrayList<GraphUser> facebookFriends) {
        //we use this method so that in the friendlist only the facebookfriends with a sportopia account are showed
        ArrayList<GraphUser> result = new ArrayList<GraphUser>();

        if (facebookFriends != null) {
            Set<String> set = new HashSet<String>();
            //fill our set, we are going to compare strings
            for (DBObject aFriend : allUsers) {
                set.add((String) aFriend.get("facebookID"));
            }

            //compare strings and put the facebook user that also has a sportopia account in the array
            for (GraphUser facebookFriend : facebookFriends) {
                if (set.contains(facebookFriend.getId())) {
                    result.add(facebookFriend);
                }
            }
            return result;
        }
        return result;
    }

    //we want to sort the usersArray alphabetically
    private static Comparator<GraphUser> sortUsers = new Comparator<GraphUser>() {
        @Override
        public int compare(GraphUser first, GraphUser second) {
            String user1 = first.getName().toLowerCase();
            String user2 = second.getName().toLowerCase();

            //ascending order
            return user1.compareTo(user2);
        }
    };
}

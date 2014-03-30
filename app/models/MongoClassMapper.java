package models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kedar on 3/29/14.
 */
public class MongoClassMapper {

    protected static HashMap<String, Class> map;
    static {
        map.put("user_profiles", UserProfile.class);

    }

    public static Class mapCollection (String collection) {
        return map.get(collection);
    }

}

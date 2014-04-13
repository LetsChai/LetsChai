package mongo;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by kedar on 3/16/14.
 */
public class UserProfile {

    private ObjectId _id;
    private String first_name;
    private String last_name;
    private byte age;
    private short height;
    private String current_city;
    private String religion;
    private String occupation;
    private String employer;
    private List education;
    private String ethnicity;
    private String nationality;
    private String[] i_am = new String[3];
    private String[] i_look = new String[3];
    private String[] i_like = new String[3];
    private String[] my_date = new String[3];

    // empty constructor for Jongo to use
    private UserProfile () {}

    /**
     * regular constructors
     **/

    public String getEthnicity() {
        return ethnicity;
    }

    public String getNationality() {
        return nationality;
    }

    public byte getAge() {
        return age;
    }

    public String getName() {
        return first_name + " " + last_name;
    }
}

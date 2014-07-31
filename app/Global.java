import play.Application;
import play.GlobalSettings;
import play.libs.F;
import play.mvc.Result;
import play.mvc.Results;

/**
 * Created by kedar on 7/30/14.
 */
public class Global extends GlobalSettings {

    @Override
    public void onStart(Application app) {
        // start chai service
        // start friend caching service

    }

    @Override
    public void onStop (Application app) {

    }

}

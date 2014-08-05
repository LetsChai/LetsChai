import akka.actor.Cancellable;
import classes.Service;
import org.joda.time.DateTime;
import play.Application;
import play.GlobalSettings;
import play.libs.Akka;
import play.libs.F;
import play.mvc.Result;
import play.mvc.Results;
import scala.concurrent.duration.Deadline;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by kedar on 7/30/14.
 */
public class Global extends GlobalSettings {

    List<Cancellable> services = new ArrayList<>();

    @Override
    public void onStart(Application app) {
        // find time until 4AM
        DateTime fourAM = new DateTime().withHourOfDay(4).withMinuteOfHour(0);
        DateTime nextFourAM = fourAM.isBeforeNow() ? fourAM.plusDays(1) : fourAM;
        Long msUntil = nextFourAM.getMillis() - new DateTime().getMillis();

        // schedule Chai service
        Cancellable algorithm = Akka.system().scheduler().schedule(
                Duration.create(msUntil, TimeUnit.MILLISECONDS),
                Duration.create(1, TimeUnit.DAYS),
                Service::algorithm,
                Akka.system().dispatcher() );
        services.add(algorithm);

        // schedule friend caching service
        FiniteDuration frequency = Duration.create(20, TimeUnit.MINUTES);
        Cancellable cacher = Akka.system().scheduler().schedule(
                frequency,
                frequency,
                () -> Service.newUserActions((int) frequency.toMillis()),
                Akka.system().dispatcher());
        services.add(cacher);
    }

    @Override
    public void onStop (Application app) {
        services.stream().forEach(service -> service.cancel());
    }

}

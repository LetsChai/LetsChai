package controllers;

import actions.Auth;
import classes.Query;
import clients.LetsChaiAWS;
import models.Chai;
import models.Friends;
import models.User;
import org.joda.time.DateTime;
import play.Logger;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import types.*;
import uk.co.panaxiom.playjongo.PlayJongo;
import views.html.*;

import java.util.*;

public class Application extends Controller {

    @Auth.None
    public static Result landing () {
        return ok(landing.render());
    }

    @Auth.None
    public static Result thankyou () {
        return ok(thankyou.render());
    }

    @Auth.None
    public static Result newusersurvey () {
        return ok(newusersurvey.render());
    }

    @Auth.WithUser
    public static Result chai () {
        String userId = session().get("user");
        Query query =  new Query();
        Chai todaysChai = query.todaysChai(userId);

        if (todaysChai == null)   // no chai today!
            return redirect(controllers.routes.Application.nochai());

        User myMatch = User.findOne(todaysChai.getTarget());
        Friends friends = query.friends(userId, todaysChai.getTarget());
        if (friends == null)
            friends = new Friends(Arrays.asList(userId, myMatch.getUserId()));
        return ok(chai.render(myMatch, todaysChai, friends));
    }

    @Auth.WithUser
    public static Result profile () {
        User user = (User) ctx().args.get("user");
        user.forceNoCachePictures();
        return ok(profile.render(user));
    }

    @Auth.WithUser
    public static Result editProfile () {
        User user = (User) ctx().args.get("user");
        return ok(editprofile.render(user));
    }

    @Auth.UpdateUser
    public static Result updateProfile () {
        User user = (User) ctx().args.get("user");
        Map<String, String[]> params = request().body().asFormUrlEncoded();

        try {
            user.setHeight(Integer.parseInt(params.get("height")[0]));
        } catch (NumberFormatException e) {
            // continue without setting the user's height
        }
        user.setCity(params.get("city")[0]);
        user.setPincode(Integer.parseInt(params.get("pincode")[0]));
        user.setOccupation(params.get("occupation")[0]);
        user.setAnswers(params.get("answer"));
        user.setReligion(Religion.valueOf(params.get("religion")[0]));

        Date birthday = new DateTime(
                Integer.parseInt(params.get("year")[0]),
                Integer.parseInt(params.get("month")[0]),
                Integer.parseInt(params.get("day")[0]), 0, 0
        ).toDate();
        user.setBirthday(birthday);

        List<Education> education = new ArrayList<>();
        String [] eduParams = params.get("education");
        if (!eduParams[0].trim().equals(""))  // blank schools count as no entry
            education.add(new Education(eduParams[0].trim(), Education.EducationType.valueOf(eduParams[1])));
        if (!eduParams[2].trim().equals(""))
            education.add(new Education(eduParams[2].trim(), Education.EducationType.valueOf(eduParams[3])));
        user.setEducation(education);

        return redirect(controllers.routes.Application.profile());
    }

    @Auth.WithUser
    public static Result preferences () {
        User user = (User) ctx().args.get("user");
        return ok(preferences.render(user.getPreferences()));
    }

    @Auth.UpdateUser
    public static Result updatePreferences () {
        Map<String, String[]> params = request().body().asFormUrlEncoded();
        User user = (User) ctx().args.get("user");
        User.Preferences pref = user.getPreferences();

        pref.setGender(Gender.valueOf(params.get("gender")[0]));
        pref.setAge(new AgeRange(Integer.parseInt(params.get("age_min")[0]), Integer.parseInt(params.get("age_max")[0])));

        List<Religion> religions = new ArrayList<Religion>();
        for (String r: params.get("religion")) {
            religions.add(Religion.valueOf(r));
        }
        pref.setReligion(religions);

        return redirect(controllers.routes.Application.preferences());
    }

    @Auth.WithUser
    public static Result settings () {
        User user = (User) ctx().args.get("user");
        return ok(settings.render(user));
    }

    @Auth.UpdateUser
    public static Result updateSettings () {
        User user = (User) ctx().args.get("user");
        Map<String, String[]> params = request().body().asFormUrlEncoded();
        user.setEmail(params.get("email")[0]);
        return redirect(controllers.routes.Application.settings());
    }

    @Auth.WithUser
    public static Result editPictures () {
        User user = (User) ctx().args.get("user");
        user.setDefaultPicture(LetsChaiAWS.s3Link("images/silhouette.png"));
        user.forceNoCachePictures();
        return ok(editpictures.render(user));
    }

    @Auth.UpdateUser
    @BodyParser.Of(value = BodyParser.FormUrlEncoded.class, maxLength = 500 * 1024)
    public static Result uploadPictures () {
        User user = (User) ctx().args.get("user");
        Map<String, String[]> formData = request().body().asFormUrlEncoded();
        List<String> pictures = new ArrayList<String>();

        List<String> keys = Arrays.asList("image0", "image1", "image2", "image3");
        int i = 0;
        for (String key: keys) {
            String base64Image = formData.get(key)[0];
            if (base64Image.equals("") || base64Image == null); // do nothing
            else if (base64Image.equals("delete")) {
                user.removePicture(i);
            }
            else {
                user.uploadBase64Image(base64Image, "image/jpeg", i);
            }
            i++;
        }

        // check for 2 picture minimum
        if (user.getPictureCount() < 2) {
            user.removeFlag(Flag.READY_TO_CHAI);
        }

        return redirect(controllers.routes.Application.profile());
    }

    // This is an AJAX-only route
    @Auth.Basic
    public static Result chaiDecision (Boolean decision) {
        String userId = session().get("user");
        Query query = new Query();
        Chai today = query.todaysChai(userId);
        today.setDecision(decision);
        query.updateChai(userId, today);
        return ok();
    }

    @Auth.WithUser
    public static Result deactivate () {
        User user = (User) ctx().args.get("user");
        Map<String, String[]> params = request().body().asFormUrlEncoded();
        Query query = new Query();

        if (params.containsKey("delete")) {
            query.deleteUser(user);
            return redirect(controllers.routes.Login.logout());
        } else {    // deactivate
            user.addFlag(Flag.DEACTIVATED);
            user.update();
            return redirect(controllers.routes.Application.deactivatedUser());
        }
    }

    @Auth.UpdateUser
    public static Result activate () {
        User user = (User) ctx().args.get("user");
        user.removeFlag(Flag.DEACTIVATED);
        return redirect(controllers.routes.Application.profile());
    }

    @Auth.Basic
    public static Result nochai () {
        return ok(nochai.render());
    }

    // limit to deactivated users only
    @Auth.WithUser
    public static Result deactivatedUser () {
        User user = (User) ctx().args.get("user");
        return ok(deactivated.render(user));
    }
}

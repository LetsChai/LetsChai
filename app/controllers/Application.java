package controllers;

import actions.Auth;
import models.Chai;
import models.User;
import org.joda.time.DateTime;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import types.AgeRange;
import types.Education;
import types.Gender;
import types.Religion;
import views.html.*;

import java.util.*;

public class Application extends Controller {

    public static Result landing () {
        return ok(landing.render());
    }

    public static Result thankyou () {
        return ok(thankyou.render());
    }

    public static Result newusersurvey () {
        return ok(newusersurvey.render());
    }

    @Auth.WithUser
    public static Result chai () {
        User user = (User) ctx().args.get("user");
        Chai todaysChai = user.getTodaysChai();

        if (todaysChai == null)   // no chai today!
            return redirect(controllers.routes.Application.profile());

        User myMatch = User.findOne(todaysChai.getOtherUserId());

        return ok(chai.render(myMatch, false, false, todaysChai));
    }

    @Auth.WithUser
    public static Result profile () {
        User user = (User) ctx().args.get("user");
        return ok(chai.render(user, false, true, null));
    }

    @Auth.WithUser
    public static Result editProfile () {
        User user = (User) ctx().args.get("user");
        return ok(chai.render(user, true, true, null));
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
        user.setOccupation(params.get("occupation")[0]);
        user.setAnswers(params.get("answer"));

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

        return redirect(controllers.routes.Application.editProfile());
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
        user.setDefaultPicture("/assets/images/silhouette.png");
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
            if (base64Image == "" || base64Image == null); // do nothing
            else if (base64Image.equals("delete")) {
                user.removePicture(i);
            }
            else {
                user.uploadBase64Image(base64Image, "image/jpeg", i);
            }
            i++;
        }

        return redirect(controllers.routes.Application.editPictures());
    }

    // This is an AJAX-only route
    @Auth.UpdateUser
    public static Result chaiDecision (Boolean decision) {
        User user = (User) ctx().args.get("user");
        Map<String, String[]> form = request().body().asFormUrlEncoded();
        User other = User.findOne(user.getTodaysChai().getOtherUserId());

        user.getTodaysChai().setMyDecision(decision);
        other.getTodaysChai().setOtherDecision(decision);

        User.getCollection().update("{'userId':'#'}", other.getUserId()).with(other);
        return ok();
    }
}

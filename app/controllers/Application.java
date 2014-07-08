package controllers;

import models.*;
import org.joda.time.DateTime;
import play.mvc.*;

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

    @With(Auth.class)
    public static Result chai () {
        User user = User.findOne(session().get("user"));
        return ok(chai.render(user, false));
    }

    @With(Auth.class)
    public static Result editprofile () {
        User user = User.findOne(session().get("user"));
        return ok(chai.render(user, true));
    }

    @With(Auth.class)
    public static Result updateProfile () {
        Map<String, String[]> params = request().body().asFormUrlEncoded();
        UserProfile profile = UserProfile.findOne(session().get("user"));

        profile.setHeight(Integer.parseInt(params.get("height")[0]));
        profile.setCity(params.get("city")[0]);
        profile.setOccupation(params.get("occupation")[0]);
        profile.setAnswers(params.get("answer"));

        Date birthday = new DateTime(
                Integer.parseInt(params.get("year")[0]),
                Integer.parseInt(params.get("month")[0]),
                Integer.parseInt(params.get("day")[0]), 0, 0
        ).toDate();
        profile.setBirthday(birthday);

        List<Education> education = new ArrayList<Education>();
        String [] eduParams = params.get("education");
        education.add(new Education(eduParams[0], Education.EducationType.valueOf(eduParams[1])));
        education.add(new Education(eduParams[2], Education.EducationType.valueOf(eduParams[3])));
        profile.setEducation(education);

        UserProfile.getCollection().update("{'id': '#'}", profile.getId()).with(profile);

        return redirect(controllers.routes.Application.editprofile());
    }

    @With(Auth.class)
    public static Result preferences () {
        return ok(preferences.render(UserPreference.findOne(session().get("user"))));
    }

    @With(Auth.class)
    public static Result updatePreferences () {
        Map<String, String[]> params = request().body().asFormUrlEncoded();
        UserPreference pref = new UserPreference();

        pref.setGender(Gender.valueOf(params.get("gender")[0]));
        pref.setAge(new AgeRange(Integer.parseInt(params.get("age_min")[0]), Integer.parseInt(params.get("age_max")[0])));

        List<Religion> religions = new ArrayList<Religion>();
        for (String r: params.get("religion")) {
            religions.add(Religion.valueOf(r));
        }
        pref.setReligion(religions);

        UserPreference.getCollection().update("{'userId': '#'}", session().get("user")).with(pref);

        return redirect(controllers.routes.Application.preferences());
    }

    @With(Auth.class)
    public static Result settings () {
        UserProfile profile = UserProfile.findOne(session().get("user"));
        return ok(settings.render(profile));
    }

    @With(Auth.class)
    public static Result updateSettings () {
        UserProfile profile = UserProfile.findOne(session().get("user"));
        Map<String, String[]> params = request().body().asFormUrlEncoded();

        profile.setEmail(params.get("email")[0]);

        UserProfile.getCollection().update("{'id': '#'}", profile.getId()).with(profile);

        return redirect(controllers.routes.Application.settings());
    }

    @With(Auth.class)
    public static Result editPictures () {
        return ok(editpictures.render());
    }
}

package controllers;

import actions.Auth;
import actions.AuthAction;
import actions.SessionUserAction;
import models.*;
import org.joda.time.DateTime;
import play.Logger;
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

    @Auth.WithUser
    public static Result chai () {
        User user = (User) ctx().args.get("user");
        return ok(chai.render(user, false, false));
    }

    @Auth.WithUser
    public static Result profile () {
        User user = (User) ctx().args.get("user");
        return ok(chai.render(user, false, true));
    }

    @Auth.WithUser
    public static Result editprofile () {
        User user = (User) ctx().args.get("user");
        return ok(chai.render(user, true, true));
    }

    @Auth.UpdateUser
    public static Result updateProfile () {
        User user = (User) ctx().args.get("user");
        Map<String, String[]> params = request().body().asFormUrlEncoded();

        user.setHeight(Integer.parseInt(params.get("height")[0]));
        user.setCity(params.get("city")[0]);
        user.setOccupation(params.get("occupation")[0]);
        user.setAnswers(params.get("answer"));

        Date birthday = new DateTime(
                Integer.parseInt(params.get("year")[0]),
                Integer.parseInt(params.get("month")[0]),
                Integer.parseInt(params.get("day")[0]), 0, 0
        ).toDate();
        user.setBirthday(birthday);

        List<Education> education = new ArrayList<Education>();
        String [] eduParams = params.get("education");
        education.add(new Education(eduParams[0], Education.EducationType.valueOf(eduParams[1])));
        education.add(new Education(eduParams[2], Education.EducationType.valueOf(eduParams[3])));
        user.setEducation(education);

        return redirect(controllers.routes.Application.editprofile());
    }

    @Auth.WithUser
    public static Result preferences () {
        User user = (User) ctx().args.get("user");
        return ok(preferences.render(user.getPreferences()));
    }

    @With(AuthAction.class)
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
}

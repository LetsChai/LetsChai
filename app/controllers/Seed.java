package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import seeds.PreferenceSeeder;
import seeds.UserSeeder;

/**
 * Created by kedar on 5/23/14.
 */
public class Seed extends Controller {

    public static Result users (int seedSize) {
        UserSeeder seeder = new UserSeeder();
        seeder.seed(seedSize);
        return ok(String.format("Seeded %d users", seedSize));
    }

    public static Result preferences (int seedSize) {
        PreferenceSeeder seeder = new PreferenceSeeder();
        return ok(String.format("Seeded %d preferences", seedSize));
    }
}

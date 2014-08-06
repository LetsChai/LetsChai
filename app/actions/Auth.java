package actions;

import play.mvc.With;

import java.lang.annotation.*;

/**
 * Created by kedar on 7/9/14.
 */
public class Auth {

    @With(AuthAction.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Inherited
    @Documented
    public static @interface Basic {

    }

    @With({AuthAction.class, SessionUserAction.class})
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Inherited
    @Documented
    public static @interface WithUser {

    }

    @With({AuthAction.class, SessionUserAction.class, UpdateSessionUserAction.class})
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Inherited
    @Documented
    public static @interface UpdateUser {

    }

    @With(NoAuthAction.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Inherited
    @Documented
    public static @interface None {

    }

    @With({AuthAction.class, AdminAuthAction.class})
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Inherited
    @Documented
    public static @interface Admin {

    }

}

package controllers

import play.api.mvc._
import models.LetsChaiFacebookClient
import scala.concurrent._
import jongo.types.FacebookAccessToken

/**
 * Created by kedar on 4/21/14.
 */
object LoginScala extends Controller {

  def fbLoginRedirect = Action {
    val fb = new LetsChaiFacebookClient
    Redirect(fb.getLoginUrl)
  }

//  def extractCode(code: String) = Action.async { request =>
//    val fb = new LetsChaiFacebookClient
//
//    // swap user token for code
//    val userTokenFuture: Future[FacebookAccessToken] = future { fb.obtainUserAccessToken(code) }
//
//    // set access token on FB client
//    val clientWithTokenFuture: Future[FacebookAccessToken] = userTokenFuture.map { userToken =>
//      fb.setAccessToken(userToken)
//      return fb
//    }
//
//    // swap user token for extended token
//    val extendedTokenFuture = userTokenFuture.map {userToken => future {
//      fb.obtainExtendedAccessToken(userToken.getAccessToken)
//    }}
//
//    // use user token to get FB User object and friends
////    val userObjectFuture = userTokenFuture.map { userToken => scala.concurrent.future}
//
//    extendedTokenFuture.map {extendedToken => Ok()}
//  }

}

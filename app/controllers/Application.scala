package controllers

import play.api.cache.Cache
import play.api.data.number
import play.api.data.Form
import play.api.mvc.Controller
import play.api.mvc.Session
import play.api.Play
import play.api.Logger
import java.util.UUID
import java.net.URLEncoder
import models.NumguessModel

object Application extends Controller {

  implicit val app = Play.current

  val minGuess = Play.current.configuration.getInt("application.numguess.min").get
  val maxGuess = Play.current.configuration.getInt("application.numguess.max").get

  val cacheIdKey = "cacheIdNumguess"

  def cacheId(implicit session: Session) =
    session.get(cacheIdKey).getOrElse(UUID.randomUUID.toString.replace('-', 'X'))

  val guessForm = Form(
    "value" -> number(min=minGuess, max=maxGuess)
  )

  def start = Action { implicit request =>
    val currentCacheId = cacheId
    Logger.debug("Application.start()")
    Logger.debug(cacheIdKey + " = " + currentCacheId)
    Cache.set(currentCacheId, NumguessModel(minGuess, maxGuess).reset())
    Ok(views.html.start(guessForm, minGuess, maxGuess)).withSession(cacheIdKey -> currentCacheId)
  }

  def guess(value: Long) = Action { implicit request =>
    Logger.debug("Application.guess()")
    Logger.debug("session = " + session)
    val currentCacheId = cacheId
    Logger.debug(cacheIdKey + " = " + currentCacheId)
    val previousModel = Cache.get(currentCacheId).get.asInstanceOf[NumguessModel]
    Logger.info("cached model = " + previousModel)
    val model = previousModel.guess(value.toInt)
    Cache.set(currentCacheId, model)
    Logger.info("updated model = " + model)

    if (model.comparison == 0)
      Ok(views.html.right(guessForm, model))
    else
      Ok(views.html.wrong(guessForm, model))
  }
}
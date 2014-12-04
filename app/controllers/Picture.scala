package controllers

import play.api._
import play.api.mvc._
import play.api.db._
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DBAction
import java.io.File


object Picture extends Controller {

  /**
   * Generates an `Action` that serves a static resource from an external folder
   *
   * @param absoluteRootPath the root folder for searching the static resource files.
   * @param file the file part extracted from the URL
   */
	def at(image: String): Action[AnyContent] = Action { request =>
		val fileToServe = new File(s"images/profiles/$image")

		if (fileToServe.exists) {
			Ok.sendFile(fileToServe, inline = true)
		} else {
			Logger.error("image controller failed to serve image: " + image)
			NotFound
		}
	}
}
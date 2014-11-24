package models

import play.api.db.slick.Config.driver.simple._
import play.api.Play.current

case class SSMUProfile(name: String,
        description: String,
        imageUrl: String,
        weightClass: String,
        facebook: String,
        youtube: String,
        instagram: String)

class SSMUProfiles(tag: Tag) extends Table[SSMUProfile](tag, "SSMU_PROFILES") {
  def name = column[String]("NAME", O.PrimaryKey)
  def description = column[String]("DESCRIPTION", O.NotNull)
  def imageUrl = column[String]("IMAGE_URL", O.Nullable)
  def weightClass = column[String]("WEIGHT_CLASS", O.Nullable)
  def facebook = column[String]("FACEBOOK", O.Nullable)
  def youtube = column[String]("YOUTUBE", O.Nullable)
  def instagram = column[String]("INSTAGRAM", O.Nullable)

  def * = (name, description, imageUrl, weightClass, facebook, youtube, instagram) <> ((SSMUProfile.apply _).tupled, SSMUProfile.unapply)
}

object SSMUProfiles {
	val profiles: TableQuery[SSMUProfiles] = TableQuery[SSMUProfiles]
	
	def populateInit() {
		play.api.db.slick.DB.withSession{ implicit session =>
			val initTeam = List(
			        SSMUProfile("Jonathan Fok kan", 
			                """Jonathan is a Software Engineer who likes to lift things up and put them down""",
			                "http://placehold.it/750x450",
			                "-66kg",
			                "https://www.facebook.com/jon.fokkan",
			                "https://www.youtube.com/user/jonesdoe1",
			                "http://instagram.com/jonender")
				)
			for(profile <- initTeam) {
				profiles += profile
			}
		}
	}
}
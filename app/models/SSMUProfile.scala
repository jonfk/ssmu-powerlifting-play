package models

import play.api.db.slick.Config.driver.simple._
import play.api.Play.current

case class SSMUProfile(id: Option[Int],
        userId: Int,
        name: String,
        description: String,
        imageUrl: String,
        weightClass: String,
        facebook: String,
        youtube: String,
        instagram: String)

        /* Constraints to be added
         * userId is unique */
class SSMUProfiles(tag: Tag) extends Table[SSMUProfile](tag, "SSMU_PROFILES") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def userId = column[Int]("USER_ID")
    def name = column[String]("NAME", O.NotNull)
    def description = column[String]("DESCRIPTION", O.Nullable)
    def imageUrl = column[String]("IMAGE_URL", O.Nullable)
    def weightClass = column[String]("WEIGHT_CLASS", O.Nullable)
    def facebook = column[String]("FACEBOOK", O.Nullable)
    def youtube = column[String]("YOUTUBE", O.Nullable)
    def instagram = column[String]("INSTAGRAM", O.Nullable)

  def * = (id.?, userId, name, description, imageUrl, weightClass, facebook, youtube, instagram) <> ((SSMUProfile.apply _).tupled, SSMUProfile.unapply)
  
  // Foreign key email of user
  def user = foreignKey("USER_PROFILE_FK", userId, Users.users)(_.id)
}

object SSMUProfiles {
	val profiles: TableQuery[SSMUProfiles] = TableQuery[SSMUProfiles]
	
	def populateInit() {
		play.api.db.slick.DB.withSession{ implicit session =>
			val initTeam = List(
			        SSMUProfile(None, 1,
			                "Jonathan Fok kan", 
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
	
	def create(userId: Int, name: String,
	        description: String, imageUrl: String, weightClass: String,
	        facebook: String, youtube: String, instagram: String) : Int = {

		play.api.db.slick.DB.withSession{ implicit session =>
			val profileId = (profiles returning profiles.map(_.id)) += SSMUProfile(None, userId, name, description, imageUrl, weightClass, facebook, youtube, instagram)
			profileId
		}
	}
	
	def exists(userId: Int): Boolean = {
		play.api.db.slick.DB.withSession{ implicit session =>
    		!(for { p <- profiles if p.userId === userId } yield p).list.isEmpty
		}
	}
	
	def getProfile(userId : Int) : SSMUProfile = {
		play.api.db.slick.DB.withSession{ implicit session =>
    		(for { p <- profiles if p.userId === userId } yield p).list.head
		}
	}
	
	def updateProfile(userId: Int,
	        name: String,
	        description: String,
	        imageUrl: String,
	        weightClass: String,
	        facebook: String,
	        youtube: String,
	        instagram: String)(implicit session: Session) {
		        updateName(userId, name)
		        updateDescription(userId, description)
		        updateWeightClass(userId, weightClass)
		        updateImageUrl(userId, imageUrl)
		        updateFacebook(userId, facebook)
		        updateYoutube(userId, youtube)
		        updateInstagram(userId, instagram)
	}
	
	def updateName(userId: Int, name: String)(implicit session: Session) {
    	val q = for { p <- profiles if p.userId === userId } yield p.name
    	q.update(name)
    	q.invoker.execute
	}

	def updateDescription(userId: Int, description: String)(implicit session: Session) {
    	val q = for { p <- profiles if p.userId === userId } yield p.description
    	q.update(description)
    	q.invoker.execute
	}

	def updateImageUrl(userId: Int, imageUrl: String)(implicit session: Session) {
    	val q = for { p <- profiles if p.userId === userId } yield p.imageUrl
    	q.update(imageUrl)
    	q.invoker.execute
	}

	def updateWeightClass(userId: Int, weightClass: String)(implicit session: Session) {
    	val q = for { p <- profiles if p.userId === userId } yield p.weightClass
    	q.update(weightClass)
    	q.invoker.execute
	}

	def updateFacebook(userId: Int, facebook: String)(implicit session: Session) {
    	val q = for { p <- profiles if p.userId === userId } yield p.facebook
    	q.update(facebook)
    	q.invoker.execute
	}

	def updateYoutube(userId: Int, youtube: String)(implicit session: Session) {
    	val q = for { p <- profiles if p.userId === userId } yield p.youtube
    	q.update(youtube)
    	q.invoker.execute
	}

	def updateInstagram(userId: Int, instagram: String)(implicit session: Session) {
    	val q = for { p <- profiles if p.userId === userId } yield p.instagram
    	q.update(instagram)
    	q.invoker.execute
	}
	
	def deleteImageFile(userId: Int)(implicit session: Session) {
	    val p = (for{p <- profiles if p.userId === userId} yield p).list.head
	    if(p.imageUrl != current.configuration.getString("image.default.url").get) {
		   	import java.io.File
	    	val filename = p.imageUrl.replaceFirst("/assets/images/profiles/", "")
	    	(new File(s"public/images/profiles/$filename")).delete()
	    }
	}
}
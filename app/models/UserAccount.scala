package models

import play.api.db.slick.Config.driver.simple._
import play.api.Play.current
import org.mindrot.jbcrypt.BCrypt
import play.api.db._
import org.apache.commons.validator.routines.EmailValidator

case class User(id: Option[Int], email: String, username: String,
        firstname: String, lastname: String,
        password: String)

/*
 * Constraints:
 * Username and email have to be unique
 * The username cannot be a valid email
 * */
class Users(tag: Tag) extends Table[User](tag, "SSMU_USERS") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
	def email = column[String]("EMAIL", O.NotNull)
	def username = column[String]("USERNAME", O.NotNull)
	def firstname = column[String]("FIRSTNAME", O.Nullable)
	def lastname = column[String]("LASTNAME", O.Nullable)
	def password = column[String]("PASSWORD", O.NotNull)

	def * = (id.?, email, username, firstname, lastname, password) <> ((User.apply _).tupled, User.unapply)
}

object Users {
	val users: TableQuery[Users] = TableQuery[Users]
	
	def populateInit() {
		play.api.db.slick.DB.withSession{ implicit session =>
			val initUsers = List(
			        create("jonesdoe2@gmail.com", "jfk", "Jonathan", "Fok kan", "1234"),
			        create("j@yahoo.com", "jake", "Jake", "Shamash", "9999")
				)
			for(user <- initUsers) {
				val userId = (users returning users.map(_.id)) += user
				println(userId)
			}
		}
	}

    def create(email: String, username: String,
            firstname: String, lastname: String, password: String) = {
        val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
        User(None, email, username, firstname, lastname, hashedPassword)
    }

    def authenticateUsername(username: String, password: String) : Boolean = {
		play.api.db.slick.DB.withSession{ implicit session =>
        	//val findUser = users.filter(u: User => u.username === user.username).first
        	val findUser = (for{u <- users if u.username === username} yield u).list
        	if(findUser.isEmpty) {
        	    false
        	} else {
        		BCrypt.checkpw(password, findUser.head.password)
        	}
		}
    }

    def authenticateEmail(email: String, password: String) : Boolean = {
		play.api.db.slick.DB.withSession{ implicit session =>
			//val findUser = users.filter(u: User => u.email === user.email).first
        	val findUser = (for{u <- users if u.email === email} yield u).list
        	if(findUser.isEmpty) {
        	    false
        	} else {
        		BCrypt.checkpw(password, findUser.head.password)
        	}
		}
    }
    
    def getUser(usernameOrEmail: String) : Option[User] = {
	    val validator = EmailValidator.getInstance()
		play.api.db.slick.DB.withSession{ implicit session =>
		    val findUser = if(validator.isValid(usernameOrEmail)){
		    	(for{u <- users if u.email === usernameOrEmail} yield u).list
		    } else {
		    	(for{u <- users if u.username === usernameOrEmail} yield u).list
		    }
        	if(findUser.isEmpty) {
        	    None
        	} else {
        	    Some(findUser.head)
        	}
		}
    }
    
    def existsUsername(username: String) : Boolean = {
		play.api.db.slick.DB.withSession{ implicit session =>
        	(for{u <- users if u.username === username} yield u).list.isEmpty
		}
    }

    def existsEmail(email: String) : Boolean = {
		play.api.db.slick.DB.withSession{ implicit session =>
        	(for{u <- users if u.email === email} yield u).list.isEmpty
		}
    }
    
    def updateEmail(id: Option[Int], email: String)(implicit session: Session) {
    	val q = for { u <- users if u.id === id } yield u.email
    	q.update(email)
    	q.invoker.execute
    }

    def updateUsername(id: Option[Int], username: String)(implicit session: Session) {
    	val q = for { u <- users if u.id === id } yield u.username
    	q.update(username)
    	q.invoker.execute
    }
    
    def updateFirstname(id: Option[Int], firstname: String)(implicit session: Session) {
    	val q = for { u <- users if u.id === id } yield u.firstname
    	q.update(firstname)
    	q.invoker.execute
    }
    
    def updateLastname(id: Option[Int], lastname: String)(implicit session: Session) {
    	val q = for { u <- users if u.id === id } yield u.lastname
    	q.update(lastname)
    	q.invoker.execute
    }

    def updatePassword(id: Option[Int], password: String)(implicit session: Session) {
    	val q = for { u <- users if u.id === id } yield u.password
        val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
    	q.update(hashedPassword)
    	q.invoker.execute
    }
}
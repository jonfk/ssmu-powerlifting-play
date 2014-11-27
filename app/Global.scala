import play.api._
import play.api.db._
import play.api.Play.current
import models.SSMURecord
import play.api.db.slick.Config.driver.simple._
import models.SSMURecords
import play.api.mvc.Results._
import scala.concurrent.Future
import play.api.mvc.RequestHeader
import org.h2.jdbc.JdbcSQLException
import models._



object Global extends GlobalSettings {
	println("Testing from global")

    override def onStart(app: Application) {
        Logger.info("Application has started")
        
        try {
        	play.api.db.slick.DB.withSession{ implicit session =>
            	( Users.users.ddl ++ SSMUProfiles.profiles.ddl ++ SSMURecords.records.ddl ).create
        	}

        	Users.populateInit
        	SSMURecords.populateInit
        	SSMUProfiles.populateInit
        } catch {
            case ex: JdbcSQLException =>
                println("Database already populated or tables already created")
                ex.printStackTrace()
            case e: Exception =>
                println("Unknown Exception")
                e.printStackTrace()
        }
    }

    override def onStop(app: Application) {
        Logger.info("Application shutdown...")
        println("dropping databases")
		play.api.db.slick.DB.withSession{ implicit session =>
        	SSMURecords.records.ddl.drop
        	SSMUProfiles.profiles.ddl.drop
        	Users.users.ddl.drop
        }
    }  
    
    override def onHandlerNotFound(request: RequestHeader) = {
    	Future.successful(NotFound(
    			views.html.notFound(request.path)
    			))
    }
}
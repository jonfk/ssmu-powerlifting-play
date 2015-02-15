import play.api._
import play.api.db._
import play.api.Play.current
import models.SSMURecord
import models.SSMURecords
import play.api.db.slick.Config.driver.simple._
import play.api.mvc.Results._
import scala.concurrent.Future
import play.api.mvc.RequestHeader
import org.h2.jdbc.JdbcSQLException
import models._
import java.io.PrintWriter
import java.io.File



object Global extends GlobalSettings {
	println("Testing from global")

    override def onStart(app: Application) {
        Logger.info("Application has started")
        
        try {
        	play.api.db.slick.DB.withSession{ implicit session =>
        	    // write create statements to file
        	    val writer = new PrintWriter(new File("SQL/create.sql" ))
            	val stmts = ( Users.users.ddl ++ SSMUProfiles.profiles.ddl ++ SSMURecords.records.ddl ++ NewsItems.news.ddl ).createStatements.mkString(";\n")
            	writer.write(stmts)
        	    writer.close
        	    // Disable for live run
//        	    println("creating tables")
//            	( Users.users.ddl ++ SSMUProfiles.profiles.ddl ++ SSMURecords.records.ddl ++ NewsItems.news.ddl ).create
        	}

            // Disable for live run
//        	Users.populateInit
//        	SSMURecords.populateInit
//        	SSMUProfiles.populateInit
//        	NewsItems.populateInit
        } catch {
            case ex: JdbcSQLException =>
                println("Database already populated or tables already created")
                ex.printStackTrace()
            case ex: org.postgresql.util.PSQLException =>
                println("Database already populated or tables already created")
            case e: Exception =>
                println("Unknown Exception")
                e.printStackTrace()
        }
    }

    override def onStop(app: Application) {
        Logger.info("Application shutdown...")
		play.api.db.slick.DB.withSession{ implicit session =>
		    // write drop statements to file
        	val writer = new PrintWriter(new File("SQL/drop.sql" ))
        	val stmts = (SSMURecords.records.ddl ++ SSMUProfiles.profiles.ddl ++ Users.users.ddl ++ NewsItems.news.ddl ).dropStatements.mkString(";\n")
        	writer.write(stmts)
        	writer.close
        	// disable for live run
//        	println("dropping databases")
//        	(SSMURecords.records.ddl ++ SSMUProfiles.profiles.ddl ++ Users.users.ddl ++ NewsItems.news.ddl ).drop
        }
    }  
    
    override def onHandlerNotFound(request: RequestHeader) = {
    	Future.successful(NotFound(
    			views.html.notFound(request.path)
    			))
    }
}
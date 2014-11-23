import play.api._
import play.api.db._
import play.api.Play.current
import models.SSMURecord
import play.api.db.slick.Config.driver.simple._
import models.SSMURecords


object Global extends GlobalSettings {
	println("Testing from global")

    override def onStart(app: Application) {
        Logger.info("Application has started")

		//val ssmuRecords: TableQuery[SSMURecords] = TableQuery[SSMURecords]
		//SSMURecords.populateInit
    }

    override def onStop(app: Application) {
        Logger.info("Application shutdown...")
    }  
    
}
package connectors

import config.AppConfig
import org.scalatest.matchers.should.Matchers.should
import play.api.libs.json.JsPath.json
import play.api.libs.json.Json
import utils.SpecHelper
import io.circe.*
import io.circe.generic.auto.*
import io.circe.literal.json
import io.circe.syntax.*
class SubmissionConnectorISpec extends SpecHelper{

  val appConfig : AppConfig = app.injector.instanceOf[AppConfig]
  val connector : SubmissionsConnector = app.injector.instanceOf[SubmissionsConnector]
  lazy val url = s"http://localhost:$port/dac6/getlistofsubmissions/v1"

  import io.circe._, io.circe.generic.auto._, io.circe.syntax._

  val successResponse =
    json"""
  {
    "submissionsListResponse": {
      "responseCommon": {
        "regime": "CRFA",
        "responseParameters": [
          {
            "paramName": "paramName1",
            "paramValue": "paramValue1"
          }
        ]
      },
      "responseDetails": {
        "submissionsList": [
          {
            "fiId": "XZU9323406858",
            "fiName": "Test FI Name",
            "fileName": "GB2025GB-XZU9323406858-APIMB0256",
            "submissionStatus": "PASSED",
            "uploadDateTime": "2025-02-28T10:20:56.789Z",
            "regime": "CRS",
            "reportingYear": "2016",
            "submissionCaseId": "CRS-SUB-12224",
            "submissionType": "XML",
            "submissionFileType": "CRS702",
            "messageRefId": "GB2025GB-XZU9323406858-APIMB0256"
          },
          {
            "fiId": "XZU9323406868",
            "fiName": "Test FI Name1",
            "fileName": "GB2025GB-XZU9323406858-APIMB0266",
            "submissionStatus": "PASSED",
            "uploadDateTime": "2025-02-28T10:20:56.789Z",
            "regime": "FATCA",
            "reportingYear": "2016",
            "submissionCaseId": "FATCA-SUB-12224",
            "submissionType": "XML",
            "submissionFileType": "FATCA2",
            "messageRefId": "GB2025GB-XZU9323406858-APIMB0266",
            "submissionDeleteStatus": true
          }
        ]
      }
    }
  }
  """
  
"SubmissionsConnector" - {
  "successfully parse response body into a list of submissions in case of a 200 response" in {
    
    stubGet(url, 200, successResponse.toString)
 
  }
}
}

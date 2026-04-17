package connectors

import com.google.inject.Inject
import config.AppConfig
import connectors.HeaderGenerator.defaultHeaders
import models.HodErrors.{InvalidJson, UnexpectedResponse}
import models.{ReadSubmissionRequest, ReadSubmissionResponse, UserAnswers}
import play.api.Logging
import play.api.libs.json.{JsError, JsSuccess, Json}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}
import uk.gov.hmrc.http.client.HttpClientV2
import play.api.libs.ws.JsonBodyWritables.*
import repositories.ManualSubmissionRepository
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}



class SubmissionsConnector @Inject()(val config: AppConfig,
                                     val repository: ManualSubmissionRepository,
                                     val http: HttpClientV2)(implicit ec:ExecutionContext) extends Logging{
  
  
  def readSubmission(requestBody: ReadSubmissionRequest)(implicit hc:HeaderCarrier): Future[ReadSubmissionResponse] ={
    val correlationID=  UUID.randomUUID()
    logger.info(s"calling EIS with correlationID: ${correlationID.toString}")
    http
      .post(url"${config.readSubmissionUrl}")
      .withBody(Json.toJson(requestBody))
      .setHeader(defaultHeaders(config.readSubmissionToken, correlationID): _*)
      .execute[HttpResponse].flatMap{
        case res if res.status == 200 => res.json.validate[ReadSubmissionResponse] match {
          case JsSuccess(submissionHistory, _) => Future.successful(submissionHistory)
          case JsError(errors) =>
            logger.warn(s"Invalid json returned; $errors for corr ID : ${correlationID.toString}")
            Future.failed(InvalidJson)
        }
        case res =>
          logger.info(s"Unsuccessful call made to retrieve submission data for ${correlationID.toString}")
          Future.failed(UnexpectedResponse)
      }
  }

}

/*
 * Copyright 2026 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package testutils

import models.SubmissionsConstants.{CRFA, CRS, CRS701, FATCA, PASSED, RegimeType, XML}
import models.{
  CommonParameters,
  ReadSubmissionRequest,
  ReadSubmissionRequestCommon,
  ReadSubmissionRequestDetails,
  ReadSubmissionResponse,
  ReadSubmissionResponseCommon,
  ReadSubmissionResponseDetails,
  SubmissionsListRequest,
  SubmissionsListResponse,
  SubmittedReport
}
import models.fatcavoid.{Regime, RequestCommon, RequestDetails, VoidRequest, VoidRequestPayload}
import org.scalacheck.Gen

import java.time.LocalDate

trait TestGenerators {

  def regimeTypeGen: Gen[RegimeType] =
    Gen.oneOf(CRS, FATCA, CRFA)

  def requestParameterGen: Gen[CommonParameters] =
    for
      paramName  <- Gen.stringOfN(10, Gen.alphaNumChar)
      paramValue <- Gen.stringOfN(15, Gen.alphaNumChar)
    yield CommonParameters(paramName, paramValue)

  def requestParametersGen: Gen[Option[List[CommonParameters]]] =
    Gen.option(Gen.listOfN(2, requestParameterGen))

  def requestCommonGen: Gen[ReadSubmissionRequestCommon] =
    for
      originatingSystem  <- Gen.const("MDTP")
      transmittingSystem <- Gen.const("CADX")
      regime             <- regimeTypeGen
      requestParameters  <- requestParametersGen
    yield ReadSubmissionRequestCommon(
      originatingSystem = originatingSystem,
      transmittingSystem = transmittingSystem,
      regime = regime,
      requestParameters = requestParameters
    )

  def requestDetailsGen: Gen[ReadSubmissionRequestDetails] =
    for
      subscriptionId <- Gen.alphaNumStr
      fiId           <- Gen.numStr
    yield ReadSubmissionRequestDetails(
      subscriptionId = subscriptionId,
      fiId = Some(fiId)
    )

  def readSubmissionRequestGen: Gen[ReadSubmissionRequest] =
    for {
      requestCommon  <- requestCommonGen
      requestDetails <- requestDetailsGen
    } yield ReadSubmissionRequest(
      SubmissionsListRequest(
        requestCommon = requestCommon,
        requestDetails = requestDetails
      )
    )

  def responseCommonGen: Gen[ReadSubmissionResponseCommon] =
    for
      regime             <- regimeTypeGen
      responseParameters <- requestParametersGen
    yield ReadSubmissionResponseCommon(regime = regime, responseParameters = responseParameters)

  def responseDetailsGen: Gen[ReadSubmissionResponseDetails] =
    for
      originalMessageRefId <- Gen.alphaNumStr
      fiId                 <- Gen.numStr
      fiName               <- Gen.alphaLowerStr
      fileName             <- Gen.alphaNumStr
      submissionCaseId     <- Gen.alphaNumStr
      messageRefId         <- Gen.alphaNumStr
    yield ReadSubmissionResponseDetails(
      List(
        SubmittedReport(
          fiId = fiId,
          fiName = fiName,
          fileName = fileName,
          submissionStatus = PASSED,
          uploadDateTime = LocalDate.now().toString,
          regime = CRS,
          reportingYear = "2025",
          submissionCaseId = submissionCaseId,
          submissionType = XML,
          submissionFileType = CRS701,
          messageRefId = messageRefId
        )
      )
    )

  def readSubmissionResponseGen: Gen[ReadSubmissionResponse] =
    for {
      responseCommon <- responseCommonGen
      responseDetail <- responseDetailsGen
    } yield ReadSubmissionResponse(
      SubmissionsListResponse(
        responseCommon = responseCommon,
        responseDetails = responseDetail
      )
    )

  def requestCommonGenForFatcaVoid: Gen[RequestCommon] =
    RequestCommon(originatingSystem = "MDTP", transmittingSystem = "EIS", regime = Regime.FATCA, requestParameters = None)

  def requestDetailsGenForFatcaVoid: Gen[RequestDetails] =
    for {
      subscriptionId <- Gen.alphaNumStr
      messageRefId   <- Gen.alphaNumStr
      fiId           <- Gen.alphaNumStr
    } yield RequestDetails(subscriptionId = subscriptionId, messageRefId = messageRefId, fiId = fiId)

  def voidRequestGen: Gen[VoidRequest] =
    for {
      requestCommon  <- requestCommonGenForFatcaVoid
      requestDetails <- requestDetailsGenForFatcaVoid
    } yield VoidRequest(
      requestCommon = requestCommon,
      requestDetails = requestDetails
    )

  def voidRequestPayloadGen: Gen[VoidRequestPayload] =
    for {
      voidReq <- voidRequestGen
    } yield VoidRequestPayload(voidRequest = voidReq)

}

package utils

import models.{CommonParameters, ReadSubmissionRequest, ReadSubmissionRequestCommon, ReadSubmissionRequestDetails, SubmissionsListRequest}
import models.SubmissionsListResponseGenerator.{CRFA, CRS, FATCA, RegimeType}
import org.scalacheck.Gen

class Generators {
  def regimeTypeGen: Gen[RegimeType] =
    Gen.oneOf(CRS, FATCA, CRFA)


  def requestParameterGen: Gen[CommonParameters] =
    for
      paramName <- Gen.stringOfN(10, Gen.alphaNumChar)
      paramValue <- Gen.stringOfN(15, Gen.alphaNumChar)
    yield CommonParameters(paramName, paramValue)


  def requestParametersGen: Gen[Option[List[CommonParameters]]] =
    Gen.option(Gen.listOfN(2, requestParameterGen))


  def requestCommonGen: Gen[ReadSubmissionRequestCommon] =
    for
      originatingSystem <- Gen.const("MDTP")
      transmittingSystem <- Gen.const("CADX")
      regime <- regimeTypeGen
      requestParameters <- requestParametersGen
    yield ReadSubmissionRequestCommon(
      originatingSystem = originatingSystem,
      transmittingSystem = transmittingSystem,
      regime = regime,
      requestParameters = requestParameters
    )

  


  def subscriptionIdGen: Gen[String] =
    Gen.stringOfN(8, Gen.alphaNumChar).map(s => s"CBCID${s.take(8)}")


  def fiIdGen: Gen[Option[String]] =
    Gen.option(
      Gen.stringOfN(15, Gen.alphaNumChar).map(s => s"${s.take(6)}")
    )


  def requestDetailsGen: Gen[ReadSubmissionRequestDetails] =
    for
      subscriptionId <- subscriptionIdGen
      fiId <- fiIdGen
    yield ReadSubmissionRequestDetails(
      subscriptionId = subscriptionId,
      fiId = fiId
    )
  
  
  def readSubmissionRequestGen(regime: RegimeType): Gen[ReadSubmissionRequest] =
    for
      requestCommon <- requestCommonGen.map(_.copy(regime = regime))
      requestDetails <- requestDetailsGen
    yield ReadSubmissionRequest(SubmissionsListRequest(
      requestCommon = requestCommon,
      requestDetails = requestDetails
    ))


 

}

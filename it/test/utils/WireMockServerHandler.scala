package utils

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, urlEqualTo}
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Suite}

trait WireMockServerHandler extends BeforeAndAfterAll with BeforeAndAfterEach {
  this: Suite =>

  val wiremockPort: Int = 11111

  protected val server: WireMockServer = new WireMockServer(
    wireMockConfig.port(wiremockPort)
  )

  override def beforeAll(): Unit = {
    server.start()
    super.beforeAll()
  }

  override def beforeEach(): Unit = {
    server.resetAll()
    super.beforeEach()
  }

  override def afterAll(): Unit = {
    super.afterAll()
    server.stop()
  }

  protected def stubGet(expectedEndpoint: String, expectedStatus: Int, expectedBody: String): StubMapping =
    server.stubFor(
      get(urlEqualTo(s"$expectedEndpoint"))
        .willReturn(
          aResponse()
            .withStatus(expectedStatus)
            .withBody(expectedBody)
        )
    )

}
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

package uk.gov.hmrc.vatinsightsproxy.controllers

import play.api.http.Status.{INTERNAL_SERVER_ERROR, SERVICE_UNAVAILABLE}
import play.api.http.{HeaderNames, Status}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout, status}
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.http.UpstreamErrorResponse
import uk.gov.hmrc.vatinsightsproxy.BaseSpec
import uk.gov.hmrc.vatinsightsproxy.config.Constants
import uk.gov.hmrc.vatinsightsproxy.controllers.actions.{CorrelationIdActionImpl, FakeAllowListAction}
import uk.gov.hmrc.vatinsightsproxy.models.InsightsRequest
import uk.gov.hmrc.vatinsightsproxy.services.MockInsightsService
import uk.gov.hmrc.vatinsightsproxy.utils.MockUUIDGenerator

import scala.concurrent.Future


class InsightsControllerSpec extends BaseSpec with MockInsightsService with MockUUIDGenerator :

  def controller(userIsAllowed: Boolean): InsightsController =
    new InsightsController(
      cc = Helpers.stubControllerComponents(),
      allowListAction = new FakeAllowListAction(Helpers.stubPlayBodyParsers())(isAllowed = userIsAllowed),
      withCorrelationId = new CorrelationIdActionImpl(mockUUIDGenerator),
      insightsService = mockInsightsService
    )

  lazy val authorisedController: InsightsController = controller(userIsAllowed = true)
  lazy val unauthorisedController: InsightsController = controller(userIsAllowed = false)

  val fakeRequest: FakeRequest[JsValue] = FakeRequest("POST", "/check/insights")
    .withHeaders(
      HeaderNames.USER_AGENT -> "test-user-agent",
      Constants.xCorrelationId -> "some-correlation-id-from-upstream"
    )
    .withBody(Json.toJson(testInsightsRequest))

  "getInsights" when :

    "client is authorised" should :

      "return 200 OK with insights response for a valid request" in :

        MockInsightsService.insights(testInsightsRequest)(Future.successful(testInsightsResponse))

        val result: Future[Result] = authorisedController.getInsights()(fakeRequest)

        status(result)        shouldBe Status.OK
        contentAsJson(result) shouldBe Json.toJson(testInsightsResponse)

      s"return a 200 OK with insights response for a valid request with no provided ${Constants.xCorrelationId} header" in {

        val requestWithoutCorrelationId: FakeRequest[JsValue] = FakeRequest("POST", "/check/insights")
          .withHeaders(
            HeaderNames.USER_AGENT -> "test-user-agent"
          ).withBody(Json.toJson(testInsightsRequest))

        MockInsightsService.insights(testInsightsRequest)(Future.successful(testInsightsResponse))
        MockUUIDGenerator.generate("generated-correlation-id")

        val result: Future[Result] = authorisedController.getInsights()(requestWithoutCorrelationId)

        status(result)        shouldBe Status.OK
        contentAsJson(result) shouldBe Json.toJson(testInsightsResponse)
      }

      "return 503 ServiceUnavailable for upstream error" in :

        MockInsightsService.insights(testInsightsRequest)(
          Future.failed(
            new UpstreamErrorResponse(
              "Service unavailable",
              Status.SERVICE_UNAVAILABLE,
              Status.SERVICE_UNAVAILABLE,
              Map()
            )
          )
        )

        val result: Future[Result] = authorisedController.getInsights()(fakeRequest)

        status(result) shouldBe SERVICE_UNAVAILABLE
        (contentAsJson(result) \ "message").as[String] shouldBe "Service unavailable"

      "return 500 InternalServerError for unexpected error" in :

        MockInsightsService.insights(testInsightsRequest)(
          Future.failed(
            new Exception("Unexpected error")
          )
        )

        val result: Future[Result] = authorisedController.getInsights()(fakeRequest)


        status(result) shouldBe INTERNAL_SERVER_ERROR
        (contentAsJson(result) \ "message").as[String] shouldBe "Unexpected error"

    "client is not authorised" should :

      "return 403 Forbidden" in :

        val result: Future[Result] = unauthorisedController.getInsights()(fakeRequest)

        status(result) shouldBe Status.FORBIDDEN


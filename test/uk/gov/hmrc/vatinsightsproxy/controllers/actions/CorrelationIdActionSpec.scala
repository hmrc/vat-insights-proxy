/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.vatinsightsproxy.controllers.actions

import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.vatinsightsproxy.BaseSpec
import uk.gov.hmrc.vatinsightsproxy.config.Constants
import uk.gov.hmrc.vatinsightsproxy.models.RequestWithCorrelationId
import uk.gov.hmrc.vatinsightsproxy.utils.MockUUIDGenerator

import scala.concurrent.ExecutionContext

class CorrelationIdActionSpec extends BaseSpec with MockUUIDGenerator :

  private val action = new CorrelationIdActionImpl(mockUUIDGenerator)(ExecutionContext.global)

  def fakeRequest(correlationId: Option[String]): FakeRequest[JsValue] =
    FakeRequest("POST", "/check/insights")
      .withHeaders(Seq(correlationId.map(id => Constants.xCorrelationId -> id)).flatten: _*)
      .withBody(Json.obj())

  val testUpstreamCorrelationId = "some-correlation-from-upstream"
  val testGeneratedCorrelationId = "some-generated-correlation"

  "CorrelationIdAction" should :

    "add correlationId to the request when provided" in :
      val request = fakeRequest(Some(testUpstreamCorrelationId))

      await(action.refine(request)) shouldBe
        Right(RequestWithCorrelationId(testUpstreamCorrelationId, request))

    "generate a new correlationId when provided correlationId is blank" in :
      val request = fakeRequest(None)

      MockUUIDGenerator.generate(testGeneratedCorrelationId)

      await(action.refine(request)) shouldBe
        Right(RequestWithCorrelationId(testGeneratedCorrelationId, request))

    
  


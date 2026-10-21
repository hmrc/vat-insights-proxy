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

package uk.gov.hmrc.vatinsightsproxy.services

import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.BadGatewayException
import uk.gov.hmrc.vatinsightsproxy.BaseSpec
import uk.gov.hmrc.vatinsightsproxy.connectors.MockCipRiskConnector

import scala.concurrent.Future

class InsightsServiceSpec extends BaseSpec with MockCipRiskConnector :

  val insightsService = new InsightsService(mockCipRiskConnector)

  ".insights()" should :

      "return an InsightsResponse" when :

        "the cip-risk call is successful" in :

          MockCipRiskConnector.getStrategicRiskScore(testInsightsRequest)(Future.successful(testStrategicRiskResponse))

          val result = await(insightsService.insights(testInsightsRequest))
          result shouldBe testInsightsResponse

      "raise an exception" when :

        "the cip-risk call is unsuccessful" in :

          MockCipRiskConnector.getStrategicRiskScore(testInsightsRequest)(
            Future.failed(new BadGatewayException("CIP Risk API failure"))
          )

          val result = intercept[BadGatewayException](await(insightsService.insights(testInsightsRequest)))
          result.message should include("CIP Risk API failure")

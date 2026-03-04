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

package uk.gov.hmrc.vatinsightsproxy.connectors


import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.Helpers.*
import uk.gov.hmrc.http.UpstreamErrorResponse
import uk.gov.hmrc.vatinsightsproxy.BaseISpec
import uk.gov.hmrc.vatinsightsproxy.stubs.CipRiskStub

class CipRiskConnectorISpec extends BaseISpec with CipRiskStub :

  private lazy val connector: CipRiskConnector = app.injector.instanceOf[CipRiskConnector]

  "CipRiskConnector" when :

    "downstream connector is successful" should :

      "respond with a response" in :

        CipRisk.success(Status.OK, Json.toJson(testStrategicRiskResponse))

        val result = connector.getStrategicRiskScore(testInsightsRequest)

        result.futureValue shouldBe testStrategicRiskResponse

    "downstream connector is unsuccessful" should:

      "respond with a BadGatewayException" in :

        CipRisk.success(Status.BAD_GATEWAY, Json.obj())

        val result = intercept[UpstreamErrorResponse](await(connector.getStrategicRiskScore(testInsightsRequest)))

        result.statusCode shouldBe Status.BAD_GATEWAY

      "respond with a failure" in :

        CipRisk.failure()

        val result = intercept[Throwable](await(connector.getStrategicRiskScore(testInsightsRequest)))

        result.getMessage should include("Connection reset")

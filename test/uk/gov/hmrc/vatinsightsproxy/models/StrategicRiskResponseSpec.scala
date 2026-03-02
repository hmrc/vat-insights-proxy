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

package uk.gov.hmrc.vatinsightsproxy.models

import play.api.libs.json.Json
import uk.gov.hmrc.vatinsightsproxy.BaseSpec

class StrategicRiskResponseSpec extends BaseSpec :

  "StrategicRiskResponse" should :

    "serialize to JSON correctly" in :
      val response = StrategicRiskResponse(
        riskCorrelationId = "correlation-id",
        riskScore = 85.5,
        reasons = List("Reason 1", "Reason 2"),
        riskData = Seq(StrategicRiskData(hops = Some(3), avgHops = Some(1.5)))
      )

      val expectedJson = Json.parse("""{
        |  "graphData": {
        |    "reasons": ["Reason 1", "Reason 2"],
        |    "hops": 3,
        |    "avgHops": 1.5
        |  }
        |}""".stripMargin)

      Json.toJson(response)(StrategicRiskResponse.responseWrites) shouldBe expectedJson

    "serialize to JSON with empty riskData" in :
      val response = StrategicRiskResponse(
        riskCorrelationId = "correlation-id",
        riskScore = 85.5,
        reasons = List("Reason 1", "Reason 2"),
        riskData = Seq.empty
      )

      val expectedJson = Json.parse("""{
        |  "graphData": {
        |    "reasons": ["Reason 1", "Reason 2"]
        |  }
        |}""".stripMargin)

      Json.toJson(response)(StrategicRiskResponse.responseWrites) shouldBe expectedJson

    "serialize to JSON with missing optional fields in riskData" in :
      val response = StrategicRiskResponse(
        riskCorrelationId = "correlation-id",
        riskScore = 85.5,
        reasons = List("Reason 1", "Reason 2"),
        riskData = Seq(StrategicRiskData(hops = None, avgHops = None))
      )

      val expectedJson = Json.parse("""{
        |  "graphData": {
        |    "reasons": ["Reason 1", "Reason 2"]
        |  }
        |}""".stripMargin)

      Json.toJson(response)(StrategicRiskResponse.responseWrites) shouldBe expectedJson

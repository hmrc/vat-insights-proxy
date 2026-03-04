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

class StrategicRiskDataSpec extends BaseSpec :

  "StrategicRiskData" should :

    "serialize to JSON correctly" in :
      val data = StrategicRiskData(hops = Some(5), avgHops = Some(2.5))
      val expectedJson = Json.parse("""{
        |  "hops": 5,
        |  "avgHops": 2.5
        |}""".stripMargin)

      Json.toJson(data) shouldBe expectedJson

    "deserialize from JSON correctly" in :
      val json = Json.parse("""{
        |  "hops": 5,
        |  "avgHops": 2.5
        |}""".stripMargin)
      val expectedData = StrategicRiskData(hops = Some(5), avgHops = Some(2.5))

      json.as[StrategicRiskData] shouldBe expectedData

    "handle missing optional fields during deserialization" in :
      val json = Json.parse("""{
        |}""".stripMargin)
      val expectedData = StrategicRiskData(hops = None, avgHops = None)

      json.as[StrategicRiskData] shouldBe expectedData

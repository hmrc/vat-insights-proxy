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

class InsightsRequestSpec extends BaseSpec :

  "InsightsRequest" should :

    "serialize to JSON correctly" in :
      val request = InsightsRequest(vatRegistrationNumber = "123456789")
      val expectedJson = Json.parse("""{
        |  "vatRegistrationNumber": "123456789"
        |}""".stripMargin)

      Json.toJson(request) shouldBe expectedJson

    "deserialize from JSON correctly" in :
      val json = Json.parse("""{
        |  "vatRegistrationNumber": "123456789"
        |}""".stripMargin)
      val expectedRequest = InsightsRequest(vatRegistrationNumber = "123456789")

      json.as[InsightsRequest] shouldBe expectedRequest

    "handle empty vatRegistrationNumber during deserialization" in :
      val json = Json.parse("""{
        |  "vatRegistrationNumber": ""
        |}""".stripMargin)
      val expectedRequest = InsightsRequest(vatRegistrationNumber = "")

      json.as[InsightsRequest] shouldBe expectedRequest

    "handle missing vatRegistrationNumber during deserialization" in :
      val json = Json.parse("""{
        |}""".stripMargin)

      an[Exception] should be thrownBy json.as[InsightsRequest]
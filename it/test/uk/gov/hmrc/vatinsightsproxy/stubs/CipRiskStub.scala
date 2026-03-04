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

package uk.gov.hmrc.vatinsightsproxy.stubs

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.http.Fault
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.vatinsightsproxy.BaseISpec

trait CipRiskStub :
  this: BaseISpec =>

  object CipRisk:

    def success(status: Int, jsValue: JsValue): StubMapping =
      wireMockServer.stubFor(
        post("/str/risk/insights")
          .willReturn(
            aResponse()
              .withStatus(status)
              .withHeader("Content-Type", "application/json")
              .withBody(Json.stringify(jsValue))
          )
      )

    def failure(): StubMapping =
      wireMockServer.stubFor(
        post("/str/risk/insights")
          .willReturn(
            aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)
          )
      )

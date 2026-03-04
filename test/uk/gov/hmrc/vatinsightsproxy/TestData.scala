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

package uk.gov.hmrc.vatinsightsproxy

import uk.gov.hmrc.vatinsightsproxy.models.{InsightsRequest, InsightsResponse, StrategicRiskResponse}

trait TestData {

  val testVatRegistrationNumber = "GB123456789"

  val testStrategicRiskResponse: StrategicRiskResponse = StrategicRiskResponse(
    riskCorrelationId = "11111111-1111-1111-1111-111111111111",
    riskScore = 0,
    reasons = List.empty,
    riskData = Seq.empty
  )

  val testInsightsRequest: InsightsRequest = InsightsRequest(testVatRegistrationNumber)

  val testInsightsResponse = InsightsResponse(
    request = testInsightsRequest,
    strategicRiskResponse = testStrategicRiskResponse
  )

}

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

import org.scalamock.handlers.CallHandler2
import org.scalamock.scalatest.MockFactory
import org.scalatest.TestSuite
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.vatinsightsproxy.models.{InsightsRequest, InsightsResponse}

import scala.concurrent.Future

trait MockInsightsService extends MockFactory { this: TestSuite =>

  lazy val mockInsightsService: InsightsService = mock[InsightsService]

  object MockInsightsService :

    def insights(
      request: InsightsRequest
    )(response: Future[InsightsResponse]): CallHandler2[InsightsRequest, HeaderCarrier, Future[InsightsResponse]] =
      (mockInsightsService
        .insights(_: InsightsRequest)(_: HeaderCarrier))
        .expects(*, *)
        .returning(response)
  }


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

import com.google.inject.Inject
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.AuditExtensions.auditHeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.audit.model.ExtendedDataEvent
import uk.gov.hmrc.vatinsightsproxy.config.AppConfig
import uk.gov.hmrc.vatinsightsproxy.connectors.CipRiskConnector
import uk.gov.hmrc.vatinsightsproxy.models.{InsightsRequest, InsightsResponse, StrategicRiskResponse}

import scala.concurrent.{ExecutionContext, Future}

class InsightsService @Inject(
                               appConfig: AppConfig,
                               cipRiskConnector: CipRiskConnector,
                               auditConnector: AuditConnector
                             )(implicit ec: ExecutionContext) :

  def insights(request: InsightsRequest)(implicit hc: HeaderCarrier): Future[InsightsResponse] = {
    cipRiskConnector.getStrategicRiskScore(request).map { strategicRiskResponse =>
      val response = InsightsResponse(request, strategicRiskResponse)

      auditConnector.sendExtendedEvent(
        ExtendedDataEvent(
          auditSource = appConfig.appName,
          auditType = "VatRegistrationNumberInsightsRisk",
          tags = hc.appendToDefaultTags(hc.extraHeaders.toMap),
          detail = Json.toJson(response)
        )
      )

      response
    }
  }

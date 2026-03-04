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

import play.api.Logging
import play.api.http.HeaderNames
import play.api.libs.json.Json
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, StringContextOps, UpstreamErrorResponse}
import uk.gov.hmrc.vatinsightsproxy.config.{AppConfig, Constants}
import uk.gov.hmrc.vatinsightsproxy.models.{InsightsRequest, StrategicRiskResponse}
import uk.gov.hmrc.vatinsightsproxy.services.MetricsService

import java.util.Base64
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CipRiskConnector @Inject()(
  httpClient: HttpClientV2,
  appConfig: AppConfig,
  metricService: MetricsService)(implicit ec: ExecutionContext) extends Logging :

  private def cipRiskBasicAuthToken: String = "Basic " +
    Base64.getEncoder.encodeToString(s"${appConfig.appName}:${appConfig.cipRiskBasicAuthToken}".getBytes)

  private[connectors] def cipRiskHeaders()(implicit hc: HeaderCarrier): Seq[(String, String)] =
    hc.headers(Seq(Constants.xCorrelationId)) :+
      HeaderNames.AUTHORIZATION -> cipRiskBasicAuthToken

  def getStrategicRiskScore(request: InsightsRequest)(implicit hc: HeaderCarrier): Future[StrategicRiskResponse] = {

    val strUrl = url"${appConfig.cipRiskBaseUrl}/str/risk/insights"

    metricService.withTimer("CipRiskConnector.getStrategicRiskScore") {
      httpClient
        .post(strUrl)
        .withBody(Json.toJson(request))
        .setHeader(cipRiskHeaders(): _*)
        .execute[StrategicRiskResponse]
        .recover {
          case e: UpstreamErrorResponse =>
            logger.warn(s"[getStrategicRiskScore] Failed with UpstreamErrorResponse, message: '${e.message}'")
            throw e
          case t: Throwable             =>
            logger.warn(
              s"[getStrategicRiskScore] Exception of type ${t.getClass.getSimpleName.stripSuffix("$")}, with message: '${t.getMessage}'"
            )
            throw t
        }
    }
  }


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

package uk.gov.hmrc.vatinsightsproxy.controllers

import com.google.inject.Inject
import play.api.Logging
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.http.{HeaderCarrier, UpstreamErrorResponse}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.vatinsightsproxy.config.Constants
import uk.gov.hmrc.vatinsightsproxy.controllers.actions.{AllowListAction, CorrelationIdAction}
import uk.gov.hmrc.vatinsightsproxy.models.{DownstreamError, Error, InsightsRequest}
import uk.gov.hmrc.vatinsightsproxy.services.InsightsService

import scala.concurrent.ExecutionContext

class InsightsController @Inject()(
                                    cc: ControllerComponents,
                                    allowListAction: AllowListAction,
                                    withCorrelationId: CorrelationIdAction,
                                    insightsService: InsightsService
                                  )(implicit ec: ExecutionContext) extends BackendController(cc) with Logging :

  private val authAction = allowListAction() andThen withCorrelationId

  def getInsights: Action[JsValue] = authAction.async { implicit request =>

    implicit val insightsHeaderCarrier: HeaderCarrier = hc
      .withExtraHeaders(
        Constants.xCorrelationId -> request.correlationId
      )

    logger.debug(s"[getInsights] Received request for VAT insights with body: ${request.body}")

    withJsonBody[InsightsRequest] {
      insightsService
        .insights(_)
        .map { insightsResponse =>
          logger.debug(s"[getInsights] Insights Response: ${Json.toJson(insightsResponse)}")
          Ok(Json.toJson(insightsResponse))
        }
        .recover {
          case e: UpstreamErrorResponse =>
            logger.warn(s"[getInsights] Error in called service when processing insights request: ${e.message}")
            ServiceUnavailable(Json.toJson[Error](DownstreamError(e.message)))
          case t: Throwable =>
            logger.error(s"[getInsights] An unexpected error occurred while processing insights request: ${t.getMessage}")
            InternalServerError(Json.toJson(new Error(t.getClass.getSimpleName.stripSuffix("$"), t.getMessage)))
        }

    }
  }

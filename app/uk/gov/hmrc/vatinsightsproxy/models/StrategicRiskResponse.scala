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

import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json.{Format, Json, Reads, Writes}

case class StrategicRiskResponse(
  riskCorrelationId: String,
  riskScore: Double,
  reasons: List[String],
  riskData: Seq[StrategicRiskData]
)

object StrategicRiskResponse :
  implicit val fmt: Format[StrategicRiskResponse] = Json.format[StrategicRiskResponse]

  val responseWrites: Writes[StrategicRiskResponse] = Writes { model =>
    Json.obj(
      "graphData" -> Json.obj(
        Seq[Option[(String, JsValueWrapper)]](
          Some("reasons" -> Json.toJson(model.reasons)),
          model.riskData.headOption.flatMap(_.hops.map("hops" -> _)),
          model.riskData.headOption.flatMap(_.avgHops.map("avgHops" -> _))
        ).flatten: _*
      )
    )
  }


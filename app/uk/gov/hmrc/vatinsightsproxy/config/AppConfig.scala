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

package uk.gov.hmrc.vatinsightsproxy.config

import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}


trait AppConfig :
  def appName: String

  //Access Control config
  def allowListEnabled: Boolean
  def allowedClients: Set[String]
  def accessRequestFormUrl: String
  
  def cipRiskBaseUrl: String
  def cipRiskBasicAuthToken: String

@Singleton
class AppConfigImpl @Inject()(config: Configuration, servicesConfig: ServicesConfig) extends AppConfig :

  val appName: String = config.get[String]("appName")

  //Access Control config
  val allowListEnabled: Boolean = config.get[Boolean]("microservice.services.access-control.enabled")
  val allowedClients: Set[String] = config.get[Seq[String]]("microservice.services.access-control.allow-list").toSet
  val accessRequestFormUrl: String = config.get[String]("microservice.services.access-control.request.formUrl")

  //CIP Risk Insights config:
  val cipRiskBaseUrl: String = servicesConfig.baseUrl("cip-risk")
  val cipRiskBasicAuthToken: String = config.get[String]("microservice.services.cip-risk.basicAuthToken")
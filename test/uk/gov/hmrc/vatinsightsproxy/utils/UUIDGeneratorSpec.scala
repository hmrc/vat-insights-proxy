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

package uk.gov.hmrc.vatinsightsproxy.utils

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class UUIDGeneratorSpec extends AnyWordSpec with Matchers {

  "UUIDGeneratorImpl" should {

    "generate a valid UUID string" in {
      val generator = new UUIDGeneratorImpl()
      val uuid = generator.generate()

      uuid should not be null
    }

    "generate unique UUIDs for multiple calls" in {
      val generator = new UUIDGeneratorImpl()
      val uuid1 = generator.generate()
      val uuid2 = generator.generate()

      uuid1 should not equal uuid2
    }
  }
}

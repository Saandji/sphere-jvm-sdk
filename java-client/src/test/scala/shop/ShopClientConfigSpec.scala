package io.sphere.client
package shop

import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers

class TestSpec extends WordSpec with MustMatchers {
  "Constructing immutable instances using a nested static builder" in {
    val config = new ShopClientConfig.Builder("projectKey", "clientId", "clientSecret")
      .setCoreHttpServiceUrl("http://localhost:1234")
      .setAuthHttpServiceUrl("http://localhost:4321")
      .build

    config.getCoreHttpServiceUrl must equal ("http://localhost:1234")
    config.getAuthHttpServiceUrl must equal ("http://localhost:4321")
  }
}

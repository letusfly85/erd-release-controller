package com.jellyfish85.erd.release.controller.utils

import org.apache.commons.configuration.PropertiesConfiguration
import java.io.InputStream
import org.apache.commons.lang.StringUtils

/**
 * == ErdReleaseProp ==
 *
 *
 */
class ErdReleaseProp(environment: String) {

  val inputStream: InputStream =
    getClass.getResourceAsStream("/com/jellyfish85/erd/release/controller/define/erd.release.control.properties")

  val configuration: PropertiesConfiguration =
    new PropertiesConfiguration()

  configuration.load(inputStream, "UTF8")

  var envName: String = _
  val envKVS: java.util.Iterator[String] = configuration.getKeys("runtime.environment")
  while (envKVS.hasNext) {
    val key: String = envKVS.next()
    if (key.replaceAll("runtime.environment.", "").equals(environment)) {
      envName = configuration.getString(key)
    }
  }
  if (StringUtils.isBlank(envName)) {
    throw new RuntimeException("there is no key for environment")
  }

  val erdSchemaNameAdminUnitTest: String           = configuration.getString("erd.schema.name.admin.unit-test")

}

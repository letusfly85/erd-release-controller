package com.jellyfish85.erd.release.controller.utils

import org.apache.commons.configuration.{PropertiesConfiguration}
import java.io.InputStream

/**
 * == ErdReleaseProp ==
 *
 *
 */
class ErdReleaseProp() {

  val inputStream: InputStream =
    getClass.getResourceAsStream("/com/jellyfish85/erd/release/controller/define/erd.release.control.properties")

  val configuration: PropertiesConfiguration =
    new PropertiesConfiguration()

  configuration.load(inputStream, "UTF8")

}

package components

import controllers.domain._
import controllers.domain.admin._
import models.domain.admin._

trait ConfigHolder { this: APIAccessor =>

  /**
   *  Name of config section we use to store our configuration.
   */
  protected def defaultSectionName: String

  /**
   * Default configuration if empty or should be reset.
   */
  protected def defaultConfiguration: Map[String, ConfigSection]


  /**
   * Read values from configuration.
   */
  def config: ConfigSection = configNamed(defaultSectionName)

  /**
   * Read values from configuration.
   */
  def configNamed(sectionName: String): ConfigSection = {
    api.getConfigSection(GetConfigSectionRequest(sectionName)) match {
      case OkApiResult(GetConfigSectionResult(Some(c: ConfigSection))) =>
        if (c.values.keySet == defaultConfiguration(sectionName).values.keySet)
          c
        else {
          val updatedConfigSection = updateConfigSectionWithDefaults(sectionName, c)
          updateConfig(updatedConfigSection)
          updatedConfigSection
        }
      case _ =>
        resetConfigSection(sectionName)
        defaultConfiguration(sectionName)
    }
  }

  /**
   * Updates a field in configuration section.
   */
  def updateConfig(field: (String, String), sectionName: String = defaultSectionName): Unit = {
    api.setConfigSection(SetConfigSectionRequest(configNamed(sectionName).copy(values = config.values + field)))
  }

  /**
   * Updates the whole configuration section.
   */
  def updateConfig(configSection: ConfigSection): Unit = {
    api.setConfigSection(SetConfigSectionRequest(configSection))
  }

  /**
   * Resets component's configuration to default one.
   */
  private def resetConfigSection(sectionName: String): Unit = {
    api.setConfigSection(SetConfigSectionRequest(defaultConfiguration(sectionName)))
  }

  /**
   * @return
   */
  private def updateConfigSectionWithDefaults(sectionName: String, configSection: ConfigSection): ConfigSection = {
    val defaultSectionMap = defaultConfiguration(sectionName).values

    val inBoth = configSection.values.filterKeys(defaultSectionMap.contains)

    defaultConfiguration(sectionName).copy(values = defaultSectionMap ++ inBoth)
  }
}


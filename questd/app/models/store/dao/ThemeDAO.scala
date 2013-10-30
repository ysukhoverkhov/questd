package models.store.dao

import models.domain.theme._

private[store] trait ThemeDAO {

  def createTheme(u: Theme): Unit
  def readThemeByID(u: Theme): Option[Theme]
  def updateTheme(u: Theme): Unit
  def deleteTheme(u: Theme): Unit
  def allThemes: List[Theme]

}


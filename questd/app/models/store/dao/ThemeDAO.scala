package models.store.dao

import models.domain.theme._

private[store] trait ThemeDAO {

  def createTheme(o: Theme): Unit
  def readThemeByID(key: ThemeID): Option[Theme]
  def updateTheme(o: Theme): Unit
  def deleteTheme(key: ThemeID): Unit
  def allThemes: List[Theme]

}


package models.domain

package object stubCreators {

  import models.domain.base.ID
  import models.domain._

  def createThemeStub(id: String = ID.generateUUID, name: String = "name", desc: String = "desc") = {
    Theme(
      id = id,
      info = ThemeInfo(
        media = ContentReference(
          contentType = ContentType.Photo,
          storage = "",
          reference = ""),
        name = name,
        description = desc))
  }

}


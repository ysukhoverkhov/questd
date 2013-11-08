package models.domain

case class QuestProposalConext (
    val purchasedTheme: Option[Theme] = None,
    val selectedTheme: Option[Theme] = None,
    val numberOfPurchasedThemes: Int = 0)
    

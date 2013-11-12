package models.domain

/**
 * This can be given to client as is, thus contains only public information.
 */
case class Profile(
    level: Int = 1,
    assets: Assets = Assets(0, 0, 0),
    rights: Rights = Rights())


/**
 * What does user can do an what level.
 */
case class Rights(
    voteQuestResults: Int = 1,
    submitPhotoResults: Int = 3,
    submitVideoResults: Int = 4,
    report: Int = 5,
    inviteFriends: Int = 6,
    addToShortList: Int = 8,
    voteQuestProposals: Int = 10,
    submitPhotoQuests: Int = 12,
    submitVideoQuests: Int = 13,
    voteReviews: Int = 14,
    submitReviewsForResults: Int = 16,
    submitReviewsForProposals: Int = 18,
    giveRewards: Int = 20
    )
    

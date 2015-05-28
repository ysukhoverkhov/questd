

db.users.find().forEach(
    function(doc) {
        doc.profile.rights.unlockedFunctionality = [ "VoteQuests", "VoteSolutions", "VoteBattles", "SubmitPhotoSolutions", "SubmitVideoSolutions", "Report", "InviteFriends", "AddToFollowing", "SubmitPhotoQuests", "SubmitVideoQuests", "VoteReviews", "SubmitReviewsForSolutions", "SubmitReviewsForQuests", "GiveRewards"];
        db.users.save(doc);
   }
)




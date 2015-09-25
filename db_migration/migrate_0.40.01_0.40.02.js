

db.users.find().forEach(
    function(doc) {
        doc.profile.rights.unlockedFunctionality = [ "VoteQuestSolutions" , "SubmitPhotoSolutions" , "SubmitVideoSolutions" , "Report" , "InviteFriends" , "AddToFollowing" , "VoteQuests" , "SubmitPhotoQuests" , "SubmitVideoQuests" , "VoteReviews" , "GiveRewards"];
        db.users.save(doc);
   }
)

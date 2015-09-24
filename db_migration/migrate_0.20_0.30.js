
db.solutions.find().forEach(
    function(doc) {
        doc.status = "WaitingForCompetitor";
        db.solutions.save(doc);
   }
)


db.quests.find({"info.solveCost" : {$exists : false}}).forEach(
    function(doc) {
        doc.info.solveCost 		= { "coins" : NumberInt("10") ,		"money" : NumberInt("0") , "rating" : NumberInt("0")};
        doc.info.solveRewardWon		= { "coins" : NumberInt("0") ,		"money" : NumberInt("0") , "rating" : NumberInt("100")};
        doc.info.solveRewardLost	= { "coins" : NumberInt("0") ,		"money" : NumberInt("0") , "rating" : NumberInt("20")};

        db.quests.save(doc);
   }
)


db.quests.find().forEach(
    function(doc) {
        doc.status = "InRotation";
        db.quests.save(doc);
   }
)


db.users.find().forEach(
    function(doc) {
        doc.profile.rights.unlockedFunctionality = [ "VoteQuestSolutions" , "SubmitPhotoResults" , "SubmitVideoResults" , "Report" , "InviteFriends" , "AddToFollowing" , "VoteQuests" , "SubmitPhotoQuests" , "SubmitVideoQuests" , "VoteReviews" , "SubmitReviewsForResults" , "SubmitReviewsForProposals" , "GiveRewards"];
        db.users.save(doc);
   }
)


db.users.find().forEach(
    function(doc) {
	delete doc.profile.dailyTasks;
	delete doc.profile.dailyResults;
	delete doc.privateDailyResults;
        db.users.save(doc);
   }
)



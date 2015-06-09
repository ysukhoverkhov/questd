
db.users.find().forEach(
    function(doc) {
        doc.profile.rights.unlockedFunctionality = [ "VoteQuests", "VoteSolutions", "VoteBattles", "SubmitPhotoSolutions", "SubmitVideoSolutions", "Report", "InviteFriends", "AddToFollowing", "SubmitPhotoQuests", "SubmitVideoQuests", "VoteReviews", "SubmitReviewsForSolutions", "SubmitReviewsForQuests", "GiveRewards"];
        db.users.save(doc);
   }
)

db.quests.find({"info.solveReward" : {$exists : false}}).forEach(
    function(doc) {
	doc.info.solveReward = doc.info.solveRewardLost;

        delete doc.info.solveRewardLost;
        delete doc.info.solveRewardWon;

        db.quests.save(doc);
   }
)

db.quests.find({"info.victoryReward" : {$exists : false}}).forEach(
    function(doc) {
	doc.info.victoryReward = doc.info.solveReward;
	doc.info.defeatReward = doc.info.solveReward;

        db.quests.save(doc);
   }
)

db.solutions.find().forEach(
    function(doc) {
        doc.status = "InRotation";
        db.solutions.save(doc);
   }
)

db.battles.find({"info.victoryReward" : {$exists : false}}).forEach(
    function(doc) {
	doc.info.victoryReward = { "coins" : NumberInt("10") ,		"money" : NumberInt("0") , "rating" : NumberInt("0")};
	doc.info.defeatReward =  { "coins" : NumberInt("10") ,		"money" : NumberInt("0") , "rating" : NumberInt("0")};

        db.battles.save(doc);
   }
)

db.battles.find({"info.questId" : {$exists : false}}).forEach(
    function(doc) {
	doc.info.questId = db.quests.findOne().id;

        db.battles.save(doc);
   }
)

db.battles.find({"info.battleSides" : {$exists : false}}).forEach(
    function(doc) {
	doc.info.battleSides = [ { "solutionId" : doc.info.solutionIds[0], "authorId" : doc.info.authorIds[0], "isWinner" : true }, { "solutionId" : doc.info.solutionIds[1], "authorId" : doc.info.authorIds[1], "isWinner" : true } ]

        db.battles.save(doc);
   }
)

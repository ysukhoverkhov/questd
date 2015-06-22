
db.users.find().forEach(
    function(doc) {

        delete doc.profile.dailyTasks;
        delete doc.profile.tutorialStates;

        db.users.save(doc);
   }
)


db.users.find().forEach(
    function(doc) {

	doc.profile.assets = { "coins" : NumberInt("0"), "money" : NumberInt("0") , "rating" : NumberInt("0")};
	doc.profile.publicProfile.level = "1";
        doc.profile.rights.unlockedFunctionality = [ "VoteQuests", "VoteSolutions", "VoteBattles", "VoteReviews", "AddToFollowing"];

        db.users.save(doc);
   }
)

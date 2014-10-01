db.themes.find({cultureId : {$exists : false}}).forEach(
    function(doc) {
        doc.cultureId = "68349b7a-20ee-4f6e-8406-f468b30be783";
        db.themes.save(doc);
   }
)

db.themes.find({info : {$exists : false}}).forEach(
    function(doc) {
        var ti = {};
        ti.name = doc.text;
        ti.description = doc.comment;
        ti.media = doc.icon;
        doc.info = ti;

        // deletes the previous value
        delete doc.icon;
        delete doc.text;
        delete doc.comment;

        db.themes.save(doc);
   }
)

db.quests.find({cultureId : {$exists : false}}).forEach(
    function(doc) {
        doc.cultureId = "68349b7a-20ee-4f6e-8406-f468b30be783";
        db.quests.save(doc);
   }
)

db.quests.find({"info.authorId" : {$exists : false}}).forEach(
    function(doc) {
        doc.info.authorId = doc.authorUserId;

        delete doc.authorUserId;

        db.quests.save(doc);
   }
)

db.solutions.find({cultureId : {$exists : false}}).forEach(
    function(doc) {
        doc.cultureId = "68349b7a-20ee-4f6e-8406-f468b30be783";
        db.solutions.save(doc);
   }
)

db.solutions.find({"info.authorId" : {$exists : false}}).forEach(
    function(doc) {
        doc.info.authorId = doc.userId;

        delete doc.userId;

        db.solutions.save(doc);
   }
)

db.users.find().forEach(
    function(doc) {
        delete doc.profile.questProposalContext;
        delete doc.profile.questSolutionContext;
        delete doc.profile.questProposalVoteContext;
        delete doc.profile.questSolutionVoteContext;
	delete doc.profile.dailyResults;
        delete doc.privateDailyResults;

        db.users.save(doc);
   }
)

db.users.find().forEach(
    function(doc) {
        doc.auth.snids.FB = doc.auth.fbid;

        delete doc.auth.fbid;
        delete doc.auth.fbtoken;

        db.users.save(doc);
   }
)

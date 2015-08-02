db.users.find().forEach(
    function(doc) {

        delete doc.profile.dailyTasks;

	doc.profile.rights.unlockedFunctionality = [];

        db.users.save(doc);
   }
)

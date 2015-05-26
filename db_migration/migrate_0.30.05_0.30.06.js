

db.users.find().forEach(
    function(doc) {
	delete doc.profile.dailyTasks;
        db.users.save(doc);
   }
)



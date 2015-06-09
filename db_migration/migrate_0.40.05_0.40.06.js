
db.users.find().forEach(
    function(doc) {

        delete doc.profile.dailyTasks;
        delete doc.profile.tutorialStates;

        db.users.save(doc);
   }
)


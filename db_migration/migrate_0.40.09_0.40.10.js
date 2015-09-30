db.quests.find({"status":"OldBanned"}).forEach(
    function(doc) {
	doc.status = "AdminBanned";

        db.quests.save(doc);
   }
)

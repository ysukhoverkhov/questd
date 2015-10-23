

db.quests.find().forEach(
    function(doc) {
        doc.info.level 		= NumberInt("2");

        db.quests.save(doc);
   }
)


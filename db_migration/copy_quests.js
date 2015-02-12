db.quests.find().forEach(
    function(doc) {
        doc.cultureId = "d406dc6d-7a84-4ef9-9c64-5e725e8df608";
        doc.id = doc.id + "_ru";
        doc._id = new ObjectId();
        db.quests.insert(doc);
   }
)


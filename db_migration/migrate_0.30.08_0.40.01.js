

db.users.find().forEach(
    function(doc) {
	delete doc.messages;
        db.users.save(doc);
   }
)


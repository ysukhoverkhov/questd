
db.users.find({"auth.loginMethods" : {$exists : false}}).forEach(
    function(doc) {
	doc.auth.loginMethods = [{
		"methodName" : "FB",
		"userId" : doc.auth.snids.FB
	}];

        delete doc.auth.snids;

        db.users.save(doc);
   }
)

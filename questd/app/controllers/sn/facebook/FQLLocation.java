package controllers.sn.facebook;

import com.restfb.Facebook;
import com.restfb.types.Location;

/// Location of user generated with FQL
class FQLLocation {
	@Facebook
	Location current_location;
}


<!DOCTYPE html>
<html>
<head>
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>
<script type="text/javascript">
	var redirectUri = '${redirect_uri}';
	var accountLinkingToken = '${account_linking_token}';
	var subscriptionTrackingToken = '${subscriptionTrackingToken}';
	var subscribeRedirectUrl = '${subscribeRedirectUrl}';
	var appPath = (location.origin.indexOf("localhost") >= 0) ? "/fbia/login"
			: "/login";
	var returnUrl = location.origin + appPath + "?account_linking_token="
			+ accountLinkingToken + "&redirect_uri=" + redirectUri;
	var redirectUrl = subscribeRedirectUrl + "&return="
			+ encodeURIComponent(returnUrl);
	// Remove encodeURIComponent() here if not required. 
	console.log(redirectUrl);
	$(location).attr('href', redirectUrl);
</script>
</head>
<body></body>
</html>
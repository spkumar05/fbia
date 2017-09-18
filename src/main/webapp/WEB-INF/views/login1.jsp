<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta/css/bootstrap.min.css"
	integrity="sha384-/Y6pD6FV/Vv2HJnA6t+vslU6fwYXjCFtcEpHbNJ0lyAFsXTsjBbfaDjzALeQsN6M"
	crossorigin="anonymous">
</head>
<body>
	<div class="container" style="padding-top: 50px;">
		<form>
			<div class="form-group">
				<label for="emailAddress">Email address</label> <input type="text"
					class="form-control" id="emailAddress" name="emailAddress"
					aria-describedby="emailHelp" placeholder="Enter email"> <small
					id="emailHelp" class="form-text text-muted">We'll never
					share your email with anyone else.</small>
			</div>
			<div class="form-group">
				<label for="password">Password</label> <input type="password"
					class="form-control" id="password" name="password"
					placeholder="Password">
			</div>
			<button type="button" class="btn btn-primary" id="loginBtn">Login</button>
		</form>

		<p>${redirect_uri}</p>

		<p>${account_linking_token}</p>
	</div>
	<script src="https://code.jquery.com/jquery-3.2.1.min.js"
		integrity="sha256-hwg4gsxgFZhOsEEamdOYGBf13FyQuiTwlAQgxVSNgt4="
		crossorigin="anonymous"></script>
	<script
		src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.11.0/umd/popper.min.js"
		integrity="sha384-b/U6ypiBEHpOf/4+1nzFpr53nxSS+GLCkfwBdFNTxtclqqenISfwAzpKaMNFNmj4"
		crossorigin="anonymous"></script>
	<script
		src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta/js/bootstrap.min.js"
		integrity="sha384-h0AbiXch4ZDo7tp9hKZ4TsHbi047NrKGLO3SEJAg45jXxnGIfYzk4Si90RDIqNm1"
		crossorigin="anonymous"></script>

	<script type="text/javascript">
		function readCookie(cookieName) {
			var theCookie = " " + document.cookie;
			var ind = theCookie.indexOf(" " + cookieName + "=");
			if (ind == -1)
				ind = theCookie.indexOf(";" + cookieName + "=");
			if (ind == -1 || cookieName == "")
				return "";
			var ind1 = theCookie.indexOf(";", ind + 1);
			if (ind1 == -1)
				ind1 = theCookie.length;
			return unescape(theCookie.substring(ind + cookieName.length + 2,
					ind1));
		}

		function ServiceWrapper() {
			this.get = function(url, args, Onsuccess, Onfailure) {
				$.ajax({
					url : url,
					type : 'GET',
					datatype : "json",
					success : function(response) {
						Onsuccess(response);
					},
					error : function(response, ajaxOptions, thrownError) {
						Onfailure(response);
					}
				});
			};
		}

		var serviceWrapper = new ServiceWrapper();

		var redirectUri = '${redirect_uri}';
		var accountLinkingToken = '${account_linking_token}';

		// To be removed once the cookie is set by login flow
		var productToken = '{"id":"154743","em":"newstest@hearst.com","fn":"","ln":"","et":"1450766970.15739","gt":"1424893820.967","g2":"10518271","ic":"1425566970.15739","hl":"1","sl":"1","f1":"SFC","f2":"94920","f3":"article","f4":"0f0ba1185cb10f199ce73c2d88c77aea","f5":"438d9cbb"}';
		var hrstptokDate = new Date();
		hrstptokDate.setHours(hrstptokDate.getHours() + 24);
		document.cookie = "hrstptok=" + productToken + ";expires="
				+ hrstptokDate.toGMTString() + ";";
		// To be removed once the cookie is set by login flow

		var edbid = JSON.parse(readCookie('hrstptok')).id;

		$('#loginBtn').click(
				function() {
					var emailAddress = $('#emailAddress').val();
					var password = $('#password').val();
					if (emailAddress === password) {
						var url = "getSubscriptionPayload/" + edbid + "/"
								+ accountLinkingToken;
						serviceWrapper.get(url, "",
								successGetSubscriptionPayload,
								failureGetSubscriptionPayload);
					} else {
						alert("invalid login");
					}
				});

		function successGetSubscriptionPayload(response) {
			console.log("successGetSubscriptionPayload");
			if (response.status.code == 200) {
				var redirectUrl = redirectUri + "&account_linking_token="
						+ accountLinkingToken + "&subscription_payload="
						+ response.data;
				$(location).attr('href', redirectUrl);
			} else {
				console.log("Error getting subscription payload - "
						+ response.status.message);
			}

		}

		function failureGetSubscriptionPayload(response) {
			console.log("failureGetSubscriptionPayload");
			console.log("Error getting subscription payload - "
					+ response.status.message);
		}
	</script>
</body>
</html>
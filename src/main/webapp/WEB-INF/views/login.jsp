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
	<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js"
		integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN"
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
		var redirectUri = '${redirect_uri}';
		var accountLinkingToken = '${account_linking_token}';
		var edbid = "112861";
		$('#loginBtn').click(
				function() {
					var emailAddress = $('#emailAddress').val();
					var password = $('#password').val();
					if (emailAddress === password) {

						var redirectUrl = redirectUri
								+ "&account_linking_token="
								+ accountLinkingToken
								+ "&subscription_payload={sample_payload}";

						$(location).attr('href', redirectUrl);
					} else {
						alert("invalid login");
					}

				});
	</script>
</body>
</html>
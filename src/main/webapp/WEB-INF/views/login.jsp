<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>
<script type="text/javascript">
	var treg = treg || {};
</script>
<script type="text/javascript" src="//treg-staging.hearstnp.com/treg.js"></script>
<!-- <script type="text/javascript" src="resources/app/js/treg.js"></script> -->
</head>
<body>
	<div class="treg-gya-login-widget"></div>
	<script type="text/javascript">
		var redirectUri = '${redirect_uri}';
		var accountLinkingToken = '${account_linking_token}';
		$(function() {
			setTimeout(function() {
				getSubscriptionPayLoad();
			}, 3000);
		});

		function getSubscriptionPayLoad() {
			console.log("Active session - " + treg.hasActiveSession());
			if (treg.hasActiveSession()) {
				var edbid = JSON.parse(treg.readCookie('hrstptok')).id;
				//var url = "getSubscriptionPayload/" + edbid + "/"
				//		+ accountLinkingToken;
				//serviceWrapper.get(url, "", successGetSubscriptionPayload,
				//		failureGetSubscriptionPayload);
				var subscriptionObj = {
					"edbid" : edbid,
					"redirectUri" : redirectUri,
					"accountLinkingToken" : accountLinkingToken
				};
				console.log(JSON.stringify(subscriptionObj));
				serviceWrapper
						.post("getSubscriptionPayload", {
							subscription : JSON.stringify(subscriptionObj)
						}, successGetSubscriptionPayload,
								failureGetSubscriptionPayload);
			}
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
			this.post = function(url, args, Onsuccess, Onfailure) {
				$.ajax({
					url : url,
					type : 'POST',
					data : args,
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
		function successGetSubscriptionPayload(response) {
			console.log("successGetSubscriptionPayload");
			if (response.status.code == 200) {
				var redirectUrl = redirectUri + "&account_linking_token="
						+ accountLinkingToken + "&subscription_payload="
						+ response.data;
				$(location).attr('href', redirectUrl);
			} else {
				alert("Error getting subscription payload - "
						+ response.status.message);
			}
		}

		function failureGetSubscriptionPayload(response) {
			console.log("failureGetSubscriptionPayload");
			alert("Error getting subscription payload - "
					+ response.status.message);
		}
		var serviceWrapper = new ServiceWrapper();
	</script>
</body>
</html>
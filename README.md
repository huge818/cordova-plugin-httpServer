README.md

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="initial-scale=1, maximum-scale=1, user-scalable=no, width=device-width">
	<title></title>

	<link rel="manifest" href="manifest.json">

	<link href="lib/ionic/css/ionic.css" rel="stylesheet">
	<link href="css/style.css" rel="stylesheet">
	<script src="cordova.js"></script>
	<script src="promise-7.0.4.min.js"></script>
	<script src="jquery-3.2.1.min.js"></script>
	<script src="file-system.js"></script>
</head>
<body class="platform-android platform-cordova platform-webview">
socket http Server
<div id="show"></div>
<script>

document.addEventListener("deviceready", function(){
	var httpServer=cordova.plugins.httpServer;
	httpServer.startServer(8080,"/webroot/", function(request){
		if(request.uri=="/favicon.ico"){
			httpServer.response(request.uuid,"");
			return;
		}
		var isAPI=(request.uri.indexOf("/service/")>=0);
		if(isAPI){
			httpServer.response(request.uuid,{msg:"ok"});
			return;
		}
		var url="webroot"+request.uri;
		jQuery.ajax({
			type:"get",
			url:url,
			success:function(data){
				jQuery("#show").append("<div><pre>"+request.uri+"</pre></div>");
				httpServer.response(request.uuid,data);
			},
			error:function(err){
				httpServer.response(request.uuid,JSON.stringify(err));
			}
		});
	});
}, false);

</script>

</body>
</html>


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

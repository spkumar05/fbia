var treg=treg||{};
treg.gya=treg.gya||{};
treg.cmd=treg.cmd||[];
treg.cmd.test=123;
treg.events=[];
treg.sso={};
treg.event={};
treg.modules={};
treg.developerMode=treg.developerMode||false;
treg.screens={};
treg.logtype={};
treg.log=[];
treg.pageType=treg.pageType||"content";
treg.gigyaServiceAvailable=("yes"=="yes")?true:false;
treg.janrainServiceAvailable=("yes"=="yes")?true:false;
treg.loggingOut=false;
treg.domLoaded=false;
treg.localCSS=treg.localCSS||false;
treg.loadGigya=false;
treg.ssoId="janrain";
treg.build="1.6";
treg.reloadAfterLogin=treg.reloadAfterLogin||false;
treg.force_logout=treg.force_logout||false;
treg.load_async=treg.load_async||false;
treg.identity={id:null,edbId:null,displayName:null};
treg.server_location="treg-staging.hearstnp.com/";
treg.current_hash="0x7A4A9E2405DBF4BF62911F1A20355C150A952FCB";
treg.main_script_node=null;
treg.event.on_treg_loaded=10;
treg.event.on_dom_loaded=20;
treg.event.on_user_logged_in=30;
treg.event.onSessionFound=40;
treg.event.onSessionNotFound=50;
treg.event.onSessionEnd=60;
treg.event.onInitialSessionState=65;
treg.event.onSocialProviderFound=500;
treg.event.onAccountLinked=510;
treg.event.onAccountUnLinked=520;
treg.event.onScreenRendered=530;
treg.event.onRegistrationStart=540;
treg.event.onRegistrationSuccess=550;
treg.event.onRegistrationFailed=560;
treg.event.onLoginFailed=570;
treg.event.onReturningExperienceData=580;
treg.event.onAnalyticsDataUpdated=600;
treg.event.OnBeforeNicknameUpdate=610;
treg.event.onBeforeWidgets=620;
treg.event.onBeforeProfileWidget=630;
treg.screens.login=10;
treg.screens.edit_profile=20;
treg.screens.create_account=30;
treg.screens.password_recover=40;
treg.screens.verify_email=50;
treg.screens.accountDeactivated=60;
treg.screens.signInWaitMessage=70;
treg.logtype.info=0;
treg.logtype.warning=1;
treg.logtype.error=2;
treg.sso.localTimeout=7776000;
treg.sso.federatedTimeout=7776000;
treg.unqueueCommands=function(){while(treg.cmd.length>0){(treg.cmd.shift())()
}treg.cmd={};
treg.cmd.push=function(a){a()
}
};
treg.clearCookie=function(a){document.cookie=a+"=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/"
};
treg.setEnvCookie=function(a,b){document.cookie=a+"="+b+"; path=/"
};
treg.setConfigCookie=function(a,b){treg.setEnvCookie(a,b)
};
treg.clearConfigCookie=function(a){if(arguments.length==0){treg.clearCookie("site_url");
treg.clearCookie("environment");
treg.clearCookie("sso");
treg.clearCookie("subscriber_id");
treg.clearCookie("gigyaServiceAvailable");
treg.clearCookie("conditional1")
}else{treg.clearCookie(arguments[0])
}};
treg.dumpConfigCookies=function(){console.log("site_url :"+treg.readCookie("site_url"));
console.log("environment :"+treg.readCookie("environment"));
console.log("sso :"+treg.readCookie("sso"));
console.log("subscriber_id :"+treg.readCookie("subscriber_id"));
console.log("gigyaServiceAvailable :"+treg.readCookie("gigyaServiceAvailable"))
};
treg.readConfigFromCookies=function(){var b=treg.readCookie("site_url");
var a=treg.readCookie("environment");
var c=treg.readCookie("sso");
var d=treg.readCookie("subscriber_id");
if(typeof(b)!="undefined"){treg.url_overide=b
}if(typeof(a)!="undefined"){treg.server_location=(a=="production")?"treg.hearstnp.com/":"treg-staging.hearstnp.com/"
}if(typeof(c)!="undefined"){treg.forceLoadSSO=c;
if(c=="janrain"){treg.loadGigya=false
}else{treg.loadGigya=true
}}if(typeof(d)!="subscriber_id"){treg.subscriberid=d
}};
(function(){var a;
function b(g,d,e,f){if(a){return a[g]
}d=document.cookie.split("; ");
a={};
for(f=d.length-1;
f>=0;
f--){e=d[f].split("=");
a[e[0]]=e[1]
}return a[g]
}treg.readCookie=b
})();
treg.loadSystem=function(){treg.readConfigFromCookies();
//var e=treg.getPageUrl();
var e = "treg-hou-staging.hearstnp.com";
if(treg.readCookie("gigyaServiceAvailable")=="false"){treg.gigyaServiceAvailable=false
}var a=treg.server_location+"assets/"+treg.current_hash+"/GetJS?url="+escape(e);
var d=treg.getFullExpandedAddress(a);
if(treg.load_async){var c=document.createElement("script");
c.type="text/javascript";
c.async=true;
c.src=d;
var b=document.getElementsByTagName("script")[0];
b.parentNode.appendChild(c);
treg.logInfo("treg async queued to load: "+a)
}else{document.write("<scr"+'ipt src="'+d+'"></scr'+"ipt>");
treg.logInfo("treg queued to load: "+a)
}};
treg.renderScreen=function(a){return false
};
treg.hasActiveSession=function(){return false
};
treg.clearIdentity=function(){treg.identity.id=null;
treg.identity.edbId=null
};
treg.reloadPage=function(a){treg.logInfo("page refresh by: "+a);
if(treg.getParameterByName("notregrefresh")==""){window.location.reload(true)
}};
treg.sso.saveIdentityObject=function(c,a,d,b){};
treg.sso.clearIdentityObject=function(a){};
treg.sso.loadIdentityObject=function(a,b){b(null)
};
treg.GetCookieDomain=function(){var b=window.location.host;
var a=b;
if(b!=null){var c=b.split(".").reverse();
if(c!=null&&c.length>1){a=c[1]+"."+c[0];
if(c.length>2&&c[2].toLowerCase()=="m"){a="m."+a
}}}return a
};
treg.registerEvent=function(b,a){var c=treg.events[b];
treg.events[b]=function(d){c&&c(d);
a(d)
}
};
treg.fireEvent=function(a,b){treg.events[a]&&treg.events[a](b)
};
treg.logInfo=function(a){treg.logEvent(new Date(),a,treg.logtype.info)
};
treg.logWarning=function(a){treg.logEvent(new Date(),a,treg.logtype.warning)
};
treg.logError=function(a){treg.logEvent(new Date(),a,treg.logtype.error)
};
treg.logException=function(b,a){if(typeof a!="undefined"){if(typeof a.message!="undefined"){treg.logEvent(new Date(),b+a.message);
return
}}treg.logEvent(new Date(),b,treg.logtype.error)
};
if(typeof(window.performance)=="undefined"){treg.logEvent=function(c,a,b){treg.log.push({d:c,m:a,t:b})
};
treg.getTimeElapsed=function(){return -1
}
}else{treg.logEvent=function(c,a,b){treg.log.push({d:c,pt:window.performance.now(),m:a,t:b})
};
treg.getTimeElapsed=function(c,a,b){var d=(performance.timing.navigationStart+window.performance.now())-performance.timing.domLoading;
return d.toFixed(2)
}
}treg.dumpLog=function(){var b=treg.log;
for(var a=0;
a<b.length;
a++){var d="msg";
var e=treg.formatDateTime(b[a].d);
if(typeof(b[a].t)!="undefined"){if(b[a].t==treg.logtype.warning){d+=" warning"
}if(b[a].t==treg.logtype.error){d+=" error"
}}var c=" : "+b[a].m;
console.log("treg "+e+c)
}};
treg.dumpLogWindow=function(){var c=treg.log;
var a="";
for(var b=0;
b<c.length;
b++){var e="msg";
var f="";
if(typeof(c[b].pt)!="undefined"){f=(performance.timing.navigationStart+c[b].pt)-performance.timing.domLoading;
f=f.toFixed(2)
}else{f=treg.formatDateTime(c[b].d)
}if(typeof(c[b].t)!="undefined"){if(c[b].t==treg.logtype.warning){e+=" warning"
}if(c[b].t==treg.logtype.error){e+=" error"
}}var d='<div class="'+e+'">'+f+" : "+c[b].m+"</div>";
a+=d
}var g=window.open("","wnd");
g.document.body.innerHTML=a
};
treg.formatDateTime=function(e){var a=(e.getHours()<10?"0":"")+e.getHours();
var b=(e.getMinutes()<10?"0":"")+e.getMinutes();
var d=(e.getSeconds()<10?"0":"")+e.getSeconds();
var c=e.getMilliseconds();
return a+":"+b+":"+d+": "+c
};
treg.loadScriptAsync=function(d,a){var c=document.createElement("script");
c.async=true;
c.type="text/javascript";
var e="https:"==document.location.protocol;
c.src=(d.search(/^http/i)==-1?((e?"https:":"http:")+"//"):"")+d;
if(typeof(a)!=="undefined"){if(c.addEventListener){c.addEventListener("load",a,false)
}else{c.onreadystatechange=function(){if(c.readyState in {loaded:1,complete:1}){c.onreadystatechange=null;
a()
}}
}}var b=document.getElementsByTagName("script")[0];
b.parentNode.insertBefore(c,b);
return b
};
treg.getFullExpandedAddress=function(a){var b="https:"==document.location.protocol;
return(b?"https:":"http:")+"//"+a
};
treg.getPageUrl=function(){var a=window.location.host;
if(typeof(treg.url_overide)!="undefined"){a=treg.url_overide
}return treg.cleanUrl(a)
};
treg.cleanUrl=function(a){if(a.toLowerCase().indexOf("http://")==0){a=a.substr(7,a.length)
}if(a.toLowerCase().indexOf("https://")==0){a=a.substr(8,a.length)
}if(a.length>300){a=a.substr(0,300)
}if(a.charAt(a.length-1)=="/"){a=a.slice(0,-1)
}return a
};
treg.waitForElement=function(b,a){var c=document.getElementById(b);
if(c!=null){a()
}else{window.requestAnimationFrame(function(){treg.waitForElement(b,a)
})
}};
treg.renderHTML=function(c,b){var a=document.getElementById(c);
if(a!=null&&typeof(treg.html[b])!="undefined"){a.innerHTML=treg.html[b]
}};
treg.elementHasClass=function(a,b){return(a.className.match(new RegExp("(\\s|^)"+b+"(\\s|$)"))!=null)
};
treg.addClassToElement=function(a,b){if(!treg.elementHasClass(a,b)){a.className+=(a.className.length>0)?" "+b:b
}};
treg.removeClassFromElement=function(a,c){if(treg.elementHasClass(a,c)){var b=new RegExp("(\\s|^)"+c+"(\\s|$)");
a.className=a.className.replace(b," ")
}};
treg.loadCSS=function(a){var b=document.createElement("link");
b.setAttribute("rel","stylesheet");
b.setAttribute("type","text/css");
b.setAttribute("href",a);
document.getElementsByTagName("head")[0].appendChild(b)
};
treg.getParameterByName=function(a){a=a.replace(/[\[]/,"\\[").replace(/[\]]/,"\\]");
var b=new RegExp("[\\?&]"+a+"=([^&#]*)"),c=b.exec(location.search);
return c==null?"":decodeURIComponent(c[1].replace(/\+/g," "))
};
treg.showElement=function(b,a){try{document.getElementById(b).style.display=a?"block":"none"
}catch(c){}};
treg.getHostName=function(b){var a=b.match(/:\/\/(www[0-9]?\.)?(.[^/:]+)/i);
if(a!=null&&a.length>2&&typeof a[2]==="string"&&a[2].length>0){return a[2]
}else{return null
}};
treg.getDomain=function(d){var b=treg.getHostName(d);
var a=b;
if(b!=null){var c=b.split(".").reverse();
if(c!=null&&c.length>1){a=c[1]+"."+c[0];
if(c.length>2&&c[2].toLowerCase()=="m"){a="m."+a
}}}return a
};
treg.contentLoaded=function(m,f){var c=false,l=true,b=m.document,k=b.documentElement,a=b.addEventListener?"addEventListener":"attachEvent",j=b.addEventListener?"removeEventListener":"detachEvent",i=b.addEventListener?"":"on",g=function(n){if(n.type=="readystatechange"&&b.readyState!="complete"){return
}(n.type=="load"?m:b)[j](i+n.type,g,false);
if(!c&&(c=true)){f.call(m,n.type||n)
}},h=function(){try{k.doScroll("left")
}catch(n){setTimeout(h,50);
return
}g("poll")
};
if(b.readyState=="complete"){f.call(m,"lazy")
}else{if(b.createEventObject&&k.doScroll){try{l=!m.frameElement
}catch(d){}if(l){h()
}}b[a](i+"DOMContentLoaded",g,false);
b[a](i+"readystatechange",g,false);
m[a](i+"load",g,false)
}};
treg.loadSystem();
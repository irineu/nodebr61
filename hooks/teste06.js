//HOOK SSL

Interceptor.attach( Module.findExportByName(null, "stat"), {
    onEnter(args) {
        let s = args[0].readUtf8String();
        if(s.indexOf("su") > -1){
            const fakePath = Memory.allocUtf8String("/groselha");
            args[0] = fakePath;
        }
    }
});

Java.perform(function(){

    var JFile = Java.use("java.io.File");

    JFile.$init.overload('java.lang.String', 'java.lang.String').implementation = function(arg0, arg1){        
        if(arg1 == "su"){
            arg1 = "groselha";
        }
        return this.$init(arg0, arg1);
    }

    let JString = Java.use('java.lang.String');

    JString.equalsIgnoreCase.overload('java.lang.String').implementation = function(arg0){
        let result = this.equalsIgnoreCase(arg0);
    
        if(arg0 == "123" || this == "123"){
            console.log("equalsIgnoreCase ", this, arg0, result);
        }

        return result;
    }

    JString.equals.overload('java.lang.Object').implementation = function(arg0){
        let result = this.equals(arg0);

        if(arg0 == "123" || this == "123"){
            console.log("equals ", this, arg0, result);
        }

        return result;
    }


    var X509TrustManager = Java.use('javax.net.ssl.X509TrustManager');
    var SSLContext = Java.use('javax.net.ssl.SSLContext');
    
    // TrustManager (Android < 7) //
    ////////////////////////////////
    var TrustManager = Java.registerClass({
        // Implement a custom TrustManager
        name: 'dev.asd.test.TrustManager',
        implements: [X509TrustManager],
        methods: {
            checkClientTrusted: function(chain, authType) {},
            checkServerTrusted: function(chain, authType) {},
            getAcceptedIssuers: function() {return []; }
        }
    });
    // Prepare the TrustManager array to pass to SSLContext.init()
    var TrustManagers = [TrustManager.$new()];
    // Get a handle on the init() on the SSLContext class
    var SSLContext_init = SSLContext.init.overload(
        '[Ljavax.net.ssl.KeyManager;', '[Ljavax.net.ssl.TrustManager;', 'java.security.SecureRandom');
    try {
        // Override the init method, specifying the custom TrustManager
        SSLContext_init.implementation = function(keyManager, trustManager, secureRandom) {
            console.log('[+] Bypassing Trustmanager (Android < 7) pinner');
            SSLContext_init.call(this, keyManager, TrustManagers, secureRandom);
        };
    } catch (err) {
        console.log('[-] TrustManager (Android < 7) pinner not found');
        //console.log(err);
    }
});

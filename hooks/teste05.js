//HOOK HASH & CIPHER

Interceptor.attach( Module.findExportByName(null, "stat"), {
    onEnter(args) {
        let s = args[0].readUtf8String();
        if(s.indexOf("su") > -1){
            const fakePath = Memory.allocUtf8String("/groselha");
            args[0] = fakePath;
        }
    }
});

const toHexString = (byteArray) => {
    return Array.from(byteArray, function(byte) {
        return ('0' + (byte & 0xFF).toString(16)).slice(-2);
    }).join(' ')
}


Java.perform(function(){

    var JFile = Java.use("java.io.File");

    JFile.$init.overload('java.lang.String', 'java.lang.String').implementation = function(arg0, arg1){        
        if(arg1 == "su"){
            arg1 = "groselha";
        }
        return this.$init(arg0, arg1);
    }

    let Cipher = Java.use('javax.crypto.Cipher');
    
    Cipher.getInstance.overload('java.lang.String').implementation = function(arg0){
        console.log("");
        console.log("--------------------------------");
        console.log("tipo de criptografia: ", arg0);
        return this.getInstance(arg0);
    }

    let SecretKeySpec = Java.use('javax.crypto.spec.SecretKeySpec');

    SecretKeySpec.$init.overload('[B', 'java.lang.String').implementation = function(arg0, arg1){
        console.log("chave criptográfica:", toHexString(arg0));
        this.$init(arg0, arg1);
    }

    let IvParameterSpec = Java.use('javax.crypto.spec.IvParameterSpec');

    IvParameterSpec.$init.overload('[B').implementation = function(arg0){
        console.log("IV:", toHexString(arg0));
        this.$init(arg0);
    }

    Cipher.doFinal.overload('[B').implementation = function(arg0){
        console.log("Valor a ser criptografado (bytes): ", toHexString(arg0));
        
        var str = "";
        for(var i = 0; i < arg0.length; ++i){
            str+= (String.fromCharCode(arg0[i]));
        }

        console.log("Valor a ser criptografado (String): ",str);

        let result = this.doFinal(arg0);
        console.log("Criptograma: ",toHexString(result));
        console.log("--------------------------------");
        return result;
    }

    let MessageDigest =  Java.use('java.security.MessageDigest');
    
    MessageDigest.getInstance.overload('java.lang.String').implementation = function(arg0){
        console.log("");
        console.log("--------------------------------");
        console.log("Obtendo hasher: ", arg0);
        return this.getInstance(arg0);    
    };

    MessageDigest.update.overload('[B').implementation = function(arg0){
        console.log("Gerando hash do valor: ", toHexString(arg0));
        this.update(arg0); 
    };

    MessageDigest.digest.overload().implementation = function(){
        let result = this.digest();
        console.log("Hash gerado: ", toHexString(result));
        console.log("--------------------------------");
        return result;
    };

    let JString = Java.use('java.lang.String');
    JString.equalsIgnoreCase.overload('java.lang.String').implementation = function(arg0){
        let result = this.equalsIgnoreCase(arg0);

        if(arg0 == "123" || this == "123"){
            console.log("equalsIgnoreCase ", this, arg0, result);
        }

        return result;
    }

});


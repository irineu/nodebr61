//HOOK EQUALS

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

        console.log("equalsIgnoreCase ", this, arg0, result);
        // if(arg0 == "123" || this == "123"){
        //     console.log("equalsIgnoreCase ", this, arg0, result);
        // }

        return result;
    }

    JString.equals.overload('java.lang.Object').implementation = function(arg0){
        let result = this.equals(arg0);

        console.log("equals ", this, arg0, result);
        // if(arg0 == "123" || this == "123"){
        //     console.log("equals ", this, arg0, result);
        // }

        
        return result;
    }
});


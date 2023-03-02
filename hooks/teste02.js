//ROOT HOOK 2

Java.perform(function(){
    
    var JFile = Java.use("java.io.File");

    JFile.$init.overload('java.lang.String', 'java.lang.String').implementation = function(arg0, arg1){
        console.log("Java File Constructor -> ",arg0, arg1);
        
        if(arg1 == "su"){
            arg1 = "groselha";
        }
        return this.$init(arg0, arg1);
    }

});

Interceptor.attach( Module.findExportByName(null, "stat"), {
    onEnter(args) {
        let s = args[0].readUtf8String();
        if(s.indexOf("su") > -1){

            console.log("C++ Stat -> ", s);

            const fakePath = Memory.allocUtf8String("/groselha");
            args[0] = fakePath;
        }
    }
});

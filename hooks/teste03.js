//HOOK UI EVENTS

Java.perform(function(){
    
    console.log("");

    var JFile = Java.use("java.io.File");
    JFile.$init.overload('java.lang.String', 'java.lang.String').implementation = function(arg0, arg1){
        if(arg1 == "su"){
            arg1 = "groselha";
        }
        return this.$init(arg0, arg1);
    }

    setTimeout(() =>{
        Java.enumerateLoadedClasses({
            onMatch: function(className) {
                if(className.indexOf("nodebr61") > -1){
                    
                    let methods = Java.use(className).class.getDeclaredMethods();
                    methods.forEach(m => {

                        if(m.getName() == "onClick"){
                            console.log(className, m);
                        }

                    });
                }
            },
            onComplete: function() {}
        });
   },1000);    

    //segunda classe anônima
    var PasteClickListener = Java.use('com.irineu.nodebr61.MainActivity$2');

    PasteClickListener.onClick.overload('android.view.View').implementation = function(view){
        console.log("onClick");

      //   Java.choose("com.irineu.nodebr61.MainActivity", {
      //       onMatch: function (instance) {
      //           instance.openSecretActivity();
      //       },
      //       onComplete: function () { }
      //   });

      //return this.onClick(view);
    }

});

Interceptor.attach( Module.findExportByName(null, "stat"), {
    onEnter(args) {
        let s = args[0].readUtf8String();
        if(s.indexOf("su") > -1){
            const fakePath = Memory.allocUtf8String("/groselha");
            args[0] = fakePath;
        }
    }
});

//INSPECIONA PROJETO

Java.perform(function(){

   setTimeout(() =>{
        Java.enumerateLoadedClasses({
            onMatch: function(className) {
                if(className.indexOf("nodebr61") > -1){
                    
                    console.log(className);
                    let methods = Java.use(className).class.getDeclaredMethods();
                    methods.forEach(m => {
                        console.log(" " + m.getGenericReturnType().getTypeName() + " " + m.getName());
                    });

                    console.log("");
                }
            },
            onComplete: function() {}
        });
   },1000);
});

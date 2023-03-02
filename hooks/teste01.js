//ROOT HOOK 1

Java.perform(function(){

        let MainActivity = Java.use('com.irineu.nodebr61.MainActivity');

        MainActivity.checkRoot.overload().implementation = function(){
            let result = this.checkRoot();
            console.log("Java check root: ",result);
    
            return false;
        }

        MainActivity.nativeCheckRoot.overload().implementation = function(){
            let result = this.nativeCheckRoot();
            console.log("C++ check root:" + result);
            
            return false;
        }

 });
 
load('env.rhino.js');

Envjs.scriptTypes['text/javascript'] = true;

var specFile = "/Users/goggin/Documents/CS/chatmaster/app/views/Application/specrunner.html";

// for (i = 0; i < arguments.length; i++) {
    // specFile = arguments[i];
    
    console.log("Loading: " + specFile);
    
    window.location = specFile
// }

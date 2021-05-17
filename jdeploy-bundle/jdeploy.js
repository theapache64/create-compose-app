#! /usr/bin/env node
var fail = reason => {
    console.error(reason);
    process.exit(1);
};
function getJavaVersion() {
    try {
        var javaVersionProc = exec('java  -version', {silent:true});
        if (javaVersionProc.code !== 0) {
            return false;
        }
        var stdout = javaVersionProc.stderr;
        //console.log(javaVersionProc);
        //console.log("stdout is "+stdout);
        var regexp = /version "(.*?)"/;
        var match = regexp.exec(stdout);
        var parts = match[1].split('.');
        var join = '.';
        var versionStr = '';
        parts.forEach(function(v) {
            versionStr += v;
            if (join !== null) {
                versionStr += join;
                join = null;
            }
        });
        versionStr = versionStr.replace('_', '');
        //console.log("Java version string "+versionStr)
        return parseFloat(versionStr);
    } catch (e) {
        return false;
    }
}

var getDirectories = dirPath => fs.readdirSync(dirPath).filter(
    file => fs.statSync(path.join(dirPath, file)).isDirectory()
  );

function getEmbeddedJavaDir() {
    var _platform = os.platform();
    var _driver;
    switch (_platform) {
      case 'darwin': _platform = 'macosx'; _driver = 'Contents' + path.sep + 'Home' + path.sep + 'bin'; break;
      case 'win32': _platform = 'windows'; _driver = 'bin'; break;
      case 'linux': _driver = 'bin'; break;
      default:
        fail('unsupported platform: ' + _platform);
    }

    var jreDir = getJdeploySupportDir() + path.sep + 'node_modules' + path.sep + 'node-jre' + path.sep + 'jre';

    try {
        return jreDir + path.sep + getDirectories(jreDir)[0] + path.sep + _driver;
    } catch (e) {
        //console.log(e);
        return jreDir;
    }
}



function getJdeploySupportDir() {
    return os.homedir() + path.sep + ".jdeploy";
}
var fs = require('fs');
var os = require('os');
var path = require('path');
var jarName = "create-compose-desktop-app.main.jar";
var mainClass = "{{MAIN_CLASS}}";
var classPath = "{{CLASSPATH}}";
var port = "0";
var warPath = "";
classPath = classPath.split(':');
var classPathStr = '';
var first = true;
classPath.forEach(function(part) {
    if (!first) classPathStr += path.delimiter;
    first = false;
    classPathStr += __dirname + '/' + part;
});
classPath = classPathStr;
var shell = require("shelljs/global");
var userArgs = process.argv.slice(2);
var javaArgs = [];
javaArgs.push('-Djdeploy.base='+__dirname);
javaArgs.push('-Djdeploy.port='+port);
javaArgs.push('-Djdeploy.war.path='+warPath);
var programArgs = [];
userArgs.forEach(function(arg) {
    if (arg.startsWith('-D') || arg.startsWith('-X')) {
        javaArgs.push(arg);
    } else {
        programArgs.push(arg);
    }
});
var cmd = 'java';

env['PATH'] = getEmbeddedJavaDir() + path.delimiter + env['PATH'];
if (env['JAVA_HOME']) {
    env['PATH'] = env['JAVA_HOME'] + path.sep + 'bin' + path.delimiter + env['PATH'];
}

var javaVersion = getJavaVersion();
if (javaVersion === false || javaVersion < 1.8 || env['JDEPLOY_USE_NODE_JRE']) {
    if (!test('-e', getJdeploySupportDir())) {
        mkdir(getJdeploySupportDir());
    }
    var packageJson = getJdeploySupportDir() + path.sep + 'package.json';
    if (!test('-e', packageJson)) {
        fs.writeFileSync(packageJson, JSON.stringify({'name' : 'jdeploy-support', 'version': '1.0.0'}), 'utf8');
    }
    if (!test('-e', getEmbeddedJavaDir())) {
        var currDir = pwd();
        cd(getJdeploySupportDir());
        console.log("Installing/Updating JRE in "+getJdeploySupportDir()+"...");
        exec('npm install node-jre --save');
        cd(currDir);
    }
    env['PATH'] = getEmbeddedJavaDir() + path.delimiter + env['PATH'];
    /*
    // System java either not on path or too old
    if (!test('-e', getEmbeddedJavaDir())) {
        // Could not find embedded java dir
        // We need to install it.
        fail("Could not find embedded java at "+getEmbeddedJavaDir());

    } else {
        // Found the embedded version.  Add it to the PATH
        env['PATH'] = getEmbeddedJavaDir() + path.delimiter + env['PATH']
        console.log("Now java version is "+getJavaVersion());
        fail("Path is now "+env['PATH']);
    }
    */

}
//console.log("Java version is "+getJavaVersion());

javaArgs.forEach(function(arg) {
    cmd += ' "'+arg+'"';
});
if (jarName !== '{'+'{JAR_NAME}}') {
    cmd += ' -jar "'+__dirname+'/'+jarName+'" ';
} else {
    cmd += ' -cp "'+classPath+'" '+mainClass+' ';
}

programArgs.forEach(function(arg) {
    cmd += ' "'+arg+'"';
});
var child = exec(cmd, {async: true});
process.stdin.setEncoding('utf8');

process.stdin.on('readable', function() {
  var chunk = null;
  while (null !== (chunk = process.stdin.read())) {
    try {
      child.stdin.write(chunk);
    } catch(e){}
  }
});
child.on('close', function(code) {
    process.exit(code);
});

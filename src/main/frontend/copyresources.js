const shell = require('shelljs');

module.exports = copyresources;
copyresources.copy = copyresources;

shell.echo('Copying *js and *.html file into resource folder')
const source = process.argv[2];
const dest = process.argv[3];
copyresources(source, dest);

function copyresources(source, target) {
    // if (!shell.which('git')) {
    //     shell.echo('Sorry, this script requires git');
    //     shell.exit(1);
    // }

// shell.rm('-rf', 'out/Release');
    shell.cp('-R', source + "/*.ico", target + "/templates/");
    shell.cp('-R', source + "/*.js", target + "/static/");
    shell.cp('-R', source + "/*.css", target + "/static/");
    shell.cp('-R', source + "/*.html", target + "/templates/");
    shell.cp('-R', source + "/assets/images/", target + "/static/images");
}
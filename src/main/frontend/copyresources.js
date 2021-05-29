const shell = require('shelljs');

module.exports = copyresources;
copyresources.copy = copyresources;

shell.echo('\nCopying resource files into appropriate folders...\n')
const source = process.argv[2];
const dest = process.argv[3];
copyresources(source, dest);

function copyresources(source, target) {

    // shell.mkdir(source + "/js", source + "/css");
    // shell.mv(source + "/*.js ", source + "/js/");
    // shell.mv(source + "/*.css ", source + "/css/");
    // shell.sed('-i', /(\ssrc=")/g, ' src=\"js/', source + "/index.html");
    // shell.sed('-i', 'rel=\"stylesheet" href=\"', 'rel=\"stylesheet\" href=\"css/', source + "/index.html");


    shell.cp('-R', source + "/*.ico", target + "/static/");
    // shell.cp('-R', source + "/js/*.js", target + "/static/js");
    // shell.cp('-R', source + "/css/*.css", target + "/static/css");
    shell.cp('-R', source + "/*.js", target + "/static/");
    shell.cp('-R', source + "/*.css", target + "/static/");
    shell.cp('-R', source + "/*.html", target + "/templates/");
    shell.mkdir(target + "/static/resources");
    shell.cp('-R', source + "/resources/*", target + "/static/resources/");
    shell.cp('-R', source + "/assets/*", target + "/static/");
}
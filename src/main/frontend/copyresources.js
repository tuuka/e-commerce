const shell = require('shelljs');

module.exports = copyresources;
copyresources.copy = copyresources;

shell.echo('--------------------------\nCopying resource files into appropriate folders...\n')
const source = process.argv[2];
const dest = process.argv[3];
copyresources(source, dest);

function copyresources(source, target) {

    // shell.mkdir(source + "/js", source + "/css");
    // shell.mv(source + "/*.js ", source + "/js/");
    // shell.mv(source + "/*.css ", source + "/css/");

    shell.mkdir('-p', target + "/static/js", target + "/static/css", target + "/static/images", target + "/templates/");
    shell.cp('-R', source + "/*.ico", target + "/static/");
    shell.cp('-R', source + "/*.js", target + "/static/js/");
    shell.cp('-R', source + "/*.css", target + "/static/css/");
    shell.cp('-R', source + "/*.html", target + "/templates/");
    shell.sed('-i', /(script src=")/g, 'script src=\"js/', target + "/templates/index.html");
    shell.sed('-i', /(rel="stylesheet" href=")/g, 'rel=\"stylesheet\" href=\"css/', target + "/templates/index.html");
    shell.cp('-R', source + "/images/*", target + "/static/images/");
}

shell.echo('\nCopy Finished\n--------------------------')

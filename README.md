#scolomfr-recette

##General purpose

A web application designed to assist in the recipe work of the vocabularies used in the ScoLOMFR metadata resource standard.

## Requirements

scolomfr-recette requires **Java 8** for execution. 

##Installation

###From sources

####Fetch source code

```shell
git clone https://github.com/j-dornbusch/scolomfr-recette.git
```

####Build web static ressources

Move to webapp resources directory.

```shell
cd scolomfr-recette/scolomfr-recette/src/main/webapp/WEB-INF/resources
bower install bower.json
gulp
```
If asked by bower to choose a dependency version number, opt for the newest.

Install one of these missing components if requested (Ubuntu users may need a sudo) :

```shell
npm install -g bower 
npm install --global gulp-cli
npm install --save-dev gulp
npm install --save-dev gulp-less
npm install --save-dev browser-sync
npm install --save-dev gulp-header
npm install --save-dev gulp-clean-css
npm install --save-dev gulp-rename
npm install --save-dev gulp-uglify
```

####Build war

```shell
mvn clean install
```

Deploy target/recette.war into a Web application container like Tomcat.

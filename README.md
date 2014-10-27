# News Search

This project contains the code for searching through our dataset of news, blog posts, Twitter and Facebook data using Hadoop.

## Structure
* **src/** - the source code for Hadoop search job (in packet **edu.stanford.snap.spinn3rHadoop**)
	* **Search.java** - the *main* and *run* method for the Hadoop job
	* **Spinn3rInputFilter.java** - decides which input files to process depending on the search date limitations
	* **Spinn3rMaper.java** - mapper for Hadoop job: parse the record to Java object, checks if it satisfies search conditions and if it does write it to output
	* **utils/ParseCLI.java** - parsing command line arguments
	* **utils/DocumentFilter.java** - check if one document satisfies search contitions
	* **utils/Spinn3rDocument.java** - converting strings to objects and back
* **target/** - the Hadoop search job deployed to a runnable JAR
* **web-page/** - the folder for everything connected with the web page
	* **api/** - the REST api for communication with Hadoop client
	* **client/** - the client for communication with the api on the web server and for running the jobs on Hadoop cluster
	* **lib/** - library of common stuff
	* **www/** - the HTML and PHP code for the web page

	
## Usage
To run the search use `hadoop jar <path to the JAR> <additional arguments>`. There are several arguments required. To see help, just run the command without any additional arguments. 

## Development information
To get the development environment up and running follow these steps:
 
   1. make sure maven is installed
   2. to add the Cloudera repository put the following configuration into the `${HOME}/.m2/settings.xml` file:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings>
    <profiles>
        <profile>
            <id>standard-extra-repos</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <repositories>
                <repository>
                    <!-- Central Repository -->
                    <id>central</id>
                    <url>http://repo1.maven.org/maven2/</url>
                    <releases>
                        <enabled>true</enabled>
                    </releases>
                    <snapshots>
                        <enabled>true</enabled>
                    </snapshots>
                </repository>
                <repository>
                    <!-- Cloudera Repository -->
                    <id>cloudera</id>
                    <url>https://repository.cloudera.com/artifactory/cloudera-repos</url>
                    <releases>
                        <enabled>true</enabled>
                    </releases>
                    <snapshots>
                        <enabled>true</enabled>
                   </snapshots>
                </repository>
            </repositories>
        </profile>
    </profiles>
</settings>
```
   
   3. clone the project, navigate into its folder and run `mvn eclipse:eclipse` to make it an Eclipse project and get the dependency JARs
   4.  open Eclipse and import it using `File > Import > Existing Projects into Workspace` then `Select root directory` and click `Finish`.
   5.  to run the code locally navigate into `Search.java` and run it as java application

To compile the code into a JAR which can be run on Hadoop cluster use `mvn clean package` and  the JAR will be compiled and put into `target` directory.   

#### Additional links

The project was built following [these](http://hadoopi.wordpress.com/2013/05/25/setup-maven-project-for-hadoop-in-5mn/) instructions.

An other great tutorial could be found [here](http://blog.cloudera.com/blog/2012/08/developing-cdh-applications-with-maven-and-eclipse/).

Info about CHD5 repository is available [here](https://repository.cloudera.com/cloudera/cloudera-repos/org/apache/hadoop/hadoop-core/).
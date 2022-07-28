## Backend
The Elasticsearch is required to be running.
To run the backend firtsly use `mvn clean install -DskipTests`. After that the exacutable file can be run by `java -jar target/graphBackend-0.0.1-SNAPSHOT.jar`

### Installation and Pre-requisites:
* Installed `Python 3`
* Installed `PM4PY` - `pip install pm4py`

### Changes to files:
Go to `src\main\java\cz\muni\csirt\kypo\logic` folder
  * There is a file called CSVCreator.java
  * In that file, change the first variable called `IP_ADDRESS` to value `localhost`

In the same folder change PM4PY.java file:
  * Change this string `python3 target/create_svg.py ` so your OS can run it. For windows usually change it to `python target/create_svg.py `

In the target folder create file called aggregations.txt and inside that you can put commands to be aggregated.

Copy create_svg.py file from resources to target folder.
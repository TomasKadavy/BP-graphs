# kadavy-interactive-process-graphs

Bachelor thesis supervised by Radek Ošlejšek. Interactive process maps for KYPO Analyst.

## Elasticsearch
Prior to the start of the application, it is required to run Elasticsearch (https://gitlab.fi.muni.cz/cybersec/elk-la/elk-portal-commands-events).
* Follow the instructions there:  
  * Insert data to elasticsearch - `./insert-events.sh ".\2021-06-30 Qualitative interview study"` (the data are in this "2021-06-30 Qualitative interview study" folder)
    * The isertion of data is done only once. If you have already done that in the past, go to the next step and run the database (`docker-compose up`)
  * Run the Elasticsearch database - `docker-compose up`

## My application
* To run the frontend and backend in docker - `docker-compose up`
* Open web browser at `http://localhost:4200/`
* Supported web browser is Google Chrome
  
* If you are using linux, or the docker does not work correctly (host.docker.internal is not supported), you firstly need to:
  * Go to `graphBackend\src\main\java\cz\muni\csirt\kypo\logic` folder
  * There is a file called CSVCreator.java
  * In that file, change the first variable called `IP_ADDRESS` to your local/private IPv4 address
  * Then you can run the docker-compose file

## Application without docker
Running frontend and backend separately without a docker can be done, although not recommended. Follow the README file in each folder for information.
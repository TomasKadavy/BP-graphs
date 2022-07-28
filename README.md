# Bachelor thesis

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
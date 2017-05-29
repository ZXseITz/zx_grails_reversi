# WebEngineering Module, Graded Exercise

## Commit Proposal

Matrikel Number: 15-469-411

Project idea: Reversi webpage

Web page that provides a service to play Reversi against a bot or a player

## Project confirmation

Confirmed, D. König.


## Project delivery

How to start the project:
`grailsw run-app`

How to test the project:
Unit tests under `src/test/java/reversi`

Project description:
See `ReversiDescription.html`

External contributions:
* `javax.servlet` (web servlet)
* `javax.websocket` (web socket)
* `com.google.gson` (google json handling)
* `jQuery` (js library)
* `jUnit` (Unit testing)
* `Mockito` (Mock testing)

Other comments:
* Addintional config `grails-app/init/mvc/DefaultReversiConfig` to register the web server as listener, linked in `grails-app/init/mvc/Application`
* Overridden getEndpointInstance method as new end point config `grails-app/init/mvc/ReversiEndPointConfig` for unique server instance, linked as configurator in `src/main/java/reversi/ReversiServer`

I'm particular proud of:
* Unique thread safe web server using web sockets
* bot concurrent sounding algorithm
* thread safe pvp matcher
* Unit tests with mocks


## Project grading 

< to be filled by lecturer>

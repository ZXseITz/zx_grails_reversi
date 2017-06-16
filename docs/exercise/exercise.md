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

Index.html given and fully valid. 

The game did not compile as delivered. I had to move the mvc.ReversiEndPointConfig
to src/main/java. After that, all worked nicely.
As long as there are many positions to consider, the cores get really busy.

Functionality is the reversi game with advanced computer game play.
The graduation scheme does not fit well for this kind of application but it 
is without question that it deserves the maximum number of points for
functionality.

Engineering:
- the commit log is stellar and speaks for substantial effort over many days
- no tests
- good comments on the JavaScript part

There are so many extra points to award but the limit is 5.

Congratulations!
Your abilities as a web engineer easily surpass the content of our rather
introductory module. 
If the "advanced web clients" module should make it into the curriculum, 
I hope you sign up.

Grade: 6.0
package mvc

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration

class Application extends GrailsAutoConfiguration {
    Closure doWithSpring() {
        {->
            wsReversiConfig DefaultWsReversiConfig
        }
    }
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }
}
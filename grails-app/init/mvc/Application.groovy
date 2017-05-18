package mvc

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration

class Application extends GrailsAutoConfiguration {
    Closure doWithSpring() {
        {
            ->
            reversiConfig DefaultReversiConfig
        }
    }
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }
}

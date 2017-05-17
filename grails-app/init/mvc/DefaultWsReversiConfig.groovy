package mvc

import reversi.ReversiServer
import org.springframework.boot.web.servlet.ServletContextInitializer
import org.springframework.context.annotation.Bean

import javax.servlet.ServletContext
import javax.servlet.ServletException

/**
 * Created by Claudio on 18.05.2017.
 */
class DefaultWsReversiConfig {
    @Bean
    ServletContextInitializer myInitializer() {
        return new ServletContextInitializer() {
            @Override
            void onStartup(ServletContext servletContext) throws ServletException {
                servletContext.addListener(ReversiServer)
            }
        }
    }
}

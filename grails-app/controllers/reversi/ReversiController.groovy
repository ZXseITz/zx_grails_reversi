package reversi

import grails.web.Controller
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo

@Controller
class ReversiController {
    def index() {
        Token.Color client = Token.Color.WHITE
        BoardModel model = new BoardModel()
        Board board = new Board(model)
        board.setUpBoard(client)
        render view:"index", model:[boardModel: model]
    }

    @MessageMapping("/add")
    @SendTo("/topic/messages")
    String send(String message) {
        return "Server: " + message
    }
}

package reversi

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo

class ReversiController {


    def index() {
        Token.Color client = Token.Color.WHITE
        BoardModel model = new BoardModel()
        Board board = new Board(model)
        board.setUpBoard(client)
        render view:"index", model:[boardModel: model]
    }

    @MessageMapping("/hello")
    @SendTo("/topic/hello")
    protected String hello(String world) {
        return "hello from controller, ${world}!"
    }
}


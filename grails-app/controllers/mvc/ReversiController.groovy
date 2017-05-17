package mvc

import reversi.Board
import reversi.BoardModel
import reversi.Token

class ReversiController {
    def index() {
        Token.Color client = Token.Color.WHITE
        BoardModel model = new BoardModel()
        Board board = new Board(model)
        board.setUpBoard(client)
        render view:"index", model:[boardModel: model]
    }
}


package reversi

class ReversiController {

    def index() {
        BoardModel model = new BoardModel();
        Board board = new Board(model)
        board.get(3, 3).color = Token.Color.WHITE
        board.get(4, 4).color = Token.Color.WHITE
        board.get(3, 4).color = Token.Color.BLACK
        board.get(4, 3).color = Token.Color.BLACK

        render view:"index", model:[boardModel: model]
    }
}


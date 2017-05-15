package reversi

class ReversiController {

    def index() {
        BoardModel model = new BoardModel();
        Board board = new Board(model)
        board.setUpBoard()

        render view:"index", model:[boardModel: model]
    }
}


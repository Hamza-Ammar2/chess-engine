import java.awt.*;

public class Board implements Runnable {
    Thread thinking;
    Box[][] boxes = new Box[8][8];
    Piece[] pieces = new Piece[32];
    char[][] position = {
        {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'},
        {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},
        {'0', '0', '0', '0', '0', '0', '0', '0'},
        {'0', '0', '0', '0', '0', '0', '0', '0'},
        {'0', '0', '0', '0', '0', '0', '0', '0'},
        {'0', '0', '0', '0', '0', '0', '0', '0'},
        {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
        {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'} 
    };
    Legalmoves legalmoves = new Legalmoves();

    int held_piece_index = -1; 
    int WIDTH;
    int HEIGHT;
    int boxHeight;
    int pieceHeight;
    Brain opponent;

    Board(int width, int height) {
        WIDTH = width;
        HEIGHT = height;
        boxHeight = WIDTH / 8;

        createBoard();
        updatePieces();
        opponent = new Brain(true);
    }

    void createBoard() {
       for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                boolean isblack = (i+j) % 2 > 0;

                boxes[i][j] = new Box(new int[]{i, j}, boxHeight, isblack);
            }
        } 

        pieceHeight = (int) (boxHeight * 0.7f);
        pieces[0] = new Piece(new int[]{7, 4}, boxHeight, pieceHeight, 'K', false);
        pieces[1] = new Piece(new int[]{0, 4}, boxHeight, pieceHeight, 'k', true);
        pieces[2] = new Piece(new int[]{0, 0}, boxHeight, pieceHeight, 'r', true);
        pieces[3] = new Piece(new int[]{0, 1}, boxHeight, pieceHeight, 'n', true);
        pieces[4] = new Piece(new int[]{0, 2}, boxHeight, pieceHeight, 'b', true);
        pieces[5] = new Piece(new int[]{0, 3}, boxHeight, pieceHeight, 'q', true);
        pieces[6] = new Piece(new int[]{0, 5}, boxHeight, pieceHeight, 'b', true);
        pieces[7] = new Piece(new int[]{0, 6}, boxHeight, pieceHeight, 'n', true);
        pieces[8] = new Piece(new int[]{0, 7}, boxHeight, pieceHeight, 'r', true);
        pieces[9] = new Piece(new int[]{1, 0}, boxHeight, pieceHeight, 'p', true);
        pieces[10] = new Piece(new int[]{1, 1}, boxHeight, pieceHeight, 'p', true);
        pieces[11] = new Piece(new int[]{1, 2}, boxHeight, pieceHeight, 'p', true);
        pieces[12] = new Piece(new int[]{1, 3}, boxHeight, pieceHeight, 'p', true);
        pieces[13] = new Piece(new int[]{1, 4}, boxHeight, pieceHeight, 'p', true);
        pieces[14] = new Piece(new int[]{1, 5}, boxHeight, pieceHeight, 'p', true);
        pieces[15] = new Piece(new int[]{1, 6}, boxHeight, pieceHeight, 'p', true);
        pieces[16] = new Piece(new int[]{1, 7}, boxHeight, pieceHeight, 'p', true);

        pieces[17] = new Piece(new int[]{7, 0}, boxHeight, pieceHeight, 'R', false);
        pieces[18] = new Piece(new int[]{7, 1}, boxHeight, pieceHeight, 'N', false);
        pieces[19] = new Piece(new int[]{7, 2}, boxHeight, pieceHeight, 'B', false);
        pieces[20] = new Piece(new int[]{7, 3}, boxHeight, pieceHeight, 'Q', false);
        pieces[21] = new Piece(new int[]{7, 5}, boxHeight, pieceHeight, 'B', false);
        pieces[22] = new Piece(new int[]{7, 6}, boxHeight, pieceHeight, 'N', false);
        pieces[23] = new Piece(new int[]{7, 7}, boxHeight, pieceHeight, 'R', false);
        pieces[24] = new Piece(new int[]{6, 0}, boxHeight, pieceHeight, 'P', false);
        pieces[25] = new Piece(new int[]{6, 1}, boxHeight, pieceHeight, 'P', false);
        pieces[26] = new Piece(new int[]{6, 2}, boxHeight, pieceHeight, 'P', false);
        pieces[27] = new Piece(new int[]{6, 3}, boxHeight, pieceHeight, 'P', false);
        pieces[28] = new Piece(new int[]{6, 4}, boxHeight, pieceHeight, 'P', false);
        pieces[29] = new Piece(new int[]{6, 5}, boxHeight, pieceHeight, 'P', false);
        pieces[30] = new Piece(new int[]{6, 6}, boxHeight, pieceHeight, 'P', false);
        pieces[31] = new Piece(new int[]{6, 7}, boxHeight, pieceHeight, 'P', false);
    }

    public void draw(Graphics g) {
        for (Box[] boxs : boxes) {
            for (Box box : boxs) {
                box.draw(g);
            }
        }

        for (Piece piece : pieces) {
            piece.draw(g);
        }

        if (held_piece_index > -1) pieces[held_piece_index].draw(g);
    }


    private Piece getPieceFromID(int[] ID) {
        for (Piece piece : pieces) {
            if (piece.dead) continue;
            if (piece.id[0] == ID[0] && piece.id[1] == ID[1]) return piece;
        }
        return null;
    }


    public void checkPieces(int X, int Y) {
        for (int i = 0; i < 32; i++) {
            if (!pieces[i].isTarget(X, Y)) continue;
            held_piece_index = i;
            break;
        }
    }

    public void checkBoxes(int X, int Y) {
        Piece piece = pieces[held_piece_index];

        for (int[] move : piece.moves.moves) {
            Box box = boxes[move[0]][move[1]];
            if (!box.isTarget(X, Y)) continue;

            Piece deadpiece = getPieceFromID(box.id);
            if (deadpiece != null) deadpiece.die();
            updatePos(piece.id, box.id);
            piece.update(box.id);
            updatePieces();

            held_piece_index = -1;
            //opponentMove();
            thinking = new Thread(this);
            thinking.start();
            return;
        }

        piece.update(piece.id);
        held_piece_index = -1;
    }


    private void opponentMove() {
        int[] Move = opponent.makeMove(position);
        int[] id = {Move[0], Move[1]};
        int[] move = {Move[2], Move[3]};

        Piece piece = getPieceFromID(id);
        Piece deadpiece = getPieceFromID(move);
        if (deadpiece != null) deadpiece.die();

        updatePos(piece.id, move);
        piece.update(move);
        updatePieces();
    }


    private void updatePos(int[] oldPos, int[] newPos) {
        char piece = position[oldPos[0]][oldPos[1]];

        position[oldPos[0]][oldPos[1]] = '0';
        position[newPos[0]][newPos[1]] = piece;
    }

    private void updatePieces() {
        int[] whiteKing = pieces[0].id;
        int[] blackKing = pieces[1].id;
        boolean whiteCheck = legalmoves.legality.isInCheck(whiteKing, position, false);
        boolean blackCheck = legalmoves.legality.isInCheck(blackKing, position, true);

        for (Piece piece : pieces) {
            boolean isInCheck = piece.isblack ? blackCheck : whiteCheck;
            int[] kingID = piece.isblack ? blackKing : whiteKing;
            piece.updateMoves(legalmoves.updatePiece(piece.id, kingID, position, piece.type, isInCheck));
        }
    }


    public void run() {
        opponentMove();
    }
}

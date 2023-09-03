import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Brain {
    static int Infinity = 99999999;
    boolean isblack;
    int maxDepth = 6;
    int Depth = 6;
    int minDepth = 4;
    int positions = 0;
    boolean startCount = true;
    int transpositions = 0;
    PieceTable piecetable = new PieceTable();


    HashMap<Long, SavedValue> hashtable = new HashMap<>();
    HashTable table = new HashTable();
    long hashed = 0;
    AllMoves searchedMoves = new AllMoves(new ArrayList<Move>(), false);

    int pawn = 100;
    int knight = 300;
    int bishop = 300;
    int rook = 500;
    int queen = 900;

    Brain(boolean ISBLACK) {
        isblack = ISBLACK;
    }

    public Pos board2Pos(char[][] position) {
        Pos pos = new Pos();
        pos.position = position;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                char box = position[i][j];
                if (box == '0') continue;

                
                long num = getNum(i, j);
                pos.updatePiece(box, num);
            }
        }

        return pos;
    }

    
    private Value minimaxExtra(Pos pos, int depth, boolean isblack, Value value) {
        if (depth == maxDepth) {
            return value;
        }

        Depth++;
        int factor = (float) (Depth/2f) == Depth/2 ? 1 : -1;

        Collections.sort(searchedMoves.moves, (move1, move2) -> {
            
            return factor*(move2.score - move1.score);
        });
        if (Depth == maxDepth) startCount = true;
        return minimaxExtra(pos, Depth, isblack, minimax(pos, Depth, -Infinity, Infinity, isblack));
    }
    


    private Value minimax(Pos pos, int depth, int alpha, int beta, boolean isblack) {
        if (depth == 0) {
            return pos.evaluate();
        }
        if (!isblack) {
            Value value = new Value(-Infinity);
            AllMoves allmoves = pos.getAllMoves(isblack, depth);
            for (Move move : allmoves.moves) {
                pos.applyMove(move, depth);
                
                updateHashed(move, isblack);
                SavedValue savedvalue = hashtable.get(hashed);
                if (savedvalue != null) {
                    if (savedvalue.depth >= depth - 1) {
                        if (startCount) transpositions++;
                        if (savedvalue.score > value.score) {
                            value.score = savedvalue.score;
                            value.move = pos.posMove;
                        }

                        pos.unApplyMove(move);
                        updateHashed(move, isblack);
                        alpha = Math.max(value.score, alpha);
                        move.score = savedvalue.score;
                        if (value.score >= beta) {
                            savedvalue.alpha = alpha;
                            savedvalue.beta = beta;
                            break;
                        }
                        continue;
                    }
                }
                 
                Value newValue = minimax(pos, depth - 1, alpha, beta, !isblack);
                pos.unApplyMove(move);
                
                
                if (newValue.score > value.score) value = newValue;
                alpha = Math.max(value.score, alpha);
                
                hashtable.put(hashed, new SavedValue(newValue.score, depth - 1, alpha, beta));
                updateHashed(move, isblack);
                
                move.score = newValue.score;
                if (value.score >= beta) break;
            }
            if (value.move == null) {
                value.move = pos.posMove;
                if (!allmoves.isInCheck) value.score = 0;
            }
            if (depth == Depth) searchedMoves = allmoves;

            return value;
        } else {
            Value value = new Value(Infinity);
            AllMoves allMoves = pos.getAllMoves(isblack, depth);
            for (Move move : allMoves.moves) {
                pos.applyMove(move, depth);
                
                updateHashed(move, isblack);
                SavedValue savedvalue = hashtable.get(hashed);
                if (savedvalue != null) {
                    if (savedvalue.depth >= depth - 1) {
                        if (startCount) transpositions++;
                        if (savedvalue.score < value.score) {
                            value.score = savedvalue.score;
                            value.move = pos.posMove;
                        }

                        pos.unApplyMove(move);
                        updateHashed(move, isblack);
                        beta = Math.min(value.score, beta);
                        move.score = savedvalue.score;
                        if (value.score <= alpha) {
                            savedvalue.beta = beta;
                            savedvalue.alpha = alpha;
                            break;
                        }
                        continue;
                    }
                }
                 
                Value newValue = minimax(pos, depth - 1, alpha, beta, !isblack);
                pos.unApplyMove(move);
                

                if (newValue.score < value.score) value = newValue;
                beta = Math.min(value.score, beta);
                move.score = newValue.score;
                
                hashtable.put(hashed, new SavedValue(newValue.score, depth - 1, alpha, beta));
                updateHashed(move, isblack);

                if (value.score <= alpha) break;
            }
            if (value.move == null) {
                value.move = pos.posMove;
                if (!allMoves.isInCheck) value.score = 0;
            }
            if (depth == Depth) searchedMoves = allMoves;

            return value;
        }
    }



    public int[] makeMove(char[][] position) {
        positions = 0;
        transpositions = 0;
        Pos pos = board2Pos(position);
        hashed = table.hash(position, isblack);
        //Value value = minimax(pos, Depth, -Infinity, Infinity, isblack);
        startCount = false;
        searchedMoves = new AllMoves(new ArrayList<Move>(), false);
        Depth = minDepth;
        Value value = minimaxExtra(pos, Depth, isblack, minimax(pos, Depth, -Infinity, Infinity, isblack));
        System.out.println("positions: " + String.valueOf(positions));
        System.out.println("transpositions: " + String.valueOf(transpositions));
        System.out.println("-------------------------------");
        return new int[]{value.move.oldPos[0], value.move.oldPos[1], value.move.move[0], value.move.move[1]};
    }



    private long getNum(int y, int x) {
        int i = 63 - (y*8 + x);
        long num = 1;

        num = num << i;
        return num;
    }

    
    private void updateHashed(Move move, boolean isblack) {
        if (isblack) hashed ^= table.blackToMove;
        hashed ^= table.getHashFromId(move.piece, move.oldPos);
        hashed ^= table.getHashFromId(move.piece, move.move);

        if (move.deadpiece != '0') hashed ^= table.getHashFromId(move.deadpiece, move.move);
    }
    

    private class Pos {
        Legalmoves legalmoves = new Legalmoves();
        char[][] position;

        Move posMove;
        long whiteKing = 0;
        long blackKing = 0;
        long whiteKnight = 0;
        long blackKnight = 0;
        long whiteBishop = 0;
        long blackBishop = 0;
        long whiteRook = 0;
        long blackRook = 0;
        long whiteQueen = 0;
        long blackQueen = 0;
        long whitePawn = 0;
        long blackPawn = 0;

        Pos() {

        }

        public Value evaluate() {
            int score = 0;
            char[] pieces = {'p', 'n', 'b', 'r', 'q', 'k', 'P', 'N', 'B', 'R', 'Q', 'K'};
            for (char piece : pieces) {
                int factor = IsBlack(piece) ? -1 : 1;
                int worth = getWorth(piece);
                List<int[]> ids = numsFromLong(numFromType(piece));
                for (int[] id : ids) {
                    score += factor*(worth + piecetable.getPosScore(piece, id));
                }
                //score += factor*worth*numsFromLong(numFromType(piece)).size();
            }

            Value value = new Value(score);
            value.move = posMove;
            return value;
        }

        private int getWorth(char type) {
            switch(Character.toLowerCase(type)) {
                case 'p':
                    return pawn;
                case 'n':
                    return knight;
                case 'b':
                    return bishop;
                case 'r':
                    return rook;
                case 'q':
                    return queen;
                case 'k':
                    return 0;
            }

            return 0;
        }

        public void applyMove(Move move, int depth) {
            updatePiece(move.piece, getNum(move.oldPos[0], move.oldPos[1]));
            updatePiece(move.piece, getNum(move.move[0], move.move[1]));
            if (move.deadpiece != '0') {
                updatePiece(move.deadpiece, getNum(move.move[0], move.move[1]));
            }

            if (depth == Depth) posMove = move;
            if (startCount) positions++;

            position[move.oldPos[0]][move.oldPos[1]] = '0';
            position[move.move[0]][move.move[1]] = move.piece;
        }

        public void unApplyMove(Move move) {
            updatePiece(move.piece, getNum(move.oldPos[0], move.oldPos[1]));
            updatePiece(move.piece, getNum(move.move[0], move.move[1]));
            if (move.deadpiece != '0') {
                updatePiece(move.deadpiece, getNum(move.move[0], move.move[1]));
            }

            position[move.oldPos[0]][move.oldPos[1]] = move.piece;
            position[move.move[0]][move.move[1]] = move.deadpiece;
        }

        public void updatePiece(char type, long num) {
            char kind = Character.toLowerCase(type);
            boolean isblack = type == kind;

            switch(kind) {
                case 'p':
                    if (isblack) blackPawn ^= num;
                    else whitePawn ^= num;
                    break;
                case 'n':
                    if (isblack) blackKnight ^= num;
                    else whiteKnight ^= num;
                    break;
                case 'b':
                    if (isblack) blackBishop ^= num;
                    else whiteBishop ^= num;
                    break;
                case 'r':
                    if (isblack) blackRook ^= num;
                    else whiteRook ^= num;
                    break;
                case 'q':
                    if (isblack) blackQueen ^= num;
                    else whiteQueen ^= num;
                    break;
                case 'k':
                    if (isblack) blackKing ^= num;
                    else whiteKing ^= num;
                    break;
            }
        }
        
        public AllMoves getAllMoves(boolean isblack, int depth) {
            if (depth == Depth && searchedMoves.moves.size() != 0) {
                return searchedMoves;
            }
            
            List<Move> moves = new ArrayList<Move>();
            int[] kingID = findKing(isblack);
            boolean isInCheck = legalmoves.legality.isInCheck(kingID, position, isblack);
            long map = getPawnMap(isblack);

            char[] pieces;
            if (isblack) pieces = new char[]{'p', 'n', 'b', 'r', 'q', 'k'};
            else pieces = new char[]{'P', 'N', 'B', 'R', 'Q', 'K'};

            for (char type : pieces) {
                List<int[]> ids = numsFromLong(numFromType(type));
                for (int[] id : ids) {
                    List<int[]> pieceMoves = legalmoves.updatePiece(id, kingID, position, type, isInCheck).moves;
                    for (int[] move : pieceMoves) {
                        char deadpiece = position[move[0]][move[1]];
                        Move Move = new Move(type, deadpiece, id, move);
                        Move.score = getMoveScore(Move, map);
                        moves.add(Move);
                    }
                }
            }

            Collections.sort(moves, (move1, move2) -> {
                return move2.score - move1.score;
            });
            

            return new AllMoves(moves, isInCheck);
        }

        private int getMoveScore(Move move, long map) {
            int score = 0;
            char piece = move.piece;
            char deadpiece = move.deadpiece;

            if (deadpiece != '0') {
                int diff = (getWorth(piece) - getWorth(deadpiece))*10;
                if (diff == 0) diff = getWorth(piece)*10;
                if (diff < 0) diff *= -1/10;
                score += diff;
            }

            if (map >= (map ^ getNum(move.move[0], move.move[1]))) {
                score -= (getWorth(piece) - getWorth('p'))*10;
            }

            score += piecetable.getPosScore(piece, move.move);
            return score;
        } 

        private long getPawnMap(boolean isblack) {
            long forward;
            if (isblack) {
                forward = whitePawn << 8;
            } else forward = blackPawn >> 8;

            long forwardRight = forward >> 1;
            long forwardLeft = forward << 1;

            return forwardLeft ^ forwardRight;
        }

        public char[][] pos2Board() {
            char[][] position = {
                {'0', '0', '0', '0', '0', '0', '0', '0'},
                {'0', '0', '0', '0', '0', '0', '0', '0'},
                {'0', '0', '0', '0', '0', '0', '0', '0'},
                {'0', '0', '0', '0', '0', '0', '0', '0'},
                {'0', '0', '0', '0', '0', '0', '0', '0'},
                {'0', '0', '0', '0', '0', '0', '0', '0'},
                {'0', '0', '0', '0', '0', '0', '0', '0'},
                {'0', '0', '0', '0', '0', '0', '0', '0'}
            };

            long[] nums = {
                whitePawn, whiteKnight, whiteBishop, whiteRook, whiteQueen, whiteKing,
                blackPawn, blackKnight, blackBishop, blackRook, blackQueen, blackKing
            };
            char[] types = {
                'P', 'N', 'B', 'R', 'Q', 'K', 'p', 'n', 'b', 'r', 'q', 'k'
            };

            for (int i = 0; i < 12; i++) {
                long num = nums[i];
                char type = types[i];
                List<int[]> locs = numsFromLong(num);
                for (int[] loc : locs) {
                    position[loc[0]][loc[1]] = type;
                }
            }

            return position;
        }

        private List<int[]> numsFromLong(long num) {
            String string = Long.toBinaryString(num);
            int margin = 64 - string.length();
            List<int[]> nums = new ArrayList<int[]>();

            for (int i = 0; i < string.length(); i++) {
                if (string.charAt(i) == '0') continue;
                int y = (margin + i) / 8;
                int x = margin + i - y*8;
                
                nums.add(new int[]{y, x});
            }

            return nums;
        }
        
        private long numFromType(char type) {
            char kind = Character.toLowerCase(type);
            boolean isblack = type == kind;

            switch(kind) {
                case 'p':
                    if (isblack) return blackPawn;
                    return whitePawn;
                case 'n':
                    if (isblack) return blackKnight;
                    return whiteKnight;
                case 'b':
                    if (isblack) return blackBishop;
                    return whiteBishop;
                case 'r':
                    if (isblack) return blackRook;
                    return whiteRook;
                case 'q':
                    if (isblack) return blackQueen;
                    return whiteQueen;
                case 'k':
                    if (isblack) return blackKing;
                    return whiteKing;
            }

            return 0;
        }

        public int[] findKing(boolean isblack) {
            long king = isblack ? blackKing : whiteKing;
            List<int[]> nums = numsFromLong(king);
            if (nums.size() == 0) {
                char[][] pos = pos2Board();
                for (char[] row : pos) {
                    String line = "";
                    for (char box : row) {
                        line += box;
                    }

                    System.out.println(line);
                }
                System.out.println("---------");
                System.out.println(posMove.piece);
                System.out.println(Character.toString(posMove.move[0]) + Character.toString(posMove.move[1]));
                System.out.println("---------");
            }

            return nums.get(0);
        }
    }

    private class Move {
        int score = 0;
        char deadpiece;
        char piece;
        int[] oldPos;
        int[] move;
        Move(char Piece, char Deadpiece, int[] OldPos, int[] Move) {
            deadpiece = Deadpiece;
            piece = Piece;
            oldPos = OldPos;
            move = Move;
        }
    }

    private class Value {
        int score;
        Move move;
        Value(int Score) {
            score = Score;
        }
    }

    private boolean IsBlack(char type) {
        return Character.toLowerCase(type) == type;
    }

    private class AllMoves {
        boolean isInCheck;
        List<Move> moves = new ArrayList<Move>();
        AllMoves(List<Move> Moves, boolean ISINCHECK) {
            isInCheck = ISINCHECK;
            moves = Moves;
        }
    }

    private class PieceTable {
        int[] pawnPos = {
            0,  0,  0,  0,  0,  0,  0,  0,
            50, 50, 50, 50, 50, 50, 50, 50,
            10, 10, 20, 30, 30, 20, 10, 10,
            5,  5, 10, 25, 25, 10,  5,  5,
            0,  0,  0, 20, 20,  0,  0,  0,
            5, -5,-10,  0,  0,-10, -5,  5,
            5, 10, 10,-20,-20, 10, 10,  5,
            0,  0,  0,  0,  0,  0,  0,  0
        };

        int[] knightPos = {
            -50,-40,-30,-30,-30,-30,-40,-50,
            -40,-20,  0,  0,  0,  0,-20,-40,
            -30,  0, 10, 15, 15, 10,  0,-30,
            -30,  5, 15, 20, 20, 15,  5,-30,
            -30,  0, 15, 20, 20, 15,  0,-30,
            -30,  5, 10, 15, 15, 10,  5,-30,
            -40,-20,  0,  5,  5,  0,-20,-40,
            -50,-40,-30,-30,-30,-30,-40,-50,
        };

        int[] bishopPos = {
            -20,-10,-10,-10,-10,-10,-10,-20,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -10,  0,  5, 10, 10,  5,  0,-10,
            -10,  5,  5, 10, 10,  5,  5,-10,
            -10,  0, 10, 10, 10, 10,  0,-10,
            -10, 10, 10, 10, 10, 10, 10,-10,
            -10,  5,  0,  0,  0,  0,  5,-10,
            -20,-10,-10,-10,-10,-10,-10,-20,
        };
        
        int[] rookPos = {
            0,  0,  0,  0,  0,  0,  0,  0,
            5, 10, 10, 10, 10, 10, 10,  5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            0,  0,  0,  5,  5,  0,  0,  0
        };


        int[] queenPos = {
            -20,-10,-10, -5, -5,-10,-10,-20,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -10,  0,  5,  5,  5,  5,  0,-10,
            -5,  0,  5,  5,  5,  5,  0, -5,
            0,  0,  5,  5,  5,  5,  0, -5,
            -10,  5,  5,  5,  5,  5,  0,-10,
            -10,  0,  5,  0,  0,  0,  0,-10,
            -20,-10,-10, -5, -5,-10,-10,-20
        };


        int[] kingPos = {
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -20,-30,-30,-40,-40,-30,-30,-20,
            -10,-20,-20,-20,-20,-20,-20,-10,
            20, 20,  0,  0,  0,  0, 20, 20,
            20, 30, 10,  0,  0, 10, 30, 20
        };
        PieceTable() {}

        public int getPosScore(char type, int[] id) {
            int[] table = new int[]{};
            boolean isblack = IsBlack(type);
            switch(Character.toLowerCase(type)) {
                case 'p':
                    table = pawnPos;
                    break;
                case 'n':
                    table = knightPos;
                    break;
                case 'b':
                    table = bishopPos;
                    break;
                case 'r':
                    table = rookPos;
                    break;
                case 'q':
                    table = queenPos;
                    break;
                case 'k': 
                    table = kingPos;
                    break;
            }

            int i = id[0]*8 + id[1];
            if (isblack)
                i = 63 - i;
            return table[i];
        }
    } 
}
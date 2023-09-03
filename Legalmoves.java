import java.util.ArrayList;
import java.util.List;

public class Legalmoves {
    IsLegal legality = new IsLegal();

    Legalmoves() {

    }

    public Moves updatePiece(int[] id, int[] kingID, char[][] position, char type, boolean isInCheck) {
        char kind = Character.toLowerCase(type);
        boolean isblack = type == kind;

        switch(kind) {
            case 'n':
                return knight(id, kingID, position, isblack, isInCheck);
            case 'b':
                return bishop(id, kingID, position, isblack, isInCheck);
            case 'r':
                return rook(id, kingID, position, isblack, isInCheck);
            case 'q':
                return queen(id, kingID, position, isblack, isInCheck);
            case 'p':
                return pawn(id, kingID, position, isblack, isInCheck);
            case 'k':
                return king(id, position, isblack, isInCheck);
        }

        return null;
    }


    public Moves knight(int[] id, int[] kingID, char[][] position, boolean isblack, boolean isInCheck) {
        List<int[]> moves = new ArrayList<int[]>();

        int y = id[0];
        int x = id[1];
        char isPinned = legality.isPinned(id, kingID, position, isblack);

        int[] upRight = {y - 2, x + 1};
        int[] upLeft = {y - 2, x - 1};
        int[] rightUp = {y - 1, x + 2};
        int[] leftUp = {y - 1, x - 2};
        int[] downRight = {y + 2, x + 1};
        int[] downLeft = {y + 2, x - 1};
        int[] rightDown = {y + 1, x + 2};
        int[] leftDown = {y + 1, x - 2};

        int[][] poses = {upRight, upLeft, rightUp, rightDown, leftDown, leftUp, downLeft, downRight};

        for (int[] pos : poses) {
            if (isOutOfBounds(pos)) continue;
            char box = position[pos[0]][pos[1]];

            if (box == '0') {
                if (!legality.isLegal(id, pos, kingID, position, isblack, isInCheck, isPinned)) continue;
                moves.add(pos);
                continue;
            }

            boolean checker = IsBlack(box);
            if (checker != isblack) {
                if (!legality.isLegal(id, pos, kingID, position, isblack, isInCheck, isPinned)) continue;
                moves.add(pos);
            }  
        }

        return new Moves(moves);
    }


    public Moves rook(int[] id, int[] kingID, char[][] position, boolean isblack, boolean isInCheck) {
        List<int[]> moves = new ArrayList<int[]>();
        int y = id[0];
        int x = id[1];
        char isPinned = legality.isPinned(id, kingID, position, isblack);

        boolean[] blockers = {false, false, false, false};
        for (int i = 1; i < 8; i++) {
            int[] up = {y - i, x};
            int[] down = {y + i, x};
            int[] right = {y, x + i};
            int[] left = {y, x - i};

            int[][] pos = {up, down, right, left};
            for (int j = 0; j < 4; j++) {
                if (isOutOfBounds(pos[j]) | blockers[j]) continue;

                char box = position[pos[j][0]][pos[j][1]];
                
                if (box == '0') {
                    if (!legality.isLegal(id, pos[j], kingID, position, isblack, isInCheck, isPinned)) continue;
                    moves.add(pos[j]);
                    continue;
                }

                boolean checker = IsBlack(box);
                if (checker != isblack) {
                    if (!legality.isLegal(id, pos[j], kingID, position, isblack, isInCheck, isPinned)) {
                        blockers[j] = true;
                        continue;
                    }
                    moves.add(pos[j]);
                }
                blockers[j] = true;
            }
        }

        return new Moves(moves);
    }


    public Moves bishop(int[] id, int[] kingID, char[][] position, boolean isblack, boolean isInCheck) {
        List<int[]> moves = new ArrayList<int[]>();
        int y = id[0];
        int x = id[1];
        char isPinned = legality.isPinned(id, kingID, position, isblack);

        boolean[] blockers = {false, false, false, false};
        for (int i = 1; i < 8; i++) {
            int[] upRight = {y - i, x + i};
            int[] upLeft = {y - i, x - i};
            int[] downRight = {y + i, x + i};
            int[] downLeft = {y + i, x - i};

            int[][] pos = {upRight, upLeft, downLeft, downRight};
            for (int j = 0; j < 4; j++) {
                if (isOutOfBounds(pos[j]) | blockers[j]) continue;
                char box = position[pos[j][0]][pos[j][1]];

                if (box == '0') {
                    if (!legality.isLegal(id, pos[j], kingID, position, isblack, isInCheck, isPinned)) continue;
                    moves.add(pos[j]);
                    continue;
                }

                boolean checker = IsBlack(box);
                if (checker != isblack) {
                    if (!legality.isLegal(id, pos[j], kingID, position, isblack, isInCheck, isPinned)) {
                        blockers[j] = true;
                        continue;
                    }
                    moves.add(pos[j]);
                }
                blockers[j] = true;
            }  
        }

        return new Moves(moves);
    }


    public Moves queen(int[] id, int[] kingID, char[][] position, boolean isblack, boolean isInCheck) {
        List<int[]> moves = new ArrayList<int[]>();
        int y = id[0];
        int x = id[1];
        char isPinned = legality.isPinned(id, kingID, position, isblack);

        boolean[] blockers = {false, false, false, false, false, false, false, false};
        for (int i = 1; i < 8; i++) {
            int[] up = {y - i, x};
            int[] down = {y + i, x};
            int[] right = {y, x + i};
            int[] left = {y, x - i};
            int[] upRight = {y - i, x + i};
            int[] upLeft = {y - i, x - i};
            int[] downRight = {y + i, x + i};
            int[] downLeft = {y + i, x - i};

            int[][] pos = {up, down, left, right, upRight, upLeft, downLeft, downRight};
            for (int j = 0; j < 8; j++) {
                if (isOutOfBounds(pos[j]) | blockers[j]) continue;
                char box = position[pos[j][0]][pos[j][1]];

                if (box == '0') {
                    if (!legality.isLegal(id, pos[j], kingID, position, isblack, isInCheck, isPinned)) continue;
                    moves.add(pos[j]);
                    continue;
                }

                boolean checker = IsBlack(box);
                if (checker != isblack) {
                    if (!legality.isLegal(id, pos[j], kingID, position, isblack, isInCheck, isPinned)) {
                        blockers[j] = true;
                        continue;
                    }
                    moves.add(pos[j]);
                }
                blockers[j] = true;
            }  
        }

        return new Moves(moves);
    }


    public Moves pawn(int[] id, int[] kingID, char[][] position, boolean isblack, boolean isInCheck) {
        List<int[]> moves = new ArrayList<int[]>();
        int y = id[0];
        int x = id[1];
        char isPinned = legality.isPinned(id, kingID, position, isblack);
        int direction = isblack ? 1 : -1;

        int[] forward = {y + direction, x};
        int[] forwardRight = {y + direction, x + 1};
        int[] forwardLeft = {y + direction, x - 1};

        if (isOutOfBounds(forward)) return new Moves(moves);
        char forBox = position[forward[0]][forward[1]];
        if (forBox == '0') {
            if(legality.isLegal(id, forward, kingID, position, isblack, isInCheck, isPinned)) moves.add(forward);
            if ((y == 1 && isblack) | (y == 6 && !isblack)) {
                int[] doubleStep = {y + direction*2, x};
                char ForBox = position[doubleStep[0]][doubleStep[1]];
                if (ForBox == '0') {
                    if (legality.isLegal(id, doubleStep, kingID, position, isblack, isInCheck, isPinned)) moves.add(doubleStep);
                }
            }
        }

        int[][] corners = {forwardLeft, forwardRight};
        for (int[] corner : corners) {
            if (isOutOfBounds(corner)) continue;
            char oppBox = position[corner[0]][corner[1]];
            if (oppBox == '0') continue;
            
            boolean checker = IsBlack(oppBox);
            if (checker != isblack) {
                if (!legality.isLegal(id, corner, kingID, position, isblack, isInCheck, isPinned)) continue;
                moves.add(corner);
            }
        }

        return new Moves(moves);
    }


    public Moves king(int[] id, char[][] position, boolean isblack, boolean isInCheck) {
        List<int[]> moves = new ArrayList<int[]>();
        int y = id[0];
        int x = id[1];
        char isPinned = '0';

        int[] up = {y - 1, x};
        int[] down = {y + 1, x};
        int[] right = {y, x + 1};
        int[] left = {y, x - 1};
        int[] upRight = {y - 1, x + 1};
        int[] upLeft = {y - 1, x - 1};
        int[] downRight = {y + 1, x + 1};
        int[] downLeft = {y + 1, x - 1};

        int[][] poses = {up, down, left, right, upRight, upLeft, downLeft, downRight};
        for (int[] pos : poses) {
            if (isOutOfBounds(pos)) continue;
            char box = position[pos[0]][pos[1]];
            if (box == '0') {
                if (!legality.isLegal(id, pos, id, position, isblack, isInCheck, isPinned)) continue;
                moves.add(pos);
                continue;
            }

            boolean checker = IsBlack(box);
            if (checker != isblack) {
                if (!legality.isLegal(id, pos, id, position, isblack, isInCheck, isPinned)) continue;
                moves.add(pos);
            }
        }

        return new Moves(moves);
    }



    private boolean isOutOfBounds(int[] id) {
        int y = id[0];
        int x = id[1];
        if (y < 0 | x < 0 | x > 7 | y > 7) return true;
        return false;
    }
    private boolean IsBlack(char type) {
        return Character.toLowerCase(type) == type;
    }
}

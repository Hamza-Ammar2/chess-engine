public class IsLegal {
    IsLegal() {}

    public boolean isInCheck(int[] kingID, char[][] position, boolean isblack) {
        int y = kingID[0];
        int x = kingID[1];
        char[][] attackers = {
            new char[]{'q', 'r'}, new char[]{'q', 'r'},new char[]{'q', 'r'}, new char[]{'q', 'r'},
            new char[]{'q', 'b'}, new char[]{'q', 'b'}, new char[]{'q', 'b'}, new char[]{'q', 'b'}
        };
        char[][] specialAttackers = {
            new char[]{'q', 'r', 'k'}, new char[]{'q', 'r', 'k'},new char[]{'q', 'r', 'k'}, new char[]{'q', 'r', 'k'},
            new char[]{'q', 'b', 'k'}, new char[]{'q', 'b', 'k'}, new char[]{'q', 'b', 'k'}, new char[]{'q', 'b', 'k'}
        };
        if (isblack) {
            specialAttackers[7] = new char[]{'q', 'b', 'k', 'p'};
            specialAttackers[6] = new char[]{'q', 'b', 'k', 'p'};
        } else {
            specialAttackers[5] = new char[]{'q', 'b', 'k', 'p'};
            specialAttackers[4] = new char[]{'q', 'b', 'k', 'p'};
        }

        boolean[] safes = {false, false, false, false, false, false, false, false};
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
            char[][] attacks = attackers;
            if (i == 1) attacks = specialAttackers;
            for (int j = 0; j < 8; j++) {
                if (isOutOfBounds(pos[j]) | safes[j]) continue;
                char box = position[pos[j][0]][pos[j][1]];

                if (box == '0') continue;
                boolean checker = IsBlack(box);
                if (checker == isblack) {
                    safes[j] = true;
                    continue;
                }

                if (contains(attacks[j], Character.toLowerCase(box))) return true;
                safes[j] = true;
            }
        }

        if (knightChecks(kingID, position, isblack)) return true;
        return false;
    }


    public char isPinned(int[] id, int[] kingID, char[][] position, boolean isblack) {
        int y = id[0];
        int x = id[1];
        int Y = kingID[0];
        int X = kingID[1];
        char king = position[Y][X];
        char[] attackers = new char[]{};
        char dir = '0';

        int xDir = 2;
        int yDir = 2;

        if (Y == y) {
            xDir = X > x ? 1 : -1;
            yDir = 0;
            attackers = new char[]{'q', 'r'};
            dir = 'h';
        }
        if (X == x) {
            xDir = 0;
            yDir = Y > y ? 1 : -1;
            attackers = new char[]{'q', 'r'};
            dir = 'v';
        }
        if ((X-x)*(X-x) == (Y-y)*(Y-y)) {
            xDir = X > x ? 1 : -1;
            yDir = Y > y ? 1 : -1;
            attackers = new char[]{'q', 'b'};
            dir = 'd';
        }

        if (xDir == 2 | yDir == 2) return '0';
        char[] safes = {'0', '0'};
        for (int i = 1; i < 8; i++) {
            int[] towards = {y + i*yDir, x + i*xDir};
            int[] away = {y - i*yDir, x - i*xDir};
            
            int[][] pos = {towards, away};
            for (int j = 0; j < 2; j++) {
                if (isOutOfBounds(pos[j]) | safes[j] != '0') continue;
                char box = position[pos[j][0]][pos[j][1]];

                if (box == '0') continue;
                if (box == king) {
                    safes[j] = '2';
                    continue;
                }

                boolean checker = IsBlack(box);
                if (checker == isblack) {
                    safes[j] = '1';
                    continue;
                }
                if (contains(attackers, Character.toLowerCase(box))) {
                    safes[j] = '2';
                    continue;
                }
                safes[j] = '1';
            }
        }

        if (safes[0] == '2' && safes[1] == '2') return dir;
        return '0';
    }


    public boolean isLegal(int[] id, int[] newPos, int[] kingID, char[][] position, boolean isblack, boolean isInCheck, char isPinned) {
        if (isInCheck && isPinned != '0') return false;
        char piece = position[id[0]][id[1]];
        if (isInCheck | piece == 'k' | piece == 'K') {
            char old = position[newPos[0]][newPos[1]];
            position[newPos[0]][newPos[1]] = piece;
            position[id[0]][id[1]] = '0';

            int[] king = kingID;
            if (piece == 'k' | piece == 'K') king = newPos;
            boolean inCheck = isInCheck(king, position, isblack);

            position[newPos[0]][newPos[1]] = old;
            position[id[0]][id[1]] = piece;
            if (inCheck) return false;
            return true; 
        }

        if (isPinned == '0') return true;
        if (isAligned(isPinned, id, newPos, kingID)) return true;
        return false;
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
    private boolean contains(char[] list, char type) {
        for (char elm : list) {
            if (type == elm) return true;
        }
        return false;
    }
    private boolean knightChecks(int[] kingID, char[][] position, boolean isblack) {
        int y = kingID[0];
        int x = kingID[1];
        
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

            if (box == '0') continue;

            boolean checker = IsBlack(box);
            if (checker == isblack) continue;
            if (Character.toLowerCase(box) == 'n') return true;  
        }

        return false;
    }
    private boolean isAligned(char dir, int[] oldPos, int[] id, int[] kingID) {
        switch(dir) {
            case 'h':
                if (id[0] == kingID[0]) return true;
                return false;
            case 'v':
                if (id[1] == kingID[1]) return true;
                return false;
            case 'd':
                if ((kingID[0] - id[0])*(kingID[0] - id[0]) == (kingID[1] - id[1])*(kingID[1] - id[1])
                    &&  getSign(kingID[0] - id[0]) == getSign(kingID[0] - oldPos[0]) 
                    &&  getSign(kingID[1] - id[1]) == getSign(kingID[1] - oldPos[1])
                ) return true;
                return false;
        }
        return true;
    }
    private int getSign(int x) {
        if (x < 0) return -1;
        return 1;
    }
}
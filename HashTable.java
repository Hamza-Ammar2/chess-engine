public class HashTable {
    long BigNum = Long.MAX_VALUE;
    long blackToMove = (long) (BigNum*Math.random());
    long[][] table = new long[12][64];

    HashTable() {
        init_zobrist();
    }
    
    private void init_zobrist() {
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 64; j++) {
                table[i][j] = (long) (BigNum*Math.random());
            }
        }
    }

    public long hash(char[][] position, boolean isblack) {
        long h = 0;
        if (isblack) h ^= blackToMove;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                char box = position[i][j];
                if (box == '0') continue;

                h ^= getHashFromId(box, new int[]{i, j});
            }
        }

        return h;
    }

    public long getHashFromId(char type, int[] id) {
        int i = 8*id[0] + id[1];
        return table[getIndex(type)][i];
    }


    private int getIndex(char type) {
        switch(type) {
            case 'p':
                return 0;
            case 'P':
                return 1;
            case 'n':
                return 2;
            case 'N':
                return 3;
            case 'b':
                return 4;
            case 'B':
                return 5;
            case 'r':
                return 6;
            case 'R':
                return 7;
            case 'q':
                return 8;
            case 'Q':
                return 9;
            case 'k':
                return 10;
            case 'K':
                return 11;
        }
        return -1;
    }
}
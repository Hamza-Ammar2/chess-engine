public class SavedValue {
    int score;
    int depth;
    int alpha;
    int beta;
    SavedValue(int score, int depth, int alpha, int beta) {
        this.score = score;
        this.depth = depth;
        this.alpha = alpha;
        this.beta = beta;
    }
} 
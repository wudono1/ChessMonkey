package chess.search;
import chess.move;

public class transpositionTable {
    public Entry[] entries;
    public int power2TTEntries = 26;
    public int zobristRightShift = 64 - power2TTEntries;
    public int numTTEntries = 2^power2TTEntries;
    //public int entrySize = 80;
    public byte bucketSize = 4;
    public byte bucketIntervals = 2;
    public int lookupFailed = 30001;
    public final short MATE_SCORE = -25000;
    private final short FLAG_EXACT = 1;
    private final short FLAG_LOWER_BOUND = 0;
    private final short FLAG_UPPER_BOUND = 2;
    private final int moveRightShift = 0;
    private final int evalRightShift = 16;

    public transpositionTable() {
        //long longEntries = (tableSizeInGigabytes * 1024 * 1024 * 1024L - 16) / (entrySize + 4 + (8 - ((entrySize + 4) % 8)));
        //total bytes available divided by bytes per entry
        entries = new Entry[numTTEntries + 1];
        clearTable();

    }

    public void clearTable() {
        for (int i = 0; i < entries.length; i++)
        {
            entries[i] = new Entry();
        }
    }

    public int getIndex(long zobristHash) {
        return (int)(zobristHash >>> zobristRightShift);
    }

    public short convertMateScoreEvalToEntry(short score, int depthSearched) { //converting mate scores
        if (score <= -15000) {
            return (short)(score + 10 * (depthSearched));
        }
        return score;
    }

    public void addEval(long zHash, short alpha, short beta, int depthFromCurrent, short eval, int bestMove) {
        //depthFromCurrentToEnd = (total max iteration depth) - (depth from root to current position)
        eval = convertMateScoreEvalToEntry(eval, depthFromCurrent);
        byte flag = 1;
        if (eval == alpha) {flag = 0;}
        if (eval == beta) {flag = 2;}
        int index = getIndex(zHash);
        int lowestDepth = Integer.MAX_VALUE;
        int lowestDepthIndex = index;
        for (int i = index; i < index + bucketSize * bucketIntervals; i += bucketIntervals) {
            if (i < numTTEntries && entries[i].depth == -1) {
                entries[i] = new Entry(zHash, eval, (short)(depthFromCurrent), bestMove, flag);
                return;
            } else if (entries[i].zKey == zHash && entries[i].depth <= depthFromCurrent) {
                entries[i].changeEvals(eval, bestMove, (short)(depthFromCurrent), flag);
                return;
            } else {
                if (entries[i].depth < lowestDepth) {
                    lowestDepth = entries[i].depth;
                    lowestDepthIndex = i;
                }
            }
        }
        entries[lowestDepthIndex] = new Entry(zHash, eval, (short)(depthFromCurrent), bestMove, flag);
    }

    public int convertMateScoreEntryToEval(int value, int depthToCurrentRoot) {
        int score = value >> evalRightShift;
        if (score <= -15000) {
            return (short)(MATE_SCORE + 10 * (depthToCurrentRoot));
        }
        return score;
    }

    public int returnPastEval(long zHash, int plyDepthToCurrent, int plyDepthRemaining, int alpha, int beta) {
        //plyDepthToCurrent = (max iteration depth) - (depth from root node to current position)
        int eval = lookupFailed;
        int index = getIndex(zHash);
        for (int i = index; i < index + bucketSize * bucketIntervals; i += bucketIntervals) {
            if (i < numTTEntries) {
                if (entries[i].zKey == zHash && entries[i].depth >= plyDepthRemaining) {
                    eval = convertMateScoreEntryToEval(entries[i].evalAndMove,
                            plyDepthToCurrent);
                    switch (entries[i].flag) {
                        case (FLAG_EXACT):
                            return eval;
                        case (FLAG_UPPER_BOUND): //move was worse than alpha cutoff at time of evaluation
                            if (eval < alpha) {
                                return alpha;
                            }
                        case (FLAG_LOWER_BOUND): //move was greater than beta cutoff at time of evaluation
                            if (eval > beta) {
                                return beta;
                            }
                    }
                }
            }
        }
        //if no suitable eval found, return lookup failed
        return eval;
    }

}

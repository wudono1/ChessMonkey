package chess.search;
import chess.move;

public class transpositionTable {
    public Entry[] entries;
    public int numValues;
    public int entrySize = 80;
    public byte bucketSize = 4;
    public byte bucketIntervals = 2;
    public int lookupFailed = 30001;
    public final short MATE_SCORE = -25000;
    private final short FLAG_EXACT = 1;
    private final short FLAG_LOWER_BOUND = 0;
    private final short FLAG_UPPER_BOUND = 2;

    public transpositionTable(int tableSizeInGigabytes) {
        long longEntries = (tableSizeInGigabytes * 1024 * 1024 * 1024L - 16) / (entrySize + 4 + (8 - ((entrySize + 4) % 8)));
        numValues = (int)(longEntries);
        //total bytes available divided by bytes per entry
        entries = new Entry[(int)(longEntries)];
        clearTable();
        clearTable();
    }

    public void clearTable() {
        for (int i = 0; i < entries.length; i++)
        {
            entries[i] = new Entry();
        }
    }

    public int getIndex(long zobristHash) {
        return (int)(zobristHash % numValues);
    }

    public short convertMateScoreEvalToEntry(short score, int depthSearched) {
        if (score <= -17000) {
            return (short)(score + 25 * (depthSearched));
        }
        return score;
    }

    public short convertMateScoreEntryToEval(short score, int depthToCurrentRoot) {
        if (score <= -17000) {
            return (short)(MATE_SCORE + 25 * (depthToCurrentRoot));
        }
        return score;
    }

    public void addEval(long zHash, short alpha, short beta, int depthSearchedFromCurrentPos, short eval, move bestMove) {
        //depthSearchedFromCurrentPos = (total max iteration depth) - (depth from root to current position)
        eval = convertMateScoreEvalToEntry(eval, depthSearchedFromCurrentPos);
        int lowestDepth = Integer.MAX_VALUE;
        int lowestDepthIndex = -1;
        short flag = 1;
        if (eval == alpha) { flag = 0;} if (eval == beta) { flag = 2;}
        int index = getIndex(zHash);
        for (int i = index; i < index + bucketSize * bucketIntervals; i += bucketIntervals) {
            if (entries[i].depth == -1) {
                entries[i] = new Entry(zHash, eval, depthSearchedFromCurrentPos, bestMove, flag);
                return;
            } else if (entries[i].zKey == zHash && entries[i].depth <= depthSearchedFromCurrentPos) {
                entries[i].changeEvals(eval, depthSearchedFromCurrentPos, bestMove, flag);
                return;
            } else {
                if (entries[i].depth < lowestDepth) {
                    lowestDepth = entries[i].depth;
                    lowestDepthIndex = i;
                }
            }
        }
        entries[lowestDepthIndex] = new Entry(zHash, eval, depthSearchedFromCurrentPos, bestMove, flag);
    }

    public int returnPastEval(long zHash, int depthfromRootToCurrent, int depthToSearchFromCurrent, short alpha, short beta) {
        //depthToSearchFromCurrent = (max iteration depth) - (depth from root node to current position)
        int eval = lookupFailed;
        int index = getIndex(zHash);
        for (int i = index; i < index + bucketSize * bucketIntervals; i += bucketIntervals) {
            if (entries[i].zKey == zHash && (entries[i].depth >= depthToSearchFromCurrent || entries[i].eval < -17000)) {
                int out = convertMateScoreEntryToEval(entries[i].eval, depthfromRootToCurrent);
                switch (entries[i].flag) {
                    case (FLAG_EXACT):
                        return out;
                    case (FLAG_UPPER_BOUND): //move was worse than alpha cutoff at time of evaluation
                        if (entries[i].eval < alpha) { return alpha;}
                    case (FLAG_LOWER_BOUND): //move was greater than beta cutoff at time of evaluation
                        if (entries[i].eval > beta) { return beta; }
                }
            }
        }
        //if no suitable eval found, return lookup failed
        return eval;
    }

    public void addEntryToTable() {

    }
}

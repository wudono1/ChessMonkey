package chess.search;
import chess.move;

public class transpositionTable {
    public Entry[] entries;
    public int numValues;
    public int entrySize = 80;
    public byte bucketSize = 4;
    public byte bucketIntervals = 2;
    public int lookupFailed = 30001;
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

    public void addEval(long zHash, short alpha, short beta, int depthFromCurrentPos, short eval, move bestMove) {
        int lowestDepth = Integer.MAX_VALUE;
        int lowestDepthIndex = -1;
        short flag = 1;
        if (eval == alpha) { flag = 0;} if (eval == beta) { flag = 2;}
        int index = getIndex(zHash);
        for (int i = index; i < index + bucketSize * bucketIntervals; i += bucketIntervals) {
            if (entries[i].depth == -1) {
                entries[i] = new Entry(zHash, eval, depthFromCurrentPos, bestMove, flag);
                return;
            } else if (entries[i].zKey == zHash && entries[i].depth <= depthFromCurrentPos) {
                entries[i].changeEvals(eval, depthFromCurrentPos, bestMove, flag);
                return;
            } else {
                if (entries[i].depth < lowestDepth) {
                    lowestDepth = entries[i].depth;
                    lowestDepthIndex = i;
                }
            }
        }
        entries[lowestDepthIndex] = new Entry(zHash, eval, depthFromCurrentPos, bestMove, flag);
    }

    public int returnPastEval(long zHash, int depthFromRoot, short alpha, short beta) {
        int eval = lookupFailed;
        int index = getIndex(zHash);
        for (int i = index; i < index + bucketSize * bucketIntervals; i += bucketIntervals) {
            if (entries[i].zKey == zHash && entries[i].depth >= depthFromRoot) {
                switch (entries[i].flag) {
                    case (FLAG_EXACT):
                        return entries[i].eval;
                    case (FLAG_UPPER_BOUND): //move was worse than alpha cutoff at time of evaluation
                        if (entries[i].eval < alpha) { return alpha;}
                    case (FLAG_LOWER_BOUND): //move was greater than beta cutoff at time of evaluation
                        if (entries[i].eval > beta) { return beta; }
                }
            }
        }
        return eval;
    }

    public void addEntryToTable() {

    }
}

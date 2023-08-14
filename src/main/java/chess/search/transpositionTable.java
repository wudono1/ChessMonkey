package chess.search;
import chess.zobrist;
import chess.move;

public class transpositionTable {
    public Entry[] entries;
    public int numValues;
    public int entrySize = 64;

    public transpositionTable(int tableSizeInGigabytes) {
        long longEntries = (tableSizeInGigabytes * 1024 * 1024 * 1024L - 16) / entrySize;
        numValues = (int)(longEntries);
        //total bytes available divided by bytes per entry
        entries = new Entry[(int)(longEntries)];
    }

    public void clearTable() {
        for (int i = 0; i < entries.length; i++)
        {
            entries[i] = null;
        }
    }

    public int getTableIndex(long zobristHash) {
        return (int)(zobristHash % numValues);
    }
}

# ChessMonkey, a Java chess engine
ChessMonkey takes a position as input in [FEN Notation](https://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation) and outputs the best move for the position and a numerical evaluation in centipawns, where 100 centipawns are equivalent to a one pawn advantage, a positive evaluation is in favor of white, and a negative evaluation is in favor of black.\
To run ChessMonkey, download the files and input a position in FEN Notation in String format into the negamax.java file within the iterativeDeepeningSearch() method. Most popular chess websites such as chess.com and lichess.org automatically return a FEN notation in their analysis boards.

# Features:
Move generation:\
bitboard representation with hyperbola quintessence for sliding pieces\
movegen testing with perft

Search:\
Negamax evaluation with alpha-beta pruning\
Zobrist hashing and transposition tables\
Quiescence search

Evaluation heuristics:\
Material count\
Middlegame and endgame piece-square tables\
Tapered evaluation for game phase transitions

In progress:\
Static exchange evaluation\
Endgame piece-square tables/tapered evaluation and game phase transitions

# Example of Results:


To run our code, you should first find the FEN notation of the position you want to run. This is most easily done by opening an online analysis board editor with an FEN function. In the example below, we want to analyze the starting position of the Najdorf Sicilian. To do this, we go to lichess.org's analysis board ([linked here](https://lichess.org/analysis)), input the position, and find the FEN bar, which is directly below the analysis board. Below is an example. The FEN notation is highlighted under the board:
![alt text](https://github.com/wudono1/ChessMonkey/blob/master/run_examples/25_12_23_EXAMPLES/lichessScreenshot.png)

After finding our FEN notation, we paste the FEN into our negamax.java file (relative path src/main/java/chess/search/negamax.java) into the appropriate line within the method
# iterativeDeepeningSearch
.

package game2048;

import java.util.Formatter;
import java.util.Observable;


/**
 * The state of a game of 2048.
 *
 * @author TODO: YOUR NAME HERE
 */
public class Model extends Observable {
    /**
     * Current contents of the board.
     */
    private Board board;
    /**
     * Current score.
     */
    private int score;
    /**
     * Maximum score so far.  Updated when game ends.
     */
    private int maxScore;
    /**
     * True iff game is ended.
     */
    private boolean gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /**
     * Largest piece value.
     */
    public static final int MAX_PIECE = 2048;

    /**
     * A new 2048 game on a board of size SIZE with no pieces
     * and score 0.
     */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
        gameOver = false;
    }

    /**
     * A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes.
     */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        int size = rawValues.length;
        board = new Board(rawValues, score);
        this.score = score;
        this.maxScore = maxScore;
        this.gameOver = gameOver;
    }

    /**
     * Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     * 0 <= COL < size(). Returns null if there is no tile there.
     * Used for testing. Should be deprecated and removed.
     */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /**
     * Return the number of squares on one side of the board.
     * Used for testing. Should be deprecated and removed.
     */
    public int size() {
        return board.size();
    }

    /**
     * Return true iff the game is over (there are no moves, or
     * there is a tile with value 2048 on the board).
     */
    public boolean gameOver() {
        checkGameOver();
        if (gameOver) {
            maxScore = Math.max(score, maxScore);
        }
        return gameOver;
    }

    /**
     * Return the current score.
     */
    public int score() {
        return score;
    }

    /**
     * Return the current maximum game score (updated at end of game).
     */
    public int maxScore() {
        return maxScore;
    }

    /**
     * Clear the board to empty and reset the score.
     */
    public void clear() {
        score = 0;
        gameOver = false;
        board.clear();
        setChanged();
    }

    /**
     * Add TILE to the board. There must be no Tile currently at the
     * same position.
     */
    public void addTile(Tile tile) {
        board.addTile(tile);
        checkGameOver();
        setChanged();
    }

    /**
     * Tilt the board toward SIDE. Return true iff this changes the board.
     * <p>
     * 1. If two Tile objects are adjacent in the direction of motion and have
     * the same value, they are merged into one Tile of twice the original
     * value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     * tilt. So each move, every tile will only ever be part of at most one
     * merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     * value, then the leading two tiles in the direction of motion merge,
     * and the trailing tile does not.
     */
    public boolean tilt(Side side) {
        boolean changed;
        changed = false;
        board.setViewingPerspective(side);
        int size = board.size();
        for (int i = 0; i < size; i++) {
            int originalScore = score;
            boolean whether = false;
            //calculateWhether
             if (board.tile(i, 3) != null && board.tile(i, 2) != null && board.tile(i, 3).value() == board.tile(i, 2).value() && board.tile(i, 1) != null) {
                 whether = true;
             }




            //
            for (int j = size - 1; j >= 0; j--) {
                // i 代表的是col, j 代表的是row
                if (j == 3) {
                    continue;
                } else if (j == 2) {
                    if (board.tile(i, 3) == null) {
                        //在第三行没有tile
                        if (board.tile(i, j) == null) {
                            continue;
                        } else {
                            board.move(i, j + 1, board.tile(i, j));
                            changed = true;
                        }
                    } else {
                        //在第三行有tile
                        if (board.tile(i, j) == null) {
                            continue;
                        } else {
                            if (board.tile(i, j).value() == board.tile(i, 3).value()) {
                                score = score + 2 * board.tile(i, j).value();
                                board.move(i, j + 1, board.tile(i, j));
                                changed = true;
                            } else {
                                continue;
                            }
                        }
                    }
                } else if (j == 1) {
                    if (board.tile(i, j) == null) {
                        continue;
                    } else {
                        // j = 2处有tile
                        if (board.tile(i, 2) != null) {
                            if (board.tile(i, 1).value() == board.tile(i, 2).value()) {
                                changed = true;
                                score = score + 2 * board.tile(i, 1).value();
                                board.move(i, 2, board.tile(i, 1));
                            } else {
                                continue;
                            }
                        } else {
                            //j = 2处没有tile
                            if (board.tile(i, 3) == null) {
                                // j = 3处没有tile
                                changed = true;
                                board.move(i, 3, board.tile(i, 1));
                            } else {
                                //j = 3处有tile
                                if (originalScore != score) {
                                    changed = true;
                                    board.move(i, 2, board.tile(i, 1));
                                } else {
//
                                    if (board.tile(i, 1).value() == board.tile(i, 3).value()) {
                                        score += 2 * board.tile(i, 1).value();
                                        changed = true;
                                        board.move(i, 3, board.tile(i, 1));
                                    } else {
                                        changed = true;
                                        board.move(i, 2, board.tile(i, 1));
                                    }
                                }
                            }
                        }
                    }
                } else if (j == 0) {
                    if (board.tile(i, j) == null) {
                        continue;
                    } else {
                        //j = 0有tile
                        if (board.tile(i, 1) != null) {
                            //j = 1有tile
                            if (board.tile(i, 1).value() == board.tile(i, 0).value()) {
                                score += 2 * board.tile(i, 1).value();
                                changed = true;
                                board.move(i, 1, board.tile(i, 0));
                            } else {
                                continue;
                            }
                        } else {
                            // j =1 没有tile
                            if (board.tile(i, 2) != null) {

                                if (originalScore == score) {
                                    if (board.tile(i, 2).value() == board.tile(i, 0).value()) {
                                        score += 2 * board.tile(i, 0).value();
                                        changed = true;
                                        board.move(i, 2, board.tile(i, 0));
                                    } else {
                                        changed = true;
                                        board.move(i, 1, board.tile(i, 0));
                                    }
                                } else if (whether) {
                                    if (board.tile(i, 0).value() == board.tile(i, 2).value()) {
                                        score += 2 * board.tile(i, 0).value();
                                        changed = true;
                                        board.move(i, 2, board.tile(i, 0));
                                    } else {
                                        board.move(i, 1, board.tile(i, 0));
                                        changed = true;
                                    }
                                } else {
                                    board.move(i, 1, board.tile(i, 0));
                                    changed = true;
                                }
                                //TODO: j = 2是由j = 3和j= 2合并到j = 3
                                //TODO: j = 2处有tile
                            } else {
                                // j = 2处没有tile
                                if (board.tile(i, 3) == null) {
                                    // j = 3处没有tile
                                    board.move(i, 3, board.tile(i, 0));
                                    changed = true;
                                } else {
                                    // j = 3处有tile
                                    if (originalScore != score) {
                                        board.move(i, 2, board.tile(i, 0));
                                        changed = true;
                                    } else {
                                        if (board.tile(i, 0).value() == board.tile(i, 3).value()) {
                                            score += 2 * board.tile(i, 0).value();
                                            board.move(i, 3, board.tile(i, 0));
                                            changed = true;
                                        } else {
                                            board.move(i, 2, board.tile(i, 0));
                                            changed = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        board.setViewingPerspective(Side.NORTH);
//        if (score > maxScore) {
//            maxScore = score;
//        }

        checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }

    /**
     * Checks if the game is over and sets the gameOver variable
     * appropriately.
     */
    private void checkGameOver() {
        gameOver = checkGameOver(board);
    }

    /**
     * Determine whether game is over.
     */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /**
     * Returns true if at least one space on the Board is empty.
     * Empty spaces are stored as null.
     */
    public static boolean emptySpaceExists(Board b) {
        int size = b.size();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (b.tile(i, j) == null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {
        int size = b.size();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (b.tile(i, j) != null && b.tile(i, j).value() == MAX_PIECE) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public static boolean atLeastOneMoveExists(Board b) {
        if (emptySpaceExists(b)) {
            return true;
        }
        int size = b.size();

        //左
//        if (b.tile(i, j).value() == b.tile(i - 1, j).value()) {
//            return true;
//        }
        //上
//        if (b.tile(i, j).value() == b.tile(i, j + 1).value()) {
//            return true;
//        }
        //右
//        if (b.tile(i, j).value() == b.tile(i + 1, j).value()) {
//            return true;
//        }
        //下
//        if (b.tile(i, j).value() == b.tile(i, j - 1).value()) {
//            return true;
//        }
        // i 表示 col, j 表示 row
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == 0) {
                    if (j == 0) {
                        if (b.tile(i, j).value() == b.tile(i, j + 1).value()) {
                            return true;
                        }
                        if (b.tile(i, j).value() == b.tile(i + 1, j).value()) {
                            return true;
                        }
                    } else if (j == 3) {
                        if (b.tile(i, j).value() == b.tile(i + 1, j).value()) {
                            return true;
                        }
                        if (b.tile(i, j).value() == b.tile(i, j - 1).value()) {
                            return true;
                        }
                    } else {
                        if (b.tile(i, j).value() == b.tile(i, j + 1).value()) {
                            return true;
                        }

                        if (b.tile(i, j).value() == b.tile(i + 1, j).value()) {
                            return true;
                        }

                        if (b.tile(i, j).value() == b.tile(i, j - 1).value()) {
                            return true;
                        }
                    }
                } else if (i == 2 || i == 1) {
                    if (j == 0) {
                        if (b.tile(i, j).value() == b.tile(i - 1, j).value()) {
                            return true;
                        }

                        if (b.tile(i, j).value() == b.tile(i, j + 1).value()) {
                            return true;
                        }

                        if (b.tile(i, j).value() == b.tile(i + 1, j).value()) {
                            return true;
                        }

                    } else if (j == 3) {

                        if (b.tile(i, j).value() == b.tile(i - 1, j).value()) {
                            return true;
                        }



                        if (b.tile(i, j).value() == b.tile(i + 1, j).value()) {
                            return true;
                        }

                        if (b.tile(i, j).value() == b.tile(i, j - 1).value()) {
                            return true;
                        }
                    } else {

                        if (b.tile(i, j).value() == b.tile(i - 1, j).value()) {
                            return true;
                        }

                        if (b.tile(i, j).value() == b.tile(i, j + 1).value()) {
                            return true;
                        }

                        if (b.tile(i, j).value() == b.tile(i + 1, j).value()) {
                            return true;
                        }

                        if (b.tile(i, j).value() == b.tile(i, j - 1).value()) {
                            return true;
                        }
                    }

                } else if (i == 3) {
                    if (j == 0) {
                        if (b.tile(i, j).value() == b.tile(i, j + 1).value()) {
                            return true;
                        }
                        if (b.tile(i, j).value() == b.tile(i - 1, j).value()) {
                            return true;
                        }
                    } else if (j == 3) {
                        if (b.tile(i, j).value() == b.tile(i - 1, j).value()) {
                            return true;
                        }
                        if (b.tile(i, j).value() == b.tile(i, j - 1).value()) {
                            return true;
                        }
                    } else {
                        if (b.tile(i, j).value() == b.tile(i - 1, j).value()) {
                            return true;
                        }
                        if (b.tile(i, j).value() == b.tile(i, j - 1).value()) {
                            return true;
                        }
                        if (b.tile(i, j).value() == b.tile(i, j + 1).value()) {
                            return true;
                        }

                    }
                }
            }
        }
        return false;
    }


    @Override
    /** Returns the model as a string, used for debugging. */
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    /** Returns whether two models are equal. */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    /** Returns hash code of Model’s string. */
    public int hashCode() {
        return toString().hashCode();
    }
}

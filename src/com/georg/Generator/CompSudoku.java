package com.georg.Generator;

import com.georg.Level;
import com.georg.Sudoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Georg on 30/05/16.
 * <p>
 * A subclass of {@link Sudoku} specialised
 * for doing computations.
 */
public class CompSudoku extends Sudoku {
    private int index = 0;

    public CompSudoku(Level l) {
        super(l);
    }

    private CompSudoku(Level l, byte[] field) throws RuntimeException {
        super(l, field);
    }

    public CompSudoku(Sudoku su) {
        this(su.getDifficulty(), su.getField());
        moveNext();
    }

    private CompSudoku(CompSudoku su) {
        this(su.getDifficulty(), su.getField().clone());
        index = su.index;
    }

    public int getAtIndex() {
        return field[index];
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int i) {
        index = i;
    }

    public void moveNext() {
        while (field[index] != NAN && !isIndexLast())
            moveIndex();

    }

    public boolean isIndexLast() {
        for (int i = index; i < FIELD_COUNT; i++) {
            if (field[i] == NAN)
                return false;
        }

        return true;
    }

    public void moveIndex() {
        if (index < (FIELD_COUNT - 1))
            index++;
    }

    public void resetIndex() {
        index = 0;
        moveNext();
    }

    public void moveIndexReverse() {
        if (index > 0)
            index--;
    }

    public void setField(byte n) {
        field[index] = n;
    }

    public void addSecureByte(byte in) {
        if (field[index] != NAN)
            return;
        field[index] = in;
        moveNext();
    }

    public List<CompSudoku> expand() {
        Boolean[] possible = new Boolean[MAX_NUM];
        Arrays.fill(possible, true);
        possible = rule_3(rule_2(rule_1(possible)));

        List<CompSudoku> ret = new ArrayList<>();
        for (byte i = 1; i <= MAX_NUM; i++) {
            if (possible[i - 1]) {
                CompSudoku c = new CompSudoku(this);
                c.addSecureByte(i);
                ret.add(c);
            }
        }
        return ret;
    }

    public List<Byte> getAvailable() {
        Boolean[] possible = new Boolean[MAX_NUM];
        Arrays.fill(possible, true);
        possible = rule_3(rule_2(rule_1(possible)));

        List<Byte> ret = new ArrayList<>();
        for (byte i = 1; i <= MAX_NUM; i++) {
            if (possible[i - 1])
                ret.add(i);
        }
        return ret;
    }


    /**
     * Rule one is the first rule for solving sudokus named in
     * the paper. In this case it is that there cannot be
     * any double numbers in a row.
     *
     * @param possible the number variable. From 1 to 9, where false
     *                 marks not available anymore and true marks the
     *                 opposite.
     * @return returns the edited, now valid number variable. See input.
     */
    private Boolean[] rule_1(Boolean[] possible) {
        for (int i = 0; i < FIELD_SIZE; i++) {
            byte f = field[i + index / FIELD_SIZE * FIELD_SIZE];
            if (f != NAN)
                possible[f - 1] = false;
        }
        return possible;
    }

    /**
     * Rule one is the second rule for solving sudokus named in
     * the paper. In this case there cannot be a double number
     * in a column.
     *
     * @param possible the number variable. From 1 to 9, where false
     *                 marks not available anymore and true marks the
     *                 opposite.
     * @return returns the edited, now valid number variable. See input.
     */
    private Boolean[] rule_2(Boolean[] possible) {
        for (int i = 0; i < FIELD_SIZE; i++) {
            byte f = field[i * FIELD_SIZE + index % FIELD_SIZE];
            if (f != NAN)
                possible[f - 1] = false;
        }
        return possible;
    }

    /**
     * Rule one is the third rule for solving sudokus named in
     * the paper. In this case there cannot be a double number
     * in a block.
     *
     * @param possible the number variable. From 1 to 9, where false
     *                 marks not available anymore and true marks the
     *                 opposite.
     * @return returns the edited, now valid number variable. See input.
     */
    private Boolean[] rule_3(Boolean[] possible) {
        for (int i = 0; i < FIELD_SIZE; i++) {
            byte f = field[i % BLOCK_SIZE + (i / BLOCK_SIZE) * FIELD_SIZE
                    + (index % FIELD_SIZE) / BLOCK_SIZE * BLOCK_SIZE + index / (FIELD_SIZE * BLOCK_SIZE) * (FIELD_SIZE * BLOCK_SIZE)];
            if (f != NAN)
                possible[f - 1] = false;
        }
        return possible;
    }


    int getNumTotalFields() {
        int count = 0;
        for (byte f : field)
            if (f != -1)
                count++;
        return count;
    }

    int getLowerBoundRC() {
        int lowerBound = FIELD_SIZE;
        for (int i = 0; i < FIELD_SIZE; i++) {
            int countR = 0, countC = 0;
            for (int j = 0; j < FIELD_SIZE; j++) {
                if (field[i * FIELD_SIZE + j] != -1)
                    countR++;
                if (field[j * FIELD_SIZE + i] != -1)
                    countC++;
            }
            lowerBound = Math.min(Math.min(countC, countR), lowerBound);
            if (lowerBound == 0)
                break;
        }
        return lowerBound;
    }

    public CompSudoku digClone(int i) {
        CompSudoku ret = new CompSudoku(this);
        ret.field[i] = -1;
        return ret;
    }
}

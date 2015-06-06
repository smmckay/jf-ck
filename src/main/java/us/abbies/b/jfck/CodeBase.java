package us.abbies.b.jfck;

import java.io.IOException;

public abstract class CodeBase implements Runnable {
    private int idx;
    private short[] cells = new short[256];

    protected void right() {
        if (++idx >= cells.length) {
            short[] newCells = new short[cells.length * 2];
            System.arraycopy(cells, 0, newCells, 0, cells.length);
            cells = newCells;
        }
    }

    protected void left() {
        if (--idx < 0) {
            throw new AssertionError("Fell off the end of the tape");
        }
    }

    protected void inc() {
        cells[idx] = (short) (cells[idx] + 1 % 256);
    }

    protected void dec() {
        cells[idx] = (short) (cells[idx] - 1 % 256);
    }

    protected void out() {
        System.out.print((char) cells[idx]);
    }

    protected void in() {
        try {
            int c = System.in.read();
            cells[idx] = (short) (c == -1 ? 0 : c % 256);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected int get() {
        return cells[idx];
    }
}

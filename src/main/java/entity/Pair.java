package entity;

public class Pair<L, R> {
    private L _left;
    private R _right;

    public Pair(L left, R right) {
        this._left = left;
        this._right = right;
    }

    public L left() {
        return _left;
    }

    public R right() {
        return _right;
    }
}

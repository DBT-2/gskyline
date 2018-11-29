package entity;

import java.util.*;

public abstract class SkylineGroup {
    public int maxIndex = -1;
    public int maxLayer = -1;
    protected Set<Point> childrenSet = null;
    private List<Point> tailSet = null;

    public Set<Point> getChildrenSet() {
        if (childrenSet == null)
            childrenSet = calChiddrenSet();
        return childrenSet;
    }

    public void setChildrenSet(Set<Point> childrenSet) {
        this.childrenSet = childrenSet;
    }

    abstract protected Set<Point> calChiddrenSet();

    public List<Point> getTailSet(DSG dsg) {
        if (tailSet == null)
            tailSet = calTailSet(dsg);
        return tailSet;
    }

    private List<Point> calTailSet(DSG dsg) {
        return dsg.tailSet(maxIndex);
    }

    abstract public boolean canAdd(Point point);

    /**
     * add returns a skyline group that is THIS plus the given point.
     * @param point
     * @return
     */
    abstract public SkylineGroup add(Point point);

    /**
     * discard cleans containers to help gc.
     */
    abstract public void discard();
}

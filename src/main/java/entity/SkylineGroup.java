package entity;

import java.util.*;

public class SkylineGroup {
    public int level;
    public Set<Point> pts = null;
    public int maxIndex = -1;
    public int maxLayer = -1;
    private Set<Point> childrenSet = null;
    private Set<Point> tailSet = null;

    public SkylineGroup(Set<Point> pts) {
        this.pts = pts;
    }


    public Set<Point> getChildrenSet() {
        if (childrenSet == null)
            childrenSet = calChidrenSet();
        return childrenSet;
    }

    public void setChildrenSet(Set<Point> childrenSet) {
        this.childrenSet = childrenSet;
    }

    private Set<Point> calChidrenSet() {
        Set<Point> childrenSet = new HashSet<>();
        for (Point point : pts) {
            childrenSet.addAll(point.children);
        }
        return childrenSet;
    }

    public Set<Point> getTailSet(DSG dsg) {
        if (tailSet == null)
            tailSet = calTailSet(dsg);
        return tailSet;
    }

    private Set<Point> calTailSet(DSG dsg) {
        return new HashSet<>(dsg.tailSet(maxIndex));
    }

    @Override
    public String toString() {
        return "SkylineGroup{" +
                "pts=" + pts +
                '}';
    }
}

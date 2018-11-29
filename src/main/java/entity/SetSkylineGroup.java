package entity;

import java.util.HashSet;
import java.util.Set;

public class SetSkylineGroup extends SkylineGroup {

    public Set<Point> points;
    public SetSkylineGroup(Set<Point> points) {
        this.points = points;
    }

    @Override
    protected Set<Point> calChiddrenSet() {
        Set<Point> childrenSet = new HashSet<>();
        for (Point point : points) {
            childrenSet.addAll(point.children);
        }
        return childrenSet;
    }

    @Override
    public boolean canAdd(Point point) {
        for (Point parent : point.parents) {
            if (!points.contains(parent))
                return false;
        }
        return true;
    }

    @Override
    public SkylineGroup add(Point point) {
        Set<Point> newPoints = new HashSet<>(points);
        SetSkylineGroup newGroup = new SetSkylineGroup(newPoints);
        return newGroup;
    }

    @Override
    public void discard() {
        points = null;
    }

    @Override
    public int level() {
        return points.size();
    }

}

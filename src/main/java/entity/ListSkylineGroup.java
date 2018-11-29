package entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListSkylineGroup extends SkylineGroup {
    public List<Point> points;

    public ListSkylineGroup() {

    }

    public ListSkylineGroup(ArrayList<Point> points) {
        this.points = points;
    }

    @Override
    protected Set<Point> calChiddrenSet() {
        Set<Point> childrenSet = new HashSet<>();
        for(Point point : points) {
            childrenSet.addAll(point.children);
        }
        return childrenSet;
    }

    @Override
    public boolean canAdd(Point point) {
        for(Point parent : point.parents) {
            if (!points.contains(parent))
                return false;
        }
        return true;
    }

    @Override
    public SkylineGroup add(Point point) {
        ListSkylineGroup skylineGroup = new ListSkylineGroup();
        List<Point> newPoints = new ArrayList<>(points);
        newPoints.add(point);
        skylineGroup.points = newPoints;
        return skylineGroup;
    }

    @Override
    public void discard() {
        points = null;
    }
}

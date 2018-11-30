package entity;

import conf.Config;

import java.util.*;

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
        ListSkylineGroup skylineGroup = (ListSkylineGroup) Config.groupManager.allocate();
        List<Point> newPoints = new ArrayList<>(points);
        newPoints.add(point);
        skylineGroup.points = newPoints;
        return skylineGroup;
    }

    @Override
    public void discard() {
        super.discard();
        points = null;
        Config.groupManager.free(this);
    }

    @Override
    public int level() {
        return points.size();
    }

    @Override
    public boolean isChildren(Point another) {
        for(Point point : points) {
            if(point.children.contains(another))
                return true;
        }
        return false;
    }

    public static class Manager extends GroupManager<ListSkylineGroup> {

        @Override
        protected ListSkylineGroup createSkylineGroup() {
            return new ListSkylineGroup();
        }

        @Override
        public ListSkylineGroup generateRoot() {
            return new ListSkylineGroup(new ArrayList<>());
        }
    }
}

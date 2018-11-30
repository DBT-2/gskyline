package entity;

import conf.Config;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SetSkylineGroup extends SkylineGroup {

    public Set<Point> points;
    public SetSkylineGroup(){}
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
        newPoints.add(point);
        SetSkylineGroup newGroup = (SetSkylineGroup) Config.groupManager.allocate();
        newGroup.points = newPoints;
        return newGroup;
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

    public static class Manager extends GroupManager<SetSkylineGroup> {

        @Override
        protected SetSkylineGroup createSkylineGroup() {
            return new SetSkylineGroup();
        }

        @Override
        public SetSkylineGroup generateRoot() {
            return new SetSkylineGroup(Collections.emptySet());
        }
    }

}

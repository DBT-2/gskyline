package entity;

import java.util.List;
import java.util.Set;

public class UnitGroup {
    public List<Point> pts;
    public List<Integer> parentSet;
    public List<UnitGroup> tailSet;

    public UnitGroup(List<Point> pts, List<Integer> parentSet) {
        this.pts = pts;
        this.parentSet = parentSet;
    }
}

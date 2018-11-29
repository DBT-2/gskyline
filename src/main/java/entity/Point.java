package entity;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Point implements Comparable<Point>{
    public static final int DEFAULT_LABEL = -1;

    // this comparator compares by point.fields[0]
    public static final Comparator<Point> DIMENSION_COMPARATOR = new Comparator<Point>() {
        @Override
        public int compare(Point o1, Point o2) {
            return Double.compare(o1.fields[0], o2.fields[0]);
        }
    };

    // this comparator compares by point.index[0]
    public static final Comparator<Point> INDEX_COMPARATOR = new Comparator<Point>() {
        @Override
        public int compare(Point o1, Point o2) {
            return Double.compare(o1.index, o2.index);
        }
    };

    private Set<Point> unitGroup = null;
    // the position of this point after sorting
    public int index;
    // type of fields is fixed for efficiency
    public double[] fields;
    public Set<Point> children = new HashSet<>();
    public Set<Point> parents = new HashSet<>();
    public int layer;
    // the position of this point in data source
    public int label;

    public Point(double[] fields, int label) {
        this.fields = fields;
        this.label = label;
    }

    public Set<Point> getUnitGroup() {
        if (unitGroup == null)
            unitGroup = calculateUnitGroup();
        return unitGroup;

    }

    private Set<Point> calculateUnitGroup() {
        Set<Point> unitGroup = new HashSet<>();
        unitGroup.add(this);
        for (Point parent : parents) {
            unitGroup.addAll(parent.getUnitGroup());
        }
        return unitGroup;
    }

    public boolean dominate(Point other) {
        boolean hasLessValue = false;
        for (int i = 0; i < fields.length; i++) {
            if (this.fields[i] < other.fields[i])
                hasLessValue = true;
            else if (this.fields[i] > other.fields[i])
                return false;
        }
        return hasLessValue;
    }

    public boolean isSkyline() {
        return layer == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return label == point.label;
    }

    @Override
    public int hashCode() {
        return Objects.hash(label);
    }

    @Override
    public int compareTo(Point o) {
        return DIMENSION_COMPARATOR.compare(this, o);
    }

    @Override
    public String toString() {
        return String.valueOf(label);
    }
}

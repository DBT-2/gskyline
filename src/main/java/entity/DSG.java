package entity;

import java.util.List;

public class DSG {
    public List<Point> pts;

    public DSG(List<Point> points) {
        this.pts = points;
    }

    /**
     * tailSet return a sub set ranges [index+1:]
     * @param index
     * @return
     */
    public List<Point> tailSet(int index) {
        return pts.subList(index + 1, pts.size());
    }
}

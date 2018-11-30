package entity;

import java.util.List;

public class DSG {
    public List<Point> pts;
    public List<Point>[] layers;

    public DSG(List<Point> points, List<Point>[] layers) {
        this.pts = points;
        this.layers = layers;
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

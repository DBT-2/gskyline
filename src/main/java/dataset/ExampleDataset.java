package dataset;

import entity.Point;

import java.util.ArrayList;

import static alg.Utils.reindex;

public class ExampleDataset extends ArrayList<Point> implements Dataset {

    public ExampleDataset() {
       double[][] data = new double[][]{{4, 400}, {24, 380}, {14, 340}, {36, 300}, {26, 280},
            {8, 260}, {40, 200}, {20, 180}, {34, 140}, {28, 120},
            {16, 60}};
       int i = 1;
       for(double[] d : data) {
           Point point = new Point(d, i++);
           this.add(point);
       }
       this.sort(Point::compareTo);
       reindex(this);
    }
}

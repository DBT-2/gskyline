package dataset;

import entity.Point;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static alg.Utils.reindex;

public class CSVDataset extends ArrayList<Point> implements Dataset {

    private static final String SEPARATOR = " ";

    public CSVDataset(String fileName, int limit) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            String line;
            int cnt = 0;
            while ((line = bufferedReader.readLine()) != null) {
                String[] fields = line.split(SEPARATOR);
                double[] doubleFileds = new double[fields.length];
                for (int i = 0; i < fields.length; i++) {
                    doubleFileds[i] = Double.parseDouble(fields[i]);
                }
                Point point = new Point(doubleFileds, cnt++);
                this.add(point);
                if(limit > 0 && cnt >= limit)
                    break;
            }
            this.sort(Point::compareTo);
            reindex(this);
        }
    }
}

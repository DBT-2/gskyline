package alg.skylinelayer;

import entity.Pair;
import entity.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SkylineLayerMD {
    private static final Logger LOGGER = LoggerFactory.getLogger(SkylineLayerMD.class);

    public static Pair<List<Point>[], List<Point>> run(List<Point> pts, int k) {
        long startTime = System.currentTimeMillis();

        pts.sort(Point::compareTo);
        pts.get(0).layer = 0;
        List<Point>[] layers = new List[k];
        for (int i = 0; i < k; i++) {
            layers[i] = new ArrayList<>();
        }
        layers[0].add(pts.get(0));

        for (int i = 1; i < pts.size(); i++) {
            Point current = pts.get(i);
            boolean layerFound = false;
            for (int j = 0; j < k; j++) {
                List<Point> layer = layers[j];
                boolean dominated = false;
                for (Point point : layer) {
                    if (point.dominate(current)) {
                        dominated = true;
                        break;
                    }
                }
                if (! dominated) {
                    layerFound = true;
                    current.layer = j;
                    layer.add(current);
                    break;
                }
            }
            if (!layerFound) {
                current.layer = k;
            }
        }
        LOGGER.info("2D skyline layer consumed: {}ms", System.currentTimeMillis() - startTime);

        List<Point> newPts = new ArrayList<>();
        for (List<Point> layer : layers) {
            newPts.addAll(layer);
        }

        return new Pair<>(layers, newPts);
    }

}

package alg.skylinelayer;

import entity.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static alg.Utils.binDomSearch;
import static alg.Utils.reindex;

public class SkylineLayer2D {
    private static final Logger LOGGER = LoggerFactory.getLogger(SkylineLayer2D.class);

    public static List<Point> Run(List<Point> pts, int k) {
        LOGGER.info("Starting 2D {}-SkylineLayer of {} pts", k, pts.size());
        long startTime = System.currentTimeMillis();
        pts.sort(Point::compareTo);
        pts.get(0).layer = 0;
        int maxLayer = 0;
        List<Point> tails = new ArrayList<>();
        tails.add(pts.get(0));

        for (int i = 1; i < pts.size(); i++) {
            Point current = pts.get(i);
            if (! tails.get(0).dominate(current)) {
                current.layer = 0;
                tails.set(0, current);
            } else if (tails.get(maxLayer).dominate(current)) {
                if (maxLayer < k - 1) {
                    maxLayer++;
                    tails.add(current);
                    current.layer = maxLayer;
                } else
                    current.layer = maxLayer + 1;
            } else {
                int pos = binDomSearch(tails, current);
                current.layer = pos;
                tails.set(pos, current);
            }
        }
        LOGGER.info("2D skyline layer consumed: {}ms", System.currentTimeMillis() - startTime);

        List<Point> ret = new ArrayList<>();
        for(Point pt : pts) {
            if(pt.layer < k) {
                ret.add(pt);
            }
        }
        reindex(ret);
        return ret;
    }
}

package alg;

import entity.DSG;
import entity.Point;
import entity.SkylineGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Utils {
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    public static int binDomSearch(List<Point> pts, Point pt) {
        int lo = 0;
        int hi = pts.size();
        int mid;
        while (lo < hi) {
            mid = (lo + hi) / 2;
            if (! pts.get(mid).dominate(pt)) {
                hi = mid;
            } else {
                lo = mid + 1;
            }
        }
        return lo;
    }

    public static void reindex(List<Point> pts) {
        for (int i = 0; i < pts.size(); i++) {
            pts.get(i).index = i;
        }
    }

    public static List<Point>[] splitLayer(List<Point> pts, int k) {
        List<Point>[] layers = new List[k];
        for (int i = 0; i < k; i++) {
            layers[i] = new ArrayList<>();
        }
        for (Point point : pts) {
            if (point.layer < k) {
                layers[point.layer].add(point);
            }
        }
        return layers;
    }

    public static void calParentChildren(List<Point>[] layers, int k) {
        for (int i = 1; i < k; i++) {
            for (Point pt : layers[i]) {
                for (int j = 0; j < i; j++) {
                    for(Point above : layers[j]) {
                        if(above.dominate(pt)) {
                            pt.parents.add(above);
                            above.children.add(pt);
                        }
                    }
                }
            }
        }
    }

    public static List<SkylineGroup> preprocess(DSG dsg, int k) {
        long startTime = System.currentTimeMillis();
        List<Point> pts = dsg.pts;
        List<Point> toRemove = new ArrayList<>();
        List<SkylineGroup> finalGroups = new ArrayList<>();
        for(Point point : pts) {
            Set<Point> uGroup = point.getUnitGroup();
            if(uGroup.size() > k) {
                LOGGER.debug("UGroup of {} is {}", point, uGroup);
                toRemove.add(point);
            } else if(uGroup.size() == k) {
                finalGroups.add(new SkylineGroup(uGroup));
                toRemove.add(point);
            }
        }
        LOGGER.info("removed {} points because their union group sizes exceed k or beyond kth layer", toRemove.size());
        pts.removeAll(toRemove);
        cleanChildrenParents(pts, toRemove);
        reindex(pts);
        LOGGER.info("preprocess consumed {}ms", System.currentTimeMillis() - startTime);
        return finalGroups;
    }

    private static void cleanChildrenParents(List<Point> points, List<Point> removedPoints) {
        for(Point point : points) {
            for (Point removed : removedPoints) {
                point.parents.remove(removed);
                point.children.remove(removed);
            }
        }
    }

}

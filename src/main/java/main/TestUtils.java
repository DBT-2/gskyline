package main;

import alg.skylinelayer.SkylineLayer2D;
import alg.skylinelayer.SkylineLayerMD;
import entity.DSG;
import entity.Pair;
import entity.Point;
import entity.SkylineGroup;

import java.util.List;

import static alg.Utils.calParentChildren;
import static alg.Utils.splitLayer;

public class TestUtils {
    public static DSG dsg2D(List<Point> pointList, int k) {
        pointList = SkylineLayer2D.Run(pointList, k);
        List<Point>[] layers = splitLayer(pointList, k);
        calParentChildren(layers, k);
        return new DSG(pointList, layers);
    }

    public static DSG dsgMD(List<Point> points, int k) {
        Pair<List<Point>[], List<Point>> pair = SkylineLayerMD.run(points, k);
        calParentChildren(pair.left(), k);
        return new DSG(pair.right(), pair.left());
    }

//    public static void validateSkylineGroup(List<SkylineGroup> groups, DSG dsg) {
//        for (SkylineGroup group : groups) {
//            for (Point point : group.pts) {
//                for (Point another : dsg.pts) {
//                    if (another.dominate(point) && !group.pts.contains(another)){
//                        throw new RuntimeException(String.format("%s is not a skyline group", group));
//                    }
//                }
//            }
//        }
//
//    }
}

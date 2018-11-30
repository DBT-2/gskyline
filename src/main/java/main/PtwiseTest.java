package main;

import alg.gskyline.PtwiseGSkyline;
import dataset.Dataset;
import dataset.ExampleDataset;
import entity.DSG;
import entity.Point;
import entity.SkylineGroup;

import java.util.List;

public class PtwiseTest {

    private Point getPointByLabel(List<Point> pts, int label) {
        for(Point point : pts) {
            if (point.label == label)
                return point;
        }
        return null;
    }

    private void manualSkylineLayer(List<Point> pointList) {
        Point[] labelPoints = new Point[pointList.size() + 1];
        for(int i = 0; i < pointList.size(); i++) {
            labelPoints[i + 1] = getPointByLabel(pointList, i + 1);
        }

        int[][] edges = new int[][]{{6, 3}, {3, 2}, {8, 2}, {11, 8}, {11, 10}, {6, 5},
             {8, 5}, {10, 9}, {8, 7}, {5, 4}, {9, 4}, {9, 7}};
        for(int[] edge : edges) {
            labelPoints[edge[0]].children.add(labelPoints[edge[1]]);
            labelPoints[edge[1]].parents.add(labelPoints[edge[0]]);
        }
        
        int[][] layers = new int[][]{{1, 6, 11}, {3, 8, 10}, {2, 5, 9}, {4, 7}};
        int layerNum = 0;

        for (int[] layer : layers) {
            for(int pt : layer) {
                labelPoints[pt].layer = layerNum;
            }
            layerNum++;
        }
    }

    public void run() {
        Dataset dataset = new ExampleDataset();
        manualSkylineLayer(dataset);
        DSG dsg = new DSG(dataset);
        int k = 4;
        List<SkylineGroup> groups = PtwiseGSkyline.runBFS(dsg, k, -1);
        System.out.println(groups.size());
    }

    public static void main(String[] args) {
        PtwiseTest test = new PtwiseTest();
        test.run();
    }
}

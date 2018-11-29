package main;

import alg.gskyline.PtwiseGSkyline;
import dataset.ExampleDataset;
import entity.DSG;
import entity.Point;
import entity.SkylineGroup;

import java.io.IOException;
import java.util.List;

public class SkylineLayerTest {

    public void run() throws IOException {
        int k = 4;
        List<Point> points = new ExampleDataset();
        DSG dsg = TestUtils.dsg2D(points, k);
        List<SkylineGroup> groups = PtwiseGSkyline.run(dsg, k);

        points = new ExampleDataset();
        dsg = TestUtils.dsgMD(points, k);
        groups = PtwiseGSkyline.run(dsg, k);
    }

    public static void main(String[] args) throws IOException {
        SkylineLayerTest test = new SkylineLayerTest();
        test.run();
    }
}
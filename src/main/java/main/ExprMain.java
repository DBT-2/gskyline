package main;

import alg.gskyline.PtwiseGSkyline;
import dataset.CSVDataset;
import dataset.Dataset;
import entity.DSG;
import entity.SkylineGroup;

import java.io.IOException;
import java.util.List;

public class ExprMain {
    public static void main(String[] args) throws IOException {
        String filename = "/Users/koutakashi/codes/gskyline/data/anti_8.txt";
        int k = 4;
        int dataLimit = 9800;
        int groupLimit = 10000;

        MonitorThread monitorThread = new MonitorThread();
        monitorThread.start();

        Dataset dataset = new CSVDataset(filename, dataLimit);
        //Dataset dataset = new ExampleDataset();
        DSG dsg = TestUtils.dsg2D(dataset, k);
        List<SkylineGroup> groupList = PtwiseGSkyline.runDFS(dsg, k, groupLimit);

        try {
            monitorThread.interrupt();
            monitorThread.join();
        } catch (InterruptedException ignored) {
        }

        System.out.println(String.format("Max memory usage: %dMB", monitorThread.getMaxMemUsage() / (1024*1024)));
        //TestUtils.validateSkylineGroup(groupList, dsg);
    }
}

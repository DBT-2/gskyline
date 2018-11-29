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
        String filename = "/Users/koutakashi/codes/gskyline/data/anti_2.txt";
        int k = 4;
        int limit = 10000;

        MonitorThread monitorThread = new MonitorThread();
        monitorThread.start();

        Dataset dataset = new CSVDataset(filename, limit);
        DSG dsg = TestUtils.dsgMD(dataset, k);
        List<SkylineGroup> groupList = PtwiseGSkyline.runBFS(dsg, k);

        try {
            monitorThread.interrupt();
            monitorThread.join();
        } catch (InterruptedException ignored) {
        }

        System.out.println(String.format("Max memory usage: %dMB", monitorThread.getMaxMemUsage() / (1024*1024)));
        // TestUtils.validateSkylineGroup(groupList, dsg);
    }
}

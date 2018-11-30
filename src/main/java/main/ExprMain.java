package main;

import alg.Algorithms;
import alg.gskyline.PtwiseGSkyline;
import conf.Config;
import dataset.CSVDataset;
import dataset.Dataset;
import entity.DSG;
import entity.SkylineGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExprMain {

    public static void runOnce(ExprConfig config) throws IOException {
        String fileName = config.filename;
        int k = config.k;
        int dataLimit = config.dataLimit;
        int groupLimit = config.groupLimit;
        boolean isMD = config.isMD;
        Algorithms alg = config.algorithm;
        System.out.println(String.format("Expr settings: file=%s, k=%s, dataLimit=%s, groupLimit=%s, alg=%s",
                fileName, k, dataLimit, groupLimit, alg));

        Config.groupManager.init();

        long startTime = System.currentTimeMillis();
        MonitorThread monitorThread = new MonitorThread();
        monitorThread.start();

        Dataset dataset = new CSVDataset(fileName, dataLimit);
        //Dataset dataset = new ExampleDataset();
        DSG dsg = null;
        if (isMD) {
            dsg = TestUtils.dsgMD(dataset, k);
        } else {
            dsg = TestUtils.dsg2D(dataset, k);
        }

        List<SkylineGroup> groupList = null;
        switch (alg) {
            case POINT_WISE_BFS:
                groupList = PtwiseGSkyline.runBFS(dsg, k, groupLimit);
                break;
            case POINT_WISE_DFS:
                groupList = PtwiseGSkyline.runDFS(dsg, k, groupLimit);
                break;
            case UNIT_WISE:
                // TODO: add implementation
        }
        long elapsed = System.currentTimeMillis() - startTime;
        try {
            monitorThread.interrupt();
            monitorThread.join();
        } catch (InterruptedException ignored) {
        }

        System.out.println(String.format("Time consumption: %sms, # of Groups : %s", elapsed, groupList.size()));
        System.out.println(String.format("Max memory usage: %dMB", monitorThread.getMaxMemUsage() / (1024*1024)));
        //TestUtils.validateSkylineGroup(groupList, dsg);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        List<ExprConfig> exprConfigs = new ArrayList<>();
        exprConfigs.add(new ExprConfig("/Users/koutakashi/codes/gskyline/data/corr_4.txt", 4,
                -1, -1, Algorithms.POINT_WISE_DFS));
//        exprConfigs.add(new ExprConfig("/Users/koutakashi/codes/gskyline/data/anti_2.txt", 4,
//                -1, -1, Algorithms.POINT_WISE_DFS));

        for (ExprConfig config : exprConfigs) {
            long timeOut = 5 * 60 * 1000; // in ms

            TimeOutThread timeOutThread = new TimeOutThread(timeOut);
            timeOutThread.start();
            runOnce(config);
            timeOutThread.interrupt();
            timeOutThread.join();
        }
    }

    public static class ExprConfig {
        public String filename;
        public int k;
        public int dataLimit;
        public int groupLimit;
        public Algorithms algorithm;
        public boolean isMD;

        public ExprConfig(String filename, int k, int dataLimit, int groupLimit, Algorithms algorithm) {
            this.filename = filename;
            this.k = k;
            this.dataLimit = dataLimit;
            this.groupLimit = groupLimit;
            this.algorithm = algorithm;
            this.isMD = !filename.contains("_2.txt");
        }
    }
}

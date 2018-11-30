package alg.gskyline;

import conf.Config;
import entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static alg.Utils.preprocess;

public class PtwiseGSkyline {

    private static final Logger LOGGER = LoggerFactory.getLogger(PtwiseGSkyline.class);

    private static List<Point> filterTailSet(List<Point> tailSet, Set<Point> childrenSet, int maxLayer) {
        List<Point> toRemove = new ArrayList<>();
        for (Point point : tailSet) {
            if (!childrenSet.contains(point) && !point.isSkyline()) {
                toRemove.add(point);
            } else if(point.layer - maxLayer >= 2) {
                toRemove.add(point);
            }
        }
        LOGGER.debug("{} points filtered", toRemove.size());
        int i = 0, j = 0;
        List<Point> ret = new ArrayList<>();
        if(toRemove.size() > 0) {
            while(i < tailSet.size()) {
                if(j < toRemove.size() && tailSet.get(i) == toRemove.get(j)) {
                    i ++;
                    j ++;
                } else {
                    ret.add(tailSet.get(i++));
                }
            }
            return ret;
        } else {
            return tailSet;
        }
    }

    public static List<SkylineGroup> runBFS(DSG dsg, int k, int groupLimit) {
        LOGGER.info("Start {} point-wise group skyline...", k);

        List<SkylineGroup> finalGroups = preprocess(dsg, k);
        LOGGER.info("Found {} final groups after preprocess", finalGroups.size());

        List<SkylineGroup> currGroups = new ArrayList<>();
        List<SkylineGroup> nextGroups = new ArrayList<>();
        //currGroups.add(new ListSkylineGroup(new ArrayList<>()));
        currGroups.add(Config.groupManager.getRoot());
        //currGroups.add(new SetSkylineGroup(Collections.emptySet()));

        for (int i = 1; i <= k; i++) {
            long startTime = System.currentTimeMillis();
            int pruned = 0;
            for(int j = 0; j < currGroups.size(); j++) {
                SkylineGroup group = currGroups.get(j);
                LOGGER.debug("Current group: {}", group);
                Set<Point> childrenSet = group.getChildrenSet();
                int maxLayer = group.maxLayer;
                List<Point> tailSet= group.getTailSet(dsg);

                LOGGER.debug("Tail set before: {}", tailSet);
                tailSet = filterTailSet(tailSet, childrenSet, maxLayer);
                LOGGER.debug("Tail set after: {}", tailSet);

                pruned += addCandidateBFS(tailSet, group, nextGroups, i, k);
                if (groupLimit > 0 && i == k && (nextGroups.size() + finalGroups.size()) >= groupLimit) {
                    break;
                }

                group.discard();
                group = null;
                currGroups.set(j, null);
            }
            List<SkylineGroup> temp = currGroups;
            currGroups = nextGroups;
            nextGroups = temp;
            nextGroups.clear();

            long elapsed = System.currentTimeMillis() - startTime;
            LOGGER.info("Level {} consumed {}ms, found {} skyline groups, avg speed {}, pruned {}"
                    , i, elapsed, currGroups.size(), currGroups.size() / (double) elapsed, pruned);
            LOGGER.debug("Level {} : {}", i, currGroups);
        }
        currGroups.addAll(finalGroups);
        if (groupLimit > 0 && currGroups.size() > groupLimit) {
            return currGroups.subList(0, groupLimit);
        }
        return currGroups;
    }

    public static List<SkylineGroup> runDFS(DSG dsg, int k, int groupLimit) {
        LOGGER.info("Start {} point-wise group skyline...", k);

        List<SkylineGroup> finalGroups = preprocess(dsg, k);
        LOGGER.info("Found {} final groups after preprocess", finalGroups.size());

        Stack<SkylineGroup> groupStack = new Stack<>();
        //currGroups.add(new ListSkylineGroup(new ArrayList<>()));
        groupStack.push(Config.groupManager.getRoot());
        //currGroups.add(new SetSkylineGroup(Collections.emptySet()));

        long startTime = System.currentTimeMillis();
        int pruned = 0;
        while (!groupStack.empty()) {
            SkylineGroup group = groupStack.pop();
            Set<Point> childrenSet = group.getChildrenSet();
            int maxLayer = group.maxLayer;
            List<Point> tailSet= group.getTailSet(dsg);

            LOGGER.debug("Tail set before: {}", tailSet);
            tailSet = filterTailSet(tailSet, childrenSet, maxLayer);
            LOGGER.debug("Tail set after: {}", tailSet);

            pruned += addCandidateDFS(tailSet, group, groupStack, finalGroups, group.level(), k);
            if(groupLimit > 0 && finalGroups.size() >= groupLimit)
                break;

            group.discard();
            group = null;
        }

        long elapsed = System.currentTimeMillis() - startTime;
        LOGGER.info("DFS skyline group consumed {}ms, found {} skyline groups, avg speed {}, pruned {}"
                , elapsed, finalGroups.size(), finalGroups.size() / (double) elapsed, pruned);
        LOGGER.debug("Final groups {}", finalGroups);
        if (groupLimit > 0 && finalGroups.size() > groupLimit) {
            return finalGroups.subList(0, groupLimit);
        }
        return finalGroups;
    }

    private static int addCandidateBFS(List<Point> tailSet, SkylineGroup currGroup, List<SkylineGroup> nextGroups,
                                       int currLevel, int maxLevel) {
        int pruned = 0;
        for(Point point : tailSet) {
            if (!currGroup.canAdd(point)){
                pruned ++;
                continue;
            }

            SkylineGroup newGroup = currGroup.add(point);

            if (currLevel < maxLevel) {
                int newMaxIndex = currGroup.maxIndex >= point.index? currGroup.maxIndex : point.index;
                int newMaxLayer = currGroup.maxLayer >= point.layer? currGroup.maxLayer : point.layer;
                newGroup.maxIndex = newMaxIndex;
                newGroup.maxLayer = newMaxLayer;
                Set<Point> newChildrenSet = new HashSet<>(currGroup.getChildrenSet());
                newChildrenSet.addAll(point.children);
                newGroup.setChildrenSet(newChildrenSet);
            }
            nextGroups.add(newGroup);
        }
        return pruned;
    }

    private static int addCandidateDFS(List<Point> tailSet, SkylineGroup currGroup, Stack<SkylineGroup> groupStack,
                                       List<SkylineGroup> finalGroups, int currLevel, int maxLevel) {
        int pruned = 0;
        for(Point point : tailSet) {
            if (!currGroup.canAdd(point)){
                pruned ++;
                continue;
            }

            SkylineGroup newGroup = currGroup.add(point);

            if (currLevel + 1 < maxLevel) {
                int newMaxIndex = currGroup.maxIndex >= point.index? currGroup.maxIndex : point.index;
                int newMaxLayer = currGroup.maxLayer >= point.layer? currGroup.maxLayer : point.layer;
                newGroup.maxIndex = newMaxIndex;
                newGroup.maxLayer = newMaxLayer;
                Set<Point> newChildrenSet = new HashSet<>(currGroup.getChildrenSet());
                newChildrenSet.addAll(point.children);
                newGroup.setChildrenSet(newChildrenSet);
                groupStack.push(newGroup);
            } else {
                finalGroups.add(newGroup);
            }
        }
        return pruned;
    }
}

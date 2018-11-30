package alg.gskyline;

import conf.Config;
import entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static alg.Utils.preprocess;

public class PtwiseGSkyline {

    private static final Logger LOGGER = LoggerFactory.getLogger(PtwiseGSkyline.class);
    private static TailSetIterator tailSetIterator;

    public static Collection<SkylineGroup> runBFS(DSG dsg, int k, int groupLimit) {
        LOGGER.info("Start {} point-wise group skyline...", k);
        tailSetIterator = new TailSetIterator(dsg);

        Collection<SkylineGroup> finalGroups = preprocess(dsg, k);
        LOGGER.info("Found {} final groups after preprocess", finalGroups.size());

        List<SkylineGroup> currGroups = new ArrayList<>();
        List<SkylineGroup> nextGroups = new ArrayList<>();
        currGroups.add(Config.groupManager.getRoot());

        for (int i = 1; i <= k; i++) {
            long startTime = System.currentTimeMillis();
            int pruned = 0;
            for(int j = 0; j < currGroups.size(); j++) {
                SkylineGroup group = currGroups.get(j);
                // LOGGER.debug("Current group: {}", group);

                Iterable<Point> tailSet = calTailSet(group, dsg);
                pruned += addCandidateBFS(tailSet, group, nextGroups, i, k);
                if (groupLimit > 0 && i == k && (nextGroups.size() + finalGroups.size()) >= groupLimit) {
                    break;
                }
                group.discard();
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
        finalGroups.addAll(currGroups);
        return finalGroups;
    }

    public static Collection<SkylineGroup> runDFS(DSG dsg, int k, int groupLimit) {
        LOGGER.info("Start {} point-wise group skyline...", k);
        tailSetIterator = new TailSetIterator(dsg);

        Collection<SkylineGroup> finalGroups = preprocess(dsg, k);
        LOGGER.info("Found {} final groups after preprocess", finalGroups.size());

        Stack<SkylineGroup> groupStack = new Stack<>();
        groupStack.push(Config.groupManager.getRoot());

        long startTime = System.currentTimeMillis();
        int pruned = 0;
        while (!groupStack.empty()) {
            SkylineGroup group = groupStack.pop();

            Iterable<Point> tailSet = calTailSet(group, dsg);

            pruned += addCandidateDFS(tailSet, group, groupStack, finalGroups, group.level(), k);
            if(groupLimit > 0 && finalGroups.size() >= groupLimit)
                break;

            group.discard();
        }

        long elapsed = System.currentTimeMillis() - startTime;
        LOGGER.info("DFS skyline group consumed {}ms, found {} skyline groups, avg speed {}, pruned {}"
                , elapsed, finalGroups.size(), finalGroups.size() / (double) elapsed, pruned);
        LOGGER.debug("Final groups {}", finalGroups);
        if (groupLimit > 0 && finalGroups.size() > groupLimit) {
            return finalGroups;
        }
        return finalGroups;
    }

    private static Iterable<Point> calTailSet(SkylineGroup group, DSG dsg) {
        tailSetIterator.reset(group.maxIndex, group.maxLayer, group.getChildrenSet());
        return tailSetIterator;
    }

    private static int addCandidateBFS(Iterable<Point> tailSet, SkylineGroup currGroup, List<SkylineGroup> nextGroups,
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

    private static int addCandidateDFS(Iterable<Point> tailSet, SkylineGroup currGroup, Stack<SkylineGroup> groupStack,
                                       Collection<SkylineGroup> finalGroups, int currLevel, int maxLevel) {
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

    public static class TailSetIterator implements Iterator<Point>, Iterable<Point> {
        private int index;
        private List<Point> pts;
        private int maxLayer;
        private Set<Point> childrenSet;

        private Point next = null;
        private boolean moreNext = true;

        public TailSetIterator(DSG dsg) {
            this.pts = dsg.pts;
        }

        @Override
        public boolean hasNext() {
            if(!moreNext)
                return false;
            if(next != null)
                return true;
            fetch();
            return next != null;
        }

        @Override
        public Point next() {
            if(!moreNext)
                return null;
            if(next == null)
                fetch();
            Point ret = next;
            next = null;
            return ret;
        }

        private void fetch() {
            if (index >= pts.size()) {
                moreNext = false;
                return;
            }
            while (index < pts.size()) {
                Point candidate = pts.get(index++);
                if (candidate.layer - maxLayer < 2 && (childrenSet.contains(candidate) || candidate.isSkyline())) {
                    next = candidate;
                    break;
                }
            }
            if (next == null)
                moreNext = true;
        }

        public void reset(int index, int maxLayer, Set<Point> childrenSet) {
            this.index = index + 1;
            this.maxLayer = maxLayer;
            this.childrenSet = childrenSet;

            this.next = null;
            this.moreNext = true;
        }

        @Override
        public Iterator<Point> iterator() {
            return this;
        }
    }
}

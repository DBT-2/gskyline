package alg.gskyline;

import entity.DSG;
import entity.Point;
import entity.SkylineGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static alg.Utils.preprocess;

public class PtwiseGSkyline {

    private static final Logger LOGGER = LoggerFactory.getLogger(PtwiseGSkyline.class);

    private static boolean canAdd(SkylineGroup group, Point pt) {
        Set<Point> pts = group.pts;
        for (Point parent : pt.parents) {
            if (!pts.contains(parent))
                return false;
        }
        return true;
    }

    private static void filterTailSet(Set<Point> tailSet, Set<Point> childrenSet, int maxLayer) {
        List<Point> toRemove = new ArrayList<>();
        for (Point point : tailSet) {
            if (!childrenSet.contains(point) && !point.isSkyline()) {
                toRemove.add(point);
            } else if(point.layer - maxLayer >= 2) {
                toRemove.add(point);
            }
        }
        tailSet.removeAll(toRemove);
        LOGGER.debug("{} points filtered", toRemove.size());
    }

    public static List<SkylineGroup> run(DSG dsg, int k) {
        LOGGER.info("Start {} point-wise group skyline...", k);

        List<SkylineGroup> finalGroups = preprocess(dsg, k);
        LOGGER.info("Found {} final groups after preprocess", finalGroups.size());

        List<SkylineGroup> currGroups = new ArrayList<>();
        List<SkylineGroup> nextGroups = new ArrayList<>();
        currGroups.add(new SkylineGroup(Collections.emptySet()));

        for (int i = 1; i <= k; i++) {
            long startTime = System.currentTimeMillis();
            int pruned = 0;
            for(int j = 0; j < currGroups.size(); j++) {
                SkylineGroup group = currGroups.get(j);
                Set<Point> childrenSet = group.getChildrenSet();
                int maxLayer = group.maxLayer;
                Set<Point> tailSet= group.getTailSet(dsg);

                LOGGER.debug("Tail set before: {}", tailSet);
                filterTailSet(tailSet, childrenSet, maxLayer);
                LOGGER.debug("Tail set after: {}", tailSet);

                pruned += addCandidate(tailSet, group, nextGroups, i, k);

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
        return currGroups;
    }

    private static int addCandidate(Set<Point> tailSet, SkylineGroup currGroup, List<SkylineGroup> nextGroups,
                                    int currLevel, int maxLevel) {
        int pruned = 0;
        for(Point point : tailSet) {
            if (!canAdd(currGroup, point)){
                pruned ++;
                continue;
            }
            Set<Point> newPts = new HashSet<>(currGroup.pts);
            newPts.add(point);
            SkylineGroup newGroup = new SkylineGroup(newPts);

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
}

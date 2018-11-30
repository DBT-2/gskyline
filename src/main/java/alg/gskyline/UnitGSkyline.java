package alg.gskyline;

import entity.Point;
import entity.UnitGroup;

import java.util.*;

public class UnitGSkyline {

    private static List<UnitGroup> buildUnitGroup(List<Point>[] layers, int k) {
        List<UnitGroup> ret = new ArrayList<>();

        for(int i = k - 1; i >= 0; i--) {
            for(Point point : layers[i]) {
                if(point.parents.size() < k) {
                    List<Point> pointList = new ArrayList<>();
                    pointList.add(point);
                    List<Integer> parentSet = new ArrayList<>();
                    for(Point pt : point.parents)
                        parentSet.add(pt.index);
                    parentSet.add(point.index);
                    UnitGroup unitGroup = new UnitGroup(pointList, parentSet);
                    ret.add(unitGroup);
                }
            }
        }
        return ret;
    }

    private static List<UnitGroup> preprocess(List<UnitGroup> groups, int k, List<List<Integer>> resultGroups) {
        List<UnitGroup> newGroups = new ArrayList<>();
        for(UnitGroup unitGroup : groups) {
            if(unitGroup.parentSet.size() == k) {
                resultGroups.add(unitGroup.parentSet);
            } else {
                newGroups.add(unitGroup);
            }
        }
        return newGroups;
    }

    private static void calTailSet(List<UnitGroup> groups) {
        for(int i = 0; i < groups.size(); i++) {
            groups.get(i).tailSet = groups.subList(i + 1, groups.size());
        }
    }

    private static List<UnitGroup> subset(List<UnitGroup> groups, int k, List<List<Integer>> resultGroups) {
        int i;
        for (i = groups.size() - 1; i >= 0; i--) {
            List<Integer> allSet = new ArrayList<>();
            for (int j = i; j < groups.size(); j++) {
                allSet.removeAll(groups.get(j).parentSet);
                allSet.addAll(groups.get(j).parentSet);
            }
            if(allSet.size() == k) {
                List<Point> pointList = new ArrayList<>();
                for(k = i; k < groups.size(); k++) {
                    pointList.remove(groups.get(k).pts.get(0));
                    pointList.add(groups.get(k).pts.get(0));
                }
                UnitGroup newGroup = new UnitGroup(pointList, allSet);
                resultGroups.add(newGroup.parentSet);
                break;
            } else if(allSet.size() > k) {
                break;
            }
        }
        return groups.subList(0, i);
    }

    private static void unitwise(List<UnitGroup> unitGroups, int k, List<List<Integer>> resultGroups) {
        List<UnitGroup> currLevel = unitGroups;
        List<UnitGroup> nextLevel = new ArrayList<>();

        for(int i = 0; i < k; i++) {
            if(currLevel.size() == 0)
                break;
            for(UnitGroup unitGroup : currLevel) {
                for(int d = 0; d < unitGroup.tailSet.size(); d++) {
                    UnitGroup candidate = unitGroup.tailSet.get(d);
                    if(unitGroup.parentSet.contains(candidate.pts.get(0).index))
                        continue;

                    List<Integer> newParents = new ArrayList<>(unitGroup.parentSet);
                    newParents.removeAll(candidate.parentSet);
                    newParents.addAll(candidate.parentSet);
                    if(newParents.size() == k) {
//                        List<Point> newPoint = new ArrayList<>(unitGroup.pts);
//                        newPoint.addAll(candidate.pts);
                          resultGroups.add(newParents);
                    } else if (newParents.size() <= k) {
                        if(newParents.size() == unitGroup.parentSet.size())
                            continue;
//                        List<Point> newPoint = new ArrayList<>(unitGroup.pts);
//                        newPoint.addAll(candidate.pts);
                        UnitGroup newGroup = new UnitGroup(null, newParents);
                        newGroup.tailSet = candidate.tailSet;
                        nextLevel.add(newGroup);
                    }
                }
            }
            List<UnitGroup> temp = currLevel;
            currLevel = nextLevel;
            nextLevel = temp;
            nextLevel = new ArrayList<>();
        }
    }

    public static List<List<Integer>> unitGroupWise(List<Point>[] layers, int k) {
        long startTime = System.currentTimeMillis();
        List<List<Integer>> resultGroups = new ArrayList<>();
        List<UnitGroup> initialGrous = buildUnitGroup(layers, k);
        initialGrous = preprocess(initialGrous, k, resultGroups);
        calTailSet(initialGrous);
        initialGrous = subset(initialGrous, k, resultGroups);
        System.out.println("preprocess consumed " + (System.currentTimeMillis() - startTime) + "ms");
        unitwise(initialGrous, k, resultGroups);
        System.out.println("grouping consumed " + (System.currentTimeMillis() - startTime) + "ms");
        return resultGroups;
    }
}

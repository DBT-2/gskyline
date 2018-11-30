package entity;

import conf.Config;

import java.util.HashSet;
import java.util.Set;

public class TreeSkylineGroup extends SkylineGroup {

    public GroupTreeNode treeNode = null;

    public TreeSkylineGroup(){}

    public TreeSkylineGroup(GroupTreeNode node) {
        this.treeNode = node;
    }

    protected Set<Point> calChiddrenSet() {
        Set<Point> childrenSet = new HashSet<>();
        GroupTreeNode currNode = treeNode;
        while (currNode.nodePoint != null) {
            childrenSet.addAll(currNode.nodePoint.children);
            currNode = currNode.parent;
        }
        return childrenSet;
    }

    @Override
    public boolean canAdd(Point point) {
        for (Point parent : point.parents) {
            if (!treeNode.contains(parent))
                return false;
        }
        return true;
    }

    @Override
    public SkylineGroup add(Point point) {
        GroupTreeNode newNode = new GroupTreeNode(point, this.treeNode);
        newNode.parent = this.treeNode;
        newNode.nodePoint = point;
        newNode.depth = treeNode.depth + 1;
        TreeSkylineGroup newGroup = (TreeSkylineGroup) Config.groupManager.allocate();
        newGroup.treeNode = newNode;
        return newGroup;
    }

    @Override
    public void discard() {
        super.discard();
        treeNode = null;
        Config.groupManager.free(this);
    }

    @Override
    public int level() {
        return treeNode.depth;
    }

    @Override
    public String toString() {
        return "{" +
                 treeNode +
                '}';
    }

    public static class Manager extends GroupManager<TreeSkylineGroup> {

        @Override
        protected TreeSkylineGroup createSkylineGroup() {
            return new TreeSkylineGroup();
        }

        @Override
        public TreeSkylineGroup generateRoot() {
            return new TreeSkylineGroup(new GroupTreeNode(null, null));
        }
    }
}

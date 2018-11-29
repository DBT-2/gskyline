package entity;

import java.util.HashSet;
import java.util.Set;

public class TreeSkylineGroup extends SkylineGroup {

    public GroupTreeNode treeNode = null;

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
        GroupTreeNode newNode = TreeNodeManager.INSTANCE.allocate();
        newNode.depth = treeNode.depth + 1;
        return new TreeSkylineGroup(newNode);
    }

    @Override
    public void discard() {
        TreeNodeManager.INSTANCE.free(treeNode);
    }

    @Override
    public int level() {
        return treeNode.depth;
    }
}

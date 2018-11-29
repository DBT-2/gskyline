package entity;

public class GroupTreeNode implements Cloneable {
    public Point nodePoint;
    public GroupTreeNode parent;

    public GroupTreeNode(Point point, GroupTreeNode treeNode) {
        this.nodePoint = point;
        this.parent = treeNode;
    }

    public boolean contains(Point point) {
        GroupTreeNode currNode = this;
        while (currNode.nodePoint != null) {
            if (currNode.nodePoint.equals(point))
                return true;
            currNode = currNode.parent;
        }
        return false;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}

package entity;

public class GroupTreeNode implements Cloneable {
    public Point nodePoint;
    public GroupTreeNode parent;
    public int depth = 0;

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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        GroupTreeNode currNode = this;
        while (currNode.nodePoint != null) {
            builder.append(currNode.nodePoint.toString()).append(",");
            currNode = currNode.parent;
        }
        return  builder.toString();
    }
}

package entity;

import java.util.LinkedList;
import java.util.Queue;

/**
 * TreeNodeManager manages reusable GroupTreeNode objects to lower GC overhead.
 */
public class TreeNodeManager {
    private static final int INITIAL_CAPACITY = 100000;
    private static final int STEP = 50000;
    public static final TreeNodeManager INSTANCE = new TreeNodeManager();

    private Queue<GroupTreeNode> nodeQueue = new LinkedList<>();

    private TreeNodeManager() {
        for (int i = 0; i < INITIAL_CAPACITY; i ++) {
            nodeQueue.add(new GroupTreeNode(null, null));
        }
    }

    private void addMore() {
        for (int i = 0; i < STEP; i ++) {
            nodeQueue.add(new GroupTreeNode(null, null));
        }
    }

    public GroupTreeNode allocate() {
        if(nodeQueue.size() == 0) {
            addMore();
        }
        return nodeQueue.remove();
    }

    public void free(GroupTreeNode node) {
        nodeQueue.add(node);
    }
}

package entity;

import java.util.LinkedList;
import java.util.Queue;

/**
 * GroupManager manages reusable GroupTreeNode objects to lower GC overhead.
 */
public abstract class GroupManager<T extends SkylineGroup> {
    private static final int INITIAL_CAPACITY = 100000;
    private static final int STEP = 50000;

    private Queue<T> nodeQueue = new LinkedList<>();
    private T root;

    protected GroupManager() {
        for (int i = 0; i < INITIAL_CAPACITY; i ++) {
            nodeQueue.add(createSkylineGroup());
        }
    }

    private void addMore() {
        for (int i = 0; i < STEP; i ++) {
            nodeQueue.add(createSkylineGroup());
        }
    }

    public T allocate() {
        if(nodeQueue.size() == 0) {
            addMore();
        }
        return nodeQueue.remove();
    }

    abstract protected T createSkylineGroup();

    abstract protected T generateRoot();

    public T getRoot() {
        if (root == null)
            root = generateRoot();
        return root;
    }

    public void free(T obj) {
        if(obj != root)
            nodeQueue.add(obj);
    }
}

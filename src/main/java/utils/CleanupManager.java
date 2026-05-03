package utils;

import java.util.ArrayDeque;
import java.util.Deque;

public class CleanupManager {


    private final Deque<Runnable> cleanupStack = new ArrayDeque<>();

    public void register(Runnable action) {
        cleanupStack.push(action);
    }

    public void cleanup() {
        while (!cleanupStack.isEmpty()) {
            try {
                cleanupStack.pop().run();
            } catch (Exception e) {
                System.err.println("Cleanup failed: " + e.getMessage());
            }
        }
    }


}

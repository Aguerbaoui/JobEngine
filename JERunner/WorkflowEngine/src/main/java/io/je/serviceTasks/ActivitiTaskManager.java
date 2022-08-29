package io.je.serviceTasks;

import java.util.HashMap;

public class ActivitiTaskManager {

    private static HashMap<String, ActivitiTask> tasks;

    public static void addTask(ActivitiTask task) {
        if (tasks == null) {
            tasks = new HashMap<>();
        }

        tasks.put(task.getTaskId(), task);
    }

    public static ActivitiTask getTask(String taskId) {
        return tasks.get(taskId);
    }
}

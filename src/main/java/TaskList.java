public class TaskList {
    private Task[] tasks;
    private int taskCount;

    public TaskList() {
        this.tasks = new Task[100];
        this.taskCount = 0;
    }

    public void addToDo(String description) {
        Task newTask = new ToDo(description);
        this.tasks[taskCount] = newTask;
        this.taskCount++;
    }

    public void addEvent(String description, String from, String to) {
        Task newTask = new Event(description, from, to);
        this.tasks[taskCount] = newTask;
        this.taskCount++;
    }

    public void addDeadline(String description, String deadline) {
        Task newTask = new Deadline(description, deadline);
        this.tasks[taskCount] = newTask;
        this.taskCount++;
    }

    public void markAsDone(int index) {
        if (index < 1 || index > this.taskCount || this.tasks[index] == null) {
            System.out.printf("Task %d does not exist.\n", index);
            return;
        }
        this.tasks[index].markAsDone();
    }

    public void markAsUndone(int index) {
        if (index < 1 || index > this.taskCount || this.tasks[index] == null) {
            System.out.printf("Task %d does not exist.\n", index);
            return;
        }
        this.tasks[index].markAsUndone();
    }

    public void printTaskList() {
        if (this.taskCount == 0) {
            System.out.println("You have no task saved.");
            return;
        }

        for (int i = 0; i < this.taskCount; i++) {
            Task curr = this.tasks[i];
            System.out.printf("%d. [%s] %s %n", i + 1, curr.getStatusIcon(), curr.getDescription());
        }
    }
}

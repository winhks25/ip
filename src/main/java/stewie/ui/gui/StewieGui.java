package stewie.ui.gui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import stewie.model.TaskList;
import stewie.parser.Command;
import stewie.parser.Parser;

/**
 * Represents the modern Instagram-inspired chat workspace for Stewie.
 *
 * The view keeps the existing parser and task list as its application logic,
 * while presenting task actions as chat messages and interactive task cards.
 */
public class StewieGui extends BorderPane {
    private static final Pattern TASK_PATTERN = Pattern.compile("\\[([TDE])] \\[(X| )] (.*)");
    private static final String[] QUICK_COMMANDS = {"todo plan my week", "list", "help"};

    private final TaskList taskList;
    private final VBox conversation;
    private final ScrollPane conversationScroll;
    private final Label taskSummary;
    private final TextField messageField;

    /**
     * Creates a chat workspace connected to the supplied task list.
     *
     * @param taskList the task list used by the application
     */
    public StewieGui(TaskList taskList) {
        this.taskList = taskList;
        this.conversation = new VBox(18);
        this.conversationScroll = createConversationScroll();
        this.taskSummary = new Label();
        this.messageField = new TextField();

        setLeft(createSidebar());
        setCenter(createChatPanel());
        addWelcomeMessage();
        refreshTaskSummary();
    }

    /**
     * Creates the sidebar containing the brand, navigation, and task summary.
     *
     * @return the styled sidebar
     */
    private VBox createSidebar() {
        VBox sidebar = new VBox(24);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(30, 22, 26, 22));
        sidebar.setPrefWidth(270);

        HBox brand = new HBox(12);
        brand.setAlignment(Pos.CENTER_LEFT);
        StackPane logo = createLogo(46);
        VBox brandText = new VBox(2);
        Label brandName = new Label("stewie.");
        brandName.getStyleClass().add("brand-name");
        Label brandTagline = new Label("your tiny task studio");
        brandTagline.getStyleClass().add("muted-label");
        brandText.getChildren().addAll(brandName, brandTagline);
        brand.getChildren().addAll(logo, brandText);

        VBox navigation = new VBox(8);
        navigation.getChildren().addAll(
                createNavigationButton("⌂", "Home", true),
                createNavigationButton("⌕", "Discover", false),
                createNavigationButton("♡", "Saved ideas", false));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox summaryCard = new VBox(12);
        summaryCard.getStyleClass().add("summary-card");
        Label summaryTitle = new Label("Your space");
        summaryTitle.getStyleClass().add("summary-title");
        taskSummary.getStyleClass().add("summary-value");
        Label summaryHint = new Label("Keep the little things moving.");
        summaryHint.getStyleClass().add("summary-hint");
        summaryHint.setWrapText(true);
        summaryCard.getChildren().addAll(summaryTitle, taskSummary, summaryHint);

        Label footer = new Label("made for small wins  ✦");
        footer.getStyleClass().add("muted-label");

        sidebar.getChildren().addAll(brand, navigation, spacer, summaryCard, footer);
        return sidebar;
    }

    /**
     * Creates the central chat panel with its header, conversation, and composer.
     *
     * @return the styled chat panel
     */
    private VBox createChatPanel() {
        VBox chatPanel = new VBox();
        chatPanel.getStyleClass().add("chat-panel");

        HBox header = new HBox(14);
        header.getStyleClass().add("chat-header");
        header.setAlignment(Pos.CENTER_LEFT);

        StackPane avatar = createLogo(48);
        VBox identity = new VBox(3);
        Label title = new Label("Stewie Assistant");
        title.getStyleClass().add("chat-title");
        HBox status = new HBox(6);
        status.setAlignment(Pos.CENTER_LEFT);
        Circle onlineDot = new Circle(4, Color.web("#5de39b"));
        Label statusText = new Label("online · ready to help");
        statusText.getStyleClass().add("muted-label");
        status.getChildren().addAll(onlineDot, statusText);
        identity.getChildren().addAll(title, status);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);
        Label headerHint = new Label("TASKS / CHAT");
        headerHint.getStyleClass().add("header-hint");
        header.getChildren().addAll(avatar, identity, headerSpacer, headerHint);

        VBox.setVgrow(conversationScroll, Priority.ALWAYS);
        VBox composer = createComposer();
        chatPanel.getChildren().addAll(header, conversationScroll, composer);
        return chatPanel;
    }

    /**
     * Creates the scrollable message area.
     *
     * @return a scroll pane containing the conversation
     */
    private ScrollPane createConversationScroll() {
        conversation.getStyleClass().add("conversation");
        conversation.setPadding(new Insets(28, 48, 28, 48));

        ScrollPane scrollPane = new ScrollPane(conversation);
        scrollPane.getStyleClass().add("conversation-scroll");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        return scrollPane;
    }

    /**
     * Creates the command composer at the bottom of the chat panel.
     *
     * @return the composer controls
     */
    private VBox createComposer() {
        VBox composer = new VBox(12);
        composer.getStyleClass().add("composer");

        HBox quickCommands = new HBox(8);
        quickCommands.setAlignment(Pos.CENTER_LEFT);
        for (String command : QUICK_COMMANDS) {
            Button chip = new Button(command);
            chip.getStyleClass().add("quick-chip");
            chip.setOnAction(event -> {
                messageField.setText(command);
                messageField.requestFocus();
            });
            quickCommands.getChildren().add(chip);
        }

        HBox inputRow = new HBox(10);
        inputRow.setAlignment(Pos.CENTER_LEFT);
        messageField.setPromptText("Message Stewie about your next small win...");
        messageField.getStyleClass().add("message-field");
        messageField.setOnAction(event -> sendMessage());
        HBox.setHgrow(messageField, Priority.ALWAYS);

        Button sendButton = new Button("➤");
        sendButton.getStyleClass().add("send-button");
        sendButton.setOnAction(event -> sendMessage());
        inputRow.getChildren().addAll(messageField, sendButton);
        composer.getChildren().addAll(quickCommands, inputRow);
        return composer;
    }

    /**
     * Adds Stewie's initial greeting and command suggestions.
     */
    private void addWelcomeMessage() {
        appendMessage(false, "Hey, I’m Stewie ✨\nTell me what you want to remember, plan, or find.");
        appendMessage(false, "Try `todo buy groceries`, `list`, or `find groceries` to get started.");
    }

    /**
     * Sends the text currently in the composer to the task command handler.
     */
    private void sendMessage() {
        String rawInput = messageField.getText().trim();
        if (rawInput.isEmpty()) {
            return;
        }

        appendMessage(true, rawInput);
        messageField.clear();
        handleCommand(rawInput.toLowerCase());
        refreshTaskSummary();
        scrollToBottom();
    }

    /**
     * Handles one normalized command using the existing Stewie parser.
     *
     * @param input the normalized user command
     */
    private void handleCommand(String input) {
        if ("help".equals(input)) {
            appendMessage(false,
                    "I can help with `todo`, `event`, `deadline`, `list`, `find`, `mark`, `unmark`, `delete`, and "
                            + "`update`. "
                            + "For example: `todo call Mum`.");
            return;
        }

        Command command = Parser.getCommand(input);
        try {
            switch (command) {
                case BYE:
                    appendMessage(false, "I’ll be here whenever your next idea shows up. ✦");
                    break;
                case LIST:
                    showTaskList("Here’s what is currently on your list.");
                    break;
                case MARK:
                    updateTaskStatus(input, true);
                    break;
                case UNMARK:
                    updateTaskStatus(input, false);
                    break;
                case DEADLINE:
                    addDeadline(input);
                    break;
                case EVENT:
                    addEvent(input);
                    break;
                case TODO:
                    addTodo(input);
                    break;
                case DELETE:
                    deleteTask(input);
                    break;
                case FIND:
                    findTasks(input);
                    break;
                case UPDATE:
                    updateTask(input);
                    break;
                default:
                    appendMessage(false,
                            "I didn’t quite catch that. Try `todo`, `event`, `deadline`, `list`, `find`, "
                                    + "`mark`, `unmark`, `delete`, or `update`.");
                    break;
            }
        } catch (IllegalArgumentException exception) {
            appendMessage(false, exception.getMessage());
        }
    }

    /**
     * Adds a todo task from a user command.
     *
     * @param input the normalized todo command
     */
    private void addTodo(String input) {
        try {
            taskList.addToDo(Parser.parseTodo(input));
            appendMessage(false, "Done — that’s on your list now ✨");
        } catch (ArrayIndexOutOfBoundsException exception) {
            appendMessage(false, "Tell me what the todo is, for example: `todo call Mum`.");
        }
    }

    /**
     * Adds a deadline task from a user command.
     *
     * @param input the normalized deadline command
     */
    private void addDeadline(String input) {
        try {
            String[] parsedInput = Parser.parseDeadline(input);
            taskList.addDeadline(parsedInput[0], parsedInput[1]);
            appendMessage(false, "Deadline saved. Future you will be grateful. ⏳");
        } catch (ArrayIndexOutOfBoundsException exception) {
            appendMessage(false,
                    "Use `deadline <description> /by <date>`, such as `deadline submit report /by 25 Dec 2026`.");
        }
    }

    /**
     * Adds an event task from a user command.
     *
     * @param input the normalized event command
     */
    private void addEvent(String input) {
        try {
            String[] parsedInput = Parser.parseEvent(input);
            taskList.addEvent(parsedInput[0], parsedInput[1], parsedInput[2]);
            appendMessage(false, "Event added to your timeline. 📅");
        } catch (ArrayIndexOutOfBoundsException exception) {
            appendMessage(false, "Use `event <description> /from <date> /to <date>`.");
        }
    }

    /**
     * Updates the completion status of a task.
     *
     * @param input the normalized mark or unmark command
     * @param isDone whether the task should be marked complete
     */
    private void updateTaskStatus(String input, boolean isDone) {
        int index = Parser.getTaskIndex(input);
        if (!isValidTaskIndex(index)) {
            appendMessage(false, "Choose a task number that exists, such as `mark 1`.");
            return;
        }

        if (isDone) {
            taskList.markAsDone(index);
            appendMessage(false, "That one is done — nice work. ✅");
        } else {
            taskList.markAsUndone(index);
            appendMessage(false, "Task reopened. Sometimes plans change.");
        }
        showTaskList("Here’s the refreshed view:");
    }

    /**
     * Deletes a task from the task list.
     *
     * @param input the normalized delete command
     */
    private void deleteTask(String input) {
        int index = Parser.getTaskIndex(input);
        if (!isValidTaskIndex(index)) {
            appendMessage(false, "Choose a task number that exists, such as `delete 1`.");
            return;
        }

        taskList.deleteTask(index);
        appendMessage(false, "Removed. A little more breathing room. ✦");
        showTaskList("Here’s what remains:");
    }

    /**
     * Updates a task description from a user command.
     *
     * @param input the normalized update command
     */
    private void updateTask(String input) {
        int index = Parser.getUpdateTaskIndex(input);
        String description = Parser.parseUpdateDescription(input);
        if (!isValidTaskIndex(index) || description.isBlank()) {
            appendMessage(false, "Use `update <number> <description>`, such as `update 1 call Mum`.");
            return;
        }

        taskList.updateTask(index, description);
        appendMessage(false, "Updated. The details are still safely attached. ✨");
        showTaskList("Here’s the refreshed view:");
    }

    /**
     * Searches the task list and displays matching task cards.
     *
     * @param input the normalized find command
     */
    private void findTasks(String input) {
        String[] keywords = Parser.parseFindKeywords(input);
        String[] matches = taskList.findTasks(keywords);
        if (matches.length == 0) {
            appendMessage(false, "No matches yet. Try a different keyword or `list` to see everything.");
            return;
        }

        appendMessage(false, "I found these for you:");
        VBox matchesBox = new VBox(8);
        matchesBox.getStyleClass().add("task-group");
        for (int index = 0; index < matches.length; index++) {
            matchesBox.getChildren().add(createTaskCard(index + 1, matches[index], false));
        }
        conversation.getChildren().add(matchesBox);
    }

    /**
     * Displays every current task as an interactive card.
     *
     * @param response the assistant message shown above the cards
     */
    private void showTaskList(String response) {
        appendMessage(false, response);
        String[] tasks = taskList.produceTaskList();
        if (tasks.length == 0) {
            appendMessage(false, "Your list is clear. A perfect place for the next small win.");
            return;
        }

        VBox taskGroup = new VBox(8);
        taskGroup.getStyleClass().add("task-group");
        for (int index = 0; index < tasks.length; index++) {
            taskGroup.getChildren().add(createTaskCard(index + 1, tasks[index], true));
        }
        conversation.getChildren().add(taskGroup);
    }

    /**
     * Creates one task card for the conversation.
     *
     * @param index the one-based task number
     * @param taskText the task text from the task list
     * @param isInteractive whether the card should expose task actions
     * @return the task card
     */
    private HBox createTaskCard(int index, String taskText, boolean isInteractive) {
        Matcher matcher = TASK_PATTERN.matcher(taskText);
        String type = matcher.matches() ? matcher.group(1) : "T";
        boolean isDone = matcher.matches() && "X".equals(matcher.group(2));
        String description = matcher.matches() ? matcher.group(3) : taskText;

        HBox card = new HBox(12);
        card.getStyleClass().add("task-card");
        if (isDone) {
            card.getStyleClass().add("task-done");
        }
        card.setAlignment(Pos.CENTER_LEFT);

        Label typeBadge = new Label(type);
        typeBadge.getStyleClass().addAll("task-type", "task-type-" + type.toLowerCase());

        VBox details = new VBox(3);
        HBox.setHgrow(details, Priority.ALWAYS);
        Label taskLabel = new Label(description);
        taskLabel.getStyleClass().add("task-description");
        taskLabel.setWrapText(true);
        Label numberLabel = new Label(String.format("task %02d", index));
        numberLabel.getStyleClass().add("task-number");
        details.getChildren().addAll(taskLabel, numberLabel);

        if (isInteractive) {
            CheckBox doneBox = new CheckBox();
            doneBox.setSelected(isDone);
            doneBox.getStyleClass().add("task-check");
            doneBox.setOnAction(event -> {
                if (doneBox.isSelected()) {
                    taskList.markAsDone(index - 1);
                } else {
                    taskList.markAsUndone(index - 1);
                }
                refreshTaskSummary();
                showTaskList("Updated — your list is looking good:");
            });

            Button deleteButton = new Button("×");
            deleteButton.getStyleClass().add("delete-button");
            deleteButton.setOnAction(event -> {
                taskList.deleteTask(index - 1);
                refreshTaskSummary();
                showTaskList("Removed — here’s the current view:");
            });
            card.getChildren().addAll(typeBadge, details, doneBox, deleteButton);
        } else {
            card.getChildren().addAll(typeBadge, details);
        }
        return card;
    }

    /**
     * Adds a message bubble to the conversation.
     *
     * @param isUser whether the message came from the user
     * @param text the message text
     */
    private void appendMessage(boolean isUser, String text) {
        Label message = new Label(text);
        message.setWrapText(true);
        message.setMaxWidth(570);
        message.getStyleClass().add("message-bubble");
        message.getStyleClass().add(isUser ? "user-bubble" : "assistant-bubble");

        HBox row = new HBox(message);
        row.setAlignment(isUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        row.getStyleClass().add("message-row");
        conversation.getChildren().add(row);
    }

    /**
     * Refreshes the task count displayed in the sidebar.
     */
    private void refreshTaskSummary() {
        String[] tasks = taskList.produceTaskList();
        long completedTasks = 0;
        for (String task : tasks) {
            if (task.contains("[X]")) {
                completedTasks++;
            }
        }
        taskSummary.setText(String.format("%d tasks  ·  %d done", tasks.length, completedTasks));
    }

    /**
     * Returns whether a zero-based task index points to an existing task.
     *
     * @param index the zero-based task index
     * @return true when the index is valid
     */
    private boolean isValidTaskIndex(int index) {
        return index >= 0 && index < taskList.produceTaskList().length;
    }

    /**
     * Scrolls the conversation to its newest content.
     */
    private void scrollToBottom() {
        Platform.runLater(() -> conversationScroll.setVvalue(1.0));
    }

    /**
     * Creates a circular Stewie logo with a gradient-themed style class.
     *
     * @param size the diameter of the logo
     * @return a logo node
     */
    private StackPane createLogo(double size) {
        Circle circle = new Circle(size / 2);
        circle.getStyleClass().add("logo-circle");
        Label mark = new Label("S");
        mark.getStyleClass().add("logo-mark");

        StackPane logo = new StackPane(circle, mark);
        logo.setMinSize(size, size);
        logo.setPrefSize(size, size);
        logo.setMaxSize(size, size);
        return logo;
    }

    /**
     * Creates one item in the sidebar navigation.
     *
     * @param icon the navigation icon
     * @param labelText the navigation label
     * @param isActive whether the item represents the current page
     * @return a navigation button
     */
    private Button createNavigationButton(String icon, String labelText, boolean isActive) {
        Button button = new Button(icon + "    " + labelText);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.getStyleClass().add("navigation-button");
        if (isActive) {
            button.getStyleClass().add("navigation-button-active");
        }
        return button;
    }
}

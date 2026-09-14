package stewie.ui.gui;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kordamp.ikonli.javafx.FontIcon;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import stewie.model.TaskList;
import stewie.parser.Command;
import stewie.parser.Parser;
import stewie.storage.StorageException;
import stewie.ui.Dialogue;

/**
 * Represents the modern Instagram-inspired chat workspace for Stewie.
 *
 * The view keeps the existing parser and task list as its application logic,
 * while presenting task actions as chat messages and interactive task cards.
 */
public class StewieGui extends BorderPane {
    private static final Pattern TASK_PATTERN = Pattern.compile("\\[([TDE])] \\[(X| )] (.*)");
    private static final String[] QUICK_COMMANDS = {"todo plan my week", "list", "help"};

    private static final String COMMAND_HELP = """
            The instructions. A brief reading should spare us both a great deal of theatre.

            todo <description>
            Add a task without a date.

            deadline <description> /by <date>
            Add a task with a deadline.

            event <description> /from <date> /to <date>
            Add an event with start and end dates.

            list
            Show all tasks and their numbers.

            find <keyword> [more keywords]
            Find tasks matching keywords.

            mark <number>
            Mark a task as done.

            unmark <number>
            Mark a task as undone.

            delete <number>
            Remove a task.

            update <number> [description] [d/<date>] [from/<date>] [to/<date>]
            Change one or more fields; omitted fields stay unchanged.
            Use d/ or by/ for deadlines, and from/ or to/ for events.

            help
            Show this command reference in Chat.

            bye
            Show a farewell message in Chat.

            Replace <...> with your values; [...] means optional.
            Task numbers start at 1. Use list to check the current numbers.
            Dates include 2026-08-12, 12/08/2026, 12-08-2026, 12.08.2026,
            12 Aug 2026, 12 August 2026, Aug 12, 2026, or August 12, 2026.
            Example: deadline submit report /by 12/08/2026
            """;

    private final Image stewiePhoto = loadPhoto();
    private final TaskList taskList;
    private final VBox conversation;
    private final ScrollPane conversationScroll;
    private final Label taskSummary;
    private final TextField messageField;
    private final VBox chatPanel;
    private final VBox listPanel;
    private final VBox helpPanel;
    private final VBox listTaskContainer;
    private Timeline scrollAnimation;
    // Keeps each newly completed task visible until its individual delay expires.
    private final Map<Integer, PauseTransition> completionDelays = new HashMap<>();

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
        this.chatPanel = createChatPanel();
        this.listTaskContainer = new VBox(8);
        this.listPanel = createListPanel();
        this.helpPanel = createHelpPanel();

        setLeft(createSidebar());
        setCenter(chatPanel);
        addWelcomeMessage();
        if (!taskList.getLoadWarning().isEmpty()) {
            appendMessage(false, taskList.getLoadWarning());
        }
        refreshTaskSummary();
    }

    /**
     * Creates the sidebar containing the brand, navigation, and task summary.
     *
     * @return the styled sidebar
     */
    private VBox createSidebar() {
        VBox sidebar = createSidebarContainer();
        sidebar.getChildren().addAll(createBrand(), createNavigation(), createSidebarSpacer(),
                createSummaryCard(), createSidebarFooter());
        return sidebar;
    }

    /** Creates the sidebar's outer layout. */
    private VBox createSidebarContainer() {
        VBox sidebar = new VBox(24);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(30, 22, 26, 22));
        sidebar.setPrefWidth(270);
        return sidebar;
    }

    /** Creates the sidebar portrait, brand name, and tagline. */
    private HBox createBrand() {
        HBox brand = new HBox(12);
        brand.setAlignment(Pos.CENTER_LEFT);
        StackPane logo = createLogo(46);
        VBox brandText = new VBox(2);
        Label brandName = new Label("stewie.");
        brandName.getStyleClass().add("brand-name");
        Label brandTagline = new Label("a modest command centre");
        brandTagline.getStyleClass().add("muted-label");
        brandText.getChildren().addAll(brandName, brandTagline);
        brand.getChildren().addAll(logo, brandText);

        return brand;
    }

    /** Creates navigation controls whose handlers only select a page and its highlight. */
    private VBox createNavigation() {
        VBox navigation = new VBox(8);
        Button chatButton = createNavigationButton("fth-message-circle", "Chat", true);
        Button listButton = createNavigationButton("fth-list", "My List", false);
        Button helpButton = createNavigationButton("fth-help-circle", "Help", false);
        chatButton.setOnAction(event -> {
            showChatPanel();
            selectNavigation(chatButton, listButton, helpButton);
        });
        listButton.setOnAction(event -> {
            showListPanel();
            selectNavigation(listButton, chatButton, helpButton);
        });
        helpButton.setOnAction(event -> {
            showHelpPanel();
            selectNavigation(helpButton, chatButton, listButton);
        });
        navigation.getChildren().addAll(chatButton, listButton, helpButton);
        return navigation;
    }

    /** Restores the conversation while discarding pending list animations. */
    private void showChatPanel() {
        cancelCompletionDelays();
        setCenter(chatPanel);
    }

    /** Refreshes the list on entry, retaining delays when its selected tab is clicked again. */
    private void showListPanel() {
        if (getCenter() != listPanel) {
            cancelCompletionDelays();
            refreshListPanel();
        }
        setCenter(listPanel);
    }

    /** Opens the reference and stops timers belonging to the previous list view. */
    private void showHelpPanel() {
        cancelCompletionDelays();
        setCenter(helpPanel);
    }

    /** Stops pending completion timers before discarding their callbacks. */
    private void cancelCompletionDelays() {
        completionDelays.values().forEach(PauseTransition::stop);
        completionDelays.clear();
    }

    /** Keeps exactly one navigation button highlighted. */
    private void selectNavigation(Button selected, Button... others) {
        for (Button button : others) {
            button.getStyleClass().remove("navigation-button-active");
        }
        if (!selected.getStyleClass().contains("navigation-button-active")) {
            selected.getStyleClass().add("navigation-button-active");
        }
    }

    /** Creates flexible space that keeps the summary near the bottom. */
    private Region createSidebarSpacer() {
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    /** Creates the card containing the live task count and its explanatory labels. */
    private VBox createSummaryCard() {
        VBox summaryCard = new VBox(12);
        summaryCard.getStyleClass().add("summary-card");
        Label summaryTitle = new Label("Your space");
        summaryTitle.getStyleClass().add("summary-title");
        taskSummary.getStyleClass().add("summary-value");
        Label summaryHint = new Label("Even grand plans need a list.");
        summaryHint.getStyleClass().add("summary-hint");
        summaryHint.setWrapText(true);
        summaryCard.getChildren().addAll(summaryTitle, taskSummary, summaryHint);

        return summaryCard;
    }

    /** Creates the sidebar's closing caption. */
    private Label createSidebarFooter() {
        Label footer = new Label("supervised by a genius");
        footer.getStyleClass().add("muted-label");
        return footer;
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
        Label statusText = new Label("online · awaiting your agenda");
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
     * Creates the My List panel with a heading and a scrollable task container.
     *
     * @return the styled list panel
     */
    private VBox createListPanel() {
        VBox listPanelBox = new VBox();
        listPanelBox.getStyleClass().add("chat-panel");

        HBox header = new HBox(14);
        header.getStyleClass().add("chat-header");
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("My List");
        title.getStyleClass().add("chat-title");

        header.getChildren().add(title);
        listTaskContainer.getStyleClass().add("task-group");
        listTaskContainer.setPadding(new Insets(28, 48, 28, 48));

        ScrollPane listScroll = new ScrollPane(listTaskContainer);
        listScroll.getStyleClass().add("conversation-scroll");
        listScroll.setFitToWidth(true);
        listScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        listScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        VBox.setVgrow(listScroll, Priority.ALWAYS);

        listPanelBox.getChildren().addAll(header, listScroll);
        return listPanelBox;
    }

    /**
     * Creates a scrollable reference containing every GUI command format.
     *
     * @return The styled Help panel.
     */
    private VBox createHelpPanel() {
        VBox panel = new VBox();
        panel.getStyleClass().add("chat-panel");
        HBox header = new HBox();
        header.getStyleClass().add("chat-header");
        Label title = new Label("Help — Command formats");
        title.getStyleClass().add("chat-title");
        header.getChildren().add(title);

        Label commands = new Label(COMMAND_HELP);
        commands.setWrapText(true);
        commands.getStyleClass().addAll("message-bubble", "assistant-bubble");
        VBox content = new VBox(commands);
        content.setPadding(new Insets(28, 48, 28, 48));
        ScrollPane scroll = new ScrollPane(content);
        scroll.getStyleClass().add("conversation-scroll");
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        panel.getChildren().addAll(header, scroll);
        return panel;
    }

    /**
     * Displays unfinished and completed tasks while preserving their original task numbers.
     */
    private void refreshListPanel() {
        listTaskContainer.getChildren().clear();
        VBox unfinishedTasks = new VBox(8);
        VBox completedTasks = new VBox(8);
        String[] tasks = taskList.produceTaskList();
        for (int index = 0; index < tasks.length; index++) {
            Matcher matcher = TASK_PATTERN.matcher(tasks[index]);
            boolean isDone = matcher.matches() && "X".equals(matcher.group(2));
            boolean isPending = completionDelays.containsKey(index);
            HBox card = createListTaskCard(index, tasks[index], isDone, isPending);
            if (isDone && !isPending) {
                completedTasks.getChildren().add(card);
            } else {
                unfinishedTasks.getChildren().add(card);
            }
        }

        addListSection("Unfinished", unfinishedTasks,
                "No unfinished tasks. Remarkable. Add a task in Chat when ambition returns.");
        addListSection("Completed", completedTasks, "No completed tasks yet. I await your first triumph.");
    }

    /**
     * Creates a list card whose checkbox completes or reopens the original task.
     *
     * @param index the zero-based task index
     * @param taskText the formatted task description
     * @param isDone whether the task is complete
     * @param isPending whether its completion delay is still running
     * @return the task card with its status control
     */
    private HBox createListTaskCard(int index, String taskText, boolean isDone, boolean isPending) {
        HBox card = createTaskCard(index + 1, taskText, false);
        card.getChildren().add(createListStatusCheckbox(index, isDone, isPending));
        if (isPending) {
            card.setOpacity(0.4);
        }
        return card;
    }

    /** Creates a checkbox tied to the task revision shown in My List. */
    private CheckBox createListStatusCheckbox(int index, boolean isDone, boolean isPending) {
        long cardRevision = taskList.getRevision();
        CheckBox statusBox = new CheckBox();
        statusBox.getStyleClass().addAll("task-check", "list-complete-check");
        statusBox.setSelected(isDone);
        statusBox.setDisable(isPending);
        statusBox.setAccessibleText("Mark task " + (index + 1) + (isDone ? " as undone" : " as done"));
        statusBox.setOnAction(event -> handleListStatusChange(index, isDone, cardRevision, statusBox));
        return statusBox;
    }

    /** Applies a current list-card action and restores its checkbox if saving fails. */
    private void handleListStatusChange(int index, boolean isDone, long cardRevision, CheckBox statusBox) {
        if (cardRevision != taskList.getRevision()) {
            statusBox.setSelected(isDone);
            refreshListPanel();
            return;
        }
        try {
            changeListTaskStatus(index, isDone);
        } catch (StorageException exception) {
            statusBox.setSelected(isDone);
            showStorageError(exception);
        }
        refreshTaskSummary();
        refreshListPanel();
    }

    /** Reopens a task immediately or completes it with a delay before regrouping its card. */
    private void changeListTaskStatus(int index, boolean isDone) {
        if (isDone) {
            taskList.markAsUndone(index);
        } else {
            taskList.markAsDone(index);
            scheduleCompletionRefresh(index);
        }
    }

    /** Gives each completed task its own three-second display delay. */
    private void scheduleCompletionRefresh(int index) {
        PauseTransition removalDelay = new PauseTransition(Duration.seconds(3));
        completionDelays.put(index, removalDelay);
        removalDelay.setOnFinished(event -> {
            completionDelays.remove(index);
            refreshListPanel();
        });
        removalDelay.play();
    }

    /** Displays a failed card action even when the user is currently viewing My List. */
    private void showStorageError(StorageException exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Task change was not saved");
        alert.setContentText(exception.getMessage());
        alert.showAndWait();
    }

    /**
     * Adds a titled task section, showing a message when it has no cards.
     *
     * @param title the section heading
     * @param cards the task cards in this section
     * @param emptyText the message for an empty section
     */
    private void addListSection(String title, VBox cards, String emptyText) {
        Label heading = new Label(title);
        heading.getStyleClass().add("chat-title");
        if (cards.getChildren().isEmpty()) {
            Label emptyMessage = new Label(emptyText);
            emptyMessage.setWrapText(true);
            emptyMessage.getStyleClass().add("muted-label");
            cards.getChildren().add(emptyMessage);
        }
        listTaskContainer.getChildren().addAll(heading, cards);
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
                sendMessage();
                messageField.requestFocus();
            });
            quickCommands.getChildren().add(chip);
        }

        HBox inputRow = new HBox(10);
        inputRow.setAlignment(Pos.CENTER_LEFT);
        messageField.setPromptText("Your next task, if you please...");
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
        appendMessage(false, Dialogue.GREETING);
        appendMessage(false, "We shall start simply: `todo buy groceries`, `list`, or `find groceries`.");
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
        handleCommand(Parser.normalize(rawInput));
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
            appendMessage(false, COMMAND_HELP);
            return;
        }

        Command command = Parser.getCommand(input);
        try {
            switch (command) {
                case BYE:
                    appendMessage(false, Dialogue.GOODBYE);
                    break;
                case LIST:
                    showTaskList(Dialogue.LIST);
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
                            "What precisely is the plan? Try `todo`, `event`, `deadline`, `list`, `find`, "
                                    + "`mark`, `unmark`, `delete`, or `update`.");
                    break;
            }
        } catch (StorageException exception) {
            appendMessage(false, exception.getMessage());
        } catch (IllegalArgumentException exception) {
            appendMessage(false, "A slight flaw in your plan: " + exception.getMessage());
        }
    }

    /**
     * Adds a todo task from a user command.
     *
     * @param input the normalized todo command
     */
    private void addTodo(String input) {
        taskList.addToDo(Parser.parseTodo(input));
        appendMessage(false, Dialogue.ADDED);
    }

    /**
     * Adds a deadline task from a user command.
     *
     * @param input the normalized deadline command
     */
    private void addDeadline(String input) {
        String[] parsedInput = Parser.parseDeadline(input);
        taskList.addDeadline(parsedInput[0], parsedInput[1]);
        appendMessage(false, "Deadline recorded. Time is now officially judging you.");
    }

    /**
     * Adds an event task from a user command.
     *
     * @param input the normalized event command
     */
    private void addEvent(String input) {
        String[] parsedInput = Parser.parseEvent(input);
        taskList.addEvent(parsedInput[0], parsedInput[1], parsedInput[2]);
        appendMessage(false, "Event scheduled. I trust the occasion warrants all this organisation.");
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
            appendMessage(false,
                    "That task exists only in your imagination. Use a listed number with `mark` or `unmark`.");
            return;
        }

        if (isDone) {
            taskList.markAsDone(index);
            appendMessage(false, "Completed. Rather well done, actually. Let us not make a scene.");
        } else {
            taskList.markAsUndone(index);
            appendMessage(false, "Reopened. A strategic reconsideration, shall we call it?");
        }
        showTaskList("The revised agenda, for your inspection:");
    }

    /**
     * Deletes a task from the task list.
     *
     * @param input the normalized delete command
     */
    private void deleteTask(String input) {
        int index = Parser.getTaskIndex(input);
        if (!isValidTaskIndex(index)) {
            appendMessage(false, "I cannot delete an imaginary task. Use a listed number, such as `delete 1`.");
            return;
        }

        taskList.deleteTask(index);
        appendMessage(false, "Deleted. I have dismissed it from our affairs.");
        showTaskList("The surviving commitments:");
    }

    /**
     * Updates a task description from a user command.
     *
     * @param input the normalized update command
     */
    private void updateTask(String input) {
        int index = Parser.getUpdateTaskIndex(input);
        String[] updates = Parser.parseUpdate(input);
        if (!isValidTaskIndex(index) || areAllUpdateFieldsMissing(updates)) {
            appendMessage(false,
                    "Details, please: `update <number> [description] [d/<deadline>] [from/<from>] [to/<to>]`.");
            return;
        }

        taskList.updateTask(index, updates[0], updates[1], updates[2], updates[3]);
        appendMessage(false, Dialogue.UPDATED);
        showTaskList("The revised agenda, for your inspection:");
    }

    /**
     * Checks whether an update command contains at least one field.
     *
     * @param updates Parsed update fields.
     * @return whether no update field was supplied
     */
    private boolean areAllUpdateFieldsMissing(String[] updates) {
        for (String update : updates) {
            if (update != null) {
                return false;
            }
        }
        return true;
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
            appendMessage(false, "Nothing matches. Even my brilliance needs a clue. Try another keyword or `list`.");
            return;
        }

        appendMessage(false, "Aha. The evidence you requested:");
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
            appendMessage(false, Dialogue.EMPTY);
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
        TaskCardData data = parseTaskCardData(taskText);
        HBox card = createTaskCardLayout(index, data);
        if (isInteractive) {
            addChatTaskControls(card, index, data.isDone());
        }
        return card;
    }

    /**
     * Represents the display fields extracted from a formatted task.
     *
     * @param type Task type badge.
     * @param isDone Whether the task is complete.
     * @param description Task description including any dates.
     */
    private record TaskCardData(String type, boolean isDone, String description) {
    }

    /** Decodes task text once, retaining the existing fallback for unrecognized text. */
    private TaskCardData parseTaskCardData(String taskText) {
        Matcher matcher = TASK_PATTERN.matcher(taskText);
        if (!matcher.matches()) {
            return new TaskCardData("T", false, taskText);
        }
        return new TaskCardData(matcher.group(1), "X".equals(matcher.group(2)), matcher.group(3));
    }

    /** Assembles a task card from its styled container, badge, and description. */
    private HBox createTaskCardLayout(int index, TaskCardData data) {
        HBox card = createTaskCardContainer(data.isDone());
        card.getChildren().addAll(createTaskTypeBadge(data.type()), createTaskDetails(index, data.description()));
        return card;
    }

    /** Creates the outer card with its completion styling. */
    private HBox createTaskCardContainer(boolean isDone) {
        HBox card = new HBox(12);
        card.getStyleClass().add("task-card");
        if (isDone) {
            card.getStyleClass().add("task-done");
        }
        card.setAlignment(Pos.CENTER_LEFT);
        return card;
    }

    /** Creates a badge identifying the task type. */
    private Label createTaskTypeBadge(String type) {
        Label typeBadge = new Label(type);
        typeBadge.getStyleClass().addAll("task-type", "task-type-" + type.toLowerCase());
        return typeBadge;
    }

    /** Creates the task description and its original one-based number. */
    private VBox createTaskDetails(int index, String description) {
        VBox details = new VBox(3);
        HBox.setHgrow(details, Priority.ALWAYS);
        Label taskLabel = new Label(description);
        taskLabel.getStyleClass().add("task-description");
        taskLabel.setWrapText(true);
        Label numberLabel = new Label(String.format("task %02d", index));
        numberLabel.getStyleClass().add("task-number");
        details.getChildren().addAll(taskLabel, numberLabel);
        return details;
    }

    /** Adds actions that share the revision captured when their card was created. */
    private void addChatTaskControls(HBox card, int index, boolean isDone) {
        long cardRevision = taskList.getRevision();
        card.getChildren().addAll(createChatStatusCheckbox(index, isDone, cardRevision),
                createChatDeleteButton(index, cardRevision));
    }

    /** Creates the completion control for a chat task card. */
    private CheckBox createChatStatusCheckbox(int index, boolean isDone, long cardRevision) {
        CheckBox doneBox = new CheckBox();
        doneBox.setSelected(isDone);
        doneBox.getStyleClass().add("task-check");
        doneBox.setOnAction(event -> handleChatStatusChange(index, isDone, cardRevision, doneBox));
        return doneBox;
    }

    /** Applies a chat status change, restoring the old checkbox when the card is stale or saving fails. */
    private void handleChatStatusChange(int index, boolean isDone, long cardRevision, CheckBox doneBox) {
        if (!isCurrentCard(cardRevision)) {
            doneBox.setSelected(isDone);
            return;
        }
        try {
            changeTaskStatus(index - 1, doneBox.isSelected());
        } catch (StorageException exception) {
            doneBox.setSelected(isDone);
            showStorageError(exception);
            return;
        }
        refreshTaskSummary();
        showTaskList("Status revised. Our little operation advances:");
        scrollToBottom();
    }

    /** Saves a completion state using a zero-based task index. */
    private void changeTaskStatus(int index, boolean isDone) {
        if (isDone) {
            taskList.markAsDone(index);
        } else {
            taskList.markAsUndone(index);
        }
    }

    /** Creates a delete control for the revision shown in a chat card. */
    private Button createChatDeleteButton(int index, long cardRevision) {
        Button deleteButton = new Button("×");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(event -> handleChatDelete(index, cardRevision));
        return deleteButton;
    }

    /** Deletes a task only while the card still identifies the current task list. */
    private void handleChatDelete(int index, long cardRevision) {
        if (!isCurrentCard(cardRevision)) {
            return;
        }
        try {
            taskList.deleteTask(index - 1);
        } catch (StorageException exception) {
            showStorageError(exception);
            return;
        }
        refreshTaskSummary();
        showTaskList("Dismissed from the agenda. Here is what remains:");
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

    /** Rejects stale chat controls before their old task numbers can affect another task. */
    private boolean isCurrentCard(long cardRevision) {
        if (cardRevision == taskList.getRevision()) {
            return true;
        }
        showTaskList("That task card is out of date. Use the refreshed list below.");
        scrollToBottom();
        return false;
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
     * Smoothly scrolls the conversation to its newest content after measuring new cards.
     */
    private void scrollToBottom() {
        Platform.runLater(() -> {
            if (scrollAnimation != null) {
                scrollAnimation.stop();
            }
            // Measure newly added cards before scrolling to the updated bottom edge.
            chatPanel.applyCss();
            chatPanel.layout();
            scrollAnimation = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(conversationScroll.vvalueProperty(), conversationScroll.getVvalue())),
                    new KeyFrame(Duration.millis(450),
                            new KeyValue(conversationScroll.vvalueProperty(), conversationScroll.getVmax(),
                                    Interpolator.EASE_BOTH)));
            scrollAnimation.play();
        });
    }

    /** Loads the optional portrait, allowing the interface to use a text logo if it is missing or corrupt. */
    private Image loadPhoto() {
        URL resource = StewieGui.class.getResource("/images/stewie_photo.png");
        if (resource == null) {
            return null;
        }
        Image image = new Image(resource.toExternalForm());
        return image.isError() ? null : image;
    }

    /**
     * Creates a Stewie image logo while preserving the original proportions.
     *
     * @param size the width and height available for the logo
     * @return a logo node
     */
    private StackPane createLogo(double size) {
        ImageView portrait = new ImageView(stewiePhoto);
        portrait.setFitWidth(size);
        portrait.setFitHeight(size);
        portrait.setPreserveRatio(true);
        portrait.setSmooth(true);
        portrait.setAccessibleText("Stewie");

        StackPane logo = stewiePhoto == null ? new StackPane(new Label("S")) : new StackPane(portrait);
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
        FontIcon navigationIcon = new FontIcon(icon);
        navigationIcon.setIconSize(20);
        Button button = new Button(labelText, navigationIcon);
        button.setGraphicTextGap(16);
        navigationIcon.iconColorProperty().bind(button.textFillProperty());
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.getStyleClass().add("navigation-button");
        if (isActive) {
            button.getStyleClass().add("navigation-button-active");
        }
        return button;
    }
}

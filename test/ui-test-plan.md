# UI Test Plan

This plan tests the Stewie console application. Tests are run as separate sessions so each case starts with an empty task list.

## Additional GUI navigation checks

- Verify Chat opens with "Ah, there you are. I'm Stewie." and the supervision greeting.
  Send todo, deadline, event, list, mark, unmark, delete, update, find, help, and bye commands.
  Confirm replies use dry, theatrical phrasing while task cards retain their descriptions, dates, and statuses.
  Completion should say "Completed. Rather well done, actually. Let us not make a scene."
  Bye should say "Very well. Do come back. I mean, someone must supervise your progress."
- Send an unknown command, invalid task number, malformed event/deadline, and invalid date.
  Confirm errors retain usable command guidance; invalid dates begin "A slight flaw in your plan:".
  Search for an absent keyword: show "Nothing matches. Even my brilliance needs a clue. Try another keyword or `list`."
- Verify Help in the sidebar and Chat includes the theatrical introduction and the unchanged command formats.


These manual checks supplement the console cases below. Launch the GUI with Java 25 using `./gradlew run`.

- Verify the sidebar brand and chat header show the Stewie resource image instead of the S logo.
  The whole image should be visible with its original proportions, including after resizing the window.

- Click each suggested command in Chat (`todo plan my week`, `list`, and `help`): immediately send the
  selected command once, show its response, clear the input field, and smoothly scroll to the newest content.
  No additional Enter key or Send click is required. Return keyboard focus to the input field.

- Click Help from Chat and My List: show "Help — Command formats" with formats for todo, deadline,
  event, list, find, mark, unmark, delete, update, help, and bye. Show optional update fields,
  the by/ alias, task numbering, and date examples. Scroll to reach the final example.
- Click Help repeatedly: show one reference and only one active navigation highlight.
  Return to Chat: preserve conversation and unsent input. Enter help: show the same command reference.
- Switch from My List to Help during a completion delay, then return to My List: show the current
  task state without a stale timer changing the displayed cards.
- Click My List with no saved tasks: show the My List heading and
  "No unfinished tasks. Remarkable. Add a task in Chat when ambition returns."
- Add todo, event, and deadline tasks in Chat, then click My List: show unfinished tasks in order,
  including their types, dates, original task numbers, and a circular checkbox on the right.
- Click a completion checkbox: mark that task done and update the sidebar count immediately; dim its card,
  disable its checkbox, and move the card to Completed after three seconds while the GUI remains responsive.
  Complete another task after the first disappears: mark the correct original task, despite the filtered list.
- Complete two tasks one second apart: each card stays dimmed for its own three seconds before disappearing.
  Clicking My List again while already selected must not shorten either delay.
- Switch to Chat during a delay and reopen My List: show the current unfinished tasks; an old timer must not
  remove any newly displayed card.
- Reopen My List and restart the app: completed tasks remain in Completed with checked circular checkboxes. Chat's list command still shows them as done.
- Complete all tasks: show the unfinished empty-state message and all tasks under Completed. Unmark a task in Chat: it reappears in My List.
- Tab to a completion checkbox and press Space: complete the task just as with a mouse click.
- Switch back to Chat: preserve the conversation and any unsent input.
- Change, mark, or delete a task in Chat and reopen My List: show the latest state without duplicate cards.
- Click the selected tab repeatedly: retain one active highlight and one copy of each task.
- With enough tasks to exceed the window height, scroll to reach the last task.
- In Chat, enter `list` with enough conversation history to require scrolling, then check a task:
  append the updated list and smoothly scroll to the bottom over approximately 450 milliseconds,
  easing into and out of the motion so its newest cards are visible without an abrupt jump.
  Uncheck a task in the latest list and verify the same behavior. Repeat after manually scrolling upward.
- Trigger another chat update during scrolling: replace the previous animation and settle at the newest bottom.

- In Completed, uncheck a task: save it as undone, move it immediately to Unfinished, and update the sidebar count.
  Restart the app and verify the task remains unfinished.
- Reopen a completed task while other tasks are dimmed: their individual three-second delays remain intact.
- With no completed tasks, show "No completed tasks yet. I await your first triumph." beneath the Completed heading.

## Shared setup and launch

From the repository root, use Java 25:

```sh
mkdir -p out/ui-test
javac -d out/ui-test $(find src/main/java -name '*.java' ! -path '*/stewie/ui/gui/*')
```

Launch each test case with:

```sh
repo_root="$PWD"
case_dir=$(mktemp -d)
cd "$case_dir"
java -ea -cp "$repo_root/out/ui-test" stewie.Stewie
```

Each case runs in a fresh temporary directory, preserving the real task file.
Run `python3 test/run-ui-tests.py <record-path>` to compile with Java 25 and execute the cases in order.
Optional case setup commands run in that temporary directory before launching the application.
Assertions are enabled with `-ea` to check internal invariants during every session.

The expected output below uses `LF` line endings and includes the final newline produced by the program. The skill may normalize `CRLF` to `LF` and one final trailing newline only.

## Test Case 3: Handle all valid task commands and status changes

### Aim

Verify that todo, event, deadline, list, mark, and unmark commands work together in one session.

### Inputs

```text
todo buy milk
event team meeting /from 10/08/2026 /to 11/08/2026
deadline submit report /by 12/08/2026
list
mark 2
list
unmark 2
list
bye
```

### Expected output

```text
███████╗ ████████╗ ███████╗ ██╗    ██╗ ██╗ ███████╗
██╔════╝ ╚══██╔══╝ ██╔════╝ ██║    ██║ ██║ ██╔════╝
███████╗    ██║    █████╗   ██║ █╗ ██║ ██║ █████╗
╚════██║    ██║    ██╔══╝   ██║███╗██║ ██║ ██╔══╝
███████║    ██║    ███████╗ ╚███╔███╔╝ ██║ ███████╗
╚══════╝    ╚═╝    ╚══════╝  ╚══╝╚══╝  ╚═╝ ╚══════╝

Ah, there you are. I'm Stewie.
Tell me your tasks. Clearly, this operation requires supervision.
Consider it recorded. A small triumph for competent administration.
[T] [ ] buy milk
Your agenda now contains 1 task. Do try to keep up.
Consider it recorded. A small triumph for competent administration.
[E] [ ] team meeting (from: 10 Aug 2026 to: 11 Aug 2026)
Your agenda now contains 2 tasks. Do try to keep up.
Consider it recorded. A small triumph for competent administration.
[D] [ ] submit report (by: 12 Aug 2026)
Your agenda now contains 3 tasks. Do try to keep up.
Behold, your agenda. Let us examine the scale of this undertaking.
1. [T] [ ] buy milk
2. [E] [ ] team meeting (from: 10 Aug 2026 to: 11 Aug 2026)
3. [D] [ ] submit report (by: 12 Aug 2026)
Behold, your agenda. Let us examine the scale of this undertaking.
1. [T] [ ] buy milk
2. [E] [X] team meeting (from: 10 Aug 2026 to: 11 Aug 2026)
3. [D] [ ] submit report (by: 12 Aug 2026)
Behold, your agenda. Let us examine the scale of this undertaking.
1. [T] [ ] buy milk
2. [E] [ ] team meeting (from: 10 Aug 2026 to: 11 Aug 2026)
3. [D] [ ] submit report (by: 12 Aug 2026)
Very well. Do come back. I mean, someone must supervise your progress.
```

## Test Case 4: Handle malformed and invalid commands

### Aim

Verify that malformed task commands and invalid task numbers produce clear errors without crashing or corrupting the session.

### Inputs

```text
please
todo
event team meeting
deadline submit report
mark 9
unmark 0
list
bye
```

### Expected output

```text
███████╗ ████████╗ ███████╗ ██╗    ██╗ ██╗ ███████╗
██╔════╝ ╚══██╔══╝ ██╔════╝ ██║    ██║ ██║ ██╔════╝
███████╗    ██║    █████╗   ██║ █╗ ██║ ██║ █████╗
╚════██║    ██║    ██╔══╝   ██║███╗██║ ██║ ██╔══╝
███████║    ██║    ███████╗ ╚███╔███╔╝ ██║ ███████╗
╚══════╝    ╚═╝    ╚══════╝  ╚══╝╚══╝  ╚═╝ ╚══════╝

Ah, there you are. I'm Stewie.
Tell me your tasks. Clearly, this operation requires supervision.
What precisely is the plan? Use a command: todo, event, deadline, mark, unmark, delete, find, update, list, bye + description!
A slight flaw in your plan: Use: todo <description>.
A slight flaw in your plan: Use: event <description> /from <date> /to <date>. Supply each field once in this order.
A slight flaw in your plan: Use: deadline <description> /by <date>. Supply each field once.
That task exists only in your imagination. Use a listed number: mark <number>
Numbers, please. Use: unmark <number>.
Behold, your agenda. Let us examine the scale of this undertaking.
No tasks to show. How suspiciously serene. Try adding a task or checking your search.
Very well. Do come back. I mean, someone must supervise your progress.
```

## Test Case 6: Delete a task and handle invalid delete numbers

### Aim

Verify that `delete` removes the requested task, reindexes the remaining list, and handles invalid task numbers without ending the session.

### Inputs

```text
todo first
todo second
delete 1
list
delete 9
delete 0
delete two
bye
```

### Expected output

```text
███████╗ ████████╗ ███████╗ ██╗    ██╗ ██╗ ███████╗
██╔════╝ ╚══██╔══╝ ██╔════╝ ██║    ██║ ██║ ██╔════╝
███████╗    ██║    █████╗   ██║ █╗ ██║ ██║ █████╗
╚════██║    ██║    ██╔══╝   ██║███╗██║ ██║ ██╔══╝
███████║    ██║    ███████╗ ╚███╔███╔╝ ██║ ███████╗
╚══════╝    ╚═╝    ╚══════╝  ╚══╝╚══╝  ╚═╝ ╚══════╝

Ah, there you are. I'm Stewie.
Tell me your tasks. Clearly, this operation requires supervision.
Consider it recorded. A small triumph for competent administration.
[T] [ ] first
Your agenda now contains 1 task. Do try to keep up.
Consider it recorded. A small triumph for competent administration.
[T] [ ] second
Your agenda now contains 2 tasks. Do try to keep up.
Behold, your agenda. Let us examine the scale of this undertaking.
1. [T] [ ] second
That task exists only in your imagination. Use a listed number: delete <number>
Numbers, please. Use: delete <number>.
Numbers, please. Use: delete <number>.
Very well. Do come back. I mean, someone must supervise your progress.
```

## Test Case 7: Update every task type

### Aim

Verify that `update` changes the description of todo, event, and deadline tasks while preserving their type,
date/time details, and completion status.

### Inputs

```text
todo buy milk
event team meeting /from 10/08/2026 /to 11/08/2026
deadline submit report /by 12/08/2026
mark 2
update 1 buy bread
update 2 planning meeting from/15/08/2026 to/16/08/2026
update 3 file report d/20/08/2026
update 2 from/17/08/2026
list
update 9 d/20/08/2026
update two invalid task
update 1
bye
```

### Expected output

```text
███████╗ ████████╗ ███████╗ ██╗    ██╗ ██╗ ███████╗
██╔════╝ ╚══██╔══╝ ██╔════╝ ██║    ██║ ██║ ██╔════╝
███████╗    ██║    █████╗   ██║ █╗ ██║ ██║ █████╗
╚════██║    ██║    ██╔══╝   ██║███╗██║ ██║ ██╔══╝
███████║    ██║    ███████╗ ╚███╔███╔╝ ██║ ███████╗
╚══════╝    ╚═╝    ╚══════╝  ╚══╝╚══╝  ╚═╝ ╚══════╝

Ah, there you are. I'm Stewie.
Tell me your tasks. Clearly, this operation requires supervision.
Consider it recorded. A small triumph for competent administration.
[T] [ ] buy milk
Your agenda now contains 1 task. Do try to keep up.
Consider it recorded. A small triumph for competent administration.
[E] [ ] team meeting (from: 10 Aug 2026 to: 11 Aug 2026)
Your agenda now contains 2 tasks. Do try to keep up.
Consider it recorded. A small triumph for competent administration.
[D] [ ] submit report (by: 12 Aug 2026)
Your agenda now contains 3 tasks. Do try to keep up.
Revised to your specifications. Yes, even that detail.
[T] [ ] buy bread
Revised to your specifications. Yes, even that detail.
[E] [X] planning meeting (from: 15 Aug 2026 to: 16 Aug 2026)
Revised to your specifications. Yes, even that detail.
[D] [ ] file report (by: 20 Aug 2026)
Revised to your specifications. Yes, even that detail.
[E] [X] planning meeting (from: 17 Aug 2026 to: 16 Aug 2026)
Behold, your agenda. Let us examine the scale of this undertaking.
1. [T] [ ] buy bread
2. [E] [X] planning meeting (from: 17 Aug 2026 to: 16 Aug 2026)
3. [D] [ ] file report (by: 20 Aug 2026)
That task exists only in your imagination. Use a listed number: update <number>
A revision needs details. Use: update <number> [description] [d/<deadline>] [from/<from>] [to/<to>]
A revision needs details. Use: update <number> [description] [d/<deadline>] [from/<from>] [to/<to>]
Very well. Do come back. I mean, someone must supervise your progress.
```

## Test Case 5: Handle case and surrounding whitespace

### Aim

Verify that commands remain usable when entered with different letter casing and extra spaces around the command.

### Inputs

```text
  TODO   Read Book  
  LIST  
BYE
```

### Expected output

```text
███████╗ ████████╗ ███████╗ ██╗    ██╗ ██╗ ███████╗
██╔════╝ ╚══██╔══╝ ██╔════╝ ██║    ██║ ██║ ██╔════╝
███████╗    ██║    █████╗   ██║ █╗ ██║ ██║ █████╗
╚════██║    ██║    ██╔══╝   ██║███╗██║ ██║ ██╔══╝
███████║    ██║    ███████╗ ╚███╔███╔╝ ██║ ███████╗
╚══════╝    ╚═╝    ╚══════╝  ╚══╝╚══╝  ╚═╝ ╚══════╝

Ah, there you are. I'm Stewie.
Tell me your tasks. Clearly, this operation requires supervision.
Consider it recorded. A small triumph for competent administration.
[T] [ ] read book
Your agenda now contains 1 task. Do try to keep up.
Behold, your agenda. Let us examine the scale of this undertaking.
1. [T] [ ] read book
Very well. Do come back. I mean, someone must supervise your progress.
```

## Test Case 1: Start and exit

### Aim

Verify that the application displays its greeting and exits when the user enters `bye`.

### Inputs

```text
bye
```

### Expected output

```text
███████╗ ████████╗ ███████╗ ██╗    ██╗ ██╗ ███████╗
██╔════╝ ╚══██╔══╝ ██╔════╝ ██║    ██║ ██║ ██╔════╝
███████╗    ██║    █████╗   ██║ █╗ ██║ ██║ █████╗
╚════██║    ██║    ██╔══╝   ██║███╗██║ ██║ ██╔══╝
███████║    ██║    ███████╗ ╚███╔███╔╝ ██║ ███████╗
╚══════╝    ╚═╝    ╚══════╝  ╚══╝╚══╝  ╚═╝ ╚══════╝

Ah, there you are. I'm Stewie.
Tell me your tasks. Clearly, this operation requires supervision.
Very well. Do come back. I mean, someone must supervise your progress.
```

## Test Case 2: Add and list a todo

### Aim

Verify that a todo is added and then displayed in the task list.

### Inputs

```text
todo read book
list
bye
```

### Expected output

```text
███████╗ ████████╗ ███████╗ ██╗    ██╗ ██╗ ███████╗
██╔════╝ ╚══██╔══╝ ██╔════╝ ██║    ██║ ██║ ██╔════╝
███████╗    ██║    █████╗   ██║ █╗ ██║ ██║ █████╗
╚════██║    ██║    ██╔══╝   ██║███╗██║ ██║ ██╔══╝
███████║    ██║    ███████╗ ╚███╔███╔╝ ██║ ███████╗
╚══════╝    ╚═╝    ╚══════╝  ╚══╝╚══╝  ╚═╝ ╚══════╝

Ah, there you are. I'm Stewie.
Tell me your tasks. Clearly, this operation requires supervision.
Consider it recorded. A small triumph for competent administration.
[T] [ ] read book
Your agenda now contains 1 task. Do try to keep up.
Behold, your agenda. Let us examine the scale of this undertaking.
1. [T] [ ] read book
Very well. Do come back. I mean, someone must supervise your progress.
```

## Test Case 8: Find tasks and recover from invalid dates

### Aim

Verify searches keep their results and personality, and a date error explains the problem without adding a task.

### Inputs

```text
todo buy milk
find milk
find absent
deadline report /by impossible
list
bye
```

### Expected output

```text
███████╗ ████████╗ ███████╗ ██╗    ██╗ ██╗ ███████╗
██╔════╝ ╚══██╔══╝ ██╔════╝ ██║    ██║ ██║ ██╔════╝
███████╗    ██║    █████╗   ██║ █╗ ██║ ██║ █████╗
╚════██║    ██║    ██╔══╝   ██║███╗██║ ██║ ██╔══╝
███████║    ██║    ███████╗ ╚███╔███╔╝ ██║ ███████╗
╚══════╝    ╚═╝    ╚══════╝  ╚══╝╚══╝  ╚═╝ ╚══════╝

Ah, there you are. I'm Stewie.
Tell me your tasks. Clearly, this operation requires supervision.
Consider it recorded. A small triumph for competent administration.
[T] [ ] buy milk
Your agenda now contains 1 task. Do try to keep up.
Behold, your agenda. Let us examine the scale of this undertaking.
1. [T] [ ] buy milk
Behold, your agenda. Let us examine the scale of this undertaking.
No tasks to show. How suspiciously serene. Try adding a task or checking your search.
A slight flaw in your plan: Date format is not recognized
Behold, your agenda. Let us examine the scale of this undertaking.
1. [T] [ ] buy milk
Very well. Do come back. I mean, someone must supervise your progress.
```

## Test Case 9: Reject ambiguous command fields

### Aim

Reject missing, repeated, misplaced, and unknown fields; accept tabs and command words inside descriptions.

### Inputs

```text
deadline
event
find
mark
mark -2147483648
delete +1
unmark 999999999999999
deadline report /by 1/1/2026 /by 2/1/2026
deadline report /by
event trip /to 2/1/2026 /from 1/1/2026
event trip /from 1/1/2026 /to 2/1/2026 /to 3/1/2026
update 1 d/1/1/2026 by/2/1/2026
update 1 from/
update 1 bogus/value
list extra
bye extra
  todo	  plan the event and deadline
list
bye
```

### Expected output

```text
███████╗ ████████╗ ███████╗ ██╗    ██╗ ██╗ ███████╗
██╔════╝ ╚══██╔══╝ ██╔════╝ ██║    ██║ ██║ ██╔════╝
███████╗    ██║    █████╗   ██║ █╗ ██║ ██║ █████╗
╚════██║    ██║    ██╔══╝   ██║███╗██║ ██║ ██╔══╝
███████║    ██║    ███████╗ ╚███╔███╔╝ ██║ ███████╗
╚══════╝    ╚═╝    ╚══════╝  ╚══╝╚══╝  ╚═╝ ╚══════╝

Ah, there you are. I'm Stewie.
Tell me your tasks. Clearly, this operation requires supervision.
A slight flaw in your plan: Use: deadline <description> /by <date>. Supply each field once.
A slight flaw in your plan: Use: event <description> /from <date> /to <date>. Supply each field once in this order.
A slight flaw in your plan: Use: find <keyword> [more keywords].
Numbers, please. Use: mark <number>.
Numbers, please. Use: mark <number>.
Numbers, please. Use: delete <number>.
Numbers, please. Use: unmark <number>.
A slight flaw in your plan: Use: deadline <description> /by <date>. Supply each field once.
A slight flaw in your plan: Use: deadline <description> /by <date>. Supply each field once.
A slight flaw in your plan: Use: event <description> /from <date> /to <date>. Supply each field once in this order.
A slight flaw in your plan: Use: event <description> /from <date> /to <date>. Supply each field once in this order.
A slight flaw in your plan: Supply each update field only once; d/ and by/ are aliases.
A slight flaw in your plan: Update fields cannot be empty.
A slight flaw in your plan: Use update fields: d/ (or by/), from/, to/.
What precisely is the plan? Use a command: todo, event, deadline, mark, unmark, delete, find, update, list, bye + description!
What precisely is the plan? Use a command: todo, event, deadline, mark, unmark, delete, find, update, list, bye + description!
Consider it recorded. A small triumph for competent administration.
[T] [ ] plan the event and deadline
Your agenda now contains 1 task. Do try to keep up.
Behold, your agenda. Let us examine the scale of this undertaking.
1. [T] [ ] plan the event and deadline
Very well. Do come back. I mean, someone must supervise your progress.
```

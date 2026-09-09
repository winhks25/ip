# UI Test Plan

This plan tests the Stewie console application. Tests are run as separate sessions so each case starts with an empty task list.

## Shared setup and launch

From the repository root, use Java 25:

```sh
mkdir -p out/ui-test
javac -d out/ui-test $(find src/main/java -name '*.java' ! -path '*/stewie/ui/gui/*')
```

Launch each test case with:

```sh
rm -f data/stewie.txt
java -cp out/ui-test stewie.Stewie
```

The saved task file is removed before each case so the cases remain independent.

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

Hey there! I'm Stewie. 
Wanna have a chat?
Tell me whats on your list!!
Got it! Added the following to your list.
[T] [ ] buy milk
Now you have 1 tasks in the list. 
Got it! Added the following to your list.
[E] [ ] team meeting (from: 10 Aug 2026 to: 11 Aug 2026)
Now you have 2 tasks in the list. 
Got it! Added the following to your list.
[D] [ ] submit report (by: 12 Aug 2026)
Now you have 3 tasks in the list. 
Here is your list of tasks.
1. [T] [ ] buy milk
2. [E] [ ] team meeting (from: 10 Aug 2026 to: 11 Aug 2026)
3. [D] [ ] submit report (by: 12 Aug 2026)
Here is your list of tasks.
1. [T] [ ] buy milk
2. [E] [X] team meeting (from: 10 Aug 2026 to: 11 Aug 2026)
3. [D] [ ] submit report (by: 12 Aug 2026)
Here is your list of tasks.
1. [T] [ ] buy milk
2. [E] [ ] team meeting (from: 10 Aug 2026 to: 11 Aug 2026)
3. [D] [ ] submit report (by: 12 Aug 2026)
Bye, see you later!
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

Hey there! I'm Stewie. 
Wanna have a chat?
Tell me whats on your list!!
Please add a command: todo, event, deadline, mark, unmark, delete, find, update, list, bye + description!
Please add a command: todo, event, deadline, mark, unmark, delete, find, update, list, bye + description!
Add event tasks in the format: event <description> /from <date or time> /to<date or time>
Add deadline task in the format: deadline <description> /by <deadline>
Please type in a valid task number in the format: mark <number>
Please enter a valid task number in the format: unmark <number>.
Here is your list of tasks.
You have no task saved.
Bye, see you later!
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

Hey there! I'm Stewie. 
Wanna have a chat?
Tell me whats on your list!!
Got it! Added the following to your list.
[T] [ ] first
Now you have 1 tasks in the list. 
Got it! Added the following to your list.
[T] [ ] second
Now you have 2 tasks in the list. 
Here is your list of tasks.
1. [T] [ ] second
Please type in a valid task number in the format: delete <number>
Please enter a valid task number in the format: delete <number>.
Please enter a valid task number in the format: delete <number>.
Bye, see you later!
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
update 2 planning meeting
update 3 file report
list
update 9 missing task
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

Hey there! I'm Stewie. 
Wanna have a chat?
Tell me whats on your list!!
Got it! Added the following to your list.
[T] [ ] buy milk
Now you have 1 tasks in the list. 
Got it! Added the following to your list.
[E] [ ] team meeting (from: 10 Aug 2026 to: 11 Aug 2026)
Now you have 2 tasks in the list. 
Got it! Added the following to your list.
[D] [ ] submit report (by: 12 Aug 2026)
Now you have 3 tasks in the list. 
Got it! Updated the following task.
[T] [ ] buy bread
Got it! Updated the following task.
[E] [X] planning meeting (from: 10 Aug 2026 to: 11 Aug 2026)
Got it! Updated the following task.
[D] [ ] file report (by: 12 Aug 2026)
Here is your list of tasks.
1. [T] [ ] buy bread
2. [E] [X] planning meeting (from: 10 Aug 2026 to: 11 Aug 2026)
3. [D] [ ] file report (by: 12 Aug 2026)
Please type in a valid task number in the format: update <number>
Please enter a valid task number and description in the format: update <number> <description>
Please enter a valid task number and description in the format: update <number> <description>
Bye, see you later!
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

Hey there! I'm Stewie. 
Wanna have a chat?
Tell me whats on your list!!
Got it! Added the following to your list.
[T] [ ] read book
Now you have 1 tasks in the list. 
Here is your list of tasks.
1. [T] [ ] read book
Bye, see you later!
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

Hey there! I'm Stewie. 
Wanna have a chat?
Tell me whats on your list!!
Bye, see you later!
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

Hey there! I'm Stewie. 
Wanna have a chat?
Tell me whats on your list!!
Got it! Added the following to your list.
[T] [ ] read book
Now you have 1 tasks in the list. 
Here is your list of tasks.
1. [T] [ ] read book
Bye, see you later!
```

# Stewie project template

This is a project template for a greenfield Java project. It's named Stewie. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/Stewie.java` file, right-click it, and choose `Run Stewie.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
                   ███████╗ ████████╗ ███████╗ ██╗    ██╗ ██╗ ███████╗
                   ██╔════╝ ╚══██╔══╝ ██╔════╝ ██║    ██║ ██║ ██╔════╝
                   ███████╗    ██║    █████╗   ██║ █╗ ██║ ██║ █████╗
                   ╚════██║    ██║    ██╔══╝   ██║███╗██║ ██║ ██╔══╝
                   ███████║    ██║    ███████╗ ╚███╔███╔╝ ██║ ███████╗
                   ╚══════╝    ╚═╝    ╚══════╝  ╚══╝╚══╝  ╚═╝ ╚══════╝
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Input validation and recovery

Commands accept surrounding whitespace, repeated spaces, and tabs. Supply each
named field once; for `update`, `d/` and `by/` are aliases for the same field.
Task numbers must be positive integers from the current list.

Dates must be real calendar dates in years 0001–9999; times are not supported.
An event must end after its start date. Tasks with the same type, description,
and dates cannot be duplicated, even if one is completed. Description casing
and repeated whitespace do not distinguish duplicates. Descriptions may contain
ordinary punctuation but cannot contain `|` or control characters.

Tasks are stored in `data/stewie.txt` relative to the launch directory. A missing
file is created on the first successful change. If the file contains damaged or
duplicate records, Stewie reports their line numbers and displays valid records
in read-only mode. Back up the file, repair the reported records, and restart.
Unreadable files and invalid UTF-8 content also open read-only; check the path,
permissions, and encoding before restarting.

A failed save leaves both the current task list and the previous saved file
unchanged. Check write permissions and free space, then retry. Saving requires
atomic file replacement on the storage filesystem. If another program changes
the file, restart Stewie to load those changes before editing tasks.

Run the console regression plan with Java 25 using
`python3 test/run-ui-tests.py out/ui-test-session.txt`. Cases run in temporary
directories and preserve your real task file. The session record contains every
input, expected-output comparison result, stdout, stderr, and exit status.

For JUnit tests, coverage reports, and the cross-platform manual checklist, see
[the testing guide](test/README.md).

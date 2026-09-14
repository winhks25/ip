# Testing Stewie

Use Java 25 for every build and application launch. On this macOS setup:

```sh
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk use java 25.0.3.fx-zulu
./gradlew check jacocoTestReport jacocoCoreReport
python3 test/run-ui-tests.py out/ui-test-session.txt
```

On Windows, select an installed JDK 25 with `JAVA_HOME` and use
`gradlew.bat check jacocoTestReport jacocoCoreReport`. The Python console-plan
runner currently requires the configured SDKMAN JDK path and POSIX shell and
permissions; it is not a native Windows test runner. The JUnit suite selects
`java.exe` on Windows and requires neither Python nor a shell.

`check` runs JUnit and Checkstyle. Reports are generated locally:

- JUnit: `build/reports/tests/test/index.html`.
- Complete coverage, including the untested GUI: `build/reports/jacoco/test/html/index.html`.
- Non-GUI coverage: `build/reports/jacoco/core/html/index.html`.
- Machine-readable coverage: the XML file alongside each report's HTML directory.
- Console sessions: `out/ui-test-session.txt`, including every input, actual
  output, comparison result, stderr, and exit status. Cases execute in the order
  written in the plan and stop on the first failure.

JaCoCo 0.8.14 provides [official Java 25 support](https://www.jacoco.org/jacoco/trunk/doc/changes.html).
The CLI tests launch actual child JVMs and merge their instrumentation with the
JUnit worker's coverage. Their data file is reset before each test task execution
to prevent old runs from inflating coverage. Run the complete suite, without a
`--tests` filter, when measuring coverage.

Verified on 2026-09-14 with macOS 26.5.1 and Zulu Java 25.0.3:

| Check | Result |
| --- | --- |
| JUnit | 67 passed; no failures, errors, or skips |
| Checkstyle | Main and test sources passed |
| Ordered console plan | All 15 sessions passed exact output comparisons |
| Non-GUI coverage | 468/472 lines (99.15%); 243/257 branches (94.55%) |
| Complete application coverage | 468/956 lines (48.95%); 243/370 branches (65.68%), including untested JavaFX code |

These are measured results for this revision and environment, not results for
the unexecuted native OS/display combinations below. Regenerate the reports
after further changes.

## Automated scope

| Area | Behaviors checked |
| --- | --- |
| Parser | Every command, unknown commands, null and missing input, whitespace, number boundaries and overflow, marker order, empty/repeated/unknown fields, update aliases, optional fields, search splitting |
| Models | Every supported date format, invalid dates and year boundaries, event ordering, task identity, unsafe descriptions, Unicode normalization, rendering, repeated marking and unmarking |
| Task list | All task types, order, snapshot isolation, searching, duplicate rejection, partial updates, metadata and completion preservation, invalid indices, deletion to empty, saved revisions, failed mutations |
| Storage | Exact serialization, all types/statuses, missing/empty files, malformed and duplicate records, line endings, UTF-8 and Burmese text, read-only recovery, external edits/creation/deletion, symbolic links, denied writes, temporary-file cleanup after success |
| Console | Real startup, EOF, bye, every command route, error recovery, warnings, persistence/restart, English and Burmese sessions, exact messages, numbering, counts, and standalone echo |

All storage and child-process tests use JUnit temporary directories. Console
stream and locale changes are restored in `finally` blocks. JUnit uses its default
sequential execution; do not enable parallel tests without coordinating every
test that changes global streams or locales. POSIX permission tests explicitly
skip on unsupported filesystems or when effective permissions are bypassed
(e.g., a privileged user). Symbolic-link tests currently run on POSIX filesystems.
A skipped test is not evidence of coverage on that platform.

The console plan's English expectations remain unchanged because no application
behavior was changed. JVM locale checks simulate formatting rules, not an entire
OS language change or a native input method. Burmese confirmations currently use
Burmese count digits (`၁`, `၂`); dates remain English and CLI task numbers use ASCII
input such as `mark 1`. Locale tests preserve that existing behavior.

## Coverage limits

The non-GUI report excludes only `stewie.ui.gui`, whose JavaFX scene construction,
event handlers, animations, images, focus, and rendering need a native toolkit.
The complete report retains those classes so the exclusion is visible. Their
shared parsing, task management, and storage logic is exercised through JUnit.

Do not chase 100% by weakening assertions or calling private methods reflectively.
The remaining non-GUI gaps are:

- Unused implicit constructors of static utility classes `Parser` and `Ui`.
- The all-missing-update fallback in `Stewie`, which valid parser output cannot
  reach after a valid task index and nonempty update have been required.
- Assertion-failure branches for invariants already enforced by constructors,
  plus redundant type checks after exact-class identity has been established.
- A cleanup exception after temporary-file deletion fails, which would require
  filesystem fault injection or a timing-dependent race.
- The non-POSIX permissions branch on this macOS filesystem. Run on Windows to
  exercise that filesystem behavior; a simulated locale does not cover it.

## Manual platform and display matrix

Use a disposable project copy or launch directory so the real agenda stays safe.
Use a Java 25 build appropriate for each OS; a JavaFX JAR built on one OS is not
proof that native libraries work on another. Run the existing
[GUI navigation and recovery checks](ui-test-plan.md#additional-gui-navigation-checks)
for each environment below. Record the actual OS/JDK versions and display scaling.

| Environment | Language | Resolution / scale | Status |
| --- | --- | --- | --- |
| macOS | English | 1440 × 900 logical workspace / Retina default | Not run manually |
| macOS | Burmese | Same display / OS language and Burmese keyboard enabled | Not run manually |
| Windows | English and Burmese | 1366 × 768 at 100%; 1920 × 1080 at 150% | Not run |
| Linux desktop | English and Burmese | 1920 × 1080 at 100%; available fractional scaling | Not run |
| Each available OS | English and Burmese | Resize to 960 × 640 minimum, 1180 × 760 default, and maximized | Not run |

For every matrix row:

1. Launch, enter all three task types, mark/unmark, update, search, delete, exit,
   and restart. Confirm the same task details and completion states survive.
2. Paste and type `todo စာအုပ် ဖတ်ရန်` using the native keyboard/input method.
   Check glyph shaping, combining marks, caret movement, deletion, clipboard,
   focus, Enter, and persistence. No missing-glyph boxes or corrupted characters.
3. Check sidebar, Help, Chat, My List, long descriptions, and dates at every
   listed size. No overlapping controls; scrolling reaches the last task and
   help entry; the portrait keeps its proportions. Verify minimum-size behavior
   when the effective desktop is smaller because of display scaling.
4. Use Tab, Shift+Tab, Space, Enter, mouse/trackpad, and window resizing. Verify
   focus remains visible and the composer is reachable. Check the existing
   delayed completion and stale-card scenarios while resizing and navigating.
5. Repeat storage-warning and failed-save checks with that OS's actual permission
   mechanism. Restore access and retry. Keep original bytes and live state after
   rejected changes; show one clear error and no false success message.
6. Record result, exact inputs, observed versus expected behavior, and screenshots
   for any failure. Do not mark an untested matrix row as passed.

Result template:

```text
OS/version and architecture:
JDK vendor/version:
OS language, keyboard, and JVM locale:
Display resolution, logical size, and scale:
Scenario and inputs:
Expected:
Actual:
PASS / FAIL / NOT RUN:
Evidence or issue:
```

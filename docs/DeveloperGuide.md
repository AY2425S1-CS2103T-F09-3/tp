---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# BridalBuddy Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

_{ list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well }_

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<box type="info" seamless>

**Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />

</box>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</box>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</box>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command are correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

A wedding planner who:
* Has a need to manage a significant number of contacts
* Has a need to manage different types of contacts (e.g. vendors, guests)
* Has a need to manage a few separate weddings concurrently
* Favours desktop applications over other alternatives
* Prefers keyboard commands over mouse interactions
* Adequately proficient with command-line applications

**Value proposition**:
* Streamlined, all-in-one wedding planning tool
* Intuitive organisation and customisation
* Stress-free and efficient planning process
* One-stop companion for wedding planners
* Simplifies time-intensive and tedious tasks of managing guest lists, schedules, and vendor contacts
* Enhances efficiency by reducing time spent navigating complex GUIs



### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​                         | I want to …​                                                                                        | So that I can…​                                                              |
|----------|---------------------------------|-----------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------|
| `* * *`  | Forgetful wedding planner       | Create entries of guests/vendors with relevant details (1.name, 2. contact info, 3. Roles, 4.Notes) | I can easily manage and track my guest/vendor list                           |
| `* * *`  | Careful wedding planner         | Update details of current guests/vendors                                                            | I can correct any mistakes or update any  additional information at any time |
| `* * *`  | Organized wedding planner       | Read/Print out a guest/vendor list                                                                  | I can quickly access and reference their details.                            |
| `* * *`  | Organized wedding planner       | Delete guests/vendors contacts that I no longer need                                                | my wedding planner stays organised                                           |
| `* *`    | Busy wedding planner            | Manage vendor contact details (photographers, caterers, florists)                                   | quickly reference and communicate with vendors                               |
| `* *`    | Organised wedding planner       | Categorise attendees (family members, friends, vendors)                                             | keep track of different groups involved in the wedding                       |

### Use cases

(For all use cases below, the **System** is the `AddressBook` and the **Actor** is the `user`, unless specified otherwise)

**Use case: Add a person**

**MSS**

1. User creates a new person with the required details (e.g. name, email, role, etc.).
2. System adds the entry.

   Use case ends.

**Extensions**

* 1a. The provided details are incomplete or invalid.
    * 1a1. System shows an error message and requests the user to re-enter the details.

      Use case resumes at step 1.

* 1b. The input command was invalid (i.e. spelling error, etc.).
    * 1b1. System tells the user the command is unrecognised.

      Use case ends.


**Use Case: Read/Print Out Entries**

**MSS**
1. User requests to view or print all people.
2. System displays or prints the list of all people.

   Use case ends.

**Extensions**
* 1a. The input command was invalid (i.e. spelling error, etc.).
    * 1a1. System tells the user the command is unrecognised.

      Use case ends.

* 2a. No entries exist.

  Use case ends.


**Use Case: Update Details of Person**

**MSS**
1. User selects an entry to update together with the fields to be updated.
2. System saves the changes and notifies user of success.

   Use case ends.

**Extensions**
* 1a. The input command was invalid (i.e. spelling error, etc.).
    * 1a1. System tells the user the command is unrecognised.

      Use case ends.

* 1b. The new details are incomplete or invalid.
    * 1b1. System shows an error message and requests the user to re-enter the details.

      Use case resumes at step 1.


**Use Case: Delete a Person**

**MSS**
1. User requests to delete an entry.
2. System deletes the entry.

   Use case ends.

**Extensions**
* 1a. The provided entry does not exist.
    * 1a1. System shows an error message.

      Use case resumes at step 1.

* 1b. The input command was invalid (i.e. spelling error, etc.).
    * 1b1. System tells the user the command is unrecognised.

      Use case ends.


**Use Case: Categorise Persons**

**MSS**
1. User requests to categorise a person (e.g., family, friends, coworkers).
2. System assigns attendees to the category.

   Use case ends.

**Extensions**
* 1a. User tries to create a duplicate category.
    * 1a1. System shows an error message.

      Use case ends.

* 1b. The input command was invalid (i.e. spelling error, etc.).
    * 1b1. System tells the user the command is unrecognised.

      Use case ends.


**Use Case: Search and Filter Persons**

**MSS**
1. User requests to search for or filter entries (e.g., by category, name, RSVP status).
2. System prompts for search or filter criteria.
3. User provides the criteria.
4. System displays the matching entries.

   Use case ends.

**Extensions**
* 1a. The input command was invalid (i.e. spelling error, etc.).
    * 1a1. System tells the user the command is unrecognised.

      Use case ends.

* 4a. No matching entries are found.
    * 4a1. System shows a message indicating no results.

      Use case ends.

### Non-Functional Requirements

Data Requirements
1. Size: The system should support storage for up to 500 guests and 50 vendors per wedding. Data size should not exceed 10 MB for each wedding event.
2. Volatility: Guest lists and vendor details may change frequently, especially closer to the event date. Therefore, the system must accommodate dynamic data updates and edits.
3. Persistency: All guest, vendor, and event information must be saved persistently in a txt file and remain accessible even after system shutdown or failure.
4. Backup Frequency: Automatic backups of data should be created whenever the application is closed.

Environment Requirements

5. Operating System: The system must be compatible with Windows, macOS, and Linux operating systems.
6. Dependencies: Java 17 should be the core language.

Accessibility
7. Provide command-line help documentation that can be accessed at any time with a simple command (help).

Capacity

8. The system should be able to handle data storage for up to 100 simultaneous weddings, each containing up to 500 guests and 50 vendors.

Compliance with Regulations
9. The system must comply with data privacy regulations, including GDPR, ensuring that personal information such as guest addresses and phone numbers are securely handled and stored.

Fault Tolerance
10. The system should handle errors such as missing commands or invalid input gracefully, providing clear error messages without causing system crashes.
11. Ensure that invalid input (e.g., incorrect phone format) does not result in data corruption.

Performance Requirements
12. The system should respond to user input within two seconds, even for lists of up to 500 guests and 50 vendors.
13. Backup operations must complete within five seconds for a wedding list of up to 500 entries.
14. System startup time should not exceed five seconds on standard hardware.


### Glossary

* **Mainstream OS**: Windows, Linux, Unix, MacOS
* **Private contact detail**: A contact detail that is not meant to be shared with others
* **CLI (Command-Line Interface)**: A text-based interface used to interact with software by typing commands.
*  **CRUD Operations**: Stands for Create, Read, Update, and Delete – the basic operations that can be performed on data.
*  **RSVP**: Short for "Répondez S'il Vous Plaît," used to track guest attendance responses for an event.
*  **Vendor**: A supplier providing services or goods for a wedding, such as photographers, florists, caterers, etc.
*  **Tag**: A keyword or label assigned to an entry to categorize and easily filter it within the list.
*  **Index**: A numerical value representing the position of an entry in a list, used to reference and perform operations on the contact.
*  **Status**: A predefined label assigned to a task or attendee, representing their state (e.g., confirmed, pending, or canceled).
*  **Task**: A wedding-related activity that requires completion, such as booking a venue, sending invites, or coordinating with vendors.
*  **Syntax**: The specific structure and format required for entering commands in the CLI.
*  **Stakeholders**: Any individuals or organizations involved in the wedding, including family members, guests, and vendors.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

1. _{ more test cases …​ }_

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: First contact is deleted from the list. Details of the deleted contact shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{explain how to simulate a missing/corrupted file, and the expected behavior}_

1. _{ more test cases …​ }_

# Tasky

# Contents
1. [Credits](#1-credits)
2. [Introduction](#2-introduction)
3. [Installation](#3-installation)
4. [User Guide](#4-user-guide)
  - [Adding tasks](#adding-tasks)
  - [Displaying tasks](#displaying-tasks)
  - [Editing a task](#editing-a-task)
  - [Deleting tasks](#deleting-tasks)
  - [Marking tasks as completed](#marking-tasks-as-completed)
  - [Marking tasks as incomplete](#marking-tasks-as-incomplete)
  - [Undoing commands](#undoing-commands)
  - [Redoing commands](#redoing-commands)
  - [Searching for tasks](#searching-for-tasks)
  - [Adding command aliases](#adding-command-aliases)
  - [Changing path to data file](#changing-path-to-data-file)
  - [Exiting Tasky](#exiting-tasky)

# 1. Credits

The team members of my project group, [cs2103aug2015-w10-4j](https://github.com/cs2103aug2015-w10-4j), for the initialization of the Tasky project (up to [V0.5](https://github.com/cs2103aug2015-w10-4j/main))

# 2. Introduction
Tasky is a _command-line_ calendar program that aims to accommodate busy users who are capable of typing quickly, such as students and office workers.

This is an open source project.

# 3. Installation
Download the latest release at [Releases](https://github.com/m133225/Tasky/releases).

Simply double click the .jar file to run Tasky!

# 4. User Guide

## Adding tasks
(command: add)

	add task123 by 11 sep 2015

This is an example to add a task using the *add* keyword with description "task123" and date 11 sep 2015. It is optional to specify the year of the task, i.e. the year will be defaulted to the current year if not indicated by the user (as shown below).

Note that the keyword to specify the end date field here is 'by'. You can also use the keyword pairs 'start... end' or 'from... to' to specify the starting and ending date-time for the event:

	add task 123 start 11 sep end 15 sep

Use of natural language date filters are also accepted:

	add task 123 by next monday
	add task 456 by tomorrow

To specify a timing for the task, simply add a time argument after the date arguments, for example

	add task 123 by today 8PM
	add task 123 start 11 sep 9AM end 11 sep 2PM

This will also store the task to a text file, which could be retrieved later by using other commands or opening the text file manually.

You can also omit the year which will then interpreted by the program as the current year, or you can omit the date entirely as well, to store the task without any date information.

To specify a location for the task, simply type "loc" or "at", followed by the location, for example

	add task 123 by today 8PM loc nus
	add task 123 start 11 sep 9AM end 11 sep 2PM loc my home

To add recurring tasks, use every [index] day(s)/week(s)/month(s)/year(s) for [index] times. For example

	add task from today to tomorrow every 2 days for 2 times
	add task from today to tomorrow every 1 month for 5 times

Note that an ending time is always required when adding recurring tasks.

### Reordering of fields

You can also add tasks without a specific ordering of the different fields.

	add meeting with client at office from tomorrow 2pm to tomorrow 5pm
	add meeting with client to tomorrow 5pm at office from tomorrow 2pm

will yield and add the exact same task.

## Displaying tasks
(command: display/clear)

	display

This command is to return to the default view.
Note that this command also clears all existing search filters.

## Editing a task
(command: edit/change)

	edit 1 task456 by 12 sep 2015

This is an example to edit the task number 1 from the [display](#displaying-tasks) to task456 and change its date to 12 sep 2015


	change 1 loc school

This is an example to edit the task number 1 from the [display](#displaying-tasks) to task456 and change the location to school

## Deleting tasks
(command: delete/del)

	delete 1  

This is an example to remove the task that is currently number one in the list.

	del 1 2 4 6

This is an example to remove the task that is currently number one, two, four, six in the list.

	del 1-6

This is an example to remove the task that is currently from number one to number six in the list.

To get the list of tasks, you can issue a display command. This command will also delete the task in the storage file. It is possible to revert the command by issuing an undo command. For more info, please take a look at [display](#displaying-tasks) and [undo](#undoing-commands)

## Marking tasks as completed
(command: mark)

	mark 1

This is an example to mark the task that is showing on the screen as number 1 on the screen as completed. To view tasks that have already been completed, key in "display done".

## Marking tasks as incomplete
(command: unmark)

	unmark 1

This is an example to mark the task that is showing on the screen as number 1 on the screen as incomplete. To view tasks that are still incomplete, key in "display undone".       

## Undoing commands
(command: undo)

	undo

This is an example to undo the previous command. If there is no previous command, Tasky will do nothing but give you a notification that you cannot undo. All update operations done by Tasky are recorded inside the main memory of Tasky and would be wiped upon program termination. Therefore, you can only undo a command if you issued it in the same session.

Note that only add, edit, delete, mark and unmark commands are supported.

## Redoing commands
(command: redo)

	redo

Pairing with undo, this command reinstates whatever undo has reverted previously. Note that redo can only be used immediately after a sequence of undo commands.

## Searching for tasks
(command: search)

	search task

This is an example to search any tasks that contain "task" in their name.

Consecutive searches can also be done to obtain a more specific filter. For example

	search task
	search 456

will only display tasks containing both keywords "task" and "456".
You can also filter the search by fields such as date or location.

	search by today

Will display tasks that are due today.

	search loc home

Will display tasks that have the word "home" in their location.

## Adding command aliases
(command: alias)

	alias add submit

This is an example to add the alias 'submit' for the 'add' command. If successful, you will now be able to add new tasks using the 'submit' alias. For example

	submit homework by sunday

will add a 'homework' task that is due on Sunday.

## Changing path to data file
(command: saveto)

	saveto new_file.txt

This is an example to change the path to the save file to new_file.txt. After this command, any changes made will be saved to the new file.

If there is already a file in the specified path, Tasky will load the data in it for you instead.

## Exiting Tasky
(command: exit)

You can exit Tasky by issuing the command

	exit

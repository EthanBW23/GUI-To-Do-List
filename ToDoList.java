package Task__3;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

// Class for creating tasks with a name, due date, priority and completed status
class Task {
    private String name;
    private String dueDate;
    private String priority;
    private boolean completed;
    
    //Constructor
    public Task(String name, String dueDate, String priority) {
        this.name = name;
        this.dueDate = dueDate;
        this.priority = priority;
        this.completed = false;
    }
    
    // Calling methods
    public String getName() {return name;}
    public String getDueDate() {return dueDate;}
    public String getPriority() {return priority;}
    public boolean isCompleted() {return completed;}
    
    // Marks the selected task as completed
    public void markCompleted() {this.completed = true;}
    
    // Returns a string of all the task's information to the list
    @Override
    public String toString() {
    	return (completed ? "[✔] " : "[ ] ") + name + " (Due: " + dueDate + ", Priority: " + priority + ")";
    }
}

// Class for the GUI application
class ToDoList {
    private DefaultListModel<Task> taskListModel; // Stores the tasks when the app is running
    private JList<Task> taskList; // GUI List for displaying all loaded tasks
    private JTextField taskField; // Input field for the task name
    private JComboBox<Integer> yearBox, dayBox; // Integer drop down boxes to select a year and day
    private JComboBox<String> monthBox, priorityBox; // String drop down boxes to select a month and priority
    private static final String FILE_NAME = "tasks.txt"; // File to save tasks for permanent storage

    // Constructor to create the GUI
    public ToDoList() {
    	// Creates the app window
        JFrame frame = new JFrame("To-Do List");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Does nothing when closing, calls a different method when closing
        frame.setSize(725, 400); // Default size of window
        
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        
        // Panel for inputing the task name
        JPanel inputPanel = new JPanel();
        taskField = new JTextField(15);
        
        // Year dropdown box
        yearBox = new JComboBox<>();
        for (int i = 2025; i <= 2030; i++) {
            yearBox.addItem(i);
        }

        // Month dropdown box
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        monthBox = new JComboBox<>(months);

        // Day dropdown box
        dayBox = new JComboBox<>();
        updateDays(); // Set days based on the selected month/year

        // Priority dropdown box
        String[] priorities = {"Low", "Medium", "High"};
        priorityBox = new JComboBox<>(priorities);
        
        // Add task button
        JButton addButton = new JButton("Add Task");
        
        // Adds all the input components
        inputPanel.add(new JLabel("Task:"));
        inputPanel.add(taskField);
        inputPanel.add(new JLabel("Due Date:"));
        inputPanel.add(dayBox);
        inputPanel.add(monthBox);
        inputPanel.add(yearBox);
        inputPanel.add(new JLabel("Priority:"));
        inputPanel.add(priorityBox);
        inputPanel.add(addButton);
        
        // Task List
        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        JScrollPane scrollPane = new JScrollPane(taskList);
        
        // Action buttons (at bottom of GUI)
        JPanel buttonPanel = new JPanel();
        JButton deleteButton = new JButton("Delete Task");
        JButton completeButton = new JButton("Mark Completed");
        JButton saveButton = new JButton("Save Tasks");
        JButton loadButton = new JButton("Load Tasks");
        
        // Adds all the action buttons
        buttonPanel.add(deleteButton);
        buttonPanel.add(completeButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        
        // Add components and their position on the GUI
        panel.add(inputPanel, BorderLayout.NORTH); // Tasks inputs at the top
        panel.add(scrollPane, BorderLayout.CENTER); // Task list in the middle
        panel.add(buttonPanel, BorderLayout.SOUTH); // Action buttons at the bottom
        
        // Makes the GUI visible
        frame.add(panel);
        frame.setVisible(true);
        
        // Action listeners (perform a method when a specific event occurs)
        addButton.addActionListener(event -> addTask());
        deleteButton.addActionListener(event -> deleteTask());
        completeButton.addActionListener(event -> markTaskCompleted());
        saveButton.addActionListener(event -> saveTasks());
        loadButton.addActionListener(event -> loadTasks());
        monthBox.addActionListener(event -> updateDays()); // Update days when the month changes
        yearBox.addActionListener(event -> updateDays());  // Update days when the year changes
        
        createFile(); // Creates text file, if there isn't already one
        loadTasks(); // Loads any tasks in the text file when the app is opened
        
        // Action listener for when the app is closed
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(frame, "Are you sure you want to exit?", "Confirm Exit", JOptionPane.YES_NO_OPTION); // Confirmation box for user
                if (choice == JOptionPane.YES_OPTION) {
                	isClosing = true;
                    saveTasks(); // Save tasks before closing
                    System.exit(0);  // Close the app
                }
            }
        });
    }
    
    // Updates the days dropdown box based on the selected month and year
    private void updateDays() {
        int selectedYear = (Integer) yearBox.getSelectedItem();
        String selectedMonth = (String) monthBox.getSelectedItem();

        int daysInMonth;
        switch (selectedMonth) {
            case "January": case "March": case "May": case "July": case "August": case "October": case "December":
                daysInMonth = 31;
                break;
            case "April": case "June": case "September": case "November":
                daysInMonth = 30;
                break;
            case "February":
                // Check for leap year
                if ((selectedYear % 4 == 0 && selectedYear % 100 != 0) || (selectedYear % 400 == 0)) {
                    daysInMonth = 29; // Leap year
                } else {
                    daysInMonth = 28;
                }
                break;
            default:
            	JOptionPane.showMessageDialog(null, "Unexpected Month:" + selectedMonth, "Error", JOptionPane.ERROR_MESSAGE); // If month is unrecognisable, displays error message
                return; // Exit early if month is incorrect
        }

        // Update the day dropdown
        dayBox.removeAllItems();
        for (int i = 1; i <= daysInMonth; i++) {
            dayBox.addItem(i);
        }
    }

    // Adds a new task to the list
    private void addTask() {
    	// Collects all the inputed task information
        String name = taskField.getText().trim();
        String dueDate = dayBox.getSelectedItem() + " " + monthBox.getSelectedItem() + " " + yearBox.getSelectedItem();
        String priority = (String) priorityBox.getSelectedItem();

        // The user left the text field empty
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Task name and due date cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE); // Error message to user
            return;
        }
        
        // Adds the task to GUI list
        taskListModel.addElement(new Task(name, dueDate, priority));
        taskField.setText(""); // Resets task text field
        JOptionPane.showMessageDialog(null, "Tasks added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE); // Confirmation to user
    }

    // Deletes selected task from GUI list
    private void deleteTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            int choice = JOptionPane.showConfirmDialog(null, "Are your sure you want to delete this task?", "Confirm Deletion", JOptionPane.YES_NO_OPTION); // Confirmation box to user
            if (choice == JOptionPane.YES_OPTION) {
                taskListModel.remove(selectedIndex); // Removes the selected from list
                JOptionPane.showMessageDialog(null, "Tasks deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE); // Confirmation to user
            }
        } else {
            JOptionPane.showMessageDialog(null, "Select a task to delete.", "Error", JOptionPane.WARNING_MESSAGE); // If the user hasn't selected a task
        }
    }

    // Marks selected task as completed
    private void markTaskCompleted() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            taskListModel.getElementAt(selectedIndex).markCompleted();
            taskList.repaint(); // adds a tick symbol in the GUI, to show the task has been marked completed
            JOptionPane.showMessageDialog(null, "Tasks marked successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Select a task to mark as completed.", "Error", JOptionPane.WARNING_MESSAGE); // No task selected to mark
        }
    }
    
    private boolean isClosing = false; // Variable to track if the app is closing
    // Saves all the tasks stored in the list to a text file for permanent storage
    private void saveTasks() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (int i = 0; i < taskListModel.size(); i++) {
                Task task = taskListModel.getElementAt(i);
                // Writes each task with all it's information
                writer.write(task.getName() + "," + task.getDueDate() + "," + task.isCompleted() + "," + task.getPriority());
                writer.newLine();
            }
            if (!isClosing) { // When closing the app
                JOptionPane.showMessageDialog(null, "Tasks saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException e) { // Catches error if the save fails
            JOptionPane.showMessageDialog(null, "Error saving tasks!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Reads the text file to see if any tasks can be loaded in the GUI list
    private void loadTasks() {
        taskListModel.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                	// Reads the specific information of the task and creates a new one
                    Task task = new Task(parts[0], parts[1], parts[3]);
                    if (Boolean.parseBoolean(parts[2])) { // If the saved task was marked as completed
                        task.markCompleted();
                    }
                    taskListModel.addElement(task); // Adds task to GUI list
                }
            }
            JOptionPane.showMessageDialog(null, "Tasks loaded successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) { // Catches error if the load fails
            JOptionPane.showMessageDialog(null, "No saved tasks found.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Creates a text file, if one doesn't already exist
    private void createFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) { // Catches error if the creation fails
                JOptionPane.showMessageDialog(null, "Error creating file!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Main method, starts the app
    public static void main(String[] args) {
        new ToDoList();
    }
}

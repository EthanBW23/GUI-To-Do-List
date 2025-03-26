package Task__3;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

class Task {
    private String name;
    private String dueDate;
    private boolean completed;
    private String priority;

    public Task(String name, String dueDate, String priority) {
        this.name = name;
        this.dueDate = dueDate;
        this.completed = false;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public String getDueDate() {
        return dueDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void markCompleted() {
        this.completed = true;
    }
    
    public String getPriority() {
        return priority;
    }
    
    @Override
    public String toString() {
    	return (completed ? "[✔] " : "[ ] ") + name + " (Due: " + dueDate + ", Priority: " + priority + ")";
    }
}

class ToDoList {
    private DefaultListModel<Task> taskListModel;
    private JList<Task> taskList;
    private JTextField taskField;
    private JComboBox<Integer> yearBox, dayBox;
    private JComboBox<String> monthBox, priorityBox;
    private static final String FILE_NAME = "tasks.txt";

    public ToDoList() {
        JFrame frame = new JFrame("To-Do List");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(650, 400);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        
        // Input Fields
        JPanel inputPanel = new JPanel();
        taskField = new JTextField(15);
        
        yearBox = new JComboBox<>();
        for (int i = 2025; i <= 2030; i++) {
            yearBox.addItem(i);
        }

        // Month Dropdown
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        monthBox = new JComboBox<>(months);

        // Day Dropdown
        dayBox = new JComboBox<>();
        updateDays(); // Set days dynamically based on the selected month/year

        // Priority Dropdown
        String[] priorities = {"Low", "Medium", "High"};
        priorityBox = new JComboBox<>(priorities);
        
        JButton addButton = new JButton("Add Task");
        
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
        
        // Action Buttons
        JPanel buttonPanel = new JPanel();
        JButton deleteButton = new JButton("Delete Task");
        JButton completeButton = new JButton("Mark Completed");
        JButton saveButton = new JButton("Save Tasks");
        JButton loadButton = new JButton("Load Tasks");
        
        buttonPanel.add(deleteButton);
        buttonPanel.add(completeButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        
        // Add components
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        frame.add(panel);
        frame.setVisible(true);
        
        // Event Listeners
        addButton.addActionListener(event -> addTask());
        deleteButton.addActionListener(event -> deleteTask());
        completeButton.addActionListener(event -> markTaskCompleted());
        saveButton.addActionListener(event -> saveTasks());
        loadButton.addActionListener(event -> loadTasks());
        monthBox.addActionListener(event -> updateDays()); // Update days when the month changes
        yearBox.addActionListener(event -> updateDays());  // Update days when the year changes
        
        createFile();
        loadTasks();
        
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(frame, "Are you sure you want to exit?", "Confirm Exit", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                	isClosing = true;
                    saveTasks(); // Save tasks before closing
                    System.exit(0);  // Close the app
                }
            }
        });
    }
    
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
                System.err.println("Unexpected month: " + selectedMonth);
                return; // Exit early if month is incorrect
        }
        
        // Debugging output
        System.out.println("Year: " + selectedYear + ", Month: " + selectedMonth + ", Days: " + daysInMonth);

        // Update the day dropdown
        dayBox.removeAllItems();
        for (int i = 1; i <= daysInMonth; i++) {
            dayBox.addItem(i);
        }
    }

    private void addTask() {
        String name = taskField.getText().trim();
        String dueDate = dayBox.getSelectedItem() + " " + monthBox.getSelectedItem() + " " + yearBox.getSelectedItem();
        String priority = (String) priorityBox.getSelectedItem();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Task name and due date cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        taskListModel.addElement(new Task(name, dueDate, priority));
        taskField.setText("");
        JOptionPane.showMessageDialog(null, "Tasks added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            int choice = JOptionPane.showConfirmDialog(null, "Are your sure you want to delete this task?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                taskListModel.remove(selectedIndex);
                JOptionPane.showMessageDialog(null, "Tasks deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Select a task to delete.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void markTaskCompleted() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            taskListModel.getElementAt(selectedIndex).markCompleted();
            taskList.repaint();
            JOptionPane.showMessageDialog(null, "Tasks marked successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Select a task to mark as completed.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private boolean isClosing = false; // Flag to track if app is closing
    private void saveTasks() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (int i = 0; i < taskListModel.size(); i++) {
                Task task = taskListModel.getElementAt(i);
                writer.write(task.getName() + "," + task.getDueDate() + "," + task.isCompleted() + "," + task.getPriority());
                writer.newLine();
            }
            if (!isClosing) { // Show dialog only when NOT closing
                JOptionPane.showMessageDialog(null, "Tasks saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving tasks!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTasks() {
        taskListModel.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    Task task = new Task(parts[0], parts[1], parts[3]);
                    if (Boolean.parseBoolean(parts[2])) {
                        task.markCompleted();
                    }
                    taskListModel.addElement(task);
                }
            }
            JOptionPane.showMessageDialog(null, "Tasks loaded successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "No saved tasks found.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void createFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error creating file!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        new ToDoList();
    }
}

// See if date ordering is easy
// ask for better explanations of the code

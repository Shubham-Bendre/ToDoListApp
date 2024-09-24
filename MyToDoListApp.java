import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

// Step 1: Define Task class representing an individual task item
class Task extends JPanel {
    // Step 2: Private fields for task components and colors
    private JLabel index;
    protected JTextField taskName; // Changed access modifier to protected
    protected JComboBox<String> priorityCombo; // Changed access modifier to protected
    private JButton done;
    private Color lavender = new Color(230, 230, 255);
    private Color mint = new Color(204, 255, 204);
    private Color doneColor = new Color(255, 204, 204);
    private boolean checked;
    private int priority;

    // Step 3: Constructor to create a new task with the given index
    Task(int index) {
        this.setPreferredSize(new Dimension(400, 40));
        this.setBackground(lavender);
        this.setLayout(new GridLayout(1, 5));

        this.index = new JLabel(String.valueOf(index));
        this.index.setHorizontalAlignment(JLabel.CENTER);
        this.add(this.index);

        taskName = new JTextField("Write something..");
        taskName.setBorder(BorderFactory.createEmptyBorder());
        taskName.setBackground(lavender);
        this.add(taskName);

        priorityCombo = new JComboBox<>(new String[]{"P1", "P2", "P3"});
        priorityCombo.setBackground(mint);
        this.add(priorityCombo);

        done = new JButton("Done");
        done.setFocusPainted(false);
        done.setBackground(doneColor);
        this.add(done);

        checked = false;
        priority = 0;

        // Step 4: ActionListener for "Done" button
        done.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeState(); // Mark task as done and update the history table
            }
        });

        // Step 5: ActionListener for Priority Combo Box
        priorityCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String priorityText = (String) priorityCombo.getSelectedItem();
                priority = getPriorityNumber(priorityText); // Set task priority
            }
        });
    }

    // Step 6: Get the numerical value of the priority from the selected text
    private int getPriorityNumber(String priorityText) {
        if (priorityText.equals("P1")) {
            return 1;
        } else if (priorityText.equals("P2")) {
            return 2;
        } else if (priorityText.equals("P3")) {
            return 3;
        }
        return 0;
    }

    // Step 7: Change the state of the task when "Done" button is clicked
    public void changeState() {
        if (!checked) {
            setBackground(mint);
            taskName.setBackground(mint);
            checked = true;
        }
    }

    // Step 8: Check if the task is marked as done
    public boolean isChecked() {
        return checked;
    }

    // Step 9: Get the priority value of the task
    public int getPriority() {
        return priority;
    }

    // Step 10: Method to update the history table in the database
    
}

// Step 11: Define TaskList class representing the list of tasks
class TaskList extends JPanel {
    // Step 12: Private field to store the list of tasks
    private List<Task> tasks;
    private Color lightYellow = new Color(255, 255, 204);

    // Step 13: Constructor to set up the task list
    TaskList() {
        tasks = new ArrayList<>();
        GridLayout layout = new GridLayout(10, 1);
        this.setLayout(layout);
        this.setPreferredSize(new Dimension(400, 560));
        this.setBackground(lightYellow);
    }

    // Step 14: Add a new task to the list
    public void addTask(Task task) {
        tasks.add(task);
        updateTasks();
    }

    // Step 15: Update the tasks in the list
    public void updateTasks() {
        this.removeAll();
        tasks.forEach(task -> this.add(task));
        this.revalidate();
        this.repaint();
    }

    // Step 16: Remove completed tasks from the list
    public void removeCompletedTasks() {
        tasks.removeIf(Task::isChecked);
        updateTasks();
    }

    // Step 17: Rearrange the tasks based on priority
    public void rearrangeTasks() {
        Collections.sort(tasks, new Comparator<Task>() {
            @Override
            public int compare(Task t1, Task t2) {
                int p1 = t1.getPriority();
                int p2 = t2.getPriority();
                return Integer.compare(p1, p2);
            }
        });

        updateTasks();
    }
}

// Step 18: Define Footer class representing the bottom section of the app
class Footer extends JPanel {
    // Step 19: Private fields for buttons and task list
    private JButton addTask;
    private JButton clear;
    private JButton rearrange;
    private TaskList taskList;
    private int taskIndex = 1;

    // Step 20: Constructor to set up the footer
    Footer(TaskList taskList) {
        this.setPreferredSize(new Dimension(400, 60));
        this.setBackground(new Color(255, 242, 204));

        addTask = new JButton("Add Task");
        addTask.setFont(new Font("Sans-serif", Font.BOLD, 16));
        addTask.setFocusPainted(false);
        this.add(addTask);

        clear = new JButton("Clear Completed");
        clear.setFont(new Font("Sans-serif", Font.BOLD, 16));
        clear.setFocusPainted(false);
        this.add(clear);

        rearrange = new JButton("Rearrange");
        rearrange.setFont(new Font("Sans-serif", Font.BOLD, 16));
        rearrange.setFocusPainted(false);
        this.add(rearrange);

        // Step 21: ActionListener for "Add Task" button
        addTask.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Task newTask = new Task(taskIndex);
                taskList.addTask(newTask);
                taskIndex++;
            }
        });

        // Step 22: ActionListener for "Clear Completed" button
        clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                taskList.removeCompletedTasks();
            }
        });

        // Step 23: ActionListener for "Rearrange" button
        rearrange.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                taskList.rearrangeTasks();
            }
        });
    }
}

// Step 24: Define MyToDoListApp class representing the main application frame
public class MyToDoListApp extends JFrame {
    // Step 25: Private fields for task list and footer
    private TaskList taskList;
    private Footer footer;

    // Step 26: Constructor to set up the main application frame
    public MyToDoListApp() {
        this.setTitle("Improved ToDo List");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Set to EXIT_ON_CLOSE
        this.setExtendedState(JFrame.MAXIMIZED_BOTH); // Start in full screen
        this.setLayout(new BorderLayout()); // Use BorderLayout for better organization

        // Step 27: Add the title "To Do List" above the task list
        JLabel titleLabel = new JLabel("To Do List");
        titleLabel.setFont(new Font("Sans-serif", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        this.add(titleLabel, BorderLayout.NORTH);

        taskList = new TaskList();
        footer = new Footer(taskList);

        this.add(taskList, BorderLayout.CENTER);
        this.add(footer, BorderLayout.SOUTH);
    }

    // Step 28: Main method to start the application
    public static void main(String[] args) {
        // Set the look and feel of the application to the system default
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MyToDoListApp app = new MyToDoListApp();
                app.setVisible(true);
            }
        });
    }
}

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;

public class UserInterface extends JFrame implements ActionListener {

    private Connection connection;                  // Oracle database connection object
    private JPanel procPanel[] = new JPanel[10];    // Houses the controls for each procedure


    private void initProcPanel0() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JLabel branchLabel = new JLabel("Branch ID:");
        JTextField branchField = new JTextField();
        procPanel[0] = panel;
    }


    public UserInterface() {
        Container container = this.getContentPane();
        container.setLayout(new BorderLayout());
        Font fontBtn = new Font(Font.SANS_SERIF, Font.PLAIN, 24);

        // left-side button menu
        JPanel menuPanel = new JPanel();
        container.add(menuPanel, BorderLayout.LINE_START);
        menuPanel.setLayout(new FlowLayout());
        menuPanel.setPreferredSize(new Dimension(540, 0));
        JButton[] procBtn = new JButton[10];
        procBtn[0] = new JButton("Show Rentals By Branch");
        procBtn[1] = new JButton("Show Supervisor Rentals");
        procBtn[2] = new JButton("Show Rentals By Owner");
        procBtn[3] = new JButton("Search Rentals");
        procBtn[4] = new JButton("Count Rentals Per Branch");
        procBtn[5] = new JButton("Create New Lease");
        procBtn[6] = new JButton("Show Leases by Renter");
        procBtn[7] = new JButton("Show Renters With Multiple Leases");
        procBtn[8] = new JButton("Show Average Rent By Town");
        procBtn[9] = new JButton("Show Leases That Expire Soon");
        for (int i = 0; i < procBtn.length; i++) {
            procBtn[i].setPreferredSize(new Dimension(500, 50));
            procBtn[i].setFont(fontBtn);
            procBtn[i].setActionCommand(((Integer)i).toString());
            procBtn[i].addActionListener(this);
            menuPanel.add(procBtn[i]);
        }

        // init panels
        for (JPanel panel : this.procPanel) {
            // anything that needs to happen to all panels
        }

        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }


    public void actionPerformed(ActionEvent event) {
        int index = Integer.parseInt(event.getActionCommand());
        this.getContentPane().add(procPanel[index], BorderLayout.CENTER);
    }


    public static void main(String[] args) {
        // dynamically load the Oracle DB driver class
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not find DB driver class!");
        }

        UserInterface ui = new UserInterface();
    }
}

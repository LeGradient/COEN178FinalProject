import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class UserInterface extends JFrame {

    private Connection connection;  // Oracle database connection object

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
        for (JButton button : procBtn) {
            button.setPreferredSize(new Dimension(500, 50));
            button.setFont(fontBtn);
            menuPanel.add(button);
        }


        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
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

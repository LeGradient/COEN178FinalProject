import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;

public class UserInterface extends JFrame implements ActionListener {

    // Oracle database connection parameters
    private static final String database = "jdbc:oracle:thin:@dagobah.engr.scu.edu:1521:db11g";
    private static final String username = "ddallaga";
    private static final String password = "testtest19";

    private Connection connection;                  // Oracle database connection object
    private JPanel procPanel[] = new JPanel[10];    // Houses the controls for each procedure
    private Font fontBtn = new Font(Font.SANS_SERIF, Font.PLAIN, 24);
    Font fontMono = new Font(Font.MONOSPACED, Font.PLAIN, 24);


    private class ProcPanel0 extends JPanel {
        public ProcPanel0() {
            this.setLayout(new BorderLayout());

            JPanel subpanel1 = new JPanel();
            subpanel1.setLayout(new FlowLayout());
            subpanel1.setPreferredSize(new Dimension(200, 0));
            subpanel1.setBackground(Color.GRAY);
            this.add(subpanel1, BorderLayout.LINE_START);

            JLabel branchLabel = new JLabel("Branch ID:");
            branchLabel.setFont(UserInterface.this.fontBtn);
            subpanel1.add(branchLabel);

            JTextField branchField = new JTextField(5);
            branchField.setFont(UserInterface.this.fontBtn);
            subpanel1.add(branchField);

            JButton submitBtn = new JButton("Submit");
            submitBtn.setFont(UserInterface.this.fontBtn);
            subpanel1.add(submitBtn);

            submitBtn.addActionListener(actionEvent -> {
                try {
                    // send query
                    String sql = "SELECT rental_id, street, city, zip " +
                                "FROM Property " +
                                "WHERE supervisor_id IN (" +
                                    "SELECT emp_id " +
                                    "FROM Employee " +
                                    "WHERE branch_id = " + branchField.getText() +
                                ")";
                    Statement stmt = UserInterface.this.connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    ResultSet result = stmt.executeQuery(sql);

                    // get column names
                    int colCount = result.getMetaData().getColumnCount();
                    String[] columns = new String[colCount];
                    for (int i = 1; i <= result.getMetaData().getColumnCount(); i++){
                        columns[i-1] = result.getMetaData().getColumnName(i);
                    }

                    // get data
                    result.last();
                    int rowCount = result.getRow();
                    result.beforeFirst();
                    String[][] data = new String[colCount][rowCount];
                    for (int i = 0; i < rowCount; i++) {
                        result.next();
                        for (int j = 0; j < colCount; j++) {
                            data[j][i] = result.getString(j + 1);
                        }
                    }
                    this.add(new JTable(data, columns), BorderLayout.CENTER);
                } catch (SQLException e) {
                    System.out.println("Could not initialize procPanel[0]!");
                    System.out.println(e);
                    System.exit(1);
                }
            });
        }
    }





    public UserInterface() {
        try {
            this.connection = DriverManager.getConnection(database, username, password);
        } catch (SQLException e) {
            System.out.println("Could not connect to database!");
            System.exit(1);
        }

        Container container = this.getContentPane();
        container.setLayout(new BorderLayout());

        // left-side button menu
        JPanel menuPanel = new JPanel();
        container.add(menuPanel, BorderLayout.LINE_START);
        menuPanel.setLayout(new FlowLayout());
        menuPanel.setPreferredSize(new Dimension(540, 600));
        menuPanel.setBackground(Color.DARK_GRAY);
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
            procBtn[i].setFont(this.fontBtn);
            procBtn[i].setActionCommand(((Integer)i).toString());
            procBtn[i].addActionListener(this);
            menuPanel.add(procBtn[i]);
        }

        // init panels
        //initProcPanel0();
        this.procPanel[0] = new ProcPanel0();
        for (JPanel panel : this.procPanel) {
            // anything that needs to happen to all panels
        }

        this.setSize(1400, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        menuPanel.setVisible(true);
    }


    public void actionPerformed(ActionEvent event) {
        int index = Integer.parseInt(event.getActionCommand());
        this.getContentPane().add(procPanel[index], BorderLayout.CENTER);
        this.getContentPane().revalidate();
    }


    public void cleanup() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            System.out.println("Could not close database connection!");
            System.exit(1);
        }
    }


    public static void main(String[] args) {
        UserInterface ui = null;
        try {
            // dynamically load the Oracle DB driver class
            Class.forName("oracle.jdbc.driver.OracleDriver");

            // initialize the UI
            ui = new UserInterface();
        } catch (ClassNotFoundException e) {
            System.out.println("Could not find DB driver class!");
            System.exit(1);
        }
    }
}

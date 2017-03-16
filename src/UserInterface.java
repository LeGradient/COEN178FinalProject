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
        private JScrollPane resultsPane = new JScrollPane();

        // extra information to be displayed above the table
        private JLabel resultsBranchLabel = new JLabel("Branch ID: ");
        private JLabel resultsManagerLabel = new JLabel("Manager: ");

        public ProcPanel0() {
            this.setLayout(new BorderLayout());

            // initialize menu
            JPanel menuPanel = new JPanel();
            menuPanel.setLayout(new FlowLayout());
            menuPanel.setPreferredSize(new Dimension(200, 0));
            menuPanel.setBackground(Color.GRAY);
            this.add(menuPanel, BorderLayout.LINE_START);

            JLabel branchLabel = new JLabel("Branch ID:");
            branchLabel.setFont(UserInterface.this.fontBtn);

            JTextField branchField = new JTextField(5);
            branchField.setFont(UserInterface.this.fontBtn);

            JButton submitBtn = new JButton("Submit");
            submitBtn.setFont(UserInterface.this.fontBtn);

            menuPanel.add(branchLabel);
            menuPanel.add(branchField);
            menuPanel.add(submitBtn);

            // initialize results area
            JPanel resultsPanel = new JPanel(new BorderLayout());
            JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
            labelPanel.add(resultsBranchLabel);
            labelPanel.add(resultsManagerLabel);
            resultsPanel.add(labelPanel, BorderLayout.PAGE_START);
            this.add(resultsPanel, BorderLayout.CENTER);

            submitBtn.addActionListener(actionEvent -> {
                this.resultsBranchLabel.setText("Branch ID: " + branchField.getText());

                // get manager name
                try {
                    String sql = "SELECT name " +
                            "FROM Employee " +
                            "WHERE branch_id = " + branchField.getText() + " " +
                            "AND job = 'manager'";
                    Statement stmt = UserInterface.this.connection.createStatement();
                    ResultSet managerResult = stmt.executeQuery(sql);
                    String managerName = "";
                    if (managerResult.next()) {
                        managerName = managerResult.getString("name");
                    }
                    this.resultsManagerLabel.setText("Manager: " + managerName);
                } catch (SQLException e) {
                    System.out.println(e);
                    System.out.println("Couldn't get manager name!");
                }

                // get available properties
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
                    for (int i = 0; i < result.getMetaData().getColumnCount(); i++){
                        columns[i] = result.getMetaData().getColumnName(i + 1);
                    }

                    // get data
                    result.last();
                    int rowCount = result.getRow();
                    result.beforeFirst();
                    String[][] data = new String[rowCount][colCount];
                    for (int i = 0; i < rowCount; i++) {
                        result.next();
                        for (int j = 0; j < colCount; j++) {
                            data[i][j] = result.getString(j + 1);
                        }
                    }

                    resultsPanel.remove(this.resultsPane);
                    this.resultsPane = new JScrollPane(new JTable(data, columns));
                    resultsPanel.add(this.resultsPane, BorderLayout.CENTER);
                    this.revalidate();
                    this.repaint();
                } catch (SQLException e) {
                    System.out.println("Could not initialize procPanel[0]!");
                    System.out.println(e);
                    System.exit(1);
                }
            });
        }
    }


    private class ProcPanel1 extends JPanel {
        private JScrollPane resultsPane = new JScrollPane();

        public ProcPanel1() {
            // initialize results area

//            // initialize menu
//            JPanel menuPanel = new JPanel();
//            menuPanel.setLayout(new FlowLayout());
//            menuPanel.setPreferredSize(new Dimension(200, 0));
//            menuPanel.setBackground(Color.GRAY);
//            this.add(menuPanel, BorderLayout.LINE_START);
//
//            JLabel branchLabel = new JLabel("Branch ID:");
//            branchLabel.setFont(UserInterface.this.fontBtn);
//
//            JTextField branchField = new JTextField(5);
//            branchField.setFont(UserInterface.this.fontBtn);
//
//            JButton submitBtn = new JButton("Submit");
//            submitBtn.setFont(UserInterface.this.fontBtn);
//
//            menuPanel.add(branchLabel);
//            menuPanel.add(branchField);
//            menuPanel.add(submitBtn);
//
//            JPanel resultsPanel = new JPanel(new BorderLayout());

            try {
                // send query
                String sql = "SELECT " +
                        "Property.supervisor_id, " +
                        "Employee.name, " +
                        "Property.rental_id, " +
                        "Property.street, " +
                        "Property.city, " +
                        "Property.zip, " +
                        "FROM Property JOIN Employee " +
                        "ON Property.supervisor_id = Employee.emp_id";
                Statement stmt = UserInterface.this.connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet result = stmt.executeQuery(sql);

                // get column names
                int colCount = result.getMetaData().getColumnCount();
                String[] columns = new String[colCount];
                for (int i = 0; i < result.getMetaData().getColumnCount(); i++){
                    columns[i] = result.getMetaData().getColumnName(i + 1);
                }

                // get data
                result.last();
                int rowCount = result.getRow();
                result.beforeFirst();
                String[][] data = new String[rowCount][colCount];
                for (int i = 0; i < rowCount; i++) {
                    result.next();
                    for (int j = 0; j < colCount; j++) {
                        data[i][j] = result.getString(j + 1);
                    }
                }

                this.remove(this.resultsPane);
                this.resultsPane = new JScrollPane(new JTable(data, columns));
                this.add(this.resultsPane, BorderLayout.CENTER);
                this.revalidate();
                this.repaint();
            } catch (SQLException e) {
                System.out.println("Could not initialize procPanel[0]!");

                System.out.println(e);
                System.exit(1);
            }
        }
    }

    private class ProcPanel2 extends JPanel {
        private JScrollPane resultsPane = new JScrollPane();

        public ProcPanel2() {
            // initialize results area

            JPanel resultsPanel = new JPanel(new BorderLayout());
            try {
                // send query
                String sql =
                        "SELECT rental_id, street, city, zip" +
                        "FROM Property" +
                        "WHERE owner_id = arg_owner;";
                Statement stmt = UserInterface.this.connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet result = stmt.executeQuery(sql);

                // get column names
                int colCount = result.getMetaData().getColumnCount();
                String[] columns = new String[colCount];
                for (int i = 0; i < result.getMetaData().getColumnCount(); i++){
                    columns[i] = result.getMetaData().getColumnName(i + 1);
                }

                // get data
                result.last();
                int rowCount = result.getRow();
                result.beforeFirst();
                String[][] data = new String[rowCount][colCount];
                for (int i = 0; i < rowCount; i++) {
                    result.next();
                    for (int j = 0; j < colCount; j++) {
                        data[i][j] = result.getString(j + 1);
                    }
                }

                this.remove(this.resultsPane);
                this.resultsPane = new JScrollPane(new JTable(data, columns));
                this.add(this.resultsPane, BorderLayout.CENTER);
                this.revalidate();
                this.repaint();
            } catch (SQLException e) {
                System.out.println("Could not initialize procPanel[0]!");
                System.out.println(e);
                System.exit(1);
            }
        }
    }



    private int indexPrev = -1;

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
        this.procPanel[0] = new ProcPanel0();
        this.procPanel[1] = new ProcPanel1();

        this.setSize(1200, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        menuPanel.setVisible(true);
    }


    public void actionPerformed(ActionEvent event) {
        int index = Integer.parseInt(event.getActionCommand());
        if (this.indexPrev != -1) {
            this.getContentPane().remove(procPanel[this.indexPrev]);
        }
        this.indexPrev = index;
        this.getContentPane().add(procPanel[index], BorderLayout.CENTER);
        this.getContentPane().revalidate();
        this.getContentPane().repaint();
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

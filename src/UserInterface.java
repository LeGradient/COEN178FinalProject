import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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

            JPanel resultsPanel = new JPanel(new BorderLayout());

            try {
                // send query
                String sql = "SELECT " +
                        "Property.supervisor_id, " +
                        "Employee.name, " +
                        "Property.rental_id, " +
                        "Property.street, " +
                        "Property.city, " +
                        "Property.zip " +
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
                System.out.println("Could not initialize procPanel[1]!");

                System.out.println(e);
                System.exit(1);
            }
        }
    }

    private class ProcPanel2 extends JPanel {
        private JScrollPane resultsPane = new JScrollPane();
        
        // extra information to be displayed above the table
        private JLabel resultsOwnerLabel = new JLabel("Owner");

        public ProcPanel2() {
            this.setLayout(new BorderLayout());

            // initialize menu
            JPanel menuPanel = new JPanel();
            menuPanel.setLayout(new FlowLayout());
            menuPanel.setPreferredSize(new Dimension(200, 0));
            menuPanel.setBackground(Color.GRAY);
            this.add(menuPanel, BorderLayout.LINE_START);

            JLabel branchLabel = new JLabel("Owner ID:");
            branchLabel.setFont(UserInterface.this.fontBtn);

            JTextField ownerField = new JTextField(5);
            ownerField.setFont(UserInterface.this.fontBtn);

            JButton submitBtn = new JButton("Submit");
            submitBtn.setFont(UserInterface.this.fontBtn);

            menuPanel.add(branchLabel);
            menuPanel.add(ownerField);
            menuPanel.add(submitBtn);

            // initialize results area
            JPanel resultsPanel = new JPanel(new BorderLayout());
            JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
            labelPanel.add(resultsOwnerLabel);
            resultsPanel.add(labelPanel, BorderLayout.PAGE_START);
            this.add(resultsPanel, BorderLayout.CENTER);

            submitBtn.addActionListener(actionEvent -> {
           

                // get owners name
               this.resultsOwnerLabel.setText("Owner ID: " + ownerField.getText());
           

                // get available properties
                try {
                    // send query
                    String sql = "SELECT rental_id, street, city, zip " +
                    		"FROM Property " +
                    		"WHERE owner_id = " + ownerField.getText();
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

    private class ProcPanel3 extends JPanel {
        private JScrollPane resultsPane = new JScrollPane();
        
        // extra information to be displayed above the table
        private JLabel resultsOwnerLabel = new JLabel("Owner");

        public ProcPanel3() {
            this.setLayout(new BorderLayout());

            // initialize menu
            JPanel menuPanel = new JPanel();
            menuPanel.setLayout(new FlowLayout());
            menuPanel.setPreferredSize(new Dimension(300, 0));
            menuPanel.setBackground(Color.GRAY);
            this.add(menuPanel, BorderLayout.LINE_START);

            JLabel rentLabelMin = new JLabel("Min Rent: ");
            rentLabelMin.setFont(UserInterface.this.fontBtn);

            JTextField rentMinField = new JTextField(12);
            rentMinField.setFont(UserInterface.this.fontBtn);

            JLabel rentLabelMax = new JLabel("Max Rent: ");
            rentLabelMax.setFont(UserInterface.this.fontBtn);
            
            JTextField rentMaxField = new JTextField(12);
            rentMaxField.setFont(UserInterface.this.fontBtn);
            
            JLabel cityLabel  = new JLabel("City : ");
            cityLabel.setFont(UserInterface.this.fontBtn);

            JTextField cityField = new JTextField(15);
            cityField.setFont(UserInterface.this.fontBtn);

            JLabel roomAmountLabel = new JLabel("Room amount: ");
            roomAmountLabel.setFont(UserInterface.this.fontBtn);
            
            JTextField roomAmountField = new JTextField(5);
            roomAmountField.setFont(UserInterface.this.fontBtn);
       
            JButton submitBtn = new JButton("Submit");
            submitBtn.setFont(UserInterface.this.fontBtn);

            menuPanel.add(rentLabelMax);
            menuPanel.add(rentMaxField);
            menuPanel.add(rentLabelMin);
            menuPanel.add(rentMinField);
            menuPanel.add(cityLabel);
            menuPanel.add(cityField);
            menuPanel.add(roomAmountLabel);
            menuPanel.add(roomAmountField);
            menuPanel.add(submitBtn);

            // initialize results area
            JPanel resultsPanel = new JPanel(new BorderLayout());
            JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
            labelPanel.add(resultsOwnerLabel);
            resultsPanel.add(labelPanel, BorderLayout.PAGE_START);
            this.add(resultsPanel, BorderLayout.CENTER);

            submitBtn.addActionListener(actionEvent -> {
           
                // get available properties
                try {
                    // send query
                    String sql = 
						"SELECT rental_id, street, city, zip " +
						"FROM Property " +
						"WHERE status = 'available' " +
						"AND num_rooms = " + roomAmountField.getText() +
						"AND city = " + "'" + cityField.getText() + "'" +
 						"AND monthly_rent >= " + rentMinField.getText() +
						"AND monthly_rent <= " + rentMaxField.getText();
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
                    System.out.println("Could not initialize procPanel[3]!");
                    System.out.println(e);
                    System.exit(1);
                }
            });
        }
    }

    private class ProcPanel4 extends JPanel {
        private JScrollPane resultsPane = new JScrollPane();

        public ProcPanel4() {
            // initialize results area

            JPanel resultsPanel = new JPanel(new BorderLayout());

            try {
                // send query
                String sql = "SELECT branch_id, COUNT(*) num " +
                			"FROM Property JOIN Employee " +
                			"ON Property.supervisor_id = Employee.emp_id " +
                			"WHERE Property.status = 'available' " +
                			"GROUP BY branch_id";
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
                System.out.println("Could not initialize procPanel[4]!");

                System.out.println(e);
                System.exit(1);
            }
        }
    }

    private class ProcPanel5 extends JPanel {
        private JScrollPane resultsPane = new JScrollPane();

        public ProcPanel5() {
            this.setLayout(new GridLayout(9, 1));

            JPanel leaseIdPanel = new JPanel(new FlowLayout());
            JLabel leaseIdLabel = new JLabel("Lease ID: ");
            JTextField leaseIdField = new JTextField(5);
            leaseIdPanel.add(leaseIdLabel);
            leaseIdPanel.add(leaseIdField);

            JPanel rentalIdPanel = new JPanel(new FlowLayout());
            JLabel rentalIdLabel = new JLabel("Rental ID: ");
            JTextField rentalIdField = new JTextField(5);
            rentalIdPanel.add(rentalIdLabel);
            rentalIdPanel.add(rentalIdField);

            JPanel renterIdPanel = new JPanel(new FlowLayout());
            JLabel renterIdLabel = new JLabel("Renter ID: ");
            JTextField renterIdField = new JTextField(5);
            renterIdPanel.add(renterIdLabel);
            renterIdPanel.add(renterIdField);

            JPanel friendNamePanel = new JPanel(new FlowLayout());
            JLabel friendNameLabel = new JLabel("Friend Name: ");
            JTextField friendNameField = new JTextField(30);
            friendNamePanel.add(friendNameLabel);
            friendNamePanel.add(friendNameField);

            JPanel friendPhonePanel = new JPanel(new FlowLayout());
            JLabel friendPhoneLabel = new JLabel("Friend Phone: ");
            JTextField friendPhoneField = new JTextField(12);
            friendPhonePanel.add(friendPhoneLabel);
            friendPhonePanel.add(friendPhoneField);

            JPanel startDatePanel = new JPanel(new FlowLayout());
            JLabel startDateLabel = new JLabel("Start Date: ");
            JTextField startDateField = new JTextField(9);
            startDatePanel.add(startDateLabel);
            startDatePanel.add(startDateField);

            JPanel endDatePanel = new JPanel(new FlowLayout());
            JLabel endDateLabel = new JLabel("End Date: ");
            JTextField endDateField = new JTextField(9);
            endDatePanel.add(endDateLabel);
            endDatePanel.add(endDateField);

            JLabel statusLabel = new JLabel();

            JButton submitBtn = new JButton("Create Lease");
            submitBtn.addActionListener(actionEvent -> {
                // convert dates to SQL format
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-yyyy");
                java.util.Date startDate, endDate;
                java.sql.Date sqlStartDate, sqlEndDate;
                try {
                    startDate = dateFormat.parse(startDateField.getText());
                    sqlStartDate = new java.sql.Date(startDate.getTime());
                } catch (ParseException e) {
                    System.out.println("Couldn't parse startDate!");
                    System.out.println(e);
                    return;
                }
                try {
                    endDate = dateFormat.parse(endDateField.getText());
                    sqlEndDate = new java.sql.Date(endDate.getTime());
                } catch (ParseException e) {
                    System.out.println("Couldn't parse endDate!");
                    System.out.println(e);
                    return;
                }

                // fetch status and monthly_rent
                String status;
                double monthly_rent;
                double rent;
                String selectSQL = "SELECT status, monthly_rent " +
                        "FROM Property " +
                        "WHERE rental_id = '" + leaseIdField.getText() + "'";
                try {
                    Statement stmt = UserInterface.this.connection.createStatement();
                    ResultSet result = stmt.executeQuery(selectSQL);
                    if (result.next()) {
                        status = result.getString("status");
                        monthly_rent = result.getDouble("monthly_rent");
                    } else {
                        System.out.println("No rows returned, couldn't get status and monthly_rent!");
                        return;
                    }
                    if (status.compareToIgnoreCase("rented") == 0) {
                        statusLabel.setText("Property already rented, cannot create lease!");
                        return;
                    }
                    rent = monthly_rent * (endDate.getMonth() - startDate.getMonth());
                } catch (SQLException e) {
                    System.out.println("Couldn't query for status and monthly_rent!");
                    System.out.println(e);
                    return;
                }

                // create a new lease agreement
                String insertSQL = "INSERT INTO LeaseAgreement VALUES (" +
                        leaseIdField.getText() + ", " +
                        rentalIdField.getText() + ", " +
                        renterIdField.getText() + ", " +
                        "'" + friendNameField.getText() + "', " +
                        "'" + friendPhoneField.getText() + "', " +
                        "'" + sqlStartDate.toString() + "', " +
                        "'" + sqlEndDate.toString() + "', " +
                        rent + ", " +
                        monthly_rent + ")";
                try {
                    Statement stmt = UserInterface.this.connection.createStatement();
                    int updates = stmt.executeUpdate(insertSQL);
                    if (updates == 1) {
                        statusLabel.setText("Lease created!");
                    } else {
                        statusLabel.setText("Lease not created!");
                    }
                } catch (SQLException e) {
                    System.out.println("Failed to insert new lease agreement!");
                    System.out.println(e);
                }
            });

            this.add(leaseIdPanel);
            this.add(rentalIdPanel);
            this.add(renterIdPanel);
            this.add(friendNamePanel);
            this.add(friendPhonePanel);
            this.add(startDatePanel);
            this.add(endDatePanel);
            this.add(submitBtn);
            this.add(statusLabel);
        }
    }
           
    private class ProcPanel6 extends JPanel {
        private JScrollPane resultsPane = new JScrollPane();
        private JLabel resultsOwnerLabel = new JLabel("Owner");
        public ProcPanel6() {
            this.setLayout(new BorderLayout());

            // initialize menu
            JPanel menuPanel = new JPanel();
            menuPanel.setLayout(new FlowLayout());
            menuPanel.setPreferredSize(new Dimension(200, 0));
            menuPanel.setBackground(Color.GRAY);
            this.add(menuPanel, BorderLayout.LINE_START);

            JLabel renterLabel = new JLabel("Renter: ");
            renterLabel.setFont(UserInterface.this.fontBtn);

            JTextField renterField = new JTextField(5);
            renterField.setFont(UserInterface.this.fontBtn);

            JButton submitBtn = new JButton("Submit");
            submitBtn.setFont(UserInterface.this.fontBtn);

            menuPanel.add(renterLabel);
            menuPanel.add(renterField);
            menuPanel.add(submitBtn);

            // initialize results area
            JPanel resultsPanel = new JPanel(new BorderLayout());
            JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
            labelPanel.add(resultsOwnerLabel);
            resultsPanel.add(labelPanel, BorderLayout.PAGE_START);
            this.add(resultsPanel, BorderLayout.CENTER);

            submitBtn.addActionListener(actionEvent -> {


                // get owners name
                this.resultsOwnerLabel.setText("Renter ID: " + renterField.getText());


                // get available properties
                try {
                    // send query
                    String sql = "SELECT * " +
                            "FROM LeaseAgreement " +
                            "WHERE renter_id = " + renterField.getText();
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

    private class ProcPanel7 extends JPanel {
        private JScrollPane resultsPane = new JScrollPane();

        public ProcPanel7() {
            // initialize results area

            JPanel resultsPanel = new JPanel(new BorderLayout());

            try {
                // send query
                String sql =  "SELECT renter_id, name " +
                        "    FROM Renters " +
                        "    WHERE renter_id IN ( " +
                        "        SELECT renter_id " +
                        "        FROM LeaseAgreement " +
                        "        GROUP BY renter_id " +
                        "        HAVING COUNT(*) > 1 " +
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

                this.remove(this.resultsPane);
                this.resultsPane = new JScrollPane(new JTable(data, columns));
                this.add(this.resultsPane, BorderLayout.CENTER);
                this.revalidate();
                this.repaint();
            } catch (SQLException e) {
                System.out.println("Could not initialize procPanel[1]!");

                System.out.println(e);
                System.exit(1);
            }
        }
    }

    Double sum = 0.0;
    private class ProcPanel8 extends JPanel {
        private JScrollPane resultsPane = new JScrollPane();

        // extra information to be displayed above the table
        private JLabel resultsOwnerLabel = new JLabel("Owner");
        public ProcPanel8() {
            this.setLayout(new BorderLayout());

            // initialize menu
            JPanel menuPanel = new JPanel();
            menuPanel.setLayout(new FlowLayout());
            menuPanel.setPreferredSize(new Dimension(200, 0));
            menuPanel.setBackground(Color.GRAY);
            this.add(menuPanel, BorderLayout.LINE_START);

            JLabel cityLabel = new JLabel("City: ");
            cityLabel.setFont(UserInterface.this.fontBtn);

            JTextField cityField = new JTextField(5);
            cityField.setFont(UserInterface.this.fontBtn);

            JButton submitBtn = new JButton("Submit");
            submitBtn.setFont(UserInterface.this.fontBtn);

            menuPanel.add(cityLabel);
            menuPanel.add(cityField);
            menuPanel.add(submitBtn);

            // initialize results area
            JPanel resultsPanel = new JPanel(new BorderLayout());
            JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
            JLabel leasedRent = new JLabel();
            JLabel availableRent = new JLabel();
            JLabel rentAverage = new JLabel();
            JLabel city = new JLabel();
            labelPanel.add(city);
            labelPanel.add(leasedRent);
            labelPanel.add(availableRent);
            labelPanel.add(rentAverage);
            resultsPanel.add(labelPanel, BorderLayout.PAGE_START);
            this.add(resultsPanel, BorderLayout.CENTER);

            submitBtn.addActionListener(actionEvent -> {
                city.setText("City: " + cityField.getText());

                // get manager name
                try {
                    String sql = "SELECT AVG(monthly_rent) " +
                            "FROM Property " +
                            "WHERE status = 'leased' " +
                            "AND city = " + "'" + cityField.getText() + "'";
                    Statement stmt = UserInterface.this.connection.createStatement();
                    ResultSet result = stmt.executeQuery(sql);
                    if(result.next()) {
                        leasedRent.setText("Leased Rent: " + result.getDouble(1));
                        sum += result.getDouble(1);
                    }
                    } catch (SQLException e) {
                    System.out.println(e);
                    System.out.println("Couldn't get manager name!");
                }

                // get available properties
                try {
                    // send query
                    String sql = "SELECT AVG(monthly_rent) " +
                            "FROM Property " +
                            "WHERE status = 'available' " +
                            "AND city = " + "'" + cityField.getText() +"'";
                    Statement stmt = UserInterface.this.connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    ResultSet result = stmt.executeQuery(sql);
                    if(result.next()) {
                        availableRent.setText("Available Rent: " + result.getDouble(1));
                        sum += result.getDouble(1);
                    }

                    rentAverage.setText("Average:" + (sum/2));
                    sum = 0.0;
                    // get column names
                    int colCount = result.getMetaData().getColumnCount();
                    String[] columns = new String[colCount];
                    for (int i = 0; i < result.getMetaData().getColumnCount(); i++) {
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


    private class ProcPanel9 extends JPanel {
        private JScrollPane resultsPane = new JScrollPane();

        public ProcPanel9() {
            // initialize results area

            JPanel resultsPanel = new JPanel(new BorderLayout());

            try {
                // send query
                String sql = "SELECT lease_id, Property.rental_id, street, city, zip " +
                        "FROM Property JOIN LeaseAgreement " +
                        "ON Property.rental_id = LeaseAgreement.rental_id " +
                        "WHERE MONTHS_BETWEEN(date_end, SYSDATE) <= 2";
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
                System.out.println("Could not initialize procPanel[1]!");

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
        this.procPanel[2] = new ProcPanel2();
        this.procPanel[3] = new ProcPanel3();
        this.procPanel[4] = new ProcPanel4();
        this.procPanel[6] = new ProcPanel6();
        this.procPanel[7] = new ProcPanel7();
        this.procPanel[8] = new ProcPanel8();
        this.procPanel[9] = new ProcPanel9();
        
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

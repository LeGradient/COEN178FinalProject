import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class UserInterface extends JFrame {

    private Connection connection;  // Oracle database connection object

    public UserInterface() {
        Container container = this.getContentPane();

    }


    public static void main(String[] args) {
        // dynamically load the Oracle DB driver class
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not find DB driver class!");
            return;
        }

        UserInterface ui = new UserInterface();
    }
}

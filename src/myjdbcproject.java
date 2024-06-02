import java.sql.*;
import java.io.*;

public class myjdbcproject {
    public static void main(String args[]) throws IOException {
    	Connection con = null;
        Statement stmt = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {
            // Load the driver class
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Create the connection object
            String conurl = "jdbc:mysql://LAPTOP-DRE9A1O8:3306/id_project";
            con = DriverManager.getConnection(conurl, "biswa", "biswa");
            stmt = con.createStatement();
            int choice_variable = 0;
            do {
                System.out.println("\n\n***** Banking Management System*");
                System.out.println("1. Show Customer Records");
                System.out.println("2. Add Customer Record");
                System.out.println("3. Delete Customer Record");
                System.out.println("4. Update Customer Information");
                System.out.println("5. Show Account Details of a Customer");
                System.out.println("6. Show Loan Details of a Customer");
                System.out.println("7. Deposit Money to an Account");
                System.out.println("8. Withdraw Money from an Account");
                System.out.println("9. Exit the Program");
                System.out.println("Enter your choice(1-9):");
                try {
                    choice_variable = Integer.parseInt(br.readLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input! Please enter a number.");
                    continue;
                }

                switch (choice_variable) {
                   case 1:
                	// Display customer records
                       ResultSet rs = stmt.executeQuery("SELECT * FROM Customer");
                       int rowCount = 0;
                       System.out.println("---------------------------------------------------------------------------------");
                       System.out.printf("| %-15s | %-30s | %-15s | %-9s |%n", "Customer Number", "Name", "Phone Number", "City");
                       System.out.println("---------------------------------------------------------------------------------");
                       while (rs.next()) {
                           rowCount++;
                           System.out.printf("| %-15s | %-30s | %-15s | %-9s |%n",
                                   rs.getString("cust_no"),
                                   rs.getString("name"),
                                   rs.getString("phone_no"),
                                   rs.getString("city"));
                       }
                       System.out.println("---------------------------------------------------------------------------------");
                       System.out.println(rowCount+" rows in set");
                       break;
                    case 2:
                        // Add customer record
                        System.out.println("Enter customer number:");
                        String cust_no = br.readLine();
                        System.out.println("Enter name:");
                        String name = br.readLine();
                        System.out.println("Enter phone number:");
                        String phone_no = br.readLine();
                        System.out.println("Enter city:");
                        String city = br.readLine();
                        stmt.executeUpdate("INSERT INTO Customer VALUES ('" + cust_no + "', '" + name + "', '" + phone_no + "', '" + city + "')");
                        System.out.println("Customer added successfully!");
                        break;
                    case 3:
                        // Delete customer record
                        System.out.println("Enter customer number to delete:");
                        String cust_no_delete = br.readLine();
                        stmt.executeUpdate("DELETE FROM Customer WHERE cust_no = '" + cust_no_delete + "'");
                        System.out.println("Customer record deleted successfully!");
                        break;
                    case 4:
                        // Update customer record
                        System.out.println("Enter customer number to update:");
                        String cust_no_update = br.readLine();
                        System.out.println("Enter attribute to update (1: Name, 2: Phone no, 3: City):");
                        int attribute_choice = Integer.parseInt(br.readLine());
                        String update_query = "";
                        switch (attribute_choice) {
                            case 1:
                                System.out.println("Enter new name:");
                                String new_name = br.readLine();
                                update_query = "UPDATE Customer SET name = '" + new_name + "' WHERE cust_no = '" + cust_no_update + "'";
                                break;
                            case 2:
                                System.out.println("Enter new phone number:");
                                String new_phone_no = br.readLine();
                                update_query = "UPDATE Customer SET phone_no = '" + new_phone_no + "' WHERE cust_no = '" + cust_no_update + "'";
                                break;
                            case 3:
                                System.out.println("Enter new city:");
                                String new_city = br.readLine();
                                update_query = "UPDATE Customer SET city = '" + new_city + "' WHERE cust_no = '" + cust_no_update + "'";
                                break;
                            default:
                                System.out.println("Invalid attribute choice!");
                                break;
                        }
                        if (!update_query.equals("")) {
                            stmt.executeUpdate(update_query);
                            System.out.println("Customer record updated successfully!");
                        }
                        break;
                    case 5:
                    	// Display account details of a customer
                        System.out.println("Enter customer number:");
                        String cust_no_account = br.readLine();
                        String querys = "SELECT a.* FROM DEPOSITOR d INNER JOIN ACCOUNT a ON d.account_no = a.account_no WHERE d.cust_no = ?";
                        PreparedStatement pst = con.prepareStatement(querys);
                        pst.setString(1, cust_no_account);
                        ResultSet rs_account = pst.executeQuery();
                        int rowCountAccount = 0;
                        System.out.println("-------------------------------------------------------");
                        System.out.printf("| %-15s | %-15s | %-10s | %-15s |%n", "Account Number", "Type", "Balance", "Branch Code");
                        System.out.println("-------------------------------------------------------");
                        while (rs_account.next()) {
                            rowCountAccount++;
                            System.out.printf("| %-15s | %-15s | %-10s | %-10s |%n",
                                    rs_account.getString("account_no"),
                                    rs_account.getString("type"),
                                    rs_account.getString("balance"),
                                    rs_account.getString("branch_code"));
                        }
                        System.out.println("-------------------------------------------------------");
                        System.out.println(rowCountAccount+" rows selected");
                        break;
                    case 6:
                        // Display loan details of a customer
                        System.out.println("Enter customer number:");
                        String cust_no_loan = br.readLine();
                        String query = "SELECT l.loan_no, l.amount, l.branch_code, i.inst_no, i.inst_amount " +
                                       "FROM LOAN l LEFT JOIN INSTALLMENT i ON l.loan_no = i.loan_no " +
                                       "WHERE l.cust_no = ?";
                        PreparedStatement ps = con.prepareStatement(query);
                        ps.setString(1, cust_no_loan);
                        ResultSet rs_loan = ps.executeQuery();
                        boolean foundLoan = false;
                        while (rs_loan.next()) {
                            foundLoan = true;
                            System.out.println("Loan Number: " + rs_loan.getString("loan_no"));
                            System.out.println("Loan Amount: " + rs_loan.getString("amount"));
                            System.out.println("Branch Code: " + rs_loan.getString("branch_code"));
                            String inst_no = rs_loan.getString("inst_no");
                            String inst_amount = rs_loan.getString("inst_amount");
                            if (inst_no != null && inst_amount != null) {
                                System.out.println("Installment Number: " + inst_no);
                                System.out.println("Installment Amount: " + inst_amount);
                            } else {
                                System.out.println("No installment details found for this loan.");
                            }
                            System.out.println("---------------------------");
                        }
                        if (!foundLoan) {
                            System.out.println("No loans found for this customer.");
                        }
                        break;
                    case 7:
                        // Deposit money to an account
                        System.out.println("Enter account number:");
                        String account_no_deposit = br.readLine();
                        System.out.println("Enter amount to deposit:");
                        double deposit_amount = Double.parseDouble(br.readLine());
                        stmt.executeUpdate("UPDATE Account SET balance = balance + " + deposit_amount + " WHERE account_no = '" + account_no_deposit + "'");
                        System.out.println("Money deposited successfully!");
                        break;
                    case 8:
                        // Withdraw money from an account
                        System.out.println("Enter account number:");
                        String account_no_withdraw = br.readLine();
                        System.out.println("Enter amount to withdraw:");
                        double withdraw_amount = Double.parseDouble(br.readLine());
                        ResultSet rs_balance = stmt.executeQuery("SELECT balance FROM Account WHERE account_no = '" + account_no_withdraw + "'");
                        if (rs_balance.next()) {
                            double balance = rs_balance.getDouble("balance");
                            if (balance >= withdraw_amount) {
                                stmt.executeUpdate("UPDATE Account SET balance = balance - " + withdraw_amount + " WHERE account_no = '" + account_no_withdraw + "'");
                                System.out.println("Money withdrawn successfully!");
                            } else {
                                System.out.println("Insufficient balance!");
                            }
                        } else {
                            System.out.println("Account not found!");
                        }
                        break;
                    case 9:
                        // Exit the program
                        System.out.println("Exiting program...");
                        break;
                    default:
                        System.out.println("Invalid choice! Please choose between 1 to 9.");
                }
            } while (choice_variable != 9);
        } catch (ClassNotFoundException e) {
            System.out.println("Error: Oracle JDBC driver not found");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error reading input");
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
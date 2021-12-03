package models;

import utils.DBQuery;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Public class for Customer objects used by customers table.
 */
public class Customer {
    private int customerId;
    private String customerName;
    private String address;
    private String postalCode;
    private String phone;
    private int division;

    public Customer(int customerId, String customerName, String address, String postalCode, String phone, int division) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.division = division;
    }

    /**
     * Deletes customer from database.
     * @param selectedCustomer Customer selected by user to be deleted.
     * @return Returns number of customers deletes.
     * @throws SQLException
     */
    public static int deleteCustomer(Customer selectedCustomer) throws SQLException {
        int selectedId = selectedCustomer.customerId;
        return DBQuery.deleteStatement("DELETE FROM customers WHERE Customer_ID = ?", selectedId);
    }

    /**
     * Retrieves division/state related to division ID from database.
     * @return Returns division name.
     * @throws SQLException
     */
    public String getDivisionName() throws SQLException {
        String division = "";
        try {
            ResultSet rs = DBQuery.selectQuery("SELECT Division FROM first_level_divisions WHERE Division_ID = " + this.division);
            while(rs.next()) {
                division = rs.getString("Division");
            }
            return division;
        } catch(SQLException e) {System.out.println("Exception caught in getCountryName: " + e.getMessage());}
        return division;
    }

    /**
     * Retrieves country name from database related to division ID.
     * @return Returns string of country name.
     * @throws SQLException
     */
    public String getCountryName() throws SQLException {
        try {
            int countryId = 0;
            String countryName = "";
            ResultSet idResult = DBQuery.selectQuery("SELECT COUNTRY_ID FROM first_level_divisions WHERE Division_ID = " + this.division);
            while(idResult.next()) {
                countryId = idResult.getInt("COUNTRY_ID");
            }
            ResultSet nameResult = DBQuery.selectQuery("SELECT Country FROM countries WHERE Country_ID = " + countryId);
            while(nameResult.next()) {
                countryName = nameResult.getString("Country");
            }
            return countryName;
        } catch(SQLException e) {System.out.println("Exception caught in getCountryName: " + e.getMessage());}
        return "";
    }

    /**
     * Retrieves name of state/division related to division ID from database.
     * @return Returns string of state/division name.
     * @throws SQLException
     */
    public String getStateName() throws SQLException {
        String stateName = "";
        ResultSet stateResult = DBQuery.selectQuery("SELECT Division FROM first_level_divisions WHERE Division_ID = " + this.division);
        while(stateResult.next()) {
            stateName = stateResult.getString("Division");
        }
        return stateName;
    }

    /**
     * Retrieves customerName string from object.
     * @return Returns customerName string.
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Sets customerName attribute.
     * @param customerName string to set customerName attribute to.
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Retrieves address string from object.
     * @return Returns address string.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets address attribute.
     * @param address string to set address attribute to.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Retrieves customerId integer from object.
     * @return Returns customerId integer.
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * Sets customerId attribute.
     * @param customerId integer to set customerId attribute to.
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    /**
     * Retrieves postalCode string from object.
     * @return Returns postalCode string.
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets postalCode attribute.
     * @param postalCode string to set postalCode attribute to.
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Retrieves phone string from object.
     * @return Returns phone string.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets phone attribute.
     * @param phone string to set phone attribute to.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }
}

package vvs_webapp.dbtest;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.DbSetupTracker;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.Destination;
import com.ninja_squad.dbsetup.destination.DriverManagerDestination;
import com.ninja_squad.dbsetup.operation.Operation;

import webapp.services.ApplicationException;
import webapp.services.CustomerDTO;
import webapp.services.CustomerService;
import webapp.services.CustomersDTO;
import webapp.services.SaleService;
import webapp.services.SalesDTO;

import static vvs_webapp.dbtest.DBSetup.startApplicationDatabaseForTesting;
import static vvs_webapp.dbtest.DBSetup.DB_USERNAME;
import static vvs_webapp.dbtest.DBSetup.DB_PASSWORD;
import static vvs_webapp.dbtest.DBSetup.DB_URL;
import static vvs_webapp.dbtest.DBSetup.INSERT_CUSTOMER_ADDRESS_DATA;
import static vvs_webapp.dbtest.DBSetup.DELETE_ALL;


public class DBCustomerTest{
	
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private static Destination dataSource;

	// the tracker is static because JUnit uses a separate Test instance for every
	// test method.
	private static DbSetupTracker dbSetupTracker = new DbSetupTracker();

	@BeforeClass
	public static void setupClass() {
		startApplicationDatabaseForTesting();
		dataSource = DriverManagerDestination.with(DB_URL, DB_USERNAME, DB_PASSWORD);
	}

	@Before
	public void setup() throws SQLException {

		Operation initDBOperations = Operations.sequenceOf(
			DELETE_ALL
		 , INSERT_CUSTOMER_ADDRESS_DATA
		);
		DbSetup dbSetup = new DbSetup(dataSource, initDBOperations);
		// Use the tracker to launch the DBSetup.
		// This will speed-up tests that do not change the DB. 
		dbSetupTracker.launchIfNecessary(dbSetup);

	}
	
	/*
	 * 
	 * This method tests if the SUT allows to insert a customer with the same VAT
	 * 
	 * 
	 * 
	 */
	@Test
	public void testCustomerSameVAT() throws ApplicationException {
		
		CustomersDTO customersDTO = CustomerService.INSTANCE.getAllCustomers();
		int customerNumber = customersDTO.customers.size();
		 
		CustomerService.INSTANCE.addCustomer(503183504, "FCUL", 217500000);
		assertTrue(hasClient(503183504));
		CustomerService.INSTANCE.addCustomer(503183504, "IST", 217563000);
		assertTrue(hasClient(503183504));
		
		customersDTO = CustomerService.INSTANCE.getAllCustomers();
		
		assertFalse(customersDTO.customers.size()==customerNumber+2);
	}
	
	/*
	 * 
	 * This method tests if the SUT updates the phone number without problems
	 * 
	 * 
	 * 
	 */
	@Test
	public void testCustomerUpdateContact() throws ApplicationException {
		
		
		
		CustomerService.INSTANCE.addCustomer(503183504, "FCUL", 217500000);
		assertTrue(hasClient(503183504));
		CustomerService.INSTANCE.updateCustomerPhone(503183504, 912333002);
		
		int phoneNumber = 0;
		
		CustomersDTO customersDTO = CustomerService.INSTANCE.getAllCustomers();
		for(CustomerDTO customer : customersDTO.customers)
			 if (customer.vat == 503183504)
				 phoneNumber=customer.phoneNumber;
		
		assertEquals(phoneNumber,912333002);
		
	}
	
	
	/*
	 * 
	 * This method tests if you delete all customers the list of customers will be empty
	 * 
	 * 
	 * 
	 */
	@Test
	public void testDeleteAllCustomers() throws ApplicationException {
		
		CustomerService.INSTANCE.addCustomer(503183504, "FCUL", 217500000);
		
		CustomersDTO customersDTO = CustomerService.INSTANCE.getAllCustomers();
		
		
		for(CustomerDTO customer : customersDTO.customers)
			CustomerService.INSTANCE.removeCustomer(customer.vat);
		
		assertEquals(CustomerService.INSTANCE.getAllCustomers().customers.size(),0);
		
	}
	
	
	/*
	 * 
	 * This method tests if you delete a customer and add it again raises no Exceptions
	 * 
	 * 
	 * 
	 */
	@Test
	public void testAddCustomerAgain() throws ApplicationException {
		
		int vat = 197672337;
		
		CustomerDTO customer = CustomerService.INSTANCE.getCustomerByVat(vat);
		assertNotNull(customer);
		
		CustomerService.INSTANCE.removeCustomer(vat);
		CustomerService.INSTANCE.addCustomer(customer.vat, customer.designation, customer.phoneNumber);

		CustomerDTO sameCustomer = CustomerService.INSTANCE.getCustomerByVat(customer.vat);
		assertNotNull(sameCustomer);
		
	}
	
	/*
	 * 
	 * This method tests if you delete a customer its sales will be removed as well
	 * 
	 * @throws ApplicationException
	 * 
	 */
	@Test
	public void testRemoveCustomerSales() throws ApplicationException {
		
		int vat = 197672337;
		
		CustomerDTO customer = CustomerService.INSTANCE.getCustomerByVat(vat);
		assertNotNull(customer);
		
		SaleService.INSTANCE.addSale(vat);
		
		CustomerService.INSTANCE.removeCustomer(vat);
		
		SalesDTO sales = SaleService.INSTANCE.getSaleByCustomerVat(vat);
		
		assertEquals(sales.sales.size(),0);
		
	}
	
	/*
	 * 
	 * This method tests if you add a sale, the total number of sales increases by one
	 * 
	 * @throws ApplicationException
	 * 
	 */
	@Test
	public void testAddingCustomerSales() throws ApplicationException {
		
		int numberOfSales = SaleService.INSTANCE.getAllSales().sales.size();
		
		int vat = 197672337;
		
		CustomerDTO customer = CustomerService.INSTANCE.getCustomerByVat(vat);
		assertNotNull(customer);
		
		SaleService.INSTANCE.addSale(vat);
		
		
		assertEquals(numberOfSales+1,SaleService.INSTANCE.getAllSales().sales.size());
		
	}
	
	
	/*
	 * This method tests if you can add a sale delivery several times to the same sale
	 * 
	 * Expected: Not being able to add a delivery to the same sale
	 * 
	 * Actual: You can add deliveries to the same sale.
	 * 
	 */
	@Test
	public void testRemovingSaleAndSaleDelivery() throws ApplicationException {
		
		int numberOfSales = SaleService.INSTANCE.getAllSales().sales.size();
		
		int vat = 274658933;
		
		CustomerService.INSTANCE.addCustomer(vat, "Frederico", 912002333);	
		CustomerDTO customer = CustomerService.INSTANCE.getCustomerByVat(vat);
		assertNotNull(customer);
		
		CustomerService.INSTANCE.addAddressToCustomer(vat, "Rua dos Arneiros");
		int addressID = CustomerService.INSTANCE.getAllAddresses(vat).addrs.get(0).id;
		
		SaleService.INSTANCE.addSale(vat);
		SalesDTO sales = SaleService.INSTANCE.getSaleByCustomerVat(vat);
		assertNotNull(sales.sales.get(0));
		int id = sales.sales.get(0).id;
		
		int before = SaleService.INSTANCE.getSalesDeliveryByVat(vat).sales_delivery.size();
		
		SaleService.INSTANCE.addSaleDelivery(id, addressID);
		SaleService.INSTANCE.addSaleDelivery(id, addressID);
		
		int after = SaleService.INSTANCE.getSalesDeliveryByVat(vat).sales_delivery.size();
		
		assertEquals(before+1,after);
		
	}
	
	
	
	
	private boolean hasClient(int vat) throws ApplicationException {
		 CustomersDTO customersDTO = CustomerService.INSTANCE.getAllCustomers();
		 for(CustomerDTO customer : customersDTO.customers)
			 if (customer.vat == vat)
				 return true;
		 return false;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
}
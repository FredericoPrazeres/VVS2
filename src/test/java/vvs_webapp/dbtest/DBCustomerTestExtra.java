package vvs_webapp.dbtest;


import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.sql.SQLException;

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
import webapp.services.SaleService;
import webapp.services.SalesDTO;

import static vvs_webapp.dbtest.DBSetup.startApplicationDatabaseForTesting;
import static vvs_webapp.dbtest.DBSetup.DB_USERNAME;
import static vvs_webapp.dbtest.DBSetup.DB_PASSWORD;
import static vvs_webapp.dbtest.DBSetup.DB_URL;
import static vvs_webapp.dbtest.DBSetup.INSERT_CUSTOMER_ADDRESS_DATA;
import static vvs_webapp.dbtest.DBSetup.DELETE_ALL;


public class DBCustomerTestExtra{
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private static Destination dataSource;

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
	
	
	/**
	 * 
	 * This method tests if you can add a sale to a non-existent client
	 * 
	 * Expected: This should throw an error, you should not be able to add a sale 
	 * with a non-existent client
	 * 
	 * Actual: You can add the sale
	 * 
	 * 
	 * @throws ApplicationException
	 * @author Frederico Prazeres fc56269
	 * 
	 */
	@Test
	public void testAddSaleToNonExistentClient() throws ApplicationException {
		
		int vat = 207527768;
		

		SaleService.INSTANCE.addSale(vat);
		SalesDTO sales = SaleService.INSTANCE.getSaleByCustomerVat(vat);
		assertNull(sales.sales.get(0));
	}
	
	
	
	
	/**
	 * 
	 * This method tests if you can add a sale delivery several times to the same sale
	 * 
	 * Expected: Not being able to add a delivery to the same sale
	 * 
	 * Actual: You can add several deliveries to the same sale.
	 * 
	 * @throws ApplicationException
	 * @author Frederico Prazeres fc56269
	 * 
	 */
	@Test
	public void testaddingSeveralSaleDeliveries() throws ApplicationException {
		
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
	
	/**
	 * 
	 * This method tests if you can add a sale delivery with an invalid address ID
	 * 
	 * Expected: This should throw an error, you should not be able to add a sale delivery with
	 * an address that doesn't exist
	 * 
	 * Actual: One can add a delivery with an invalid address
	 * 
	 * 
	 * @throws ApplicationException
	 * @author Frederico Prazeres fc56269
	 * 
	 */
	@Test
	public void testAddingSaleDeliveryInvalidAddress() throws ApplicationException {
		
		int vat = 274658933;
		
		CustomerService.INSTANCE.addCustomer(vat, "Frederico", 912002333);	
		CustomerDTO customer = CustomerService.INSTANCE.getCustomerByVat(vat);
		assertNotNull(customer);
		
		SaleService.INSTANCE.addSale(vat);
		SalesDTO sales = SaleService.INSTANCE.getSaleByCustomerVat(vat);
		assertNotNull(sales.sales.get(0));
		int id = sales.sales.get(0).id;
		
		SaleService.INSTANCE.addSaleDelivery(id, 533);
		int numberOfSaleDeliveries = SaleService.INSTANCE.getSalesDeliveryByVat(vat).sales_delivery.size();
		
		assertEquals(0,numberOfSaleDeliveries);
		
	}
	
	
	/**
	 * 
	 * This method tests if you can add a sale delivery to a sale that is flawed
	 * 
	 * Expected: One should not be able to place a delivery in a sale that does not belong to 
	 * a client in the database
	 * 
	 * Actual: One can add a delivery to this sale
	 * 
	 * 
	 * @throws ApplicationException
	 * @author Frederico Prazeres fc56269
	 * 
	 */
	@Test
	public void testAddingDeliveryToFlawedSale() throws ApplicationException {
		
		int vat = 22;
		
		CustomerService.INSTANCE.addCustomer(vat, "Frederico", 912002333);	
		CustomerDTO customer = CustomerService.INSTANCE.getCustomerByVat(vat);
		assertNotNull(customer);
		CustomerService.INSTANCE.addAddressToCustomer(vat, "Rua dos Arneiros");
		
		int addressId = CustomerService.INSTANCE
				.getAllAddresses(vat).addrs.get(CustomerService.INSTANCE.getAllAddresses(vat).addrs.size()-1).id;
		
		int numberOfSalesBefore = SaleService.INSTANCE.getAllSales().sales.size();
		SaleService.INSTANCE.addSale(vat); // ADDED SALE WITH INVALID VAT
		int numberOfSalesAfter = SaleService.INSTANCE.getAllSales().sales.size();
		assertEquals(numberOfSalesBefore+1,numberOfSalesAfter); // FLAWED SALE WAS ADDED
		
		SalesDTO sales = SaleService.INSTANCE.getAllSales();
		int id = sales.sales.get(sales.sales.size()-1).id; // ID da sale
		
		
		SaleService.INSTANCE.addSaleDelivery(id, addressId);

		int numberOfSaleDeliveries = SaleService.INSTANCE.getSalesDeliveryByVat(vat).sales_delivery.size();
		
		assertEquals(0,numberOfSaleDeliveries);
		
	}
	
	
	/**
	 * 
	 * This method tests if you can add a sale delivery to a sale that is closed
	 * 
	 * Expected: One should not be able to place a delivery in a sale that is closed 
	 * 
	 * Actual: One can add a delivery to this sale
	 * 
	 * 
	 * @throws ApplicationException
	 * @author Frederico Prazeres fc56269
	 * 
	 */
	@Test
	public void testAddingDeliveryToClosedSale() throws ApplicationException {
		
		
		int vat = 274658933;
		
		CustomerService.INSTANCE.addCustomer(vat, "Frederico", 912002333);	
		CustomerDTO customer = CustomerService.INSTANCE.getCustomerByVat(vat);
		assertNotNull(customer);
		
		CustomerService.INSTANCE.addAddressToCustomer(vat, "Rua dos Arneiros");
		
		int addressId = CustomerService.INSTANCE
				.getAllAddresses(vat).addrs.get(CustomerService.INSTANCE.getAllAddresses(vat).addrs.size()-1).id;
		
		
		SaleService.INSTANCE.addSale(vat);
		SalesDTO sales = SaleService.INSTANCE.getSaleByCustomerVat(vat);
		int id = sales.sales.get(sales.sales.size()-1).id; // ID da sale
		
		SaleService.INSTANCE.updateSale(id); // Fechar a sale
		
		SaleService.INSTANCE.addSaleDelivery(id, addressId);
		
		assertEquals(0,SaleService.INSTANCE.getSalesDeliveryByVat(vat).sales_delivery.size()); // Verificar se não foi adicionada delivery
		
		
	}
	
	
	
	
	
	
}
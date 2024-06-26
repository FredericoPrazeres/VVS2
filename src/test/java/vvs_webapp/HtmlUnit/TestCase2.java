package vvs_webapp.HtmlUnit;

import static org.junit.Assert.*;
import static vvs_webapp.dbtest.DBSetup.DB_PASSWORD;
import static vvs_webapp.dbtest.DBSetup.DB_URL;
import static vvs_webapp.dbtest.DBSetup.DB_USERNAME;
import static vvs_webapp.dbtest.DBSetup.DELETE_ALL;
import static vvs_webapp.dbtest.DBSetup.INSERT_CUSTOMER_ADDRESS_DATA;
import static vvs_webapp.dbtest.DBSetup.startApplicationDatabaseForTesting;

import org.junit.*;
import org.junit.rules.ExpectedException;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.DbSetupTracker;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.Destination;
import com.ninja_squad.dbsetup.destination.DriverManagerDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;

import java.io.*;
import java.sql.SQLException;


public class TestCase2{
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private static Destination dataSource;
	
	private static DbSetupTracker dbSetupTracker = new DbSetupTracker();
	
	private static final String APPLICATION_URL = "http://localhost:8080/VVS_webappdemo/";

	private static WebClient webClient;
	private static HtmlPage page;
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		webClient = new WebClient(BrowserVersion.getDefault());
		
		// possible configurations needed to prevent JUnit tests to fail for complex HTML pages
        webClient.setJavaScriptTimeout(15000);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        
		page = webClient.getPage(APPLICATION_URL);
		assertEquals(200, page.getWebResponse().getStatusCode()); // OK status
		
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

		dbSetupTracker.launchIfNecessary(dbSetup);

	}
	
	/**
	 * 
	 * This test checks if you add two addresses to a customer, the
	 * address table will only increasy by two
	 * 
	 * @throws IOException
	 * @author Frederico Prazeres fc56269
	 * 
	 */
	@Test
	public void testInsertingAddress() throws IOException {
		
		String VAT = "197672337";
		
		HtmlAnchor getCustomerLink = page.getAnchorByHref("getCustomerByVAT.html");
		HtmlPage nextPage = (HtmlPage) getCustomerLink.openLinkInNewWindow();
	
		HtmlForm getCustomerForm = nextPage.getForms().get(0);
		HtmlInput vatInput2 = getCustomerForm.getInputByName("vat");
		HtmlInput submit2 = getCustomerForm.getInputByName("submit");
		
		vatInput2.setValueAttribute(VAT);
		HtmlPage newPage = submit2.click();
		
		//Foi necessário inserir este table1 id
		int countNumberOfAddressesBefore = 0;
		if( newPage.getElementById("table1") != null) { // Tabela existe
			HtmlTable table = newPage.getHtmlElementById("table1");
			countNumberOfAddressesBefore = table.getRows().size();
		}
		
		// get a specific link
		HtmlAnchor addCustomerLink = page.getAnchorByHref("addAddressToCustomer.html");
		// click on it
		nextPage = (HtmlPage) addCustomerLink.openLinkInNewWindow();

		// get the page first form:
		HtmlForm addAddresForm = nextPage.getForms().get(0);		
		HtmlInput vatInput = addAddresForm.getInputByName("vat");
		HtmlInput addressInput = addAddresForm.getInputByName("address");
		HtmlInput doorInput = addAddresForm.getInputByName("door");
		HtmlInput postalCodeInput = addAddresForm.getInputByName("postalCode");
		HtmlInput localityInput = addAddresForm.getInputByName("locality");
		HtmlInput submit = addAddresForm.getInputByValue("Insert");
		
		vatInput.setValueAttribute(VAT);
		addressInput.setValueAttribute("Avenida da Liberdade");
		doorInput.setValueAttribute("3");
		postalCodeInput.setValueAttribute("1600");
		localityInput.setValueAttribute("Lisboa");
		submit.click();
		
		addCustomerLink = page.getAnchorByHref("addAddressToCustomer.html");
		
		addAddresForm = nextPage.getForms().get(0);		
		vatInput = addAddresForm.getInputByName("vat");
		addressInput = addAddresForm.getInputByName("address");
		doorInput = addAddresForm.getInputByName("door");
		postalCodeInput = addAddresForm.getInputByName("postalCode");
		localityInput = addAddresForm.getInputByName("locality");
		submit = addAddresForm.getInputByValue("Insert");
		
		vatInput.setValueAttribute(VAT);
		addressInput.setValueAttribute("Avenida do Brasil");
		doorInput.setValueAttribute("33");
		postalCodeInput.setValueAttribute("1600");
		localityInput.setValueAttribute("Lisboa");
		submit.click();
		
		getCustomerLink = page.getAnchorByHref("getCustomerByVAT.html");
		nextPage = (HtmlPage) getCustomerLink.openLinkInNewWindow();
	
		getCustomerForm = nextPage.getForms().get(0);
		vatInput2 = getCustomerForm.getInputByName("vat");
		submit2 = getCustomerForm.getInputByName("submit");
		
		vatInput2.setValueAttribute(VAT);
		newPage = submit2.click();
		
		
		int countNumberOfAddressesAfter = 0;
		HtmlTable table2 = newPage.getHtmlElementById("table1");
		if(table2!=null)
			countNumberOfAddressesAfter=table2.getRows().size();
		
		
		
		if(countNumberOfAddressesBefore==0)
			assertEquals(countNumberOfAddressesAfter,2);
		else
			assertEquals(countNumberOfAddressesBefore+2,countNumberOfAddressesAfter);
		
		
		
		//First row also counts
		
		
		
	}
	
	/**
	 * 
	 * This test checks if you add two customers, their information will be displayed
	 * accordingly in the "List All Customers Page"
	 * 
	 * @throws IOException
	 * @author Frederico Prazeres fc56269
	 * 
	 */
	@Test
	public void testInsertingTwoCustomers() throws IOException{
		
		HtmlAnchor getCustomerLink = page.getAnchorByHref("addCustomer.html");
		HtmlPage nextPage = (HtmlPage) getCustomerLink.openLinkInNewWindow();
	
		HtmlForm getCustomerForm = nextPage.getForms().get(0);
		HtmlInput vatInput = getCustomerForm.getInputByName("vat");
		HtmlInput designation = getCustomerForm.getInputByName("designation");
		HtmlInput phone = getCustomerForm.getInputByName("phone");
		HtmlInput submit = getCustomerForm.getInputByName("submit");
		
		vatInput.setValueAttribute("274658933");
		designation.setValueAttribute("Fred");
		phone.setValueAttribute("910203443");
		submit.click();
		
		getCustomerLink = page.getAnchorByHref("addCustomer.html");
		nextPage = (HtmlPage) getCustomerLink.openLinkInNewWindow();
	
		getCustomerForm = nextPage.getForms().get(0);
		vatInput = getCustomerForm.getInputByName("vat");
		designation = getCustomerForm.getInputByName("designation");
		phone = getCustomerForm.getInputByName("phone");
		submit = getCustomerForm.getInputByName("submit");
		
		vatInput.setValueAttribute("207527768");
		designation.setValueAttribute("Joao");
		phone.setValueAttribute("910208443");
		submit.click();
		
		HtmlAnchor getCustomersLink = page.getAnchorByHref("GetAllCustomersPageController");
		nextPage = (HtmlPage) getCustomersLink.openLinkInNewWindow();
		
		HtmlTable table = nextPage.getHtmlElementById("clients");
		
		if(table!=null) {
			
			assertEquals(table.getCellAt(0,0).asText(),"Name");
			assertEquals(table.getCellAt(0,1).asText(),"Phone");
			assertEquals(table.getCellAt(0,2).asText(),"Vat");
			
			assertEquals(table.getCellAt(table.getRows().size()-2,0).asText(),"Fred");
			assertEquals(table.getCellAt(table.getRows().size()-2,1).asText(),"910203443");
			assertEquals(table.getCellAt(table.getRows().size()-2,2).asText(),"274658933");
			
			assertEquals(table.getCellAt(table.getRows().size()-1,0).asText(),"Joao");
			assertEquals(table.getCellAt(table.getRows().size()-1,1).asText(),"910208443");
			assertEquals(table.getCellAt(table.getRows().size()-1,2).asText(),"207527768");
			
		}
		
		
	}
	
	
	/**
	 * 
	 * This test checks if a new sale is added, it will be displayed as open
	 * 
	 * @throws IOException
	 * @author Frederico Prazeres fc56269
	 * 
	 */
	@Test
	public void testNewSale() throws IOException {
		
		String VAT = "168027852";
		
		/* Ver quantas sales existem do cliente */
		
		HtmlAnchor getCustomerLink1 = page.getAnchorByHref("getSales.html");		
		HtmlPage nextPage1 = (HtmlPage) getCustomerLink1.openLinkInNewWindow();
		
		HtmlForm getCustomerForm1 = nextPage1.getForms().get(0);
		HtmlInput vatInput1 = getCustomerForm1.getInputByName("customerVat");
		HtmlInput submit1 = getCustomerForm1.getInputByValue("Get Sales");
		
		vatInput1.setValueAttribute(VAT);
		HtmlPage salesPage1 = (HtmlPage) submit1.click();
		
		// Numero de sales antes da inserção da nova sale
		int saleCountBefore = 0;
		if( salesPage1.getElementById("table1") != null) { // Tabela existe
			HtmlTable table = salesPage1.getHtmlElementById("table1");
			
			saleCountBefore = table.getRows().size();
		
		}
		
		/* Inserir sale no cliente */
		
		HtmlAnchor getCustomerLink2 = page.getAnchorByHref("addSale.html");
		HtmlPage nextPage2 = (HtmlPage) getCustomerLink2.openLinkInNewWindow();
		
		HtmlForm getCustomerForm2 = nextPage2.getForms().get(0);
		HtmlInput vatInput2 = getCustomerForm2.getInputByName("customerVat");
		HtmlInput submit2 = getCustomerForm2.getInputByValue("Add Sale");
		
		vatInput2.setValueAttribute(VAT);
		submit2.click();
		
		/* Verificar que ela está na tabela */
		
		HtmlAnchor getCustomerLink3= page.getAnchorByHref("getSales.html");		
		HtmlPage nextPage3 = (HtmlPage) getCustomerLink3.openLinkInNewWindow();
		
		HtmlForm getCustomerForm3 = nextPage3.getForms().get(0);
		HtmlInput vatInput3 = getCustomerForm3.getInputByName("customerVat");
		HtmlInput submit3 = getCustomerForm3.getInputByValue("Get Sales");
		
		vatInput3.setValueAttribute(VAT);
		HtmlPage salesPage2 = (HtmlPage) submit3.click();
		
		int saleCountAfter = 0;
		
		if( salesPage2.getElementById("table1") != null) { // Tabela existe
			HtmlTable table = (HtmlTable) salesPage2.getElementById("table1");
			saleCountAfter = table.getRows().size()-1;
			
			//Verifica que a última sale adicionada está Open e pertence ao cliente
			assertEquals(table.getCellAt(table.getRows().size()-1,3).asText(),"O");
			assertEquals(table.getCellAt(table.getRows().size()-1,4).asText(),VAT);
		}
		

		assertEquals(saleCountBefore+1,saleCountAfter);
		
		
	}
	
	
	/**
	 * 
	 * This test checks if a sale is closed, the status appears as closed (C)
	 * 
	 * @throws IOException
	 * @author Frederico Prazeres fc56269
	 * 
	 */
	@Test
	public void testClosedSale() throws IOException {
		
		//ADICIONAR SALE
		
		String VAT = "197672337";
		
		HtmlAnchor getCustomerLink = page.getAnchorByHref("addSale.html");
		HtmlPage nextPage = (HtmlPage) getCustomerLink.openLinkInNewWindow();
		
		HtmlForm getCustomerForm = nextPage.getForms().get(0);
		HtmlInput vatInput = getCustomerForm.getInputByName("customerVat");
		HtmlInput submit = getCustomerForm.getInputByValue("Add Sale");
		
		vatInput.setValueAttribute(VAT);
		submit.click();
		
		// CLOSE SALE 
		
		HtmlAnchor getCustomerLink2 = page.getAnchorByHref("UpdateSaleStatusPageControler");
		HtmlPage nextPage2 = (HtmlPage) getCustomerLink2.openLinkInNewWindow();
		
		
		HtmlTable table = nextPage2.getHtmlElementById("table1");
		String id = "";
		if( table != null) { // Tabela existe
			int tableSize = table.getRows().size();
			id = table.getCellAt(tableSize-1, 0).asText();
		}
		
		
		// CHECK SALE CLOSED
		
		HtmlForm getSaleCloseForm = nextPage2.getForms().get(0);
		HtmlInput idInput = getSaleCloseForm.getInputByName("id");
		HtmlInput submit3 = getSaleCloseForm.getInputByValue("Close Sale");
		
		idInput.setValueAttribute(id);
		submit3.click();
		
		HtmlAnchor getCustomerLink3 = page.getAnchorByHref("UpdateSaleStatusPageControler");
		HtmlPage nextPage3 = (HtmlPage) getCustomerLink3.openLinkInNewWindow();
		
		nextPage3.refresh();
		
		String status = "";
		table = nextPage3.getHtmlElementById("table1");
		if( table != null) { // Tabela existe
			
			int tableSize = table.getRows().size();
			status = table.getCellAt(tableSize-1, 3).asText();
			
		}
		
		assertEquals(status, "C");
	
	}
	
	
	
	
	/**
	 * 
	 * This test checks if you add a customer, sale and delivery
	 * every data is shown as expected
	 * 
	 * @throws IOException
	 * @author Frederico Prazeres fc56269
	 * 
	 */
	@Test
	public void testSaleDelivery() throws IOException{
		
		String VAT = "274658933";
		
		// ------------------------ INSERT CUSTOMER ------------------------ //
		
		HtmlAnchor getCustomerLink = page.getAnchorByHref("addCustomer.html");
		HtmlPage nextPage = (HtmlPage) getCustomerLink.openLinkInNewWindow();
		
		
		HtmlForm getCustomerForm = nextPage.getForms().get(0);
		HtmlInput vatInput = getCustomerForm.getInputByName("vat");
		HtmlInput designation = getCustomerForm.getInputByName("designation");
		HtmlInput phone = getCustomerForm.getInputByName("phone");
		HtmlInput submit = getCustomerForm.getInputByName("submit");
		
		vatInput.setValueAttribute("274658933");
		designation.setValueAttribute("Fred");
		phone.setValueAttribute("910203443");
		
		submit.click();
		
		// SHOW CUSTOMER
		
		HtmlAnchor getCustomersLink = page.getAnchorByHref("GetAllCustomersPageController");
		HtmlPage customerPage = (HtmlPage) getCustomersLink.openLinkInNewWindow();
		
		
		if( customerPage.getElementById("clients") != null) { // Tabela existe
			
			HtmlTable table = customerPage.getHtmlElementById("clients");
			
			int tableSize = table.getRows().size();
			
			HtmlTableCell nameCell= table.getCellAt(tableSize-1, 0);
			HtmlTableCell phoneCell= table.getCellAt(tableSize-1, 1);
			HtmlTableCell vatCell = table.getCellAt(tableSize-1, 2);	
			
			assertEquals("Fred", nameCell.asText() );
			assertEquals("910203443",phoneCell.asText() );
			assertEquals("274658933", vatCell.asText());
		}
		
		// ------------------------ INSERT CUSTOMER ------------------------ //
		
		// ------------------------ INSERT SALE ------------------------ //
		
		HtmlAnchor getSaleLink = page.getAnchorByHref("addSale.html");
		HtmlPage salePage = (HtmlPage) getSaleLink.openLinkInNewWindow();
		
		HtmlForm getSaleForm = salePage.getForms().get(0);
		vatInput = getSaleForm.getInputByName("customerVat");
		submit = getSaleForm.getInputByValue("Add Sale");
		
		vatInput.setValueAttribute(VAT);
		submit.click();
		
		// SHOW SALE
		
		getSaleLink = page.getAnchorByHref("getSales.html");		
		HtmlPage listSales = (HtmlPage) getSaleLink.openLinkInNewWindow();
		
		HtmlForm getSaleForm2 = listSales.getForms().get(0);
		vatInput = getSaleForm2.getInputByName("customerVat");
		submit = getSaleForm2.getInputByValue("Get Sales");
		
		vatInput.setValueAttribute(VAT);
		HtmlPage salesPage2 = (HtmlPage) submit.click();
		
		String saleId = "";
		
		if( salesPage2.getElementById("table1") != null) { // Tabela existe
			HtmlTable table = (HtmlTable) salesPage2.getElementById("table1");
			
			//Verifica que a última sale adicionada está Open e pertence ao cliente
			assertEquals("O",table.getCellAt(table.getRows().size()-1,3).asText());
			assertEquals(VAT, table.getCellAt(table.getRows().size()-1,4).asText());
			
			saleId = table.getCellAt(table.getRowCount()-1, 0).asText();
		}
		
		// ------------------------ INSERT SALE ------------------------ //
		
		// ------------------------ INSERT DELIVERY ------------------------ //
		
		HtmlAnchor getDeliveryLink = page.getAnchorByHref("saleDeliveryVat.html");
		HtmlPage saleDelivery = (HtmlPage) getDeliveryLink.openLinkInNewWindow();
		
		HtmlForm getDeliveryForm = saleDelivery.getForms().get(0);
		vatInput = getDeliveryForm.getInputByName("vat");
		submit = getDeliveryForm.getInputByValue("Get Customer");
		vatInput.setValueAttribute(VAT);
		
		HtmlPage saleDelivery2 = submit.click();
		HtmlForm saleDeliveryForm = saleDelivery2.getForms().get(0);
		
		HtmlInput addressInput = saleDeliveryForm.getInputByName("addr_id");
		HtmlInput saleIdInput = saleDeliveryForm.getInputByName("sale_id");
		submit = saleDeliveryForm.getInputByValue("Insert");
		
		addressInput.setValueAttribute("100");
		saleIdInput.setValueAttribute(saleId);
		
		// SHOW DELIVERY
		
		HtmlPage saleDeliveriesPage = submit.click();
		
		if( saleDeliveriesPage.getElementById("table1") != null) { // Tabela existe
			HtmlTable table = (HtmlTable) saleDeliveriesPage.getElementById("table1");
			
			//Verifica que a delivery foi adicionada
			assertEquals(saleId, table.getCellAt(table.getRows().size()-1,1).asText());
			assertEquals("100", table.getCellAt(table.getRows().size()-1,2).asText());
			
		}
		
		
		// ------------------------ INSERT DELIVERY ------------------------ //
		
		
		
	}
	
	
	
	
}
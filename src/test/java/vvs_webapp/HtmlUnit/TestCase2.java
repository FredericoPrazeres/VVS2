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
	
	// the tracker is static because JUnit uses a separate Test instance for every
	// test method.
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
		// Use the tracker to launch the DBSetup.
		// This will speed-up tests that do not change the DB. 
		dbSetupTracker.launchIfNecessary(dbSetup);

	}
	
	/*
	 * 
	 * This test checks if you add two addresses for a given, the
	 * address table will increasy by one
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
		int count = 0;
		if( newPage.getElementById("table1") != null) { // Tabela existe
			HtmlTable table = newPage.getHtmlElementById("table1");
			count = table.getRows().size();
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
		
		
		
		int count2 = 0;
		if(count==0)
			count2=-1; /* Se count for 0 a tabela esta vazia. 
						  Tira-se uma unidade a count2 
						  porque a tabela tem sempre +1 linha (descricao dos elementos)
			 			*/
		
		HtmlTable table2 = newPage.getHtmlElementById("table1");
		count2=table2.getRows().size();
		
		
		//First row also counts
		assertEquals(count+2,count2);
		
		
	}
	
	/*
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
		phone.setValueAttribute("910242523");
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
			assertEquals(table.getCellAt(table.getRows().size()-1,1).asText(),"910242523");
			assertEquals(table.getCellAt(table.getRows().size()-1,2).asText(),"207527768");
			
		}
		
		
	}
	
	
	/*
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
		
		
		int count = 0;
		if( salesPage1.getElementById("table1") != null) { // Tabela existe
			HtmlTable table = salesPage1.getHtmlElementById("table1");
			
			count = table.getRows().size();
		
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
		
		
		int count2 = 0;
		
		if( salesPage2.getElementById("table1") != null) { // Tabela existe
			HtmlTable table = (HtmlTable) salesPage2.getElementById("table1");
			count2 = table .getRows().size();
			
		}
		
		if(count==0) {
			assertNotEquals(0,count2);
		}else {
			assertEquals(count+1,count2);
		}
		
	}
	
	
	/*
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
		
		
		String id = "";
		if( nextPage2.getElementById("table1") != null) { // Tabela existe
			
			HtmlTable table = nextPage2.getHtmlElementById("table1");
			
			int tableSize = table.getRows().size();
			
			HtmlTableCell idCell= table.getCellAt(tableSize-1, 0);
			id = idCell.asText();
		
		}
		
		
		// CHECK SALE CLOSED
		
		nextPage2.refresh();
		nextPage2.refresh();
	
		
		
		HtmlForm getSaleCloseForm = nextPage2.getForms().get(0);
		HtmlInput idInput = getSaleCloseForm.getInputByName("id");
		HtmlInput submit3 = getSaleCloseForm.getInputByValue("Close Sale");
		
		idInput.setValueAttribute(id);
		submit3.click();
		
		HtmlAnchor getCustomerLink3 = page.getAnchorByHref("UpdateSaleStatusPageControler");
		HtmlPage nextPage3 = (HtmlPage) getCustomerLink3.openLinkInNewWindow();
		
		nextPage3.refresh();
		
		String status = "";
		if( nextPage3.getElementById("table1") != null) { // Tabela existe
			
			HtmlTable table = nextPage3.getHtmlElementById("table1");
			
			int tableSize = table.getRows().size();
			System.out.println(tableSize);
			HtmlTableCell statusCell= table.getCellAt(tableSize-1, 3);
			status = statusCell.asText();
		
			System.out.println(table.getRow(tableSize-1).asText());
			
		}
		
		assertEquals(status, "C");
	
	}
	/*
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
		
		HtmlAnchor getCustomersLink = page.getAnchorByHref("GetAllCustomersPageController");
		nextPage = (HtmlPage) getCustomersLink.openLinkInNewWindow();
		
		
		if( nextPage.getElementById("clients") != null) { // Tabela existe
			
			HtmlTable table = nextPage.getHtmlElementById("clients");
			
			int tableSize = table.getRows().size();
			
			HtmlTableCell nameCell= table.getCellAt(tableSize-1, 0);
			HtmlTableCell phoneCell= table.getCellAt(tableSize-1, 1);
			HtmlTableCell vatCell = table.getCellAt(tableSize-1, 2);	
			
			assertEquals(nameCell.asText(), "Fred");
			assertEquals(phoneCell.asText(), "910203443");
			assertEquals(vatCell.asText(), "274658933");
		}
		
		// ------------------------ INSERT CUSTOMER ------------------------ //
		
		// ------------------------ INSERT ADDRESS ------------------------ //
		HtmlAnchor addCustomerLink = page.getAnchorByHref("addAddressToCustomer.html");
		// click on it
		nextPage = (HtmlPage) addCustomerLink.openLinkInNewWindow();

		// get the page first form:
		HtmlForm addAddresForm = nextPage.getForms().get(0);		
		vatInput = addAddresForm.getInputByName("vat");
		HtmlInput addressInput = addAddresForm.getInputByName("address");
		HtmlInput doorInput = addAddresForm.getInputByName("door");
		HtmlInput postalCodeInput = addAddresForm.getInputByName("postalCode");
		HtmlInput localityInput = addAddresForm.getInputByName("locality");
		submit = addAddresForm.getInputByValue("Insert");
		
		vatInput.setValueAttribute(VAT);
		addressInput.setValueAttribute("Avenida da Liberdade");
		doorInput.setValueAttribute("3");
		postalCodeInput.setValueAttribute("1600");
		localityInput.setValueAttribute("Lisboa");
		submit.click();
		
		// ------------------------ INSERT ADDRESS ------------------------ //
		
		// ------------------------ INSERT SALE ------------------------ //
		
		HtmlAnchor getSaleLink3 = page.getAnchorByHref("getSales.html");
		HtmlPage salePage3 = (HtmlPage) getSaleLink3.openLinkInNewWindow();
		HtmlForm salePageForm3 = salePage3.getForms().get(0);
		
		vatInput = salePageForm3.getInputByName("customerVat");
		submit = salePageForm3.getInputByValue("Get Sales");
		
		vatInput.setValueAttribute(VAT);
		submit.click();
		
		HtmlAnchor getSaleLink4 = page.getAnchorByHref("GetSalePageController");
		HtmlPage salePage4 = (HtmlPage) getSaleLink4.openLinkInNewWindow();
		
		//Para verificar que foi adicionada uma sale à tabela
		int previousTableSize = -1;
		if( salePage4.getElementById("table1") != null) {
			HtmlTable table = salePage4.getHtmlElementById("table1");
			if(previousTableSize<table.getRows().size()) {
				previousTableSize = table.getRows().size();
			}
		}

		HtmlAnchor getSaleLink = page.getAnchorByHref("addSale.html");
		HtmlPage salePage = (HtmlPage) getSaleLink.openLinkInNewWindow();
		
		HtmlForm getSaleForm = salePage.getForms().get(0);
		vatInput = getSaleForm.getInputByName("customerVat");
		submit = getSaleForm.getInputByValue("Add Sale");
		
		vatInput.setValueAttribute(VAT);
		submit.click();
		
		HtmlAnchor getSaleLink2 = page.getAnchorByHref("UpdateSaleStatusPageControler");
		HtmlPage salePage2 = (HtmlPage) getSaleLink2.openLinkInNewWindow();
		
		String id = "";
		if( salePage2.getElementById("table1") != null) { // Tabela existe
			
			HtmlTable table = salePage2.getHtmlElementById("table1");
			
			assertEquals(table.getRows().size(), previousTableSize+1);
			
			
			// ID da sale
			id = table.getCellAt(table.getRows().size()-1, 0).asText();
			
		}
		
		// ------------------------ INSERT SALE ------------------------ //
		
		// ------------------------ INSERT DELIVERY ------------------------ //		
		
		HtmlAnchor getDeliveryLink = page.getAnchorByHref("saleDeliveryVat.html");
		HtmlPage deliveryPage = (HtmlPage) getDeliveryLink.openLinkInNewWindow();
		
		HtmlForm getDeliveryForm = deliveryPage.getForms().get(0);
		vatInput = getDeliveryForm.getInputByName("vat");
		submit = getDeliveryForm.getInputByValue("Get Customer");
		
		vatInput.setValueAttribute(VAT);
		submit.click();
		
		HtmlAnchor getDeliveryInfoLink = page.getAnchorByHref("AddSaleDeliveryPageController");
		HtmlPage deliveryInfoPage = (HtmlPage) getDeliveryInfoLink.openLinkInNewWindow();
		HtmlForm deliveryInfoForm = deliveryInfoPage.getForms().get(0);
		
		HtmlInput addressIdInput = deliveryInfoForm.getInputByName("addr_id");
		HtmlInput saleIdInput = deliveryInfoForm.getInputByName("sale_id");
		submit = getDeliveryForm.getInputByValue("Insert");
		
		addressIdInput.setValueAttribute("1");
		saleIdInput.setValueAttribute(id);
		submit.click();
		
		// ------------------------ INSERT DELIVERY ------------------------ //
		

		// ------------------------ SHOW DELIVERY ------------------------ //
		
		HtmlAnchor showDeliveryLink = page.getAnchorByHref("showDelivery.html");
		HtmlPage showDeliveryPage = (HtmlPage) showDeliveryLink.openLinkInNewWindow();
		HtmlForm showDeliveryPageForm = showDeliveryPage.getForms().get(0);
		
		vatInput = showDeliveryPageForm.getInputByName("vat");
		vatInput.setValueAttribute(VAT);
		showDeliveryPageForm.getInputByValue("Get Customer").click();
		
		getDeliveryInfoLink = page.getAnchorByHref("GetSaleDeliveryPageController");
		deliveryInfoPage = (HtmlPage) getDeliveryInfoLink.openLinkInNewWindow();
		
		
		String saleId = "-1";
		String addressId = "-1";
		//adicionei id à TABLE
		if( deliveryInfoPage.getElementById("table1") != null) {
			HtmlTable table = deliveryInfoPage.getHtmlElementById("table1");
			
			saleId = table.getCellAt(table.getRows().size()-1, 1).asText();
			addressId = table.getCellAt(table.getRows().size()-1, 1).asText();
		}
		
		assertEquals(saleId,id);
		assertEquals(addressId,"1");
		
		
	}
	
	*/
	
	
}
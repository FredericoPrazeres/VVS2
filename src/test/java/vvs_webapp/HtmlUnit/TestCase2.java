package vvs_webapp.HtmlUnit;

import static org.junit.Assert.*;
import org.junit.*;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;

import java.io.*;


public class TestCase2{
	
	
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
	}
	
	@AfterClass
	public static void takeDownClass() {
		webClient.close();
	}
		
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
			for (final HtmlTableRow row : table.getRows()) {
			    count++;
			}
		}
		
		
		System.out.println(count);
		
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
		for (final HtmlTableRow row : table2.getRows()) {
			
			
			
		    count2++;
		}
		
		
		//First row also counts
		assertEquals(count+2,count2);
		
		
	}
	
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
		
		final HtmlTable table = nextPage.getHtmlElementById("clients");
		
		for (final HtmlTableRow row : table.getRows()) {

			// Primeira linha da tabela
			if(row.equals(table.getRows().get(0))) {
				
				assertEquals(row.getCell(0).asText(),"Name");
				assertEquals(row.getCell(1).asText(),"Phone");
				assertEquals(row.getCell(2).asText(),"Vat");
				
				continue;
			}
			
			
			//Penultima linha (primeiro cliente a ter sido adicionado)
			if(row.equals(table.getRows().get(table.getRows().size()-2))){
					
				assertEquals(row.getCell(0).asText(),"Fred");
				assertEquals(row.getCell(1).asText(),"910203443");
				assertEquals(row.getCell(2).asText(),"274658933");
					
			}
			
			//Ultima linha (segundo cliente a ter sido adicionado)
			if(row.equals(table.getRows().get(table.getRows().size()-1))){
					
				assertEquals(row.getCell(0).asText(),"Joao");
				assertEquals(row.getCell(1).asText(),"910242523");
				assertEquals(row.getCell(2).asText(),"207527768");
					
			}
			
			
				
		}
		
		
		
		
	}
	
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
			for (final HtmlTableRow row : table.getRows()) {
			    count++;
			}
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
		
		vatInput1.setValueAttribute(VAT);
		HtmlPage salesPage2 = (HtmlPage) submit1.click();
		
		
		int count2 = 0;
		
		if( salesPage2.getElementById("table1") != null) { // Tabela existe
			HtmlTable table = salesPage2.getHtmlElementById("table1");
			for (final HtmlTableRow row : table.getRows()) {
			    count2++;
			}
		}
		
		if(count==0) {
			assertNotEquals(0,count2);
		}else {
			assertEquals(count+1,count2);
		}
		
	}
	
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
	
	
	
	
}
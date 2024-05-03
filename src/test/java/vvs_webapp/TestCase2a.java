package vvs_webapp;

import static org.junit.Assert.*;
import org.junit.*;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

import java.net.MalformedURLException;

import java.io.*;
import java.util.*;


public class TestCase2a{
	
	
	private static final String APPLICATION_URL = "http://localhost:8080/VVS_webappdemo/";
	private static final int APPLICATION_NUMBER_USE_CASES = 11;

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
	
	
	
	
	
	
}
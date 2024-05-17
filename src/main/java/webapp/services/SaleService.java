package webapp.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import webapp.persistence.PersistenceException;
import webapp.persistence.SaleDeliveryRowDataGateway;
import webapp.persistence.SaleRowDataGateway;
import webapp.persistence.SaleStatus;


/**
 * Handles sales transactions. 
 * Each public method implements a transaction script.
 * 
 * @author 
 * @version
 *
 */
public enum SaleService {
	INSTANCE;
	
	public SalesDTO getSaleByCustomerVat (int vat) throws ApplicationException {
		if (!isValidVAT (vat))
			throw new ApplicationException ("Invalid VAT number: " + vat);
		else try {
			List<SaleRowDataGateway> sales = new SaleRowDataGateway().getAllSales(vat);
			List<SaleDTO> list = new ArrayList<>();
			for(SaleRowDataGateway sl : sales) {
				list.add(new SaleDTO(sl.getId(), sl.getData(),sl.getTotal(), sl.getStatusId(), sl.getCustomerVat()));
			}
			SalesDTO s = new SalesDTO(list);
			return s;
		} catch (PersistenceException e) {
				throw new ApplicationException ("Customer with vat number " + vat + " not found.", e);
		}
	}
	
	public SalesDTO getAllSales() throws ApplicationException {
		try {
			List<SaleRowDataGateway> sales = new SaleRowDataGateway().getAllSales();
			List<SaleDTO> list = new ArrayList<>();
			for(SaleRowDataGateway sl : sales) {
				list.add(new SaleDTO(sl.getId(), sl.getData(),sl.getTotal(), sl.getStatusId(), sl.getCustomerVat()));
			}
			SalesDTO s = new SalesDTO(list);
			return s;
		} catch (PersistenceException e) {
				throw new ApplicationException ("Error loading sales.", e);
		}
	}
	
	
	public void addSale(int customerVat) throws ApplicationException {
		
		if(true|| CustomerService.INSTANCE.customerExists(customerVat)) {
			
			try {
				
				SaleRowDataGateway sale = new SaleRowDataGateway(customerVat, new Date());
				sale.insert();
			
			} catch (PersistenceException e) {
					throw new ApplicationException ("Can't add customer with vat number " + customerVat + ".", e);
			}
			
			
		}else{
			throw new ApplicationException ("Customer with vat number " + customerVat + " does not exist");
		}
		
	}
	
	public void updateSale(int id) throws ApplicationException {
		try {
			SaleRowDataGateway sale = new SaleRowDataGateway().getSaleById(id);
			sale.setSaleStatus(SaleStatus.CLOSED);
			sale.updateSale();
		} catch (PersistenceException e) {
				throw new ApplicationException ("Sale with id " + id + " doesn't exist.", e);
		}
	}
	
	
	public SalesDeliveryDTO getSalesDeliveryByVat (int vat) throws ApplicationException {
		try {
			List<SaleDeliveryRowDataGateway> salesd = new SaleDeliveryRowDataGateway().getAllSaleDelivery(vat);
			List<SaleDeliveryDTO> list = new ArrayList<>();
			for(SaleDeliveryRowDataGateway sd : salesd) {
				list.add(new SaleDeliveryDTO(sd.getId(), sd.getSale_id(), sd.getCustomerVat(), sd.getAddr_id()));
			}
			SalesDeliveryDTO s = new SalesDeliveryDTO(list);
			return s;
		} catch (PersistenceException e) {
				throw new ApplicationException ("Customer with vat number " + vat + " not found.", e);
		}
	}
	
	
	
	
	public int addSaleDelivery(int sale_id, int addr_id) throws ApplicationException {
		try {
			
			
			SaleRowDataGateway s = new SaleRowDataGateway().getSaleById(sale_id);
			
			/*
			if(numberOfSaleDelivery(s.getCustomerVat(), sale_id)>=1)
				throw new ApplicationException ("You can only add one delivery to a sale.");
				
			if(!CustomerService.INSTANCE.addressExists(s.getCustomerVat(), addr_id))
				throw new ApplicationException ("The address specified does not exist");
			
			if(!saleExists(sale_id))
				throw new ApplicationException ("The sale specified does not exist");
			
			if(saleClosed(sale_id))
				throw new ApplicationException ("Can't add delivery to closed sale");
			*/	
			
			
			SaleDeliveryRowDataGateway sale = new SaleDeliveryRowDataGateway(sale_id, s.getCustomerVat() ,addr_id);
			sale.insert();
			return sale.getCustomerVat();
			
		} catch (PersistenceException e) {
				throw new ApplicationException ("Can't add address to cutomer.", e);
		}
	}
	
	
	/**
	 * Counts the number of deliveries a sale has
	 * 
	 * @param vat The customer VAT
	 * @param sale_id ID of the sale
	 * 
	 * @return The number of deliveries
	 * @author Frederico Prazeres fc56269
	 * 
	 */
	private int numberOfSaleDelivery(int vat,int sale_id) throws ApplicationException{
		
			
		SalesDeliveryDTO sd = SaleService.INSTANCE.getSalesDeliveryByVat(vat);
		int count = 0;
		for(SaleDeliveryDTO saleDelivery : sd.sales_delivery) {
			
			if(saleDelivery.sale_id==sale_id)
				count++;
		}			
		
		return count;
		
	}
	
	
	/**
	 * Checks if a sale exits
	 * 
	 * @param sale_id ID of the sale
	 * 
	 * @return The number of deliveries
	 * @author Frederico Prazeres fc56269
	 * 
	 */
	private boolean saleExists(int sale_id) throws ApplicationException{
		
		SalesDTO sales = SaleService.INSTANCE.getAllSales();
		
		for(SaleDTO sale : sales.sales) {
			
			if(sale.id == sale_id)
				return true;
			
			
		}
		
		return false;
	}
	
	/**
	 * Checks if a sale is closed
	 * 
	 * @param sale_id ID of the sale
	 * 
	 * @return true if a sale is closed, false if it is not
	 * @author Frederico Prazeres fc56269
	 * 
	 */
	private boolean saleClosed(int sale_id) throws ApplicationException{
		
		SalesDTO sales = SaleService.INSTANCE.getAllSales();
		
		for(SaleDTO sale : sales.sales) {
			
			if(sale.id == sale_id) {
				if(sale.statusId == "C")
					return true;
			}

		}
		
		return false;
	}
	
	
	
	
	/**
	 * Checks if a VAT number is valid.
	 * 
	 * @param vat The number to be checked.
	 * @return Whether the VAT number is valid. 
	 */
	private boolean isValidVAT(int vat) {
		// If the number of digits is not 9, error!
		if (vat < 100000000 || vat > 999999999)
			return false;
		
		// If the first number is not 1, 2, 5, 6, 8, 9, error!
		int firstDigit = vat / 100000000;
		if (firstDigit != 1 && firstDigit != 2 && 
			firstDigit != 5 && firstDigit != 6 &&
			firstDigit != 8 && firstDigit != 9)
			return false;
		
		// Checks the congruence modules 11.
		int sum = 0;
		int checkDigit = vat % 10;
		vat /= 10;
		
		for (int i = 2; i < 10 && vat != 0; i++) {
			sum += vat % 10 * i;
			vat /= 10;
		}
		
		int checkDigitCalc = 11 - sum % 11;
		if (checkDigitCalc == 10)
			checkDigitCalc = 0;
		return checkDigit == checkDigitCalc;
	}
}

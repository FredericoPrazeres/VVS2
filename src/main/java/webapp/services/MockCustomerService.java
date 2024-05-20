package webapp.services;

import webapp.persistence.CustomerRowDataGateway;
import webapp.persistence.PersistenceException;



/**
 * Customer Service Mocked class
 * 
 * 
 * @author Frederico Prazeres fc56269
 * @throws ApplicationException
 */
public class MockCustomerService {
	
    public void addCustomer(int vat, String designation, int phoneNumber) throws ApplicationException {
        if (!isValidVAT(vat))
            throw new ApplicationException("Invalid VAT number: " + vat);
        else try {
            CustomerRowDataGateway customer = new CustomerRowDataGateway(vat, designation, phoneNumber);
            customer.insert();
        } catch (PersistenceException e) {
            throw new ApplicationException("Can't add customer with vat number " + vat + ".", e);
        }
    }

   
    private boolean isValidVAT(int vat) {
        if (vat < 100000000 || vat > 999999999)
            return false;

        int firstDigit = vat / 100000000;
        if (firstDigit != 1 && firstDigit != 2 &&
                firstDigit != 5 && firstDigit != 6 &&
                firstDigit != 8 && firstDigit != 9)
            return false;

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
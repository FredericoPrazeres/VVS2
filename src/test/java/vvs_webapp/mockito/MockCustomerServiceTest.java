package vvs_webapp.mockito;

import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import webapp.services.ApplicationException;

import webapp.services.MockCustomerService;

public class MockCustomerServiceTest {


    @InjectMocks
    private MockCustomerService customerService;

	@Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = ApplicationException.class)
    public void testAddCustomer_InvalidVat() throws Exception {
        int vat = 123;
        String designation = "New Customer";
        int phoneNumber = 987654321;

        customerService.addCustomer(vat, designation, phoneNumber);
    }

}
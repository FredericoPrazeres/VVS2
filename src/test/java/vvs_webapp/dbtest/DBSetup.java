package vvs_webapp.dbtest;

import static com.ninja_squad.dbsetup.Operations.*;
import com.ninja_squad.dbsetup.generator.ValueGenerators;
import com.ninja_squad.dbsetup.operation.Insert;
import com.ninja_squad.dbsetup.operation.Operation;

import java.util.GregorianCalendar;

import webapp.persistence.PersistenceException;

public class DBSetup {

	public static final String DB_URL = "jdbc:hsqldb:file:src/main/resources/data/hsqldb/cssdb";
	public static final String DB_USERNAME = "SA";
	public static final String DB_PASSWORD = "";

	private static boolean appDatabaseAlreadyStarted = false;
	
	public static void startApplicationDatabaseForTesting() {

		if (appDatabaseAlreadyStarted)
			return;

		try {
			webapp.persistence.DataSource.INSTANCE.connect(DB_URL, DB_USERNAME, DB_PASSWORD);
			appDatabaseAlreadyStarted = true;
		} catch (PersistenceException e) {
			throw new Error("Application DataSource could not be started");
		}
	}

	public static final Operation DELETE_ALL = deleteAllFrom("CUSTOMER", "SALE", "ADDRESS", "SALEDELIVERY");
	public static final Operation INSERT_CUSTOMER_SALE_DATA;
	public static final Operation INSERT_CUSTOMER_ADDRESS_DATA;

	static {

		Insert insertCustomers =
				 insertInto("CUSTOMER")
				 .columns("ID", "DESIGNATION", "PHONENUMBER", "VATNUMBER")
				 .values( 1, "JOSE FARIA", 914276732, 197672337)
				 .values( 2, "LUIS SANTOS", 964294317, 168027852)
				 .build();

		Insert insertAddresses =
				 insertInto("ADDRESS")
				 .withGeneratedValue("ID",
				 ValueGenerators.sequence().startingAt(100L).incrementingBy(1))
				 .columns( "ADDRESS", "CUSTOMER_VAT")
				 .values( "FCUL, Campo Grande, Lisboa", 197672337)
				 .values( "R. 25 de Abril, 101A, Porto", 197672337)
				 .values( "Av Neil Armstrong, Cratera Azul, Lua", 168027852)
				 .build();
		
		Insert insertSales = insertInto("SALE").columns("ID", "DATE", "TOTAL", "STATUS", "CUSTOMER_VAT")
				.values(1, new GregorianCalendar(2024, 02, 12), 0.0, 'O', 168027852)
				.values(2, new GregorianCalendar(2024, 04, 25), 0.0, 'O', 197672337).build();
		
		Insert insertSaleDeliveries = insertInto("SALEDELIVERY").columns("SALE_ID", "CUSTOMER_VAT", "ADDRESS_ID")
			.values(1, 168027852, 1).build();
		
		INSERT_CUSTOMER_ADDRESS_DATA = sequenceOf(insertCustomers, insertAddresses);
		INSERT_CUSTOMER_SALE_DATA = sequenceOf(insertCustomers, insertSales, insertSaleDeliveries, insertAddresses);


	}
	
	
	
	
}
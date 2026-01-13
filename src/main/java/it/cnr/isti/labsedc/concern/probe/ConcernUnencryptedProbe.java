package it.cnr.isti.labsedc.concern.probe;

import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.Random;

import jakarta.jms.JMSException;
import jakarta.jms.ObjectMessage;

import javax.naming.NamingException;

import it.cnr.isti.labsedc.concern.cep.CepType;
import it.cnr.isti.labsedc.concern.event.ConcernBaseEvent;
import it.cnr.isti.labsedc.concern.event.ConcernBaseUnencryptedEvent;
import it.cnr.isti.labsedc.concern.utils.ConnectionManager;
import it.cnr.isti.labsedc.concern.utils.DebugMessages;
import it.cnr.isti.labsedc.concern.utils.ReadFromCSV;

public class ConcernUnencryptedProbe extends ConcernAbstractProbe {

	public ConcernUnencryptedProbe(Properties settings) {
		super(settings);
	}

	public static void main(String[] args) throws UnknownHostException, InterruptedException {
		// Validate command-line arguments
		if (args.length < 2) {
			System.err.println("Usage: java -jar unencrypted-probe.jar <csv_file_path> <column_name>");
			System.err.println("Example: java -jar unencrypted-probe.jar /path/to/data.csv COLUMN_NAME");
			System.exit(1);
		}
		
		String csvPath = args[0];
		String columnName = args[1];
		
		System.out.println("CSV Path: " + csvPath);
		System.out.println("Column Name: " + columnName);
		
		//creating a probe
		ConcernUnencryptedProbe aGenericProbe = new ConcernUnencryptedProbe(
				ConnectionManager.createProbeSettingsPropertiesObject(
						"org.apache.activemq.jndi.ActiveMQInitialContextFactory",
						"tcp://localhost:61616","system", "manager",
						"TopicCF","DROOLS-InstanceOne", false, "UNENCRYPTED_Probe",
						"it.cnr.isti.labsedc.concern,java.lang,javax.security,java.util",
						"vera", "griselda"));
		//sending events
		while (true) {
		try {
			DebugMessages.line();
			DebugMessages.println(System.currentTimeMillis(), ConcernUnencryptedProbe.class.getSimpleName(),"Sending messages");

			ReadFromCSV csvReader = new ReadFromCSV(csvPath, columnName);
			String unencryptedValue = "";
			while (csvReader.hasNext()) {
                unencryptedValue = csvReader.next();
			
				sendUnencryptedEventMessage(aGenericProbe, unencryptedValue.toString(), "none");
				Random timeSlot = new Random();
				
				Thread.sleep(timeSlot.nextInt(1,40));

			}
			csvReader.close();

//			Thread.sleep(1000);
//			sendScoreMessage(aGenericProbe, "0.1");
//			sendVelocityMessage(aGenericProbe, "0.1");
//			Thread.sleep(5000);
//			sendConnectionEventMessage(aGenericProbe);
		
		} catch (IndexOutOfBoundsException | JMSException | NamingException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	}

	protected static void sendUnencryptedEventMessage(
		ConcernUnencryptedProbe aGenericProbe, 
		String unencryptedPayload, 
		String encryptionAlgorithmToUse) throws JMSException, NamingException, NoSuchAlgorithmException {
			
		ConcernBaseUnencryptedEvent<String> unencryptedEvent = new ConcernBaseUnencryptedEvent<>(
			System.currentTimeMillis(),
			"UNENCRYPTED_Probe",
			"Monitoring",
			"sessionID",
			"noChecksum",
			"UnencryptedMessage",
			unencryptedPayload,
			CepType.DROOLS,
			false, 
			encryptionAlgorithmToUse);

			try {
				ObjectMessage messageToSend = publishSession.createObjectMessage();
				messageToSend.setJMSMessageID(String.valueOf(MESSAGEID++));
				messageToSend.setObject(unencryptedEvent);

				mProducer.send(messageToSend);
				System.out.println("unencryptedEvent: " + unencryptedEvent.getData());
			}
			catch(JMSException asd) {
				asd.printStackTrace();
			}
	}
	
	@Override
	public void sendMessage(ConcernBaseEvent<?> event, boolean debug) {
	}
}

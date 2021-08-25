package io.je.utilities.logger;

import io.je.utilities.execution.JobEngine;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.SQLException;


//TODO: to be deleted, just used for testing
public class LoggingServiceTest {

	public static void publish(String projectId, LogLevel logLevel, String logDate, LogCategory category,
			LogSubModule subModule, Object message) {
			//JEConfiguration.setLoggingSystemURL("tcp://localhost");
			//JEConfiguration.setLoggingSystemZmqPublishPort(15001);
			LogMessage msg = new LogMessage(logLevel, message, logDate,  projectId,subModule,"ruleId123");
			ZMQLogPublisher.publish(msg);
		
	}

	public static void main(String[] args) {
		Connection conn = null;

		try {

			String dbURL = "jdbc:sqlserver://NJENDOUBI-PC\\SQLEXPRESS:1433;user=sa;password=io.123";
			String user = "sa";
			String pass = "io.123";
			File jarPath = new File("D:\\jars\\mssql-jdbc-8.4.1.jre8.jar");

			try {

				URLClassLoader child = new URLClassLoader(
						new URL[] {jarPath.toURI().toURL()}
				);

				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver", true, child);
				ClassLoader loader = JobEngine.class.getClassLoader();
				child.loadClass("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				Driver driver = (Driver) Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver", true, child).newInstance();
				conn = driver.connect(dbURL, null);

				if (conn != null) {
					DatabaseMetaData dm = (DatabaseMetaData) conn.getMetaData();
					System.out.println("Driver name: " + dm.getDriverName());
					System.out.println("Driver version: " + dm.getDriverVersion());
					System.out.println("Product name: " + dm.getDatabaseProductName());
					System.out.println("Product version: " + dm.getDatabaseProductVersion());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} finally {
			try {
				if (conn != null && !conn.isClosed()) {
					conn.close();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}

		/*Runnable runnable = new Runnable() {

			@Override
			public void run() {
				Integer i=0;
				while (true) {
					
		   			System.out.println("***");

					LoggingServiceTest.publish((i++).toString(), LogLevel.Inform, LocalDateTime.now().toString(), LogCategory.RUNTIME, LogSubModule.RULE,
							"Rule was added");
					System.out.println("Sent message");
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}
		};

		Thread t = new Thread(runnable);
		Date date = new Date(System.currentTimeMillis());

// Conversion
		SimpleDateFormat sdf;
		sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		//sdf.setTimeZone(TimeZone.getTimeZone("CET"));
		String text = sdf.format(date);
		System.out.println(text);*/
		//t.start();
		
	/*	ZMQSubscriber sub = new ZMQSubscriber("tcp://127.0.0.1", 15001, "SIOTH##LogTopic") {
			
			@Override
			public void run() {
				while (true)
				{
		   			System.out.println("---");

					 String data = null;
			   		 try {
			   			data = this.getSubSocket().recvStr();
			   			System.out.println("data:"+data);
			   		 }catch (Exception e) {
						e.printStackTrace();
						continue;
					}
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		};
		
		new Thread(sub).start();
*/
	}

}

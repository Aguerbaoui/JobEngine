package io.je.utilities.logger;

public class SubscriberTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SubTest test = new SubTest("tcp://192.168.0.169", 18001, "DataModelTopic");
		new Thread(test).start();
	}

}

package com.redhat.demos.fibonacciexample;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.core.ClassObjectSerializationFilter;
import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.ExecutionResults;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.api.model.KieServiceResponse.ResponseType;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.RuleServicesClient;

import com.fasterxml.jackson.databind.ObjectMapper;


public class TestFibonacci {
	
	private static final String URL = "http://34.87.47.141:8080/kie-server/services/rest/server";
    private static final String USER = "kieadm";
    private static final String PASSWORD = "kieadm";

    private static final MarshallingFormat FORMAT = MarshallingFormat.JSON;

    private static KieServicesConfiguration conf;
    private static KieServicesClient kieServicesClient;
    
    private static KieServices kieServices;
	
    public static void initialize() {
        conf = KieServicesFactory.newRestConfiguration(URL, USER, PASSWORD);
        conf.setMarshallingFormat(FORMAT);
        Set<Class<?>> allClasses = new HashSet<Class<?>>();
        allClasses.add(Fib.class);
        conf.addExtraClasses(allClasses);
        kieServicesClient = KieServicesFactory.newKieServicesClient(conf);
        kieServices = KieServices.Factory.get();
    }
    
    public static Comparator<Fib> fibComparator = new Comparator<Fib>() {

		@Override
		public int compare(Fib f1, Fib f2) {
			// TODO Auto-generated method stub
			int fib1 = f1.getSequence();
			int fib2 = f2.getSequence();
			return fib1-fib2;
		}
    	
	};
    
    
	public static void main(String[] args) {
		System.out.println("Initialising the container services");
		initialize();
		
		System.out.println("Fetching the containers");
		List<KieContainerResource> kieContainers = kieServicesClient.listContainers().getResult().getContainers();
        if (kieContainers.size() == 0) {
            System.out.println("No containers available...");
            return;
        }
		
        System.out.println("Fire Rules....");
        
        RuleServicesClient rulesClient = kieServicesClient.getServicesClient(RuleServicesClient.class);
        ClassObjectSerializationFilter filter = new ClassObjectSerializationFilter(Fib.class);
        
        KieCommands commandsFactory = kieServices.getCommands();
        
        Fib f = new Fib(50);
        
        Command<?> insert = commandsFactory.newInsert(f);
        Command<?> fireAllRules = commandsFactory.newFireAllRules();
        Command<?> getObjects = commandsFactory.newGetObjects(filter, "out");
        Command<?> dispose = commandsFactory.newDispose();
        Command<?> batchCommand = commandsFactory.newBatchExecution(Arrays.asList(insert, fireAllRules, getObjects, dispose));
        
//        ServiceResponse<String> executeResponse = rulesClient.executeCommands("Fibonnaci", batchCommand);
        ServiceResponse<ExecutionResults> executeResponse1 = rulesClient.executeCommandsWithResults("Fibonnaci", batchCommand);
       
        
        if(executeResponse1.getType() == ResponseType.SUCCESS) {
            System.out.println("Commands executed with success! Response: ");
            System.out.println(executeResponse1.getResult().getValue("out").toString());
            ObjectMapper mapper = new ObjectMapper();
            List<Fib> fibList = (List<Fib>) executeResponse1.getResult().getValue("out");
            Collections.sort(fibList, fibComparator);
            for (int i = 0; i < fibList.size(); i++) {
				System.out.println(fibList.get(i).getSequence() + " == " + fibList.get(i).getValue());
			}
          } else {
            System.out.println("Error executing rules. Message: ");
            System.out.println(executeResponse1.getMsg());
          }
		
	}

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

/**
 *
 * @author User
 */
public class YellowPage {
    public YellowPage(){
        
    }
    
    public void register(String name, String type, Agent agent){
        String serviceName = name;
        
  	try {
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(agent.getAID());
            ServiceDescription sd = new ServiceDescription();
            sd.setName(serviceName);
            sd.setType(type);
            dfd.addServices(sd);
  		
            DFService.register(agent, dfd);
  	}
  	catch (FIPAException fe) {
            fe.printStackTrace();
  	}
    }
    
    public void deRegister(Agent agent, DFAgentDescription dfName){
        try {
            DFService.deregister(agent, dfName);
        } catch(FIPAException ex) {
            ex.printStackTrace();
        }
    }
    
    
    public AID getServiceAgent(String type, Agent searchingAgent) {
  	try {
            String serviceType = type;
            
            // Build the description used as template for the search
            DFAgentDescription template = new DFAgentDescription();
            
            ServiceDescription templateSd = new ServiceDescription();
            templateSd.setType(serviceType);
            template.addServices(templateSd);
  		
            SearchConstraints sc = new SearchConstraints();
            // We want to receive 10 results at most
            sc.setMaxResults(new Long(10));
  		
            DFAgentDescription[] results = DFService.search(searchingAgent, template, sc);
            if (results.length > 0) {
  		for (int i = 0; i < results.length; ++i) {
                    DFAgentDescription dfd = results[i];
                    AID agentAID = dfd.getName();
                    System.out.println(dfd.getName());
  		}
                
                //just use the first one
                DFAgentDescription dfd = results[0];
                return dfd.getName();
            }	
            else {
                System.out.println("Agent "+searchingAgent.getLocalName()+" did not find any " + serviceType + " service");
                System.out.println("No " + serviceType + " agent service found!");
                return null;
            }
  	}
  	catch (FIPAException fe) {
            fe.printStackTrace();
  	}
        return null;
    }
    
}

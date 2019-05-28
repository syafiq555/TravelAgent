/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package travelagent;

import behaviours.ReceiveMessage;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.io.IOException;
import models.Customer;
import utils.Serialization;
import utils.YellowPage;

/**
 *
 * @author User
 */
public class CustomerAgent extends Agent {
    private CustomerUI customerUi;
    
        
    
    @Override
    protected void setup() {
        YellowPage yellowPage = new YellowPage();
        yellowPage.register(getAID().getName(), "customer_agent", this);
        customerUi = new CustomerUI(this);
        customerUi.showGui();
        yellowPage.getServiceAgent("ticket_agent", this);
        //for receiving calculation result	
	addBehaviour(new ReceiveMessage(this, "Customer"));
    }
    
    public void requestTravelTickets(Customer customer) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
       String strCustomer = "";
       
       try
        {
            strCustomer = Serialization.serializeObjectToString(customer);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
       
       msg.setContent(strCustomer);
//       msg.addReceiver(r);
    }
}

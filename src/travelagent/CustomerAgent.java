/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package travelagent;

import behaviours.ReceiveMessage;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.io.IOException;
import java.util.ArrayList;
import models.Customer;
import models.Ticket;
import utils.Serialization;
import utils.YellowPage;

/**
 *
 * @author User
 */
public class CustomerAgent extends Agent {
    private CustomerUI customerUi;
    private ArrayList<Ticket> tickets;
    private AID ticketAgent;
        
    public CustomerAgent() {
        tickets = new ArrayList<>();
    }
    
    @Override
    protected void setup() {
        YellowPage yellowPage = new YellowPage();
        yellowPage.register(getAID().getName(), "customer_agent", this);
        customerUi = new CustomerUI(this);
        customerUi.showGui();
        ticketAgent = yellowPage.getServiceAgent("ticket_agent", this);
        
        this.addBehaviour(new CyclicBehaviour(this) {
            String msgContent;
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    if (msg.getPerformative() == ACLMessage.INFORM) {
                        msgContent = msg.getContent();
                        System.out.println(msgContent);
                    }
                }
                
                block();
            }
        });
    }
    
    public void requestTravelTickets(Customer customer) {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
       String strCustomer = "";
       JSONArray array;
       
       try
        {
            strCustomer = Serialization.serializeObjectToString(customer);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
       
       msg.setContent(strCustomer);
       msg.addReceiver(ticketAgent);
       send(msg);
    }
}

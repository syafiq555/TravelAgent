/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package travelagent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Customer;
import models.Ticket;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import utils.Serialization;
import utils.YellowPage;

/**
 *
 * @author User
 */
public class BuyAgent extends Agent {
    YellowPage yellowPage = new YellowPage();
    BuyUI buyUi = new BuyUI(this);
    AID bankAgent, customerAgent;
    @Override
    protected void setup() {
        yellowPage.register(getAID().getName(), "buy_agent", this);
        bankAgent = yellowPage.getServiceAgent("bank_agent", this);;
        
        addBehaviour(new CyclicBehaviour(this) {
            int step = 0;
            String msgContent;
            @Override
            public void action() {
                ACLMessage msg = receive();
                
                if (msg != null) {
                    switch(step) {
                        case 0 :
                            if (msg.getPerformative() == ACLMessage.PROPOSE) {
                                msgContent = msg.getContent();
                                JSONParser parser = new JSONParser();
                                
                                try {
                                    JSONObject customerObj = (JSONObject) parser.parse(msgContent);
                                    Customer customer = new Customer();
                                    customer.setCustomerName(customerObj.get("customerName").toString());
                                    customer.setDate(customerObj.get("date").toString());
                                    customer.setDestination(customerObj.get("destination").toString());
                                    customer.setPrice(customerObj.get("price").toString());
                                    JSONObject ticketObject = (JSONObject) customerObj.get("ticket");
                                    Ticket ticket = new Ticket(ticketObject.get("price").toString(), ticketObject.get("destination").toString(), ticketObject.get("date").toString());
                                    ticket.setId(ticketObject.get("id").toString());
                                    customer.setTicket(ticket);
                                    System.out.println(customer.toString());
                                    buyUi.showGui();
                                    buyUi.showTicketDetail(customer);
                                    
                                } catch (ParseException ex) {
                                    Logger.getLogger(BuyAgent.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            step = 1;
                            break;
                        case 1: 
                            System.out.println("waiting bank agent");
                            if (msg.getPerformative() == ACLMessage.INFORM) {
                                ACLMessage reply = new ACLMessage(msg.getPerformative());
                                reply.setContent(msg.getContent());
                                customerAgent = yellowPage.getServiceAgent("customer_agent", this.myAgent);
                                reply.addReceiver(customerAgent);
                                send(reply);
                                System.out.println("Sent to customer agent");
                            }
                            break;
                    }
                }
                
                block();
            }           
        });
    }

    void sendPaymentToBank(Customer customer) throws IOException {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(bankAgent);
        String customerString;
        JSONObject customerObject = new JSONObject();
        customerObject.put("customerName", customer.getCustomerName());
        customerObject.put("date", customer.getDate());
        customerObject.put("deposit", customer.getDeposit());
        customerObject.put("destination", customer.getDestination());
        customerObject.put("price", customer.getPrice());
        JSONObject ticket = new JSONObject();
        ticket.put("date", customer.getTicket().getDate());
        ticket.put("destination", customer.getTicket().getDestination());
        ticket.put("id", customer.getTicket().getId());
        ticket.put("price", customer.getTicket().getPrice());
        customerObject.put("ticket", ticket);
        customerString = customerObject.toString();
        msg.setContent(customerString);
        send(msg);
    }
}

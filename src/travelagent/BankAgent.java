/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package travelagent;

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
public class BankAgent extends Agent {
    YellowPage yellowPage = new YellowPage();
    Customer customer = new Customer();
    BankUI bankUI = new BankUI(this);
    
    @Override
    protected void setup() {
        yellowPage.register(getAID().getName(), "bank_agent", this);
        addBehaviour(new CyclicBehaviour(this) {
            String msgContent;
            
            @Override
            public void action() {
                ACLMessage msg = receive();
                
                if (msg != null) {
                    if (msg.getPerformative() == ACLMessage.REQUEST) {
                        msgContent = msg.getContent();
                        System.out.println("message from [BankAgent] "+ msg.getContent());
                        JSONParser parser = new JSONParser();
                        JSONObject customerObject = null;
                        try {
                            customerObject = (JSONObject) parser.parse(msgContent);
                        } catch (ParseException ex) {
                            Logger.getLogger(BankAgent.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        customer.setCustomerName((String) customerObject.get("customerName"));
                        customer.setDate((String) customerObject.get("date"));
                        customer.setDeposit((String) customerObject.get("deposit"));
                        customer.setDestination((String) customerObject.get("destination"));
                        customer.setPrice((String) customerObject.get("price"));
                        JSONObject ticketObject = (JSONObject) customerObject.get("ticket");
                        Ticket ticket = new Ticket(ticketObject.get("price").toString(), ticketObject.get("destination").toString(), ticketObject.get("date").toString());
                        customer.setTicket(ticket);
                        
                        System.out.println(customer.toString());
                        int status = Double.parseDouble(customer.getDeposit()) >= (0.25 * Double.parseDouble(customer.getTicket().getPrice())) ? 1 : 0;
                        double calculateRemaining = Double.parseDouble(customer.getTicket().getPrice()) - Double.parseDouble(customer.getDeposit());
                        bankUI.showPaymentDetail(customer, status, calculateRemaining);
                        bankUI.showGui();
                        ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
                        reply.addReceiver(msg.getSender());
                        reply.setContent(String.valueOf(status));
                        send(reply);
                    }
                }
                
                block();
            }           
        });
    }
}
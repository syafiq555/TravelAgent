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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Customer;
import models.Ticket;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import utils.YellowPage;

/**
 *
 * @author User
 */
public class CustomerAgent extends Agent {
    private CustomerUI customerUi;
    private ArrayList<Ticket> tickets;
    private AID ticketAgent;
    private AID buyAgent;
        
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
        buyAgent = yellowPage.getServiceAgent("buy_agent", this);
        
        this.addBehaviour(new CyclicBehaviour(this) {
            String msgContent;
            int step = 0;
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    switch(step) {
                        case 0:
                            if (msg.getPerformative() == ACLMessage.INFORM) {
                                msgContent = msg.getContent();
                                System.out.println(msgContent);
                                if (!msgContent.equals("[]")){
                                    tickets.clear();
                                    JSONParser parser = new JSONParser();
                                    JSONArray arr = null;
                                    try {
                                        arr = (JSONArray) parser.parse(msgContent);
                                    } catch (ParseException ex) {
                                        Logger.getLogger(CustomerAgent.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                    for (int i = 0; i < arr.size(); i++){
                                        JSONObject obj = (JSONObject)arr.get(i);

                                        Ticket ticket = new Ticket(obj.get("price").toString(), obj.get("destination").toString(),obj.get("date").toString());
                                        ticket.setId(obj.get("id").toString());
                                        tickets.add(ticket);
                                    }
                                    customerUi.showTickets(tickets);
                                    System.out.println(msgContent);
                                    step = 1;
                                }
                            }
                            break;
                        case 1:
                            System.out.println("Waiting for buy agent");
                            if (msg.getPerformative() == ACLMessage.INFORM) {
                                customerUi.popAlert(msg.getContent());
                            }
                            break;
                    }
                    
                }
                
                block();
            }
        });
    }
    
    public Ticket getTicketViaId(String id) {
        Ticket ticket = null;
        for (int i = 0; i < tickets.size(); i++) {
            String foundTicket = tickets.get(i).getId();
            if (foundTicket.equals(id.trim())) {
                ticket = tickets.get(i);
            }
        }
        return ticket;
    }
    
//    public ArrayList<Ticket> showTicket(){
//        return tickets;
//    }
    
    public void requestTravelTickets(Customer customer) {    
       ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
       String strCustomer;
       JSONObject jsonObject = new JSONObject();
       jsonObject.put("customerName", customer.getCustomerName());
       jsonObject.put("destination", customer.getDestination());
       jsonObject.put("date", customer.getDate());
       jsonObject.put("price", customer.getPrice());

       strCustomer = jsonObject.toString();       
       
       msg.setContent(strCustomer);
       msg.addReceiver(ticketAgent);
       send(msg);
    }
    
    public void sendTicketToBuyAgent(Customer customer) {
       
       ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
       String strCustomer;
       JSONObject jsonObject = new JSONObject();
       jsonObject.put("customerName", customer.getCustomerName());
       jsonObject.put("destination", customer.getDestination());
       jsonObject.put("date", customer.getDate());
       jsonObject.put("price", customer.getPrice());
       
       JSONObject ticketObj = new JSONObject();
       ticketObj.put("id", customer.getTicket().getId());
       ticketObj.put("date", customer.getTicket().getDate());
       ticketObj.put("price", customer.getTicket().getPrice());
       ticketObj.put("destination", customer.getTicket().getDestination());
       jsonObject.put("ticket", ticketObj);
       System.out.println(jsonObject);

       System.out.println(customer.getTicket().getId());

       strCustomer = jsonObject.toString();       
       
       msg.setContent(strCustomer);
       msg.addReceiver(buyAgent);
       send(msg);
    }
}

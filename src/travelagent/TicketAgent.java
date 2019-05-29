/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package travelagent;

import behaviours.ReceiveMessage;
import behaviours.ReceiveMessageTicket;
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
public class TicketAgent extends Agent{
    TicketGUI ticketGUI;
    ArrayList<Ticket> ticketList;
    
    public ArrayList<Ticket> getTickets(){
        return ticketList;
    }
    
    @Override
    public void setup() {
        YellowPage yellowPage = new YellowPage();
        yellowPage.register(getAID().getName(), "ticket_agent", this);
        
        ticketGUI = new TicketGUI(this);
        ticketGUI.showGui();
        ticketList = new ArrayList<>();
        
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                String msgContent = "";
                ACLMessage msg = receive();
                if (msg != null) {
                    if (msg.getPerformative() == ACLMessage.REQUEST) {
                        msgContent = msg.getContent();
                        Object obj = null;
                        try {
                            obj = new JSONParser().parse(msgContent);
                        } catch (ParseException ex) {
                            Logger.getLogger(TicketAgent.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        JSONObject jsonObj = (JSONObject)obj;
                        Customer customer = new Customer();
                        customer.setDate((String)jsonObj.get("date"));
                        customer.setCustomerName((String)jsonObj.get("customerName"));
                        customer.setDestination((String)jsonObj.get("destination"));
                        customer.setPrice((String)jsonObj.get("price"));
                        JSONArray ticketsArr = filterTickets(customer);
                        ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
                        
                        reply.addReceiver(msg.getSender());
                        reply.setContent(ticketsArr.toString());
                        send(reply);
                    }
                }
                
                block();
            }
        });
        
    }
    
    public void addTicket(Ticket ticket) {
        ticketList.add(ticket);
    }
    
    private JSONArray filterTickets(Customer customer) {
        JSONArray list = new JSONArray();
        for (int i = 0; i < ticketList.size(); i++) {
            ticketList.get(i).setId(String.valueOf(i));
            float price = Float.parseFloat(ticketList.get(i).getPrice());
            String destination = ticketList.get(i).getDestination();
            String date = ticketList.get(i).getDate();
            if (customer.getDestination().equals(destination)) {
                if (customer.getDate().equals(date)) {
                    if (price <= Float.parseFloat(customer.getPrice())) {
                        JSONObject obj = new JSONObject();
                        obj.put("id", ticketList.get(i).getId());
                        obj.put("date", ticketList.get(i).getDate());
                        obj.put("destination", ticketList.get(i).getDestination());
                        obj.put("price", ticketList.get(i).getPrice());
                        
                        list.add(obj);
                    }
                }
            }
        }
        
        return list;
    }
}

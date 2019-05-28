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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Ticket;
import utils.Serialization;
import utils.YellowPage;

/**
 *
 * @author User
 */
public class Travelayler extends Agent {
    TicketGUI ticketGUI;
    ArrayList<Ticket> tickets;
    int step = 0;
    
    @Override
    public void setup(){
        tickets = new ArrayList<Ticket>();
        YellowPage yellowPage = new YellowPage();
        yellowPage.register("travelayler", "travelayler", this);
        ticketGUI = new TicketGUI(this);
        ticketGUI.showGui();
        
        this.addBehaviour(new CyclicBehaviour(this) {

            @Override
            public void action() {
                switch(step){
                    case 0:
                        ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                        message.setContent("Requesting tickets");
                        YellowPage yellowPage = new YellowPage();
                        message.addReceiver(yellowPage.getServiceAgent("ticket_agent", myAgent));
                        step = 1;
                        send(message);
                        break;
                    case 1:
                        ACLMessage reply = receive();
                        if (reply != null){
                            System.out.println(reply.getContent());
                            String msgContent=null;
                            try {
                                msgContent = reply.getContent();
                            } catch(NullPointerException ex) {
                                System.out.println("null msgContent");
                            }
                            Ticket[] tickets = null;
                            try {
                                tickets = (Ticket[]) Serialization.deserializeObjectFromString(msgContent);
                            } catch (IOException ex) {
                                Logger.getLogger(Travelayler.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (ClassNotFoundException ex) {
                                Logger.getLogger(Travelayler.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            System.out.println(Arrays.toString(tickets));
                            step = 3;
                        }
                        block();
                            break;
                    case 3:
                        break;
                    }
            }
        });
    }
}

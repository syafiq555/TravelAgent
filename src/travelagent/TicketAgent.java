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
import models.Ticket;
import utils.YellowPage;

/**
 *
 * @author User
 */
public class TicketAgent extends Agent{
    Ticket[] tickets = {new Ticket((float)10.2, "test", "test", "test"),new Ticket((float)10.1, "test1", "test1", "test1")};
    @Override
    public void setup() {
        YellowPage yellowPage = new YellowPage();
        yellowPage.register(getAID().getName(), "ticket_agent", this);
        
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    
                }
                
                block();
            }
        });
        
    }
}

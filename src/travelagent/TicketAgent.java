/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package travelagent;

import behaviours.ReceiveMessage;
import behaviours.ReceiveMessageTicket;
import jade.core.Agent;
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
        
        addBehaviour(new ReceiveMessageTicket(this, tickets));
        
    }
}

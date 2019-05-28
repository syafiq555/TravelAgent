/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package behaviours;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import java.io.IOException;
import java.util.Arrays;
import models.Ticket;
import travelagent.TicketAgent;
import utils.Serialization;
import utils.YellowPage;

/**
 *
 * @author User
 */
public class ReceiveMessageTicket extends ReceiveMessage {
    Ticket[] tickets;
    Agent agent;
    YellowPage yellowPage;
    int step = 0;
    public ReceiveMessageTicket(Agent agent, Ticket[] tickets) {
        super();
        this.agent = agent;
        yellowPage = new YellowPage();
        this.tickets = tickets;
    }

    @Override
    public void action() {
        ACLMessage msg= this.myAgent.receive();
        
            if (msg != null) {
                switch(step){
                    case 0:
                        String msgContent = msg.getContent();
                        System.out.println(msg.getSender() + " Content: "+ msgContent);
                        String str = "";
                        try
                        {
                            str = (String) Serialization.serializeObjectToString(this.tickets);
                            System.out.println(Arrays.toString(this.tickets));
                        }
                        catch(IOException ex)
                        {
                            System.out.println("Error in deserializing object" + ex.getMessage());
                        }
                        msg.createReply();
                        msg.setPerformative(ACLMessage.INFORM);
                        msg.setContent(str);
                        step = 1;
                        agent.send(msg);
                        break;
                    case 1:
                        break;
            
            }
        }
        System.out.println("["+ this.myAgent.getAID().getName() +"] CyclicBehaviour Block");
        block();
    }
    
}

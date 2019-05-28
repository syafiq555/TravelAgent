/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package behaviours;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.io.IOException;
import models.Customer;
import models.Ticket;
import utils.Serialization;

/**
 *
 * @author User
 */
public abstract class ReceiveMessage extends CyclicBehaviour {
    public ReceiveMessage() {}
    
    @Override
    public abstract void action();
}



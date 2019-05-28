package travelagent;

import com.sun.org.apache.xalan.internal.lib.ExsltStrings;
import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TravelPelanggan extends Agent {

    private static final long serialVersionUID = 1L;

    // The title of the book to buy
    private String targetNamaTravel;
    // The list of known seller agents
    private AID[] sellerAgents;

    // Put agent initializations here
    protected void setup() {
        // Print a welcome message
        System.out.println("Hello! Buyer-agent " + getAID().getName() + " is ready.");

        // Get the title of the book to buy as a start-up argument
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            targetNamaTravel = (String) args[0];
            System.out.println("Target travel adalah " + targetNamaTravel);

            // Add a TickerBehaviour that schedules a request for seller agents every minute
            addBehaviour(new TickerBehaviour(this, 15000) {
                private static final long serialVersionUID = 1L;

                protected void onTick() {
                    System.out.println("Mencoba untuk order  " + targetNamaTravel);
                    // Update the list of seller agents
                    DFAgentDescription template = new DFAgentDescription();
                    ServiceDescription sd = new ServiceDescription();
                    sd.setType("travel-order");
                    template.addServices(sd);
                    try {
                        DFAgentDescription[] result = DFService.search(myAgent, template);
                        System.out.println("Found the following seller agents:");
                        sellerAgents = new AID[result.length];
                        for (int i = 0; i < result.length; ++i) {
                            sellerAgents[i] = result[i].getName();
                            System.out.println(sellerAgents[i].getName());
                        }
                    } catch (FIPAException fe) {
                        fe.printStackTrace();
                    }

                    // Perform the request
                    myAgent.addBehaviour(new RequestPerformer());
                }
            });
        } else {
            // Make the agent terminate
            System.out.println("Spesifikasi travel yang dituju tidak ada");
            doDelete();
        }
    }

    // Put agent clean-up operations here
    protected void takeDown() {
        // Printout a dismissal message
        System.out.println("Travel-agent " + getAID().getName() + " berhenti.");
    }

    /**
     * Inner class RequestPerformer. This is the behaviour used by Book-buyer
     * agents to request seller agents the target book.
     */
    private class RequestPerformer extends Behaviour {

        private static final long serialVersionUID = 1L;

        private AID bestSeller; // The agent who provides the best offer 
        private int bestPrice;  // The best offered price
        private int bestHari;

        private AID bestSeller2; // The agent who provides the best offer 
        private int bestPrice2;  // The best offered price
        private int bestHari2;

        private int paramHariKe;
        private int repliesCnt = 0; // The counter of replies from seller agents
        private MessageTemplate mt; // The template to receive replies
        private int step = 0;

        public void action() {
            String[] hari = {"Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu"};
            SimpleDateFormat ambilHari = new SimpleDateFormat("EEEE");
            Calendar cal = Calendar.getInstance();
            String aH = ambilHari.format(cal.getTime());
            for (int a = 0; a < hari.length; a++) {
                if (aH.equals(hari[a])) {
                    paramHariKe = a;
                }
            }
            switch (step) {
                case 0:
                    // Send the cfp to all sellers
                    ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                    for (int i = 0; i < sellerAgents.length; ++i) {
                        cfp.addReceiver(sellerAgents[i]);
                    }
                    cfp.setContent(targetNamaTravel);
                    cfp.setConversationId("travel-trade");
                    cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value

                    myAgent.send(cfp);
                    // Prepare the template to get proposals
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("travel-trade"),
                            MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
                    step = 1;
                    break;
                case 1:
                    // Receive all proposals/refusals from seller agents
                    ACLMessage reply = myAgent.receive(mt);
                    if (reply != null) {
                        // Reply received
                        if (reply.getPerformative() == ACLMessage.PROPOSE) {
                            // This is an offer 
//                            int price = Integer.parseInt(reply.getContent());

                            String a = reply.getContent();
                            String b[] = a.split("/");
                            int harga = Integer.parseInt(b[0]);
                            int harike = Integer.parseInt(b[1]);

                            if (bestSeller == null || harike < bestHari2) {
                                bestPrice2 = harga;
                                bestSeller2 = reply.getSender();
                                bestHari2 = harike;
                                if (harike < bestHari2 && harga < bestPrice2) {
                                    // This is the best offer at present
                                    bestPrice2 = harga;
                                    bestSeller2 = reply.getSender();
                                    bestHari2 = harike;
                                }
                            }

                            if (bestSeller == null || (harike > paramHariKe && harike < bestHari)) {
                                bestPrice = harga;
                                bestSeller = reply.getSender();
                                bestHari = harike;
                                if (harike < bestHari && harga < bestPrice) {
                                    // This is the best offer at present
                                    bestPrice2 = harga;
                                    bestSeller2 = reply.getSender();
                                    bestHari2 = harike;
                                }
                            }

                        }
                        repliesCnt++;
                        if (repliesCnt >= sellerAgents.length) {
                            if (bestSeller == null) {
                                bestPrice = bestPrice2;
                                bestSeller = bestSeller2;
                                bestHari = bestHari2;
                            }
                            // We received all replies
                            step = 2;
                        }
                    } else {
                        block();
                    }
                    break;
                case 2:
                    // Send the purchase order to the seller that provided the best offer
                    ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                    order.addReceiver(bestSeller);
                    order.setContent(targetNamaTravel);
                    order.setConversationId("travel-trade");
                    order.setReplyWith("order" + System.currentTimeMillis());
                    myAgent.send(order);

                    // Prepare the template to get the purchase order reply
                    mt = MessageTemplate.and(
                            MessageTemplate.MatchConversationId("travel-trade"),
                            MessageTemplate.MatchInReplyTo(order.getReplyWith()));
                    step = 3;
                    break;
                case 3:
                    // Receive the purchase order reply
                    reply = myAgent.receive(mt);
                    if (reply != null) {
                        // Purchase order reply received
                        if (reply.getPerformative() == ACLMessage.INFORM) {
                            // Purchase successful. We can terminate                       
                            System.out.println(targetNamaTravel
                                    + " order berhasil dari agent "
                                    + reply.getSender().getName());
                            System.out.println("Biaya = " + bestPrice + " Hari " + hari[bestHari]);
                            myAgent.doDelete();
                        } else {
                            System.out.println("Order gagal: travel yang dituju sudah habis diorder.");
                        }

                        step = 4;
                    } else {
                        block();
                    }
                    break;
            }
        }

        public boolean done() {
            if (step == 2 && bestSeller == null) {
                System.out.println("Attempt failed: " + targetNamaTravel + " not available for sale");
            }
            return ((step == 2 && bestSeller == null) || step == 4);
        }
    }  // End of inner class RequestPerformer

}

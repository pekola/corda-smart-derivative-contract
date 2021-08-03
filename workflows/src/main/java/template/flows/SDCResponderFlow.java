package template.flows;// Add this import:
import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;
import template.flows.SDCFlow;

/**
 * Class ResponderFlow basically accepts and does nothing else so far
 *
 * @Author: Peter Kohl-Landgraf
 */
@InitiatedBy(SDCFlow.class)
public class SDCResponderFlow extends FlowLogic<String> {
    private final FlowSession otherPartySession;

    public SDCResponderFlow(FlowSession otherPartySession) {
        this.otherPartySession = otherPartySession;
    }

    @Suspendable
    @Override
    public String call() throws FlowException {
        subFlow(new ReceiveFinalityFlow(otherPartySession));
        String msg = "Confirmed Transaction - " + otherPartySession.getCounterpartyFlowInfo().toString();
        return msg;
    }
}

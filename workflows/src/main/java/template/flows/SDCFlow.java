package template.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.StateRef;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import template.contracts.SDCContract;
import template.states.SDCProcessState;

@StartableByRPC
@InitiatingFlow
@SchedulableFlow

/**
 * Class SDCFlow specifies the corda specific flow to update states and distribute and finalize across the joined nodes
 *
 * @author Peter Kohl-Landgraf
 */

public class SDCFlow extends FlowLogic<String> {
    //private final SDCProcessState.EventKey eventKey;
    private final StateRef stateRef;
    private final Party otherParty;
    private final SDCProcessState.ProcessEvent processEvent;
    /**
     * The progress tracker provides checkpoints indicating the progress of
     the flow to observers.
     */
    private final ProgressTracker progressTracker = new ProgressTracker();

    public SDCFlow(Party otherParty, SDCProcessState.ProcessEvent processEvent, StateRef stateRef) {
        this.processEvent = processEvent;
        this.otherParty = otherParty;
        this.stateRef = stateRef;
    }

    /**
     * Ininitaing a Flow from console. Input State is set to null, Start Event is TRADE_INCEPTION
     * @param otherParty
     */
    public SDCFlow(Party otherParty) {
        this.processEvent = SDCProcessState.ProcessEvent.TRADE_INCEPTION;
        this.otherParty = otherParty;
        this.stateRef = null;
    }


    /**
     * The flow logic is encapsulated within the call() method.
     */
    @Suspendable
    @Override
    public String call() throws FlowException {

            if ( getOurIdentity().equals(otherParty))
                return "State is skipped";

            // We retrieve the notary identity from the network map.
            Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

            SDCProcessState outputState =new SDCProcessState(getOurIdentity(), otherParty,this.processEvent);

            // We create the transaction components.
            Command command = new Command<>(new SDCContract.Commands.Process(), getOurIdentity().getOwningKey());

            // We create a transaction builder and add the components.
            TransactionBuilder txBuilder = new TransactionBuilder(notary)
                    .addOutputState(outputState, SDCContract.ID)
                    .addCommand(command);

            if ( this.stateRef != null ){
                StateAndRef<SDCProcessState> input = getServiceHub().toStateAndRef(stateRef);
                txBuilder.addInputState(input);
            }

            // Signing the transaction.
            SignedTransaction signedTx = getServiceHub().signInitialTransaction(txBuilder);

            // Creating a session with the other party.
            FlowSession otherPartySession = initiateFlow(otherParty);

            // We finalise the transaction and then send it to the counterparty.
            subFlow(new FinalityFlow(signedTx, otherPartySession));
        String msg = "SDC-Event: " + this.processEvent.name() + " processed on node " + getOurIdentity().getName().getOrganisation()+ " - sendTo " + otherParty.getName().getOrganisation() + " for confirmation";
        return msg;

    }

}
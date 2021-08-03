package template.states;

import template.contracts.SDCContract;
import net.corda.core.contracts.*;
import net.corda.core.flows.FlowLogicRefFactory;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;
import net.corda.core.serialization.CordaSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * Class implements a SchedulableState for a state in the sdc process
 * The state definition is event driven, i.e. the state is defined by the event which has ocurred
 * In the overwritten method nextScheduledActivity the state transition logic is defined
 *
 * @Author: Peter Kohl-Landgraf
 *
 */

@BelongsToContract(SDCContract.class)
public class SDCProcessState implements SchedulableState {

    public ProcessEvent getProcessEvent() {
        return processEvent;
    }

    public Party getPartyA() {
        return partyA;
    }

    public Party getPartyB() {
        return partyB;
    }


    /**
     * Some of the process logic is taken from {@link <a href="https://github.com/finmath/finmath-smart-derivative-contract/blob/master/src/main/java/net/finmath/smartcontract/statemachine/SmartContractStateMachine.java">HTTP/1.1 documentation</a>}.
     */
    @CordaSerializable
    public static enum ProcessEvent{
        TRADE_INCEPTION,
        START_PREFUNDING_PERIOD,
        PREFUNDING_CHECK,
        START_NEW_PROCESS_CYCLE,
        VALUATION,
        SETTLEMENT_CHECK,
        SETTLEMENT_TRANSACTION,
        TERMINATION_CAUSED_BY_INSUCCFICIENT_PREFUNDING,
        TERMINATION_CAUSED_BY_INSUFFICIENT_MARGIN,
    }


    //private variables
    private ProcessEvent processEvent;
    private Party partyA;
    private Party partyB;
    private final Instant stateActivationTime;


    private Instant    getNextScheduledEventTime(){
        Instant nextEventTime = this.stateActivationTime;
        if (this.processEvent == ProcessEvent.TRADE_INCEPTION) {
            return this.stateActivationTime.plusSeconds(5);
        }
        else if (this.processEvent == ProcessEvent.START_PREFUNDING_PERIOD) {
            return this.stateActivationTime.plusSeconds(5);
        }
        else if (this.processEvent == ProcessEvent.PREFUNDING_CHECK) {
            return this.stateActivationTime.plusSeconds(5);
        }
        else if (this.processEvent == ProcessEvent.START_NEW_PROCESS_CYCLE) {
            return this.stateActivationTime.plusSeconds(10);
        }
        else if (this.processEvent == ProcessEvent.VALUATION) {
            return this.stateActivationTime.plusSeconds(2);
        }
        else if (this.processEvent == ProcessEvent.SETTLEMENT_CHECK) {
            return this.stateActivationTime.plusSeconds(2);
        }
        else if (this.processEvent == ProcessEvent.SETTLEMENT_TRANSACTION) {
            return this.stateActivationTime.plusSeconds(5);
        }
        return nextEventTime;
    }

    public SDCProcessState(Party partyA, Party partyB, ProcessEvent processEvent) {
        this.processEvent = processEvent;
        this.partyA = partyA;
        this.partyB = partyB;
        this.stateActivationTime = Instant.now();
    }

    @ConstructorForDeserialization
    public SDCProcessState(Party partyA, Party partyB, ProcessEvent processEvent, Instant stateActivationTime ) {
        this.processEvent = processEvent;
        this.partyA = partyA;
        this.partyB = partyB;
        this.stateActivationTime = stateActivationTime;
    }


    /* This method will indicate who are the participants and required signers when
     * this state is used in a transaction. */
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(partyA,partyB);
    }

    // Defines the scheduled activity to be conducted by the SchedulableState.
    @Nullable
    @Override
    public ScheduledActivity nextScheduledActivity(@NotNull StateRef thisStateRef, @NotNull FlowLogicRefFactory flowLogicRefFactory) {
        // We get the time when the scheduled activity will occur in the constructor rather than in this method. This is
        // because calling Instant.now() in nextScheduledActivity returns the time at which the function is called, rather
        // than the time at which the state was created.
        ProcessEvent nextProcessEvent = null;
        if (this.processEvent == ProcessEvent.TRADE_INCEPTION) {
            nextProcessEvent = ProcessEvent.START_PREFUNDING_PERIOD;
        }
        else if (this.processEvent == ProcessEvent.START_PREFUNDING_PERIOD) {
            nextProcessEvent = ProcessEvent.PREFUNDING_CHECK;
        }
        else if (this.processEvent == ProcessEvent.PREFUNDING_CHECK) {
            if (true) {
                nextProcessEvent = ProcessEvent.START_NEW_PROCESS_CYCLE;
            }
            else
                nextProcessEvent = ProcessEvent.TERMINATION_CAUSED_BY_INSUCCFICIENT_PREFUNDING;
        }
        else if (this.processEvent == ProcessEvent.START_NEW_PROCESS_CYCLE) {
            nextProcessEvent = ProcessEvent.VALUATION;
        }
        else if (this.processEvent == ProcessEvent.VALUATION) {
            nextProcessEvent = ProcessEvent.SETTLEMENT_CHECK;
        }
        else if (this.processEvent == ProcessEvent.SETTLEMENT_CHECK) {
            double valuationAmount = Math.random();
            double marginBalance = 0.75;
            if (valuationAmount <= marginBalance) {
                nextProcessEvent = ProcessEvent.SETTLEMENT_TRANSACTION;
            }
            else
                nextProcessEvent = ProcessEvent.TERMINATION_CAUSED_BY_INSUFFICIENT_MARGIN;
        }
        else if (this.processEvent == ProcessEvent.SETTLEMENT_TRANSACTION) {
            nextProcessEvent = ProcessEvent.START_PREFUNDING_PERIOD;
        }
        else if (this.processEvent == ProcessEvent.TERMINATION_CAUSED_BY_INSUCCFICIENT_PREFUNDING || this.processEvent == ProcessEvent.TERMINATION_CAUSED_BY_INSUFFICIENT_MARGIN)
            return null;

        if (nextProcessEvent != null) {
            final Instant nextActivityTime = this.getNextScheduledEventTime();
            return new ScheduledActivity(flowLogicRefFactory.create("template.flows.SDCFlow", partyB, nextProcessEvent, thisStateRef), nextActivityTime);
        }
        else
            return null;
    }
}


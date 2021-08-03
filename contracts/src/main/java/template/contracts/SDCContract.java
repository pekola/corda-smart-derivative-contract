package template.contracts;

import kotlin.jvm.internal.Lambda;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import template.states.SDCProcessState;

import static net.corda.core.contracts.ContractsDSL.requireThat;

/**
 * Class SDCContrac implements a Contract in the corda framework
 * Only basic tests are performed on a ledger transaction
 *
 * @Author: Peter Kohl-Landgraf
 *
 */
public class SDCContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String ID = "template.contracts.SDCContract";

    // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
    // does not throw an exception.
    @Override
    public void verify(LedgerTransaction tx) {

        /* We can use the requireSingleCommand function to extract command data from transaction.
         * However, it is possible to have multiple commands in a signle transaction.*/
        final CommandData commandData = tx.getCommands().get(0).getValue();

        if (commandData instanceof Commands.Process) {
            //Retrieve the output state of the transaction
            final SDCProcessState output = tx.outputsOfType(SDCProcessState.class).get(0);

            //Using Corda DSL function requireThat to replicate conditions-checks
            requireThat(require -> {
                require.using("At Trade Inception input state does not exist ", output.getProcessEvent() == SDCProcessState.ProcessEvent.TRADE_INCEPTION ? tx.getInputStates().size() == 0 : true);
                require.using("Currently there should be only one output state ", tx.getOutputStates().size() == 1);
                return null;
            });
        }

    }

    public interface Commands extends CommandData {
        class Process implements SDCContract.Commands {}
        //In our hello-world app, We will only have one command.
        /*class Initiate implements SDCContract.Commands {}
        class Validate implements SDCContract.Commands {}
        class Incept implements SDCContract.Commands {}
        class CheckBuffer implements SDCContract.Commands {}
        class PerformValuation implements SDCContract.Commands {}
        class PerformSettlement implements SDCContract.Commands {}
        class PerformTermination implements SDCContract.Commands {}
        class StartPrefunding implements SDCContract.Commands {}
        class ClosePrefunding implements SDCContract.Commands {}*/
    }
}
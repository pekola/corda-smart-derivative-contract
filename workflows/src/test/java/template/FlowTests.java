package template;

import com.google.common.collect.ImmutableList;
import template.flows.SDCFlow;
import template.states.SDCProcessState;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.testing.node.MockNetwork;
import net.corda.testing.node.MockNetworkParameters;
import net.corda.testing.node.StartedMockNode;
import net.corda.testing.node.TestCordapp;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Future;

/**

 * First dummy on a Mockup Network, taken from Cordapp Template
 *
 * @author Peter Kohl-Landgraf
 */

public class FlowTests {
    private MockNetwork network;
    private StartedMockNode a;
    private StartedMockNode b;

    @Before
    public void setup() {
        network = new MockNetwork(new MockNetworkParameters().withCordappsForAllNodes(ImmutableList.of(
                TestCordapp.findCordapp("template.contracts"),
                TestCordapp.findCordapp("template.flows"))));
        a = network.createPartyNode(null);
        b = network.createPartyNode(null);
        network.runNetwork();
    }

    @After
    public void tearDown() {
        network.stopNodes();
    }

    @Test
    public void dummyTest() {
        SDCFlow flow = new SDCFlow(b.getInfo().getLegalIdentities().get(0), SDCProcessState.ProcessEvent.TRADE_INCEPTION,null);
        Future<?> future = a.startFlow(flow);
        network.runNetwork();
        QueryCriteria inputCriteria = new QueryCriteria.VaultQueryCriteria().withStatus(Vault.StateStatus.UNCONSUMED);
        SDCProcessState stateB = b.getServices().getVaultService().queryBy(SDCProcessState.class,inputCriteria).getStates().get(0).getState().getData();
        Assert.assertNotNull(stateB);

    }
}

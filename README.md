<!--<p align="center">
  <img src="https://www.corda.net/wp-content/uploads/2016/11/fg005_corda_b.png" alt="Corda" width="500">
</p>-->

# CorDapp for Smart Derivative Contract (SDC) in Java 

This is a simple first cordapp application for a Smart Derivative Contract. 
It is based on the following paper [paper](https://papers.ssrn.com/sol3/papers.cfm?abstract_id=3163074)
The corda implementation makes use of Corda's ``SchedulableState`` Logic to implement an scheduled based process logic between the participating parties.
In the demo the scheduling is based on a relative scheduled where the next event takes place serveral seconds after the preceeding one. 
The code was derived from the Java Cordapp Template
which can be cloned [here](https://github.com/corda/cordapp-template-java/).

# Pre-Requisites

See https://docs.corda.net/getting-set-up.html.

# Usage - Build and start an SDCFlow
1. Start gradle build script: ``gradlew deployNodes``. This takes a while. Get your coffee
2. Go to folder ``build/nodes/`` and start ``runnodes``. Three console windows will appear, one for each node. Wait until each node is started.
3. Go to console of BankA and type: ``start SDCFlow otherParty: BankB``
4. Trade Inception Flow will start and wait until it is finalised
5. Type: ``flow watch`` and you should see the process events appearing. Up to some random event the process will continue.
6. Go to other console of Bank B and type ``flow watch`` you will see the acceptance flows
7. Since jdbc is configured you might want to look inside DB. To get things started look [here](https://training.corda.net/prepare-and-discover/see-db/)

## List of open issues
* This is rather a demo application which only sends messages. Fully implement SDC Process Logic and according state transition checks
* Full Flow Design for manual triggered inception phase
* Include valuation process either from or by direkt usage of finmath library
* Notray node has nothing to do, set this node up
* Configure 4th node for account manager to handle account transaction management even with regard to additional buffer account
* To persist state objects in the database let ``SDCProcessState`` implement ``QueryableState`` - generateMappedObject, first link [here]()

# Usage - Doc from Cordapp Sample

## Running tests inside IntelliJ
	
We recommend editing your IntelliJ preferences so that you use the Gradle runner - this means that the quasar utils
plugin will make sure that some flags (like ``-javaagent`` - see below) are
set for you.

To switch to using the Gradle runner:

* Navigate to ``Build, Execution, Deployment -> Build Tools -> Gradle -> Runner`` (or search for `runner`)
  * Windows: this is in "Settings"
  * MacOS: this is in "Preferences"
* Set "Delegate IDE build/run actions to gradle" to true
* Set "Run test using:" to "Gradle Test Runner"

If you would prefer to use the built in IntelliJ JUnit test runner, you can run ``gradlew installQuasar`` which will
copy your quasar JAR file to the lib directory. You will then need to specify ``-javaagent:lib/quasar.jar``
and set the run directory to the project root directory for each test.

## Running the nodes

See https://docs.corda.net/tutorial-cordapp.html#running-the-example-cordapp.

## Interacting with the nodes

### Shell

When started via the command line, each node will display an interactive shell:

    Welcome to the Corda interactive shell.
    Useful commands include 'help' to see what is available, and 'bye' to shut down the node.
    
    Tue Nov 06 11:58:13 GMT 2018>>>

You can use this shell to interact with your node. For example, enter `run networkMapSnapshot` to see a list of 
the other nodes on the network:

    Tue Nov 06 11:58:13 GMT 2018>>> run networkMapSnapshot
    [
      {
      "addresses" : [ "localhost:10002" ],
      "legalIdentitiesAndCerts" : [ "O=Notary, L=London, C=GB" ],
      "platformVersion" : 3,
      "serial" : 1541505484825
    },
      {
      "addresses" : [ "localhost:10005" ],
      "legalIdentitiesAndCerts" : [ "O=PartyA, L=London, C=GB" ],
      "platformVersion" : 3,
      "serial" : 1541505382560
    },
      {
      "addresses" : [ "localhost:10008" ],
      "legalIdentitiesAndCerts" : [ "O=PartyB, L=New York, C=US" ],
      "platformVersion" : 3,
      "serial" : 1541505384742
    }
    ]
    
    Tue Nov 06 12:30:11 GMT 2018>>> 

You can find out more about the node shell [here](https://docs.corda.net/shell.html).

### Client

`clients/src/main/java/com/template/Client.java` defines a simple command-line client that connects to a node via RPC 
and prints a list of the other nodes on the network.

#### Running the client

##### Via the command line

Run the `runTemplateClient` Gradle task. By default, it connects to the node with RPC address `localhost:10006` with 
the username `user1` and the password `test`.

##### Via IntelliJ

Run the `Run Template Client` run configuration. By default, it connects to the node with RPC address `localhost:10006` 
with the username `user1` and the password `test`.

### Webserver

`clients/src/main/java/com/template/webserver/` defines a simple Spring webserver that connects to a node via RPC and 
allows you to interact with the node over HTTP.

The API endpoints are defined here:

     clients/src/main/java/com/template/webserver/Controller.java

And a static webpage is defined here:

     clients/src/main/resources/static/

#### Running the webserver

##### Via the command line

Run the `runTemplateServer` Gradle task. By default, it connects to the node with RPC address `localhost:10006` with 
the username `user1` and the password `test`, and serves the webserver on port `localhost:10050`.

##### Via IntelliJ

Run the `Run Template Server` run configuration. By default, it connects to the node with RPC address `localhost:10006` 
with the username `user1` and the password `test`, and serves the webserver on port `localhost:10050`.

#### Interacting with the webserver

The static webpage is served on:

    http://localhost:10050

While the sole template endpoint is served on:

    http://localhost:10050/templateendpoint
    
# Extending the template

You should extend this template as follows:

* Add your own state and contract definitions under `contracts/src/main/java/`
* Add your own flow definitions under `workflows/src/main/java/`
* Extend or replace the client and webserver under `clients/src/main/java/`

For a guided example of how to extend this template, see the Hello, World! tutorial 
[here](https://docs.corda.net/hello-world-introduction.html).

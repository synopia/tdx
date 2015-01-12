# tdx
# behavior trees framework

Lets begin with something really simple.

    BehaviorTreeBuilder treeBuilder = new BehaviorTreeBuilder();
    BehaviorNode node = treeBuilder.fromJson("{ sequence:[ success, success, failure ] }");

    System.out.println(new DefaultBehaviorTree(node, null).step());              // outputs "FAILURE"
    System.out.println(new Assembler("Test", node).createInstance(null).step()); // outputs "FAILURE"

We create a BehaviorTreeBuilder and use it to create the actual tree from its json representation.
This tree is then stepped one tick using two different methods.

The first one just evaluates the tree and modifies its internal state.
The second way uses an Assembler to create a jvm class and instances of this class.

The result is the same for both evaluation models (FAILURE). This is because success and failure nodes
returns its value immediatly, while the sequence loops through all successful children.

To build anything useful its necessary to create some custom actions.

    public static class Print extends BaseAction {
        private String msg;

        @Override
        public BehaviorState modify(Actor actor, BehaviorState result) {
            System.out.print(msg);
            return BehaviorState.SUCCESS;
        }
    }

    treeBuilder.registerAction("print", Print.class);
    node = treeBuilder.fromJson("{ sequence:[ success, { print:{msg:world} } ] }");

Run this tree and you get 'world' printed to your console. Since we use gson, the msg property is
populated automatically. Action classes should be considered stateless, which means you cannot store
any values at the action class directly. Instead you need to store everything at the actor instance.
This way, actions can be shared between multiple instances of a behavior tree.

    public static class Delay extends BaseAction<DefaultActor<?>> {
        private float duration;

        @Override
        public void construct(DefaultActor<?> actor) {
            actor.setValue(getId(), duration);
        }

        @Override
        public BehaviorState modify(DefaultActor<?> actor, BehaviorState result) {
            System.out.print(".");
            float timeRemaining = actor.getValue(getId());
            timeRemaining -= actor.getDelta();
            actor.setValue(getId(), timeRemaining);
            return timeRemaining>=0 ? BehaviorState.RUNNING : BehaviorState.SUCCESS;
        }
    }

    treeBuilder.registerAction("print", Print.class);
    treeBuilder.registerAction("delay", Delay.class);
    DefaultActor actor = new DefaultActor();
    actor.setDelta(0.1f);
    node = treeBuilder.fromJson("{ sequence:[ success, { delay:{duration:1}}, { print:{msg:Hello} }, { delay:{duration:1}}, { print:{msg:World} } ] }");
    DefaultBehaviorTree tree = new DefaultBehaviorTree(node, actor);
    for (int i = 0; i < 100; i++) {
        tree.step();
    }

The delay action needs to store the time its running for at the actor. Here we use the DefaultActor
to store everything.

There are four methods you can override in the Action interface:

* construct(Actor) - called once at the beginning
* prune(Actor):boolean - called every update tick. Return true, to prevent calling the node's internal update.
* modify(Actor, BehaviorState):BehaviorState - is called right after the node's internal update. You can modify the result here.
* destruct(Actor):BehaviorState - called once after this node switched from running state to any other.

So far, we only used a sequence with a couple of action nodes. Much more powerful is the Decorator,
which is an action with a child node. Now, the decorator can modify the result of the child (and the
child may be a whole behavior tree, of course).

    public static class Repeat extends BaseAction<DefaultActor<?>> {
        private int count;

        @Override
        public void construct(DefaultActor<?> actor) {
            actor.setValue(getId(), count);
        }

        @Override
        public BehaviorState modify(DefaultActor<?> actor, BehaviorState result) {
            if( result==BehaviorState.SUCCESS ) {
                int remaining = actor.getValue(getId());
                remaining--;
                actor.setValue(getId(), remaining);
                return remaining>0 ? BehaviorState.RUNNING : BehaviorState.SUCCESS;
            }
            return result;
        }
    }

    treeBuilder.registerDecorator("repeat", Repeat.class);
    node = treeBuilder.fromJson("{ sequence:[ {repeat :{ count:5, child:{print:{msg:x}}}}, success, { delay:{duration:1}}, { print:{msg:Hello} }, { delay:{duration:1}}, { print:{msg:World} } ] }");

The repeat decorator will overwrite the success signal from its child 5 times with RUNNING.

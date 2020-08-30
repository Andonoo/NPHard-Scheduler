# Algorithm Implementation

## Data Structures
Originially, GraphStream data structures were used in storing the edges and nodes of the input. However, it soon became clear that having an own implementation of a Node class would be more efficient, as we wanted a minimal representation for a task. 

GraphStream nodes and edges simply had attributes that were associable to itself in the form of a key value pair - this was unclear and difficult to use. 
As we create a large number of task nodes during our search for the optimal schedule, information required from GraphStream's graph form was extracted and parsed into our own TaskNode class. This was done through the DomainHandler.
## Pseudo Code

## Milestone 1 Algorithm Implementation
In order to achieve an efficient DFS algorithm, the team determined that we would need to find a reasonable bound value as quickly as possible. As certain DFS paths could lead
to 'dumb' schedulings, for example all tasks assigned to one processor unnecessarily, we figured a heuristic-based approach should be used to find a starting bound value. 
It was decided that we would implement a greedy scheduler, based on an earliest available processor heuristic. Prior to implenting in code, the following pseudo code was
written:

``` Let input graph G = (V, E)
Let dependencies (edges) D = adjacency list where D{Node} = {Dependencies of Node}

Find topological order on G:
	While V is not empty do
		Find vertex v from V which has no outgoing edges
		Pop v from V
		Add v to topOrder
	Reverse topOrder
	
Schedule based on ‘earliest start’ heuristic:
  Let P = {P0, P1, … Pn} 
  Let Pi = {t0, t1, … tn} where tj is the j’th task scheduled on Pi
  Let earliestStartProc = P0
  Let firstFreeProcessor = P1  //added this to optimise incase we have heaps of processors

  For task t in topOrder:
    For p in (P up to firstFreeProcessor) :
      if startTime(p, t) < startTime(earliestStart, t)
        earliestStartProc = p
    Add t to earliestStartProc with associated end time
    Update firstFreeProcessor if we’ve scheduled t on an empty processor

// Function which determines earliest possible scheduling of t in process p
startTime(p, t) {
  let d = D[t] // i.e the tasks which t is dependent on
  int startTime = p.lastTask.endTime
	
  for dependency d’ in d:
    if d’.process != p:
      startTime = max{ startTime, d’.endTime + w(d’, t) }

return startTime 
}
```
The general approach was to first find a topological order of the input tasks using Kahn's algorithm. The algorithm then iterates over these tasks one by one, scheduling 
them on the earliest availible processor. Although there were a few changes made to the algorihm during code implementation, the logic remained the same.

This produced valid schedulings on all given inputs.

## Milestone 2 Algorithm Implementation

### Search Strategy
The starting point for the algorithm implementation was the core DFS search logic. The two standard approaches to a DFS search are as follows:
  * Recursive - Generally uses a self-calling method, where each call to the method represents a 'branching' down the search tree.
  * Iterative - Generally uses a stack. On each iteration a node is popped from the stack and expanded into its child nodes, which are then pushed into the stack.
  
It was decided that an iterative approach would be taken in implementing the DFS. Although recursive DFS algorithms tend to run faster than iterative, they are unbounded in
the amount of memory which they use. As the algorithm would be used on large inputs, it was determined that avoiding these potential memory issues would be the safest
approach. 

Again, the team started by generating pseudo code for the algorithm which can be seen below.

```
Int boundVal = validScheduleFromMilestone1.totalLength();
Stack<PartialSchedule> schedules = new Stack<PartialSchedule>();
for (rootNode: rootNodes) {
  schedules.push(new PartialSchedule(root nodes)) // i.e pushing in a partial schedule for every root node
}

// Completing search
While (!schedules.isEmpty()) {
  PartialSchedule partialSchedule = orders.pop();
  childSchedules = partialSchedule.getChildren();
  schedules.push((partialSchedule));
}

return currentSolution;
```

In the pseudo code above, much of the complexity (branching, pruning and bounding) is hidden within the getChildren() method. In the actual implementation, most of 
this complexity is too hidden within the createChildren() method of PartialSchedule. However, the actual pruning is completed within the same scope of the above pseudo code.

### Branching
In the implementation, the bounding logic is encapsualated within the PartialSchedule class. This class represents two main abstractions, a schedule and a scheduling. The 
first abstraction represents state and logic pertaining to a partial schedule, as a node in the search tree. This include information such as the individual processor 
schedules, as well as useful information such as current schedule length. The second abstraction represents information pertaining to the assignment of a task to a 
processor, which essentially led to the creation of the PartialSchedule itself. This could also be thought of as the difference between this PartialSchedule and it's
parent PartialSchedule which created it. 

The actual branching of a PartialSchedule into it's child PartialSchedules is completed within the createChildren method. This essentially iterates through every combination of
a task to a processor, and generates a new PartialSchedule for that child.

### Pruning

#### Pruning Processor Search Space
In order to implement the processor pruning, every PartialSchedule keeps track of its 'first' empty processor. In the createChildren method, the PartialSchedule then
generates child PartialSchedules considering only that __one__ empty processor, along with those processors which already have tasks assigned to them. This means that
a task will never be assigned to two empty processors within the createChild() method. This successfully reduced the size of the search space, significanly improving
the efficiency of the algorithm.

#### Pruning Task Search Space
In order to avoid exploring duplicate PartialSchedules a check was implemented which tests if a PartialSchedule has been explored already before pushing it into the search stack. This took 4 attempts to get working correctly, which are described as follows:
* Storing explored PartialSchedules in a set - This very quickly led to memory issues and so was discarded.
* Storing hashes of PartialSchedules in a set - This initially seemed promising as it significantly reduced runtime but it was eventually discovered that this caused issues due to the colliding nature of hashes. i.e multiple PartialSchedules had the same hash code.
* Storing PartialSchedule unique IDs in a set - Again, this seemed promising and was even committed to master. However it was later found that on very memory intensive input graphs this led to overflow. The IDs used are described below.
* The above, with the addition of the set resetting occasionally - In order to address the memory overflow issue mentioned above, a check was added which reset the set if free memory reduced to under 100mb. This allowed the algorithm to maintain the benefit of reduced search space on most input graphs, without causing memory issues on very large inputs. It is predicted that this will also lead to performance increases on very large inputs, as it will reduce duplicate PartialSchedules within localized parts of the search space. 

The above checks were implemented as a simple if statement check, prior to pushing a child into the search tree in the DFS algorithm.

#### Pruning Based on Bottom Level Estimates
In order to further improve efficiency, a check was used to determine if a PartialSchedule was worth expanding into it's children. This was acheived by using a brute force assignment of remaining task weights to the earliest finishing processor, from largest weight to smallest (ignoring dependencies). This allows the algorithm to eliminate some PartialSchedules which are very obviously not worth expanding. This was implemented in PartialSchedules estimateFinish() method which is called upon creation of a PartialSchedule. The DFS algorithm then checks this estimate prior to pushing a child into the search tree, and if it is greater than our currently found best the child
is discarded.

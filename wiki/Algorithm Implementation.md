# Algorithm Implementation

## Data Structures

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

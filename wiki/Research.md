# Research
This section of the wiki outlines the research conducted by the team prior to working on implementation.

## Concept of Branch and Bound

![Three main sections of branch and bound](../img/Branch%20and%20bound%20concept.png "Three main sections of branch and bound")

*Note:* From this diagram and initial research, the team determined three necessary steps in order to determine an optimal solution for scheduling tasks: 
1. Searching strategy 
2. Branching strategy
3. Pruning strategy

## 1. Searching strategy 
As this is the core of the algorithm, the team determined that we should first research and select an ideal searching strategy.
Possible implementations are as follows: 

### Back-tracking branch-and-bound with DFS
Traverse through one stream of the tree to create a valid solution.
Backtrack from the very last task to continue creating valid solutions.
Use bounding to stop traversing through a sub-tree if it is already longer than a known solution.

### Greedy Algorithm
Pick the next node based on a condition.
Performance of this algorithm will rely heavily on the greedy heuristic.
Potential greedy conditions:
Lowest estimated schedule length (given in project description. - kind of implies that this is the only good one to use)
Could use a best-first search to search for the node with the greedy condition.

http://www.jcomputers.us/vol7/jcp0711-28.pdf

### Other potential algorithms
These algorithms were researched but decided were not appropriate, as there was no necessity for time bounding in this project.

#### TB-GBFS: Time-bounded greedy best first search
In order to implement TBA* (see below), we need to implement this first so this could potentially be another starting point.

http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.1014.4927&rep=rep1&type=pdf

#### TB-WA*: Time-bounded Weighted A*
I’m guessing here our cost function f() would be the cost of the current path + underestimate of bottom level. Not sure how we’d get our bottom level estimate
After trying to understand this I’m not actually sure the time-bounded part helps in our scenario. Might be mistaken but I think it’s more applicable when you need part of your solution in real time - like in a video game where you want the AI to start moving before it knows its complete path

http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.1014.4927&rep=rep1&type=pdf

### Combination of DFS and A*
Because the performance of DFS is dependent on the length of our initial schedule we could potentially include the logic of A* in this initial search?
This might have the potential to gain some of the speed benefits of A* without running into the memory problems

### Selected Search Strategy
From these options, we decided to go ahead with a DFS searching strategy and avoided A* due to the potential memory issues we could run into on larger inputs.
Given the client's timeframe, this was decided the safer option. If the team were to run into memory issues, there may not be enough time to resolve this. 

## 2. Branching Strategy
Every node in the search space can be represented as a partial scheduling of tasks to processors. Therefore the natural branching strategy is to exhaustively allocate each 'available task' to every processor, where available tasks are those which have all of their dependency tasks already scheduled. The allocation of a single task to a single processor represents one child node of the previous partial schedule. This is demonstrated below.

![](https://i.imgur.com/Fl42HTz.png)

## 3. Pruning Strategies
In order to effectively bound the search space, the team researched a number of potential pruning strategies. These are summarized below.

### Pruning the processor search space
The first and most obvious way to prune the search space would be to reduce the potential allocations of tasks to processors. With the standard exhaustive allocation of tasks to processors, there will be many equivalent partial schedules generated. This is because the assignment of a task to 2 different empty processors would be considered 2 nodes in the search tree. If we are able to eliminate these duplicates, the search space of the tree would be decreased drastically. This could likely be acheived by only ever assigning a task to a single empty processor when branching.

### Pruning the task allocation search space
Even after pruning the processor search space it is likely that duplicate, equivalent, nodes will still be generated. In order to further reduce the size of the search tree, we will need to use some mechanism of tracking explored nodes. This could be acheived using some form of set which contains nodes which have already been explored. 

### Bottom level estimations
In order to avoid branching down paths which are definitely not going to yield a new most optimal schedule, some form of basic estimation should be used to determine if a partial schedule is worth branching into. This could be achieved by taking the remaining tasks to be scheduled, and using their weights to approximate an underestimate of a given partial schedules full length (the length which it will be once all tasks are scheduled). If this full length is greater than our currently found best, we can bound the search and again reduce the search space. 

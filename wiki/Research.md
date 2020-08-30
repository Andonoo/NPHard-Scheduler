# Research
This section of the wiki outlines the research conducted by the team prior to working on implementation.

## Concept of Branch and Bound

![Three main sections of branch and bound](https://github.com/SoftEng306-2020/project-1-project-1-team-11/blob/milestone2-wiki/wiki/Branch%20and%20bound%20concept.png "Three main sections of branch and bound")

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

As a DFS searching strategy was decided, in addition for it being an exhaustive search, 




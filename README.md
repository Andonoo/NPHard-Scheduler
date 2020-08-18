# Scheduling Problem by 🥇SINNEN 11🥇
<img src="img/sinnen11.png" alt="Aimeos logo" title="Aimeos" align="right" height="70" />
This project aims to solve an NP-hard problem which involves dividing multiple tasks into
different processors whilst minimising the execution time of the program.

## Implementation technologies

**Programming language 🌏🛬**  
This project is implemented in **Java** (compatible with Java 1.8)

**OS 🐧🔍**  
The targeted OS is Linux but also works under Windows.  

**Input 📥**  
The input is a graph and the number of processors on which to schedule the graph as command
line parameters.  The graph is in dot format (.dot).  
e.g 
```
digraph "example" {
	a       [Weight=2];
	b       [Weight=3];
	a -> b  [Weight=1];
	c       [Weight=3];
	a -> c  [Weight=2];
	d       [Weight=2];
	b -> d  [Weight=2];
	c -> d  [Weight=1];
}
```

**Output 🚪**  
Output file is in dot format (.dot). It is essentially a copy of the input with two added 
attributes to each task: start time and allocated processor.  
e.g
```
digraph "example" {
	a       [Weight=2, Start=0, Processor=1];
	b       [Weight=3, Start=2, Processor=1];
	a -> b  [Weight=1];
	c       [Weight=3, Start=4, Processor=2];
	a -> c  [Weight=2];
	d       [Weight=2, Start=7, Processor=2];
	b -> d  [Weight=2];
	c -> d  [Weight=1];
}
```
## How to run
To clone, you'll need [Git](https://git-scm.com)  
```bash
# Clone this repository
$ git clone https://github.com/SoftEng306-2020/project-1-project-1-team-11.git
```
Next build the package with Maven and navigate to the target folder
```
# Go into the repository
$ cd project-1-project-1-team-11

# Go into target folder
$ cd target/
```  
You will see the following file:

`project1-1.0-jar-with-dependencies`.

Finally, use the following line to run:

```
java -jar scheduler.jar INPUT.dot P [OPTION]
```
Adjust command with the following:

```
INPUT.dot   a task graph with integer weights in dot format (file path)
P           number of processors to schedule the INPUT graph on
```

```
Optional:
-p N        use N cores for execution in parallel (default is sequential)
-v          visualise the search
-o OUTPUT   output file is named OUTPUT (default is INPUT-output.dot)
```
An example would be:
```
java -jar scheduler.jar example-dot-files/example1.dot 2 -v
```

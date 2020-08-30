# Input parsing 📥

### Reading from the command line

Running the `jar` file in the command line requires the user to pass in their arguments. There are both required and optional parameters that can be passed into it. The user can run the jar as the following:

`java -jar [jar name] [input file name] [Number of processors]`**`[OPTION]`**

**Options:**
<pre>
 −p <b>N</b>         use <b>N</b> cores for execution in parallel (default is sequential)
 -v           visualises the search
 -o <b>OUTPUT</b>    output file name is named <b>OUTPUT</b> (default is named INPUT-output.dot)
</pre>


An example of this command would be:
`java -jar scheduler.jar example1.dot 4 -v -p 4`

We have implemented our own `InputHandler` which handles and stores all the information from the command line. It also throws a `CommandLineException` if there is a parameter is passed tha tis not expected.

**Reading the input file**

To read the input (.dot file), we decided to use a dynamic graph library called 
[GraphStream](http://graphstream-project.org/).  
Using GraphSteam gave us multiple benefits:
+ Enabled us to have access to the input graph nodes, which saves us from writing the node 
implementation ourselves.
+ No need to use complex regex to parse the input. GraphStream provides an elegant solution
to read files using FileSource 

# Creating an output file 📤

[GraphStream](http://graphstream-project.org/) is also used to handle the output file. After executing the algorithm, the graph provided by Graphstream is updated with the optimal schedule information then converted into a .dot file. The `OutputHandler` formats the output file into the desired format.
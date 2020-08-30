# Visualisation Research  👀

**Requirements:**
- The user needs to be provided with a live visual feedback about the search
- The presented information should be meaningful and a reflection of the search, not just showing a progress bar or
  similar.
  
 _The exact visualisation is open-ended and is up to the project team to design and implement_
 
 ## Features 👁
 
 While looking through our algorithm, we found there were many things that we could
 show the user. We could have shown everything, but we needed to keep a balance in showing
 the user sufficient and interesting information without making the visualisation too cluttered.
 
 There were two 'sections' to our visualisation:
 1. Text based information
 2. Graphical information
 
 ### Text Based Information 📜
 These text based information could be easily shown with just text and gave meaningful information.
 This included:
 - Current status - Running or Done
 - Number of processors the user specified the scheduling to run on
 - Number of cores the user specified to run the program
 - Number of searches made
 - Input graph name
 - Current best length the algorithm has found
 
 ### Graphical Information 📊
 Only showing text can be very boring for the user and can easily be made much easier to understand
 the process with some graphical elements.
 We decided to have two main graphical components that will be prominent in the window.
 - Gantt Chart of the current best schedule
 - Visual representation of the input graph
 
 ### Performance 🚕
 It is worthy to note that constantly updating these elements will affect the performance of the algorithm.
 Running the same algorithm without the visualisation will allow the program to run faster. 
 We tried to reduce the performance impact by making sure the chart only updates if it changes. 
 
 ### Parallelisation 🔐
 It was crucial that the visualisation worked when the algorithm is running both sequentially and in parallel.
 In order to do this we had to make sure that shared variables such as search count were volatile and synchronised
 so that there is no update loss.
 
 ## Libraries 📚
 To develop our GUI, we used **JavaFX**. This made it easy to update information and show the GUI in a popup window.
 Scenebuilder was used to design the GUI and saved us from writing css.
 
 **Graphstream** was used to generate the input graph. To include it in our GUI, a snapshot of the graph is taken, 
 saved as a PNG and added into an image viewer.
 
 
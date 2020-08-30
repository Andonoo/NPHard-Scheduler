# Parallelisation of Search 🔐

The scheduler is implemented so that whilst performing the branch-and-bound search, multiple cores and multiple threads can be working concurrently.

### Multithreading strategy

The ForkJoinPool object, an ExecutorService, defines the parallelism of the scheduler - this is how many cores can be used during this concurrent action. 
It manages all the threads so that they can be correctly queued.

Initially, one task (labelled SearchTask) is invoked that will begin the search on PartialSchedules involving the root nodes. Our algorithm builds a list of PartialSchedules that 
can be branched, therefore each thread's delegated work can be quantified by a volatile number of PartialSchedules. We utilise this by declaring a minimum condition for which we 
decide that the number of PartialSchedules delegated to the thread is too much. After several tests with different sized graphs, we have labelled this number to be 10.

In simpler terms, whenever a thread is delegated too much work (the number of PartialSchedules it needs to search through is greater than 10), then we delegate one of 
these schedules to another thread and queue it using the ForkJoinPool.

### Shared data access

Whilst all the cores are running, each thread, upon creation, gains local access to a global solution and a global bound. These two fields define the best completed partial schedule
at the current point in the search. When a thread discovers a completed search that is more optimal than the global solution, it needs to update these global fields so that
this global bound can synchronised/shared for the current/present threads.

Similar to that of a lock, threads can call a synchronised method which will acquire a lock for the global fields, update these global fields with the thread's new optimal
solution, and then releases the lock after completing it's task.

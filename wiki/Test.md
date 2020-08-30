# Testing 🔬
Testing is an important phase in the waterfall development process. Initially, we had planned for unit tests, 
integration tests, smoke tests, and speed tests. However, as the implementation changed over this process, the tests
also need to change to as a result. 

The [JUnit](https://junit.org/junit4/) framework was used for the testing of our program. JUnit allowed the tests to be 
repeated easily, and it was easy to set up. The tests mainly used `assertEquals` to ensure that the expected output 
matches the actual output. 

The tests were split up into 2 categories:
+ Optimality tests
+ I/O tests

## Optimality tests
Tests that the finishing time produce by our scheduler was the optimal finishing time. These tests ensured that it would
produce the optimal finished times for any type of valid graph.

## I/O tests
Tests that our program behaves properly when the user inputs incorrect parameters. Graphstream was used to parse the
input dot file so no file parsing tests were done as it is assumed that the 3rd party library was working as it should.
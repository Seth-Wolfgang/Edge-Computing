This project is meant to benchmark the feasability of Raspberry Pis as an edge computing device.

https://ieeexplore.ieee.org/document/10187384

The tests to be run should be loaded into `trials.txt`
Usage for trials.txt:

Test;Size;Iterations;Clients

1;1;25;1  
Test 1: OCR  
Size: Small  
Iterations: 25  
Clients: 1  
  
1;3;25;1  
Test 1: OCR
Size: Large  
Iterations: 25  
Clients: 1  
  
2;1;25;10  
Test 2: Smith-Waterman  
Size: Medium  
Iterations: 25  
Clients: 10  
  
3;1;100;3  
Test 3: Logistic Regression  
Size: Small  
Iterations: 100  
Clients: 3  




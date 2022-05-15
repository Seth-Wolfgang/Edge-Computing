This project is for research/learning purposes. 


This project is meant to test the feasibility of Raspberry Pis (or other machines) in 
their capability to process data and send it back to a server. The edge server holds the 
input data The clients need and the server receives the output data.

-Main.java is meant to be run on one machine. It will run every part of this project together. 

-ComponentMains is meant to be run on many machines. One server, at least one edge server, and many clients
 should be used.

To run correctly:
1. Run ServerMain
2. Run EdgeServerMain
3. Run ClientMain

*Note IP addresses may need to be changed in order for this to work

Dependencies can be installed by running the dependencyInstaller.sh file. This will install
git, tesseract-ocr, and openjdk-17. It is meant to be any easy to use download tool for not only
getting the required software for this project, but also a lightweight download tool for the project
itself.

For up-to-date progress, see my Trello board for this project
https://trello.com/b/y01w4qjI/research-tasks

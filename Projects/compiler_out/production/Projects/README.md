#DESCRIPTION
This script will compile your code,  create the trace file, and zip up the .java and .class needed
to submit the project. Due to technology limitations it will neither write your code nor your report
for you.

#HOW TO USE THIS SCRIPT
1.) Copy files to your project folder as demonstrated below with the scripts    right outside the ir folder.
    ── curlie-science
    ├── ir <----
    ├── proj0_sa46979_code.zip
    ├── proj0_sa46979_trace.txt
    ├── sources.txt
    ├── stopwords.txt
    ├── submit.py   <----
    └── zip_shell.sh <----

2.) Let the system know where to find your script so you can run it on the terminal.
    a.) Run: export PATH=$PATH:<path to script folder> on the terminal
    b.) To avoid doing this every time you open a new terminal store this command in your .bashrc file
        run vi ~/.bashrc and paste command in a.) there
3.) Now run the script like:
    zip_shell -e <your eid> -n <project number> -m <the main class> -l <modified or added files>

###Explanation:
- project number, e.g -n 0, for project 0
- main class, class with the 'main' method
- modified or added files, if I changed InvertedIndex.java and created a new file  called MatrixMultiply.java. Create a text file containing text called sources.txt for example:
			InvertedIndex.java
			MatrixMultiply.java 
The text file should be in the same directory as the script.
	── curlie-science
	├── ir 
	├── proj0_sa46979_code.zip
	├── proj0_sa46979_trace.txt
	├── sources.txt  <-----
	├── stopwords.txt
	├── submit.py   
	└── zip_shell.sh 
	pass to script argument as -l sources.txt

YOUR FEEDBACK IS APPRECIATED. HAVE FUN

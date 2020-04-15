# JetBrainsPlugin
The project provides a basic Jetbrains plugin which wil give a set of metrics for a specified file. 
The plugin is written in Kotlin and supports both Kotlin and Java.

#How it works
1. Navigate to the file that you want to see the metrics of.
2. Select "Tools" > "MethodInspector"
3. A pop-up will display that the plugin is working as expected and is generating a report.
4. The report will be saved in the same directory as the Java/Kotlin file.

#Reports
Report generated for a Kotlin file will be shown as the example below.
```
# Method Inspector Report 
## Class: Test.kt 
-----
 
### Method main: 
Parameters: [args]  
Number of lines: 2 
Has method description: false 
```

Report generated for a Java file will be shown as the example below.
```
# Method Inspector Report 
## Class: Main.java 
-----
 
### Method main: 
   Return type: void
   Parameter list: (String[] args) 
   number of lines: 6 
   Cyclomatic Complexity: 2 
```

#Authors

|Mahmoud Alhumsi Alrefaai | Jason Bloom  |
|-------------------------|--------------|
|Cheyenne Slager          | Sheeraz Zadi |    


#Resources
The following resources will provide information needed in this project:

* IntelliJ Platform SDK docs: https://www.jetbrains.org/intellij/sdk/docs/intro/welcome.html
* An introduction to actions: https://www.jetbrains.org/intellij/sdk/docs/basics/action_system.html
* PSI: https://www.jetbrains.org/intellij/sdk/docs/basics/architectural_overview/psi.html
* Coding guidelines: https://www.jetbrains.org/intellij/sdk/docs/basics/intellij_coding_guidelines.html
* Plugin development forum: https://intellij-support.jetbrains.com/hc/en-us/community/topics/200366979-IntelliJ-IDEA-Open-API-and-Plugin-Development

The commit messages will adhere to the standards specified here:

* Commit message template: https://gist.github.com/zakkak/7e06725ebd1336bfebebe254de3de82
* 5 Tips for effective commit messages: https://www.youtube.com/watch?v=9Siot_y9wY8
# JUnit Webtesting

Spring Application that allows execution of JUnit Tests at Runtime by writing code in a Web-based Editor.


## Usage


![Code Editor Image](https://github.com/mesalamat/JUnitWebTesting/blob/master/codeditor.png "Code Editor Image!")

The Code Editor is just like your usual Code Editor, but not an IDE whatsoever.
It's based on [CodeMirror](https://codemirror.net/). To submit your code, you just have to press the ![Submit](https://github.com/mesalamat/JUnitWebTesting/blob/master/submit.png "Submit Button") Button.

The Application will then compile the Java code into a class locally(obviously this isn't very safe) & run the Tests on it via JUnit Code. You can use any JUnit Annotations you would normally use. **Also supports Lombok Annotations**.

Furthermore it prints out whether the Test was successful or not, or whether Compilation failed into the Console Log.
![Console Log](https://github.com/mesalamat/JUnitWebTesting/blob/master/consolelog.png "Console Log")
## Used Libraries

- JUnit, 
- Spring Boot 
- Thymeleaf 
- Lombok

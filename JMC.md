# Jdk Mission Control (JMC)

` JMC is use for trace JVM latency spikes, GC pauses, high CPU, OOM, and poor throughput.
   This is open-source tool.`

 `JCM is used the extract the data from JFR(JDK Flight Recoder).`
 
     JFR → open-sourced since Java 11
     Java Mission Control → open-source (EPL)

### Download (8.3.1)
     https://www.oracle.com/java/technologies/javase/products-jmc8-downloads.html

### extract JMC folder to:     
      C:\Program Files\Java\

### Final structure must be exactly:
        C:\Program Files\Java\
         └─ jmc-9.1.1_windows-x64
            └─ JDK Mission Control
               ├─ plugins
               ├─ features
               ├─ configuration
               ├─ jmc.exe
               ├─ jmc.ini
               └─ jmcc.exe

### Create shortcut at desktop
    jmc.exe

### If start issue
  Follow the below steps
  
    Open Command Prompt and run:
        where java
        where javaw
        java -version
        
    Check the path of “javaw”    
      >where javaw
      C:\Program Files\Common Files\Oracle\Java\javapath\javaw.exe
      C:\Program Files\Java\jdk-17\bin\javaw.exe

    Edit this file:
      C:\Program Files\Java\jmc-9.1.1_windows-x64\JDK Mission Control\jmc.ini

      Add these lines at the VERY TOP:
        -vm
        C:\Program Files\Java\jdk-17\bin\javaw.exe
        

 ## Practical recommendation (what most teams do)
     Scenario	             Recommended JMC
     JDK 8–11	             JMC 8.3.x
     JDK 17 (Windows)	     JMC 8.3.x ✅
     JDK 17 (Linux/macOS)	 JMC 9.1.x
     JDK 21	                 JMC 9.1.x




    

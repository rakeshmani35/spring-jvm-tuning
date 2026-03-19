# Jdk Mission COntroll

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


## Analyze the JFR in JMC (step-by-step)
Open JMC </br>
Open JMC → “File” → “Open File…” → select recording.jfr </br>

The 4 beginner tabs to focus on </br>
Different JMC versions name tabs slightly differently, but look for: </br>
1.	CPU / Method Profiling
 - o	Goal: find which methods consume CPU time
 - o	Red flags: </br>
      - high CPU in JSON serialization, logging, regex, or unnecessary work </br>
2.	Memory / Allocation
 - o	Goal: see what allocates the most objects/bytes
 - o	Red flags: </br>
   - extremely high allocation rate (creates GC pressure)
   - lots of byte[], char[], String, or repeated temporary objects </br>
3.	Garbage Collections
 - o	Goal: see GC pause times and frequency
 - o	Red flags: </br>
   - frequent pauses
   - pauses that correlate with slow JMeter latency spikes
4.	Threads / Locks (Java Monitor Blocked / Contention)
 - o	Goal: see if threads are waiting on locks
 - o	Red flags: </br>
   -	many threads blocked
   -	slowdowns while CPU is not maxed (often contention) </br>
   
Simple interpretation rule:
 - •	If latency is high and CPU is high → you’re CPU-bound (optimize code, reduce work)
 - •	If latency is high and GC pauses are frequent/long → memory/GC-bound (reduce allocations or adjust heap/GC)
 - •	If latency is high and threads are blocked → contention/bottleneck (locks, DB pool, thread pool, synchronized code)



    

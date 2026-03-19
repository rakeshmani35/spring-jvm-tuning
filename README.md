## JVM performance test step-by-step
    build a tiny Spring Boot 3 app on JDK 17
    run a JMeter load test
    collect JFR (JMC tool) + GC logs (GC analzer tool)
    read them and do a simple tuning loop

## Baseline command line
    -Xms512m 
    -Xmx512m 
    -XX:+UseG1GC 
    -XX:MaxGCPauseMillis=200 
    -Xlog:gc*:file=logs\gc.log:time,uptime,level,tags:filecount=5,filesize=20m
    -XX:StartFlightRecording=filename=recordings\app.jfr,settings=profile,dumponexit=true,maxage=1h,maxsize=250M
    -jar target/*.jar

## G1 pause target (common first knob)
        -XX:MaxGCPauseMillis=200
This asks G1 to aim for ~200ms pauses (not guaranteed).

## Tune attempt #1 (bigger heap + pause goal)
   Heap sizing
   If you see frequent GC + rising latency: </br>

   Try increasing heap: </br>
       `  from -Xms512m -Xmx512m → -Xms1g -Xmx1g`

    -Xms1g
    -Xmx1g 
    -XX:+UseG1GC 
    -XX:MaxGCPauseMillis=200 
    -Xlog:gc*:file=logs\gc.log:time,uptime,level,tags:filecount=5,filesize=20m
    -XX:StartFlightRecording=filename=recordings\app.jfr,settings=profile,dumponexit=true,maxage=1h,maxsize=250M
    -jar target/*.jar


## JMeter Dashboard
•	Cannot analyze JFR files </br>
•	Cannot analyze GC log files </br>
•	Only understands JMeter test results (JTL)
   - o	Response time
   - o	Throughput
   - o	Errors
   - o	Percentiles </br>
👉 JMeter Dashboard = load-test metrics only

## JDK Mission Control (JMC) 
•	Required (or equivalent tool) to analyze JFR </br>
•	Provides: </br>
   - o	CPU hotspots
   - o	Allocation profiling
   - o	Thread states
   - o	Lock contention
   - o	Safepoints
   - o	GC events (high level) </br>
👉 JMC = JFR analysis dashboard
    
## GC Log Analysis Tools (separate from JMeter)
GC logs need dedicated GC analyzers, for example:
 - •	JMC (basic GC insights via JFR events)
 - •	GCViewer / GCeasy (more detailed GC tuning analysis) </br>
👉 GC logs ≠ JFR ≠ JMeter


## Tool Responsibility Matrix (very important)
| Tool                     | JMeter Results | JFR   | GC Logs              |
| ------------------------ | -------------- | ----- | -------------------- |
| **JMeter Dashboard**     | ✅ Yes          | ❌ No  | ❌ No                 |
| **JDK Mission Control**  | ❌ No           | ✅ Yes | ⚠️ Partial (via JFR) |
| **GC Log Analyzers**     | ❌ No           | ❌ No  | ✅ Yes                |
| **Prometheus + Grafana** | ❌ No           | ❌ No  | ⚠️ Metrics only      |


## Correct File ↔ Tool Mapping
| **Tools**               | **File type**                  | **What it contains**                                   | **Purpose**                      |
| ----------------------- | ------------------------------ | ------------------------------------------------------ | -------------------------------- |
| **Prometheus**          | HTTP metrics (no file)         | JVM heap, GC pauses(count, total time), CPU, threads, memory usage        | Continuous JVM health monitoring |
| **Grafana**             | Queries data sources (no file) | Time-series data from Prometheus or logs               | Visualization & dashboards       |
| **Apache JMeter**       | `.jmx`                         | Load-test definition (threads, samplers, assertions)   | Define and execute load tests    |
| **Apache JMeter**       | `.jtl`                         | Test results (latency, throughput, errors)             | Analyze load-test results        |
| **JDK Mission Control** | `.jfr`                         | JVM profiling events (CPU, allocation, locks, threads) | Root-cause performance analysis  |
| **GC Analyzer**         | `gc.log`                       | Garbage collection events, pause times, GC type, heap sizes     | GC tuning & memory optimization  |

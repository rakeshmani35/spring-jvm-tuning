## JVM performance test step-by-step
    build a tiny Spring Boot 3 app on JDK 17
    run a JMeter load test
    collect JFR + GC logs
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
   If you see frequent GC + rising latency:

    Try increasing heap:
        from -Xms512m -Xmx512m → -Xms1g -Xmx1g

    -Xms1g
    -Xmx1g 
    -XX:+UseG1GC 
    -XX:MaxGCPauseMillis=200 
    -Xlog:gc*:file=logs\gc.log:time,uptime,level,tags:filecount=5,filesize=20m
    -XX:StartFlightRecording=filename=recordings\app.jfr,settings=profile,dumponexit=true,maxage=1h,maxsize=250M
    -jar target/*.jar

# Spring Boot 3 JVM Tuning Demo

A beginner-friendly project that shows how to combine:

- Spring Boot 3 + Maven
- JDK Flight Recorder (JFR)
- JDK Mission Control (JMC)
- Prometheus + Grafana
- Apache JMeter load testing

The application has CPU-heavy and memory-heavy endpoints so you can see how the JVM behaves under load.

---

## 1) What you need

- JDK 21+
- Maven 3.9+
- Docker Desktop or Docker Engine + Docker Compose
- Apache JMeter 5.6+
- JDK Mission Control (JMC)

---

## 2) Project structure

```text
jvm-tuning/
├── docker-compose.yml
├── monitoring/
│   ├── prometheus.yml
│   └── grafana/
│       ├── dashboards/
│       │   └── jvm-tuning-dashboard.json
│       └── provisioning/
│           ├── dashboards/
│           │   └── dashboards.yml
│           └── datasources/
│               └── prometheus.yml
├── jmeter/
│   └── jvm-tuning.jmx
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── java/
        └── resources/
```

---

## 3) Build the application

From the project root:

```bash
mvn clean package
```

If build succeeds, Maven creates:

```bash
target/jvm-tuning-0.0.1-SNAPSHOT.jar
```

---

## 4) Run the application

### Simple run

```bash
mvn spring-boot:run
```

### Recommended run for tuning practice

This run uses a small heap so that GC and memory pressure become visible faster.

```bash
java \
  -Xms256m \
  -Xmx256m \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -Xlog:gc*:file=gc_logs\gc.log:time,uptime,level,tags:filecount=5,filesize=20m  \
  -XX:StartFlightRecording=filename=jfr_recordings\app.jfr,settings=profile,dumponexit=true,maxage=1h,maxsize=250M \
  -jar target/jvm-tuning-0.0.1-SNAPSHOT.jar
```

```vm arguments (intellij)
-Xms256m 
-Xmx256m 
-XX:+UseG1GC 
-XX:MaxGCPauseMillis=200 
-Xlog:gc*:file=gc_logs\gc.log:time,uptime,level,tags:filecount=5,filesize=20m 
-XX:StartFlightRecording=filename=jfr_recordings\app.jfr,settings=profile,dumponexit=true,maxage=1h,maxsize=250M 
-jar target/*.jar

```


Open:

- http://localhost:8080/api/hello
- http://localhost:8080/actuator/health
- http://localhost:8080/actuator/prometheus

---

## 5) Start Prometheus and Grafana

In a second terminal:

```bash
docker compose up -d
```

Open:

- Prometheus: http://localhost:9090
- Grafana: http://localhost:3001

Grafana login:

- Username: `admin`
- Password: `admin`

The Prometheus data source and a starter dashboard are provisioned automatically.

---

## 6) Generate manual load without JMeter first

Open these endpoints a few times in your browser or curl.

### CPU-heavy endpoint

```bash
curl "http://localhost:8080/api/cpu/primes?limit=25000"
```

### Sort endpoint

```bash
curl "http://localhost:8080/api/cpu/sort?items=2500"
```

### Temporary allocation endpoint

```bash
curl "http://localhost:8080/api/memory/allocate?sizeMb=8&iterations=10"
```

### Retain memory endpoint

This intentionally keeps memory alive so you can observe heap growth.

```bash
curl "http://localhost:8080/api/memory/retain?sizeMb=10"
```

Check retained state:

```bash
curl "http://localhost:8080/api/memory/state"
```

Clear retained memory:

```bash
curl -X DELETE "http://localhost:8080/api/memory/retain"
```

---

## 7) Run JMeter load test

## GUI mode: to setup and generate load test file (.jmx)
## Non-GUI mode: to run the test from command line (recommended for accurate results)

### GUI mode

1. Open JMeter.
2. Load `jmeter/jvm-tuning.jmx`.
3. Click **Start**.
4. Watch Grafana and Prometheus while the test runs.

         ## OR
1. open JMeter bin path in windows cmd
2. run bat file
   C:\apache-jmeter-5.6.3\bin>jmeter.bat
3. Do the setup and generate the .jmx file and run the test
4. OR import (if available) the .jmx file and run the test

### Non-GUI mode (recommended)

```bash
jmeter \
  -n \
  -t jmeter/jvm-tuning.jmx \
  -l jmeter/results.jtl \
  -e -o jmeter/report
```
```
C:\apache-jmeter-5.6.3\bin>jmeter.bat -n -t "E:/workspace/spring-jvm/jvm-tuning/jmeter/jvm-tuning-demo.jmx" -l E:/workspace/spring-jvm/jvm-tuning/results.jtl -e -o E:/workspace/spring-jvm/jvm-tuning/report

Note: 
report folder should be empty before running the test, otherwise you will get an error like this:
"Error: Report output folder is not empty: E:/workspace/spring-jvm/jvm-tuning/report"
```
```
| Flag | Meaning               |
| ---- | --------------------- |
| `-n` | Non-GUI mode          |
| `-t` | Test plan (.jmx file) |
| `-l` | Results file (.jtl)   |
| `-e` | Generate HTML report  |
| `-o` | Report output folder  |

```

Open the generated HTML report:

```text
jmeter/report/index.html
```

---

## 8) Record JFR while load is running

First find the Java process id:

```bash
jcmd
```

You will see something like:

```text
12345 jvm-tuning-0.0.1-SNAPSHOT.jar
```

Start a 2-minute recording:

```bash
jcmd 12345 JFR.start \
  name=demo \
  settings=profile \
  duration=2m \
  filename=demo-recording.jfr
```

Check status:

```bash
jcmd 12345 JFR.check
```

Stop recording early if needed:

```bash
jcmd 12345 JFR.stop name=demo
```

Dump recording manually if you started one without filename:

```bash
jcmd 12345 JFR.dump name=demo filename=demo-recording.jfr
```

---

## 9) Open the recording in JMC

1. Start JDK Mission Control.
2. Open `demo-recording.jfr`.
3. Focus on these tabs first:
   - **Automated Analysis**
   - **Threads**
   - **Memory**
   - **Garbage Collections**
   - **Method Profiling**
   - **Latency**
4. When CPU load is high, check hot methods.
5. When retained memory grows, check object allocation and GC behavior.

---

## 10) What to observe in Grafana

Look at these panels:

- **HTTP requests/sec**
- **HTTP p95 latency**
- **Heap used**
- **Retained demo memory**
- **Process CPU usage**
- **GC pauses/sec**

### Simple interpretation guide

- If **requests/sec** goes up but **p95 latency** also goes up sharply, CPU or GC may be the bottleneck.
- If **heap used** keeps growing after the test, check retained objects and memory leaks.
- If **GC pauses/sec** spikes during memory tests, your heap may be too small or allocation rate too high.
- If **CPU usage** is near max during prime calculation, the workload is CPU-bound.

---

## 11) Beginner tuning experiments

Try one change at a time.

### Experiment A: Small heap

```bash
java -Xms128m -Xmx128m -jar target/jvm-tuning-0.0.1-SNAPSHOT.jar
```

Expected:
- More frequent GC
- Higher pause activity
- Higher latency during memory load

### Experiment B: Larger heap

```bash
java -Xms512m -Xmx512m -jar target/jvm-tuning-0.0.1-SNAPSHOT.jar
```

Expected:
- Fewer collections
- More stable latency
- More memory headroom

### Experiment C: Fixed heap with G1 goals

```bash
java \
  -Xms256m \
  -Xmx256m \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=100 \
  -jar target/jvm-tuning-0.0.1-SNAPSHOT.jar
```

Compare:
- p95 latency
- GC pause activity
- Throughput under JMeter

### Experiment D: Simulated memory pressure

Keep calling:

```bash
curl "http://localhost:8080/api/memory/retain?sizeMb=10"
```

Then compare JFR + Grafana behavior against:

```bash
curl -X DELETE "http://localhost:8080/api/memory/retain"
```

---

## 12) A safe beginner tuning workflow

1. **Measure first** using metrics and JFR.
2. **Change one JVM option only.**
3. **Run the same JMeter test again.**
4. **Compare latency, throughput, heap, and GC.**
5. Keep only changes that clearly help.

Do not tune many JVM flags at once. For beginners, heap size and GC behavior give the most value.

---

## 13) Good first Prometheus queries

### Request rate

```promql
sum(rate(http_server_requests_seconds_count{application="jvm-tuning"}[1m]))
```

### p95 latency

```promql
histogram_quantile(0.95, sum(rate(http_server_requests_seconds_bucket{application="jvm-tuning"}[1m])) by (le, uri, method))
```

### Heap used

```promql
jvm_memory_used_bytes{application="jvm-tuning", area="heap"}
```

### GC pause count rate

```promql
rate(jvm_gc_pause_seconds_count{application="jvm-tuning"}[1m])
```

### Demo retained memory

```promql
demo_retained_memory_bytes{application="jvm-tuning"}
```

---

## 14) Common beginner mistakes

- Looking only at CPU and ignoring latency
- Changing many JVM flags together
- Testing without a repeatable load tool
- Using a single request and calling it performance testing
- Ignoring GC metrics
- Increasing heap without checking whether it actually improves latency

---

## 15) Next things to learn after this demo

- Thread dumps with `jcmd`
- Heap dumps for leak analysis
- Comparing G1 vs ZGC on larger heaps
- Async profiling and flame graphs
- Baseline vs regression performance testing in CI

---

## 16) Clean up

Stop application with `Ctrl+C`.

Stop Prometheus and Grafana:

```bash
docker compose down
```

---

## 17) Quick start recap

```bash
mvn clean package
java -Xms256m -Xmx256m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -jar target/jvm-tuning-0.0.1-SNAPSHOT.jar
# new terminal
docker compose up -d
# new terminal
jmeter -n -t jmeter/jvm-tuning.jmx -l jmeter/results.jtl -e -o jmeter/report
# new terminal
jcmd
jcmd <PID> JFR.start name=demo settings=profile duration=2m filename=demo-recording.jfr
```


# JMeter setup steps
##  Prerequisites (Windows)
    •	Download JDK 17 (Temurin / Oracle)
    •	Set JAVA_HOME
    •	Add %JAVA_HOME%\bin to PATH

## Download Apache JMeter
    1.	Go to: https://jmeter.apache.org/download_jmeter.cgi
    2.	Download Binaries → zip
    3.	Extract to: C:\tools\apache-jmeter-5.6.3

## Start JMeter (GUI mode – first time)
  JMeter GUI mode used to do setup for load testing (prepare load test) and Non-GUI mode use load-test and generate report.
  
    •	From Command Prompt (not PowerShell):
        cd C:\tools\apache-jmeter-5.6.3\bin>jmeter.bat
JMeter UI opens. </br>
Set test plan 
### ----------------------------------------------------------

# Prepare Load Testing

## Create the Test Plan (GUI)
    •	File → New
    •	Rename Test Plan → SpringBoot-jvm-test
save it

## Add Thread Group
    Right-click Test Plan →
    Add → Threads (Users) → Thread Group
    
    Set:
    Setting	Value
    Number of Threads	100
    Ramp-Up Period	20
    Loop Count	-1 (infinite)
    💡 Stop test manually after 3–5 minutes.

## Add HTTP Request
    Right-click Thread Group →
    Add → Sampler → HTTP Request
    
    Set:
    Field	Value
    Protocol	http
    Server Name	localhost
    Port	8080
    Method	GET
    Path	/api/test

## Add HTTP Header Manager (Optional)
    Right-click HTTP Request →
    Add → Config Element → HTTP Header Manager
    Add:
    Content-Type: application/json

## Add Timers (Important for Stable RPS)  
    Constant Throughput Timer
    Right-click Thread Group →
    Add → Timer → Constant Throughput Timer
    
    Set:
    Field	Value
    Target Throughput	30000
    Calculate Throughput	All active threads
    30000 / 60 = 500 RPS

## Add Listeners
    1️⃣ Summary Report
    Right-click Thread Group →
    Add → Listener → Summary Report
    
    2️⃣ Aggregate Report
    Right-click Thread Group →
    Add → Listener → Aggregate Report

### ----------------------------------------------------

## Run in Non-GUI
   open new CMD (path: E:\workspace\spring-jvm-test\JvmTest\ where want to generate result)
   

       cd C:\tools\apache-jmeter-5.6.3\bin>jmeter.bat -n -t E:\workspace\spring-jvm-test\JvmTest\SpringBoot-jvm-test.jmx -l E:\workspace\spring-jvm-test\JvmTest\results.jtl -e -o E:\workspace\spring-jvm-test\JvmTest\report

                                               OR one by one
       cd C:\tools\apache-jmeter-5.6.3\bin>jmeter.bat -n -t "E:\workspace\spring-jvm-test\JvmTest\SpringBoot-jvm-test.jmx" -l E:\workspace\spring-jvm-test\JvmTest\results.jtl   
       cd C:\tools\apache-jmeter-5.6.3\bin>jmeter.bat -g E:\workspace\spring-jvm-test\JvmTest\results.jtl -o E:\workspace\spring-jvm-test\JvmTest\report
    

 <img width="854" height="336" alt="image" src="https://github.com/user-attachments/assets/c263f391-de77-42e3-bdf6-120eb1a2682e" />

**JMeter produced:**

E:\workspace\spring-jvm-test\JvmTest\
- **results.jtl** → raw test results
- **report/**
  - content
  - sbadmin2-1.0.7
  - index.html → HTML dashboard (click)
  - statistics

  When you start JMeter in non-GUI mode, everything defined in the test plan (SpringBoot-jvm-test.jmx) is executed, and the report is generated     

 ## After execution in Non-GUI mode
  index.html → HTML dashboard (click) </br>
  JMeter HTML Dashboard: it comes from report/index.html
   <img width="944" height="482" alt="image" src="https://github.com/user-attachments/assets/43da3e84-4559-40a2-a32c-b304d35afc43" />


### Execution truth table (very important)
| Scenario         | Test runs   | results.jtl | HTML report  |
| ---------------- | ----------  | ----------- | ------------ |
| `-n -t` only     | ✅         | ❌          | ❌           |
| `-n -t -l`       | ✅         | ✅          | ❌           |
| `-n -t -l -e -o` | ✅         | ✅          | ✅           |
| `-g -e -o`       | ❌         | (input)      | ✅           |



### Run in GUI mode
Add a Listener </br>
  - Right-click on Test Plan or Thread Group <br>
  - Add → Listener → View Results Tree </br>
  - Add → Listener → Summary Report </br>
  - Add → Listener → Aggregate Report </br>
click on 'run'

 View Results Tree
<img width="959" height="357" alt="image" src="https://github.com/user-attachments/assets/76654173-6252-4a49-b7ad-f0d098a30768" />

Summary Report
<img width="959" height="282" alt="image" src="https://github.com/user-attachments/assets/6a78823e-a66d-49b8-a21c-e7fc28da611d" />

Aggregate Report
<img width="959" height="317" alt="image" src="https://github.com/user-attachments/assets/cb3e2574-ec02-49ba-a5cf-ffff7afebb6e" />


### Different purpose
| Mode    | Purpose                    |
| ------- | -------------------------- |
| GUI     | Test design & debugging    |
| Non-GUI | Load execution & reporting |

      

### <--------------- fix issue ----------------->
## if html file not generate: (report/index.html)
Add in jmeter.properties file
    jmeter.save.saveservice.output_format=csv
    jmeter.save.saveservice.print_field_names=true
    
    jmeter.save.saveservice.time=true
    jmeter.save.saveservice.elapsed=true
    jmeter.save.saveservice.label=true
    jmeter.save.saveservice.response_code=true
    jmeter.save.saveservice.response_message=true
    jmeter.save.saveservice.thread_name=true
    jmeter.save.saveservice.data_type=true
    jmeter.save.saveservice.success=true
    jmeter.save.saveservice.failure_message=true
    jmeter.save.saveservice.bytes=true
    jmeter.save.saveservice.sent_bytes=true
    jmeter.save.saveservice.grp_threads=true
    jmeter.save.saveservice.all_threads=true
    jmeter.save.saveservice.latency=true
    jmeter.save.saveservice.connect_time=true
    jmeter.save.saveservice.assertions=true
    
  Re-run again  


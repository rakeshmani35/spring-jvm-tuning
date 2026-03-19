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
    •	JMeter UI opens.

### ----------------------------------------------------------

# Prepare Load Testing
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
   open CMD

       cd C:\tools\apache-jmeter-5.6.3\bin>jmeter.bat -n -t "C:\test\SpringBoot Load Test.jmx" -l C:\test\results.jtl -e -o C:\test\report
                                               OR
       cd C:\tools\apache-jmeter-5.6.3\bin>jmeter.bat -n -t "C:\test\SpringBoot Load Test.jmx" -l C:\test\results.jtl   
       cd C:\tools\apache-jmeter-5.6.3\bin>jmeter.bat -g C:\test\results.jtl -o C:\test\report
    

    

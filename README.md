# Rate Quote for Loan
by João Carlos (https://www.linkedin.com/in/joaocarlosvale/)

This project consists of an application to find a quote from Zopa’s market of lenders for 36-month loans that apply 
interest on a monthly basis.

## Technologies used:
* Java
* Maven 

## Example:

Files: 

  Some examples in the _resources_ folder.
  
Run:

    java -jar target/ zopa-rate-0.0.1.jar market.csv 1000
    
### Output

Requested amount: £1000 <br>
Annual Interest Rate: 7.0% <br>
Monthly repayment: £30.78 <br>
Total repayment: £1108.10 <br>


## Commands:

To generate JAR:

    mvn clean package

To run:

    java -jar target/ zopa-rate-0.0.1.jar [market_file_path] [loan_amount]
    
To run tests:

    mvn test

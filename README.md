# CodeCoverageHelper

## About The Project

Simple applicataion that can be used in conjunction with CI.
Application should get the ID of last deployment with ANT tool (see `validate` target in `build.xml`).
Then with ID it should retrieve specific info from Salesforce metadata API and calculate code coverage.
Purpose of the application is to have "early warning system" if code drops below eg. 80%, or to simply enforce higher (than standrad 75%) code coverage value. 

## Getting Started

This tool will work only if you do have Salesforce Sandbox/Production.
Tool can be attached to CI system (eg. GitLab, BitBucket) however this is optional, as tool can be launched manually

### Prerequisites

You'll need to have JDK installed. Anything above 1.8 should suffice, however newest is recommended.

### Installation

1. Clone this repository by running command 
   ```sh
   git clone https://github.com/lciesielski/CodeCoverageHelper.git
   ```
2. Run `javac` command in cloned repository folder
   ```sh
   javac -cp .;lib\* -target 1.8 -source 1.8 GetCodeCoverage.java
   ```
3. (Optional) You can test it by running compiled class manually with `java.exe`
   ```sh
   java -cp .;lib\* GetCodeCoverage "YOUR_SF_USERNAME" "YOUR_SF_PASSWORD_AND_TOKEN" "SF_SOAP_PARTNER_WSDL_ENDPOINT" "MINIMUM_COVERAGE" "DEPLOYMENT_ID" "IS_DEBUG_FLAG"
   ```
   eg.
   ```
   java -cp .;lib\* GetCodeCoverage "test.agent@qa.com" "Test123Token123" "https://test.salesforce.com/services/Soap/u/51.0" "80.5" "0Af5r000004EFIO" "false"
   ```
4. Run jar command to package the code with manifest
   ```
   jar cmf MANIFEST.MF get-code-coverage.jar GetCodeCoverage.class
   ```

<!-- USAGE EXAMPLES -->
## Usage

You can run your jar with `java.exe` with `-jar` option eg.
   ```sh
   java -jar get-code-coverage.jar "YOUR_SF_USERNAME" "YOUR_SF_PASSWORD_AND_TOKEN" "SF_SOAP_PARTNER_WSDL_ENDPOINT" "MINIMUM_COVERAGE" "DEPLOYMENT_ID" "IS_DEBUG_FLAG"
   ```
   eg.
   ```
   java -jar get-code-coverage.jar "test.agent@qa.com" "Test123Token123" "https://test.salesforce.com/services/Soap/u/51.0" "80.5" "0Af5r000004EFIO" "false"
   ```

<!-- ROADMAP -->
## Roadmap

The idea is to have as lightweight application as possible therefore currently priority would be to slim down libraries to only required components (and ditch out unused rest)
## Outage

Outage is a tool for outage case reduction from source code level. It includes command line tool, maven plugin and sonar plugin.

## Requirement

This tool support Java project only, and it require:
1. Java 1.7 +
2. Maven 3.X
3. Sonar 4.5.X

## Usage

Run from command Line
1. cd to where the outage-risk-finder.jar is
2. Run the command
   java -jar outage-risk-finder.jar -source ./src/main/java -output my_outage_risk -format csv

Run as maven plugin
1. Install the outage-maven-plugin to your repository
2. Run maven with
   mvn outage:outage

Run as sonar plugin
1. Install the sonar-outage-plugin to SonarQube
2. Execute an sonar analysis:
   mvn sonar:sonar

## Supported Rules
Outage code checklist [link](https://workspaces-emea.int.nokia.com/sites/OSSChinaRDOffice/RCAEDA/Forms/AllItems.aspx?RootFolder=%2Fsites%2FOSSChinaRDOffice%2FRCAEDA%2F2015%20Outage%20Reduction%20Proposal%2FCode%20Checking&InitialTabId=Ribbon%2ELibrary&VisibilityContext=WSSTabPersistence)

Release Notes

#Version 1.0 

First release with 19 rules implemented from outage code checking list

#Version 1.0.1

Remove Sonar properties setting function and Widget since it's not used
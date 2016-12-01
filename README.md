# DLS Repository Stack

The Digital Learning Sciences (DLS) Repository Stack includes three web applications that make up 
an XML repository services stack.

- Digital Collection System (DCS) web application - An XML collections management system 

- Digital Digital System (DDS) web application - A Search API server for XML content built on Lucene

- Repository Harvest Manager - An XML repository manager and harvester that fetches XML collections from OAI and other 
repositories and maintains normalized XML in a MySQL database. 


## Getting Started

Each application is built with ant. To build and deploy, change to the web application directory and execute  

```
ant deploy
```

### Prerequisites

Apache Tomcat 7
Java 6 to 8
Apache Ant build tool

## History

The applications that make up the DLS Repository Stack were originally managed as separate projects that
were combined and moved to GitHub. The original projects included:

- dds-webapp (previously named dds-project) - The DDS webapp was migrated from SourcForge CVS
- dcs-webapp (previously named dcs-project) - The DCS webapp was migrated from SourceForge CVS
- shared-source (previously named dlese-tools-project) - The Java code used in the DDS and DCS were originally in the dlese-tools-project on SourceForge CVS.
- frameworks- project - The XML frameworks project contains XML schemas and related XSL transforms and were originally 
in a separate project on SourceForge CVS. Most of these schemas (for example adn, dlese_anno) are hosed on the dlese.org domain.
- rhm-webapp (previously named harvest-repository-project)- The Harvest Repository Manager webapp was originally in the DLS Subversion repository,
then was moved to it's own repository on GitHub (https://github.com/NCAR/harvest-repository-project), and finally moved here
- (joai-project) - This project dependency was removed when migrated to GitHub, and the dependent OAI-PMH JSP pages were moved into the DDS project
and are used in both DDS and DCS webapps.
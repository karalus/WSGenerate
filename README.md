# WSGenerate
Generic tool to generate a language binding for WSDL&amp;XSDs

Currently a PL/SQL Webservice framework is included (which is used in productive environments at different customer sites).

Goals
- Provide base JAR for parsing XSDs and m2m to a xml content model (XCM) which is easier to use. This model defines the content including namespaces but is simpler to use thus not fully XSD equivalent.
- Provide a frontend for parsing XSDs from the command line.
- provide a generic backend for m2t utilizing oAW XPand and Xtend to generate from the XCM to text.
- provide a concrete backend for PL/SQL

Documentation
- A lot of documentation is available because it is in use since 2014 but this documentation is owned by our customers and/or written in german language. So I need to have time to create it anew :-(


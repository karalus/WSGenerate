# WSGenerate
Generic tool to generate a language binding for WSDL&amp;XSDs


Goals
- Provide base JAR for parsing XSDs and m2m to a xml content model (XCM) which is easier to use. This model defines the content including namespaces but is simpler to use thus not fully XSD equivalent.
- Provide a frontend for parsing XSDs from the command line.
- provide a generic backend for m2t utilizing oAW XPand and Xtend to generate from the XCM to text.
- provide a concrete backend for PL/SQL
- provide a concrete backend for swagger files (including JSON schema)

Further usage scenarios for XCM
- provide a mapper for JSON->XML and vice versa adhering to the XCM thus to the XSD

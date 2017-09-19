Spring boot properties

Requirements:
*A toggle may be used by one or more application/service, and also they can be overridden, for example:
	*Toggle named isButtonBlue with value a true must be configured so all applications/services may use the same value.
	*Toggle called isButtonGreen with value a true must be configured so that service Abc only use it.
	*Toggle named isButtonRed with value a true must be configured so all application/services may use the same value,
		with the exception for the service Abc that must have this toggle configured with the value set false.

*When the application/service request their toggles, they must only provide their id and version.
*Implement a Rest API to manage the toggles and to deliver the toggles values to each client application/service request.
? Try to find a way to alert each client application/service that the toggle was changed.
*When setting a new toggle, only users with admin permission may create a toggle.
*When requesting the toggles, the client application/service must be authorised to use the Toggle Service.

Getting Started

*Configure application.properties
    *toggle-properties-path -> path to toggle.properties file, toggle system will write and read, to and from this file
*toggle.properties does not allow duplicated entries, only a configuration of toggleId + toggle version is allowed
*Run ToggleSystemTests, this class validates if the system satisfies all the requirements described above
*Toggle system uses basic authentication and users are configured in SpringSecurityConfiguration (next version: use DB, encrypt passwords)
    ADMIN --> user / pass
    APP   --> Abc  / App1
    APP   --> Def  / App2

How to use it

*To add a new toggle, your user must have role ADMIN, and should call the service /addToggleStatus
    eg. "http://localhost:8080/addToggleStatus?toggleId=feature1&toggleVersion=V1.0&toggleStatus=true"
*To update a toggle status, your user must have role ADMIN, and should call the service /updateToggleStatus
    eg. "http://localhost:8080/updateToggleStatus?toggleId=feature1&toggleVersion=V1.0&toggleStatus=true"
*To remove a toggle configuration, your user must have role ADMIN, and should call the service /updateToggleStatus
    eg."http://localhost:8080/removeToggleConfig?toggleId=feature1&toggleVersion=V1.0"

Applications may read, the toggle status of each toggle, and attach specific status for their control
*To read toggle status, your user must have role APP, username identifies the app, should call the service /readToggleAppStatus
    eg. "http://localhost:8080/readToggleAppStatus?toggleId=feature1&toggleVersion=V1.0"
*To update toggle status for a specific app, your user must have role APP, username identifies the app, should call the service /updateToggleAppStatus
    (toggle status has priority over toggle status for a specific ap, if toggle status is false, toggle status for a specific app is irrelevant
    eg. "http://localhost:8080/updateToggleAppStatus?toggleId=feature1&toggleVersion=V1.0&toggleStatus=true"

Running the tests
*ToggleSystemTests was created to validate the required requirements
    *run testAddToggleAdmin and check toggle.properties
            isButtonBlue_V1.0 = true
            isButtonBlue_V1.0_statusAlwaysTrue =
            isButtonBlue_V1.0_statusAlwaysFalse =
            isButtonBlue_V1.0_toggleAccess =
            isButtonGreen_V1.0 = true
            isButtonGreen_V1.0_statusAlwaysTrue =
            isButtonGreen_V1.0_statusAlwaysFalse =
            isButtonGreen_V1.0_toggleAccess = Abc
            isButtonRed_V1.0 = true
            isButtonRed_V1.0_statusAlwaysTrue =
            isButtonRed_V1.0_statusAlwaysFalse = Abc
            isButtonRed_V1.0_toggleAccess = Abc,Def
            isButtonYellow_V1.0 = false
            isButtonYellow_V1.0_statusAlwaysTrue = Abc
            isButtonYellow_V1.0_statusAlwaysFalse =
            isButtonYellow_V1.0_toggleAccess = Def

Deployment
Not yet deployed in the live system

Built With
Java 1.8
Spring framework boot 1.5.6.RELEASE
Maven 4.0
Intellij IDEA COMMUNITY 2017.2.3

Versioning
GitHub for versioning

Authors
Gisela Costa

Acknowledgments
google and other GitHub projects



# ExaVault Java API Sample Code - v2 API

## Introduction
Welcome to the sample project for ExaVault's Java code library, which demonstrates how to use various aspects of our API with your ExaVault account. The Java code library is available [on Github](https://github.com/ExaVault/evapi-java). The library is generated from our API's [public swagger YAML file](https://www.exavault.com/api/docs/evapi_2.0_public.yaml).

## Requirements

To run this project, you'll need Java 1.7+ (or greater) installed as well as any Java IDE (i.e. Eclipse or IntelliJ). 

You'll also need an [ExaVault](https://www.exavault.com/) account as well as and an [API key and Access Token](https://www.exavault.com/developer/api-docs/#section/Obtaining-Your-API-Key-and-Access-Token).

Some sample scripts will assume your account contains the "Sample Files and Folders" folder, which come pre-loaded with a new account. 
You may need to make changes to ````DownloadFiles.java```` if that folder is not present.

## Running Your First Sample

**Step 1 - Install Java (1.7 or Higher)** 

If not installed, install [Java](https://www.java.com/en/download/help/download_options.xml).

**Step 2 - Download IDE** 

If not downloaded, either download [eclipse](https://www.eclipse.org/downloads/packages/) or [IntelliJ](https://www.jetbrains.com/idea/download/#section=windows).

Feel free to choose another IDE of your choice.

**Step 3 - Clone the repository** 
```bash
git clone https://github.com/ExaVault/evapi-java-samples.git
```
In case you've cloned the repository via GitBash, follow these steps to import the project into the IDE

- **Eclipse**: File (tab) -> Import -> Maven -> Existing maven projects -> Next (button) -> Choose the correct folder (where you've cloned the project) -> Next (button) -> Finish (button).
- **IntelliJ**: File (tab) -> Open -> Choose the correct folder (where you've cloned the project) -> ok (button). 

Alternatively, you can use IDE to directly clone the project

- **Eclipse**: Launch Eclipse -> File (tab) -> Import -> Select Git from the list -> Projects from Git -> Next -> Select Clone URI -> Next (button) -> Paste the project URI and choose a local directory to import into-> Finish (button).
- **IntelliJ**: Launch IntelliJ -> VCS (tab) -> Get from Version Control -> Choose Git from the dropdown -> Paste the project URI and choose a local directory to import into -> Clone (button). 

**Step 4 - Dependencies**

You need to import compiled Java SDK library files to your project's classpath.

Our Java SDK's compiled Jars are bundled in ````/resources/lib```` folder. Add these libs as follows:

- **For Eclipse**: Right clicks on your project in package explorer and pick Build Path -> Configure Build Path -> Java Build Path -> Libraries (tab)-> Add External JAR's.
- **For IntelliJ**: File (tab) -> Project Structure -> Global Libraries -> "+" (Button) -> Java-> Select Jar from a local directory (follow these steps for adding all jars, downloaded in the previous step).

**Step 5 - Get your API Credentials** 

The next step is to generate an API key and token from your ExaVault account. You'll need to log into the ExaVault web file manager, as an admin-level user, to get the API key and access token. See [our API reference documentation](https://www.exavault.com/developer/api-docs/v2/#section/Obtaining-Your-API-Key-and-Access-Token) for the step-by-step guide to create your key and/or token.  

If you are not an admin-level user of an ExaVault account, you'll need someone with admin-level access to follow the steps and give you the API key and access token.

**Step 6 - Add Your API Credentials to the sample code**

In the ````configuration.properties````file, you should change the following lines:

- Replace ````YOUR_API_KEY```` with your API key. Don't add any extra spaces or punctuation.
- Replace ````YOUR_ACCESS_TOKEN```` with your access token.
- Replace ````YOUR_ACCOUNT_NAME```` with the name of your ExaVault account

**Step 7 - Run the sample code**

Now you're ready to run your first sample. From your IDE, Run GetAccount.java (right-click on the file -> run as Java application) first.

If everything worked, the sample code will run and connect to your account. You'll see output similar to this:

```shell
Account used: 40GB (11.4%)
Total size: 350GB
Primary Email Address: [your_email_id]
```

## Running Other Sample Classes

There are several other sample classes that you can now run. You won't need to repeat the steps to set up your credentials  - the same information is used for all the sample classes.
Some sample classes will make changes to your account (uploading, creating shares or notifications, etc). Those are marked with an asterisk below:

Class                         | Purpose                                                                                | APIs Used                      |
------------------------------|----------------------------------------------------------------------------------------|--------------------------------|
GetAccount                    | List the amount of available space for your account                                    | AccountApi                     |
AddNotification               | Add upload and download notifications<br/>_\*adds folders to your account_             | ResourcesApi, NotificationsApi |
CreateUser                    | Add a new user with a home directory <br/>_\*adds a user and a folder to your account_ | UsersApi                       |
CompressFiles                 | Compress several files into a zip file <br/>_\*adds files and folders to your account_ | ResourcesApi                   |
DownloadFiles                 | Search for files matching a certain extension, then download them.                     | ResourcesApi                   |
GetActivity                   | List usernames who had a failed login in the last 24 hours                             | ActivityApi                    |
GetUsers                      | Print a list of users in your account                                             | UsersApi                       |
CreateShare                   | Create a new shared folder <br />_\*adds a folder to your account_      | ResourcesApi, SharesApi        |
UploadFiles                   | Upload a file to your account.<br />_\*uploads sample PDFS to your account_            | ResourcesApi                   |


## If Something Goes Wrong

**Problem - 401 Unauthorized Response**

If running the sample class returns a 401 Unauthorized error like the one shown below, there is a problem with your API credentials. Double-check that ````configuration.properties```` contains the correct values. If all else fails, you may need to log into the ExaVault web file manager and re-issue your access token.

```shell
Exception when calling AccountApi#getAccount
com.exavault.client.ApiException: Unauthorized
```

**Other problems with sample code**

If you encounter any other issues running this sample code, you can contact ExaVault support for help at support@exavault.com.

## Next Steps 

To get started writing your own code, you may either modify our samples, or download and install our library into your existing project.
For details, see the [ExaVault Java API Library repo](https://github.com/ExaVault/evapi-java).

## Author

support@exavault.com

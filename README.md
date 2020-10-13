# ExaVault Java API Sample Project - v2.0

## Introduction
Welcome to the sample project for ExaVault's Java code library, which demonstrates how to use various aspects of our API with your ExaVault account. The Java code library is available [on Github](https://github.com/ExaVault/evapi-java). The library is generated from our API's [public swagger YAML file](https://www.exavault.com/api/docs/evapi_2.0_public.yaml)

## Requirements

To run this project, you'll need Java 1.7+ (or greater) installed as well as any Java IDE. Sample project was done and exported from Eclipse.

You will also need an ExaVault account as well as and an API key and access token.

## Running Your First Sample

**Step 1 - Import sample project** 

Import JavaExample.zip to your IDE. For Eclipse File -> Import -> General -> Project from folder or archive (pick folder where JavaExample.zip is in).

**Step 2 - Install dependencies** 

Get Java SDK [on Github](https://github.com/ExaVault/evapi-java). And after that import same jar's from the SKD target and lib folders.

- target/swagger-java-client-1.0.0.jar
- target/lib/*.jar

**Step 3 - Get your API Credentials** 

The next step is to generate an API key and token from your ExaVault account. You'll need to log into the ExaVault web file manager, as an admin-level user, to get the API key and access token. See [our API reference documentation](https://www.exavault.com/developer/api-docs/v2/#section/Obtaining-Your-API-Key-and-Access-Token) for the step-by-step guide to create your key and/or token.  

If you are not an admin-level user of an ExaVault account, you'll need someone with admin-level access to follow the steps and give you the API key and access token.

**Step 4 - Add Your API Credentials to the sample code**

In the JavaExample class you should change the following lines:

- replace [evApiKey] with your API key. Don't add any extra spaces or punctuation
- replace [evAccessToken] with your access token.
- replace [accountnme] with the name of your ExaVault account

And save the class.

**Step 4 - Run the sample script**

Now you're ready to run your first sample. Try run GetAccount class first

If everything worked, the sample code will run and connect to your account. You'll see output similar to this:

```shell
Account used: 40GB (11.4%)
Total size: 350GB
Master user id: [yourId]
```

## Running Other Sample Classes

There are several other sample classes that you can now run. You won't need to repeat the steps to set up your credentials  - the same information is used for all of the sample classes.
Some of the sample classes will make changes to your account (uploading, creating shares or notifications, etc). Those are marked with an asterisk below:

Class                         | Purpose    \*=Makes changes to your account when run                                   | APIs Used                      |
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

If running the sample class returns a 401 Unauthorized error like the one shown below, there is a problem with your API credentials. Double-check that the JavaExamples class contains the correct values. If all else fails, you may need to log into the ExaVault web file manager and re-issue your access token.

```shell
Exception when calling AccountApi#getAccount
io.swagger.client.ApiException: Unauthorized
```

**Other problems with sample code**

If you encounter any other issues running this sample code, you can contact ExaVault support for help at support@exavault.com.

## Writing Your Own Code 

When you're ready to write your own code, you can use our sample code as examples. You'll need to:

1. Install our code library using this [Instructions](https://github.com/ExaVault/evapi-java)
1. Set variables within your classes for your API key and access token or you can use dotenv libraries for java to handle it in one place.
1. Whenever you instantiate an Api object (ResourcesApi, UsersApi, etc.), override the configuration to point the code at the correct API URL:
```java
public static ApiClient apiInstance; 
public static String apiUrl = "https://YOUR_ACCOUNT_NAME_HERE.exavault.com/api/v2/";

ApiClient apiClient = new ApiClient();
apiClient.setBasePath(apiUrl);
apiInstance = apiClient;  

AccountApi accountApiInstance = new AccountApi(apiInstance);
```

## Author

support@exavault.com

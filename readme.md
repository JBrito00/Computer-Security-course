# Computer Security Course Assignments

The assignments in this repo were practical implementations of the studies in Computer Security during a semester in my undergraduate studies in Computer Science and Computer Engineering.

## Overview

This repo showcases two practical assignments completed as part of the Computer Security course. The course covered a range of topics, including:

- Cryptographic mechanisms and protocols
- Public key infrastructure
- Authentication
- Authorization mechanisms, including password-based authentication
- RBAC
- OpenID Connect protocol
- OAuth2 framework

## Structure

The repo is organized into two folders, each corresponding to the specific assignment. Inside each folder, you will find the relevant source code, documentation, and additional information related to the assignment.

## How to Use

### PA1 Assignment

#### Exercise 6: Blockchain Operations

For ex6 of PA1, after compiling and run BlockChainApp.java file you have the options to add a new block to the chain or validate the existing chain using the following commands:

- To add a new block:
  ```bash
  $ addblock <origin> <destiny> <value> <filename>
  ```

- To validate:
    ```bash
    $ verifychain <filename>
    ```

#### Exercise 7: Console APP

For ex7 of PA1, after compiling and run 'ConsoleApp.java' you can encrypt text using a certificate to generate a token. With that token you can later decrypt it using the respective pfx file from the certificate you used for encryption.

- to encrypt:
  ```bash
  $ jwe enc <some string> <recipient certificate>
  ```

- to decrypt:
  ```bash
  $ jwe dec <jwe string> <recipient pfx>
  ```

### PA2 Assignment

For PA2, first, we need to ensure that when www.secure-server.edu is accessed, it is directed to localhost. Therefore, we need to add the following line to the system's host file:
```bash 
127.0.0.1 www.secure-server.edu 
```
Next, we need to add the private key to our browser certificates. All certificates and keys are located in the 'certificates-keys' folder inside the PA1 folder. For this example, we'll add the 'Alice_2.pfx' to the Chrome browser certificates:

### Steps:

1. **Open Chrome Settings:**
   - Click on the three dots in the top-right corner.
   - Select "Settings."

2. **Scroll Down to Advanced Settings:**
   - Scroll down to the bottom and click on "Advanced."

3. **Manage Certificates:**
   - Under "Privacy and security," click on "Manage certificates" under "Security."

4. **Import Certificate:**
   - In the "Certificate Manager" window, go to the "Personal" tab.
   - Click on "Import."

5. **Choose Certificate File:**
   - Browse to the location where your PFX file ("alice_2.pfx") is stored.
   - Select the file and click "Open."

6. **Enter Password:**
   - For all the passwords in the certificates-keys folder the passwords are "changeit". Provide the password and click "OK."

7. **Certificate Import Wizard:**
   - Follow the steps in the Certificate Import Wizard.
   - Choose the option to "Automatically select the certificate store based on the type of certificate."
   - Click "Next" and then "Finish."

8. **Confirm Import:**
   - You may see a security warning; confirm that you want to import the certificate.

9. **Restart Chrome:**
   - Close and restart your browser for the changes to take effect.


Now we can run our http-server-base.js and go to www.secure-server.edu to connect to the client.

## Acknowledgments

These assignments were developed within the scope of Computer Security course in a group during my undergraduate studies in Computer Science and Computer Engineering at ISEL. Some parts of the code were adapted from existing examples provided by the professors. These assignments were both developed during practical classes and outside of regular school hours. I would like to express my gratitude to the professors for their guidance during the semester.

# HTTPS Server Configuration

This assignment involves configuring an HTTPS server with and without client authentication.The server will use the certificate and private key of www.secure-server.edu, which was issued by CA1-int in the first assignment.
Information on how to run this assignment in the main readme file.

### Configurations


1. **Without Client Authentication:**
    
    
   ```bash
   const options = {
    key: fs.readFileSync('secure-server-key-17nov.pem'),
    cert: fs.readFileSync('secure-server.pem')
   }
   ```

2. **With Client Authentication (Alice_2):**
   ```bash
   const options = {
    key: fs.readFileSync('secure-server-key-17nov.pem'),
    cert: fs.readFileSync('secure-server.pem'),
    ca: fs.readFileSync('CA2-new.pem'),
    requestCert: true,
    rejectUnauthorized: true
   }
   ```

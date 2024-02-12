# Impact of ECB and CBC Modes on Repeated Patterns in BMP Images


## Overview

This assignment aims to assess the impact of using Electronic Codebook (ECB) and Cipher Block Chaining (CBC) modes of operation when the plaintext message contains repeated patterns. The provided BMP image ('trab1.bmp') serves as the basis for this analysis.
BMP files are characterized by having two distinct zones. The initial 54 bytes constitute a metadata zone, referred to as the header. The subsequent bytes (starting from position 55) represent the pixels of the image.

The header and body files were obtained from the original file trab1.bpm with the following commands:
```bash
$ head -c 54 trab1.bmp > header
$ tail -c +55 trab1.bmp > body
```
Next, I used OpenSSL to cipher the body file using the AES and DES algorithms and the ECB and CBC operation modes.

Afterwards, I use the following command for each body file created to concatenate the header with body and analyze the image.

## Results

Upon observing the images, it's possible to conclude that, in the case of messages with repeated patterns, the ECB mode is inadequate for preserving confidentiality. This is because identical blocks in the plaintext message result in identical encryption blocks. On the other hand, the CBC mode, due to the use of the initialization vector (IV) and XOR operations, results in different encryption blocks for identical plaintext blocks.
In the case of using AES and DES, the use of AES contributes to heightened security. This is attributed to the capability of the AES cipher to accommodate a significantly larger key size compared to the DES cipher.
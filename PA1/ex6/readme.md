# Simple Blockchain Implementation

The objective of this project is to implement a simple blockchain with the property of integrity verification for its blocks (chain validation). Each block in the chain is composed of a transaction `ti` with the following fields:

- **Origin:** Positive integer in decimal.
- **Destiny:** Positive integer in decimal.
- **Value:** Real number (float).

The chaining of blocks, denoted as `bi` in the blockchain, is defined by the formula:

 bi = ti  ||  hash(b{i-1}), 

for all 1 <= i <= n. In other words, a block \(bi\) is the result of concatenating a transaction \(ti\) and the hash of the previous block. This use of the hash value in the chain allows for the validation of the integrity of the block chain.

The Genesis Block, b0, contains null transaction field values:
- **Origin:** -1
- **Destiny:** -1
- **Value:** -1.0
- **Hash:** 0x0

### Storage and Retrieval

The blockchain should be stored and retrieved from a CSV (Comma-Separated Values) file. Each block corresponds to a line in this file with the following format:
```bash
<origin>,<destiny>,<value>,<hash>
```

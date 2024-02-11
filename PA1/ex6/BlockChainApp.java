import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

class Block {
    private int origin;
    private int destiny;
    private double value;
    private String previousHash;

    public Block(int origin, int destiny, double value, String previousHash) {
        this.origin = origin;
        this.destiny = destiny;
        this.value = value;
        this.previousHash = previousHash;
    }

    public String calculateHash() {
        String data = origin + destiny + value + previousHash;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getOrigin() {
        return origin;
    }

    public int getDestiny() {
        return destiny;
    }

    public double getValue() {
        return value;
    }

    public String getHash() {
        return previousHash;
    }
}

class Blockchain {
    private ArrayList<Block> chain;
    private String filename;

    public Blockchain(String filename) {
        this.filename = filename;
        chain = loadChainFromFile(); // Load the chain from the CSV file
        if (chain.size() == 0) {
            // If the chain is empty, add the genesis block
            chain.add(new Block(-1, -1, -1.0, "0x0"));
            saveChainToFile();
        }
    }

    public void addBlock(Block block) {
        chain.add(block);
        // Update the CSV file
        saveChainToFile();
    }

    public void saveChainToFile() {
        try {
            FileWriter writer = new FileWriter(filename);
            for (Block block : chain) {
                writer.write(block.getOrigin() + "," + block.getDestiny() + "," + block.getValue() + ","
                        + block.getHash() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Block> loadChainFromFile() {
        ArrayList<Block> loadedChain = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int origin = Integer.parseInt(parts[0]);
                int destiny = Integer.parseInt(parts[1]);
                double value = Double.parseDouble(parts[2]);
                String previousHash = parts[3];
                loadedChain.add(new Block(origin, destiny, value, previousHash));
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loadedChain;
    }

    public boolean validateChain() {
        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            Block previousBlock = chain.get(i - 1);

            // Verify if the hash of current block is the same as the calculated hash of
            // previous block
            if (!currentBlock.getHash().equals(previousBlock.calculateHash())) {
                return false;
            }
        }
        return true;
    }

    public Block getLatestBlock() {
        return chain.get(chain.size() - 1);
    }

    public List<Block> getBlocks() {
        return chain;
    }
}

public class BlockChainApp {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Use: java BlockChainApp <command> [args]");
            return;
        }

        String command = args[0];
        if (command.equals("addblock")) {
            if (args.length != 5) {
                System.out.println("Use: java BlockChainApp addblock <origem> <destino> <valor> <filename>");
                return;
            }

            int origin = Integer.parseInt(args[1]);
            int destiny = Integer.parseInt(args[2]);
            double value = Double.parseDouble(args[3]);
            String filename = args[4];

            Blockchain blockchain = new Blockchain(filename);
            Block newBlock = new Block(origin, destiny, value, blockchain.getLatestBlock().calculateHash());
            blockchain.addBlock(newBlock);

            System.out.println("Block added to the chain.");

        } else if (command.equals("verifychain")) {
            if (args.length != 2) {
                System.out.println("Use: java BlockChainApp verifychain <filename>");
                return;
            }

            String filename = args[1];

            Blockchain blockchain = new Blockchain(filename);

            if (blockchain.validateChain()) {
                System.out.println("Blockchain is valid.");
                for (Block block : blockchain.getBlocks()) {
                    System.out.println("Origem: " + block.getOrigin() + ", Destino: " + block.getDestiny() + ", Valor: "
                            + block.getValue() + ", Hash: " + block.calculateHash());
                }
            } else {
                System.out.println("Blockchain is invalid.");
            }
        } else {
            System.out.println("Unknown command: " + command);
        }

    }

}

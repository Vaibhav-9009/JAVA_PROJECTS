import java.io.*;
import java.util.*;

public class MyHuffmanCompressor {

    static class Node implements Comparable<Node> {
        char character;
        int frequency;
        Node left, right;

        public Node(char character, int frequency) {
            this.character = character;
            this.frequency = frequency;
            this.left = this.right = null;
        }

        public int compareTo(Node other) {
            return this.frequency - other.frequency;
        }

        public boolean isLeaf() {
            return left == null && right == null;
        }
    }

    public static Map<Character, Integer> getFrequencyMap(String text) {
        Map<Character, Integer> freqMap = new HashMap<>();
        for (char ch : text.toCharArray()) {
            freqMap.put(ch, freqMap.getOrDefault(ch, 0) + 1);
        }
        return freqMap;
    }

    public static Node createHuffmanTree(Map<Character, Integer> freqMap) {
        PriorityQueue<Node> pq = new PriorityQueue<>();
        for (var entry : freqMap.entrySet()) {
            pq.add(new Node(entry.getKey(), entry.getValue()));
        }

        if (pq.size() == 1) {
            pq.add(new Node('\0', 1));
        }

        while (pq.size() > 1) {
            Node left = pq.poll();
            Node right = pq.poll();
            Node parent = new Node('\0', left.frequency + right.frequency);
            parent.left = left;
            parent.right = right;
            pq.add(parent);
        }
        return pq.poll();
    }

    public static void generateHuffmanCodes(Node root, String code, Map<Character, String> codes) {
        if (root == null) return;
        if (root.isLeaf()) {
            codes.put(root.character, code.length() > 0 ? code : "0");
        }
        generateHuffmanCodes(root.left, code + '0', codes);
        generateHuffmanCodes(root.right, code + '1', codes);
    }

    public static void compress(String inputFile, String outputFile) {
        try {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            }

            String text = sb.toString();
            if (text.isEmpty()) {
                System.out.println("Nothing to compress. Input file is empty.");
                return;
            }

            Map<Character, Integer> freqMap = getFrequencyMap(text);
            Node root = createHuffmanTree(freqMap);
            Map<Character, String> huffmanCodes = new HashMap<>();
            generateHuffmanCodes(root, "", huffmanCodes);

            StringBuilder encodedData = new StringBuilder();
            for (char ch : text.toCharArray()) {
                encodedData.append(huffmanCodes.get(ch));
            }

            try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
                writer.println(freqMap.size());
                for (var entry : freqMap.entrySet()) {
                    writer.println((int) entry.getKey() + " " + entry.getValue());
                }
                writer.println();
                writer.print(encodedData.toString());
            }
            
            System.out.println("Compression complete! Saved to " + outputFile);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void decompress(String inputFile, String outputFile) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            int numCharacters = Integer.parseInt(reader.readLine().trim());
            Map<Character, Integer> freqMap = new HashMap<>();

            for (int i = 0; i < numCharacters; i++) {
                String[] parts = reader.readLine().split(" ");
                char ch = (char) Integer.parseInt(parts[0]);
                int freq = Integer.parseInt(parts[1]);
                freqMap.put(ch, freq);
            }
            reader.readLine();

            StringBuilder encodedData = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                encodedData.append(line);
            }
            reader.close();

            Node root = createHuffmanTree(freqMap);
            StringBuilder decodedText = new StringBuilder();
            Node current = root;
            for (char bit : encodedData.toString().toCharArray()) {
                current = (bit == '0') ? current.left : current.right;
                if (current.isLeaf()) {
                    decodedText.append(current.character);
                    current = root;
                }
            }

            try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
                writer.print(decodedText.toString());
            }
            
            System.out.println("Decompression complete! Saved to " + outputFile);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("My Custom Huffman File Compressor");
        System.out.println("1. Compress a file");
        System.out.println("2. Decompress a file");
        System.out.print("Choose an option: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            System.out.print("Enter source file: ");
            String inputFile = scanner.nextLine();
            System.out.print("Enter destination file: ");
            String outputFile = scanner.nextLine();
            compress(inputFile, outputFile);
        } else if (choice == 2) {
            System.out.print("Enter compressed file: ");
            String inputFile = scanner.nextLine();
            System.out.print("Enter output file: ");
            String outputFile = scanner.nextLine();
            decompress(inputFile, outputFile);
        } else {
            System.out.println("Invalid selection!");
        }
        scanner.close();
    }
}

package encryptdecrypt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

interface EncodingMethods {
    String encode(String message, int key);
    String decode(String encoded, int key);
}

class ShiftEncodingMethods implements EncodingMethods {
    @Override
    public String encode(String message, int key) {
        StringBuilder encoded = new StringBuilder(message);

        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            char start = Character.isUpperCase(c) ? 'A' : 'a';

            encoded.setCharAt(i, Character.isLetter(c) ? (char) ((c - start + key) % 26 + start) : c);
        }

        return encoded.toString();
    }

    @Override
    public String decode(String encoded, int key) {
        StringBuilder message = new StringBuilder(encoded);

        for (int i = 0; i < encoded.length(); i++) {
            char c = encoded.charAt(i);
            char start = Character.isUpperCase(c) ? 'A' : 'a';

            message.setCharAt(i, Character.isLetter(c) ? (char) (Math.floorMod(c - start - key, 26) + start) : c);
        }

        return message.toString();
    }
}

class UnicodeEncodingMethods implements EncodingMethods {
    public String encode(String message, int key) {
        StringBuilder encoded = new StringBuilder(message);

        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            encoded.setCharAt(i, (char) (c + key));
        }

        return encoded.toString();
    }

    public String decode(String encoded, int key) {
        StringBuilder message = new StringBuilder(encoded);

        for (int i = 0; i < encoded.length(); i++) {
            char c = encoded.charAt(i);
            message.setCharAt(i, (char) (c - key));
        }

        return message.toString();
    }
}

class Encoder {
    private EncodingMethods methods;

    public void setMethods(EncodingMethods methods) {
        this.methods = methods;
    }

    public String encode(String message, int key) {
        return methods.encode(message, key);
    }

    public String decode(String encoded, int key) {
        return methods.decode(encoded, key);
    }
}

public class Main {
    static String mode;
    static String algorithm;

    static String inputPath;
    static String outputPath;

    static String data;
    static int key;

    public static void main(String[] args) {
        parseArgs(args);

        Encoder encoder = new Encoder();

        switch (algorithm) {
            case "shift":
                encoder.setMethods(new ShiftEncodingMethods());
                break;
            case "unicode":
                encoder.setMethods(new UnicodeEncodingMethods());
                break;
            default:
                System.out.println("Wrong algorithm");
                encoder.setMethods(new ShiftEncodingMethods());
        }

        String input = data.length() > 0 ? data : importFromFile(inputPath);

        String output = "";
        switch (mode) {
            case "enc":
                output = encoder.encode(input, key);
                break;
            case "dec":
                output = encoder.decode(input, key);
                break;
            default:
                System.out.println("Wrong mode");
        }

        if (inputPath.length() > 0) {
            exportToFile(output, outputPath);
        } else {
            System.out.println(output);
        }
    }

    public static void parseArgs(String[] args) {
        Map<String, String> arguments = new HashMap<>();

        for (int i = 0; i < args.length - 1; i += 2) {
            arguments.put(args[i], args[i + 1]);
        }

        System.out.println(arguments);

        mode = arguments.getOrDefault("-mode", "enc");
        algorithm = arguments.getOrDefault("-alg", "shift");

        inputPath = arguments.getOrDefault("-in", "");
        outputPath = arguments.getOrDefault("-out", "");

        data = arguments.getOrDefault("-data", "");
        key = Integer.parseInt(arguments.getOrDefault("-key", "0"));
    }

    public static String importFromFile(String path) {
        File file = new File(path);

        try {
            Scanner scanner = new Scanner(file);
            return scanner.nextLine().strip();
        } catch (FileNotFoundException e) {
            System.out.println("Wrong path");
            return null;
        }
    }

    public static void exportToFile(String string, String path) {
        File file = new File(path);

        try {
            FileWriter writer = new FileWriter(file);
            writer.write(string + '\n');
            writer.close();
        } catch (IOException e) {
            System.out.println("Something went wrong");
        }
    }
}

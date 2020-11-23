import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;


public class Assignment1 {

    public static void main(String[] args) throws Exception {

        // takes in the first argument, checks if it exists and reads in the binary values for later encryption
        File inFile;
        byte[] binaryFileBytes = new byte[0];
        if (args.length > 0) {
            inFile = new File(args[0]);
            binaryFileBytes = new byte[(int) inFile.length()];
            try {
                FileInputStream fileInStream = new FileInputStream(inFile);
                fileInStream.read(binaryFileBytes);
                fileInStream.close();

            } catch (IOException e) {
                System.out.println("There was an error reading in file: " + args[0]);
                e.printStackTrace();
            }
        } else {
            System.err.println("Invalid arguments count:" + args.length);
            System.exit(404);
        }

        // values got from submissions page and set to be base 16 as they are hexadecimal
        BigInteger p = new BigInteger("b59dd79568817b4b9f6789822d22594f376e6a9abc0241846de426e5dd8f6eddef00b465f38f509b2b18351064704fe75f012fa346c5e2c442d7c99eac79b2bc8a202c98327b96816cb8042698ed3734643c4c05164e739cb72fba24f6156b6f47a7300ef778c378ea301e1141a6b25d48f1924268c62ee8dd3134745cdf7323", 16);

        BigInteger g = new BigInteger("44ec9d52c8f9189e49cd7c70253c2eb3154dd4f08467a64a0267c9defe4119f2e373388cfa350a4e66e432d638ccdc58eb703e31d4c84e50398f9f91677e88641a2d2f6157e2f4ec538088dcf5940b053c622e53bab0b4e84b1465f5738f549664bd7430961d3e5a2e7bceb62418db747386a58ff267a9939833beefb7a6fd68", 16);

        BigInteger A = new BigInteger("5af3e806e0fa466dc75de60186760516792b70fdcd72a5b6238e6f6b76ece1f1b38ba4e216f61a2b84ef1b5dc4151e799485b2171fcf318f86d42616b8fd8111d59552e4b5f228ee838d535b4b987f1eaf3e5de3ea0c403a6c38002b49eade15171cb861b367732460e3a9842b532761c16218c4fea51be8ea0248385f6bac0d", 16);

        // we select a secureRandom hex value and turn it into a BigInteger for us to be able to work easier
        SecureRandom bBeforeProcessing = new SecureRandom();
        byte[] bArray = new byte[128];
        bBeforeProcessing.nextBytes(bArray);
        BigInteger b = new BigInteger(toHexForFileWriting(bArray), 16);

        // I felt conflicted writing this code, I tried to manipulate bits using bitset, but this is easier and the efficiancy really does not matter that much as much as accuracy in this instance
        while (b.bitLength() != 1023) {
            bBeforeProcessing = new SecureRandom();
            bArray = new byte[128];
            bBeforeProcessing.nextBytes(bArray);
            b = new BigInteger(toHexForFileWriting(bArray), 16);
        }

        // B gets generated by the equation provided in class and written to DH.txt, taking into account the generator's hex value
        BigInteger B = squaredTwiceAndMultiplyRightToLeft(g, p, b);
        String DHFileContentFiller = toHexForFileWriting(B.toByteArray());
        Files.write(Paths.get("DH.txt"), DHFileContentFiller.getBytes());

        // generates key k by taking instead the shared value A and calculating shared secret s
        BigInteger s = squaredTwiceAndMultiplyRightToLeft(A, p, b);
        byte[] bytesSharedSecret = fromBigIntegerToHexByteArray(s);
        SecretKeySpec k = new SecretKeySpec(bytesSharedSecret, "AES");

        // I attempted initially to use UUID as a random 128 bit hex value, but it was 32 characters and various bit lengths so secure random is a better choice
        SecureRandom randomForIv = new SecureRandom();
        byte[] iV = new byte[16];
        randomForIv.nextBytes(iV);

        //writes the value of iV to IV.txt
        String iVFileContentFiller = toHexForFileWriting(iV);
        Files.write(Paths.get("IV.txt"), iVFileContentFiller.getBytes());

        // takes the binaryBytes from Assignment1.class and gives the filename Encryption.txt to encrypt to
        System.out.println(encrypt(args[0], binaryFileBytes, k, iV));

    }

    // used to build a string for all the Hexadecimal values in byte arrays we are making and also write them to their file
    private static String toHexForFileWriting(final byte[] data) {
        final StringBuilder sb = new StringBuilder(data.length * 2);
        for (final byte b : data) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }


    // used the notes pseudocode to write it with looking up BigInteger documentation. Initialisation and length have been tweaked to function as wanted.
    public static BigInteger squaredTwiceAndMultiplyRightToLeft(BigInteger a, BigInteger n, BigInteger x) {

        BigInteger y = BigInteger.ONE;
        for (int i = 0; i < x.bitLength() * 8; i++){
            if (x.testBit(i)){
                y = (y.multiply(a));
                y = y.mod(n);
            }
            a = a.multiply(a);
            a = a.mod(n);
        }

        return (y);
    }


    // fucking make sure to get this
    public static String encrypt(String fileName, byte[] fileContents, SecretKey secretKey, byte[] iV) throws Exception {

        // We have our own 128-bit iV preselected therefore we select the parameterSpec and move on to add custom padding to our binaryByte data from Assignment1.class
        IvParameterSpec iv = new IvParameterSpec(iV);
        byte[] finalFormatFile = customFilePadding(fileContents);
        String finalEncryption = "Default value for encryption, if seen in text file error occurred";
        try
        {
            //we selected the parameters we want in the instance, notably NoPadding as we already have done our own
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            // we lastly encrypt the file in its final format and write it out to the document
            byte[] encrypted = cipher.doFinal(finalFormatFile);

            try {
                // gets the 2nd positional argument which is the filename and writes a human readable hex to it
                BigInteger finalHexValue = new BigInteger( DatatypeConverter.printHexBinary(encrypted), 16);
                finalEncryption = toHexForFileWriting(finalHexValue.toByteArray());
                Files.write(Paths.get("".concat(fileName)), Collections.singleton(finalEncryption));

            } catch (FileNotFoundException e) {
                System.out.println("File Not Found.");
                e.printStackTrace();

            } catch (IOException e1) {
                System.out.println("Error Reading The File.");
                e1.printStackTrace();
            }

            // return the string to print to file
            return finalEncryption;
        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    // implements the padding as laid out in the project specification
    public static byte[] customFilePadding(byte[] fileToPad){

        int paddingBytesNeeded;
        // checks to see the length of the message and check whether there is remainder space to be filled.
        if (fileToPad.length % 16 == 0){
            // if there is no block remaining a full one will be added
            paddingBytesNeeded = 16;
        }
        else{
            // otherwise calculated for how many bytes are needed
            paddingBytesNeeded = 16 - fileToPad.length % 16;
        }

        // as they both get a 1-bit followed by 0-bits, we can just create a size limit to calculate how many 0-bits are appended
        byte[] addedBytes = Arrays.copyOf(fileToPad, fileToPad.length + paddingBytesNeeded);

        // FUCK SHIT FUCK MY ASSHOLE
        addedBytes[fileToPad.length] = (byte) Integer.parseInt("10000000", 2);

        return addedBytes;
    }

    // Have to know how to explain this...
    // munches and digests a part of the shared secret s by creating a 256-bit digest to later make the AES key k ourselves.
    public static byte[] fromBigIntegerToHexByteArray(BigInteger s) {

        byte[] hexBytes = s.toByteArray();

        //Checks for any leading zeroe and makes a copy of the array to move it to the end as to not to change the length of the byteArray
        if (hexBytes[0] == 0) {
            byte[] temp = new byte[hexBytes.length - 1];
            System.arraycopy(hexBytes, 1, temp, 0, temp.length);
            hexBytes = temp;
        }

        // initialise smaller bytes and in case it goes into the exception
        byte[] digestivesBytes = new byte[16];

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digestivesBytes = digest.digest(hexBytes);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // produces a 256-bit digest from the shared secret s to turn into the AES key k
        return digestivesBytes;
    }

}
import java.math.BigInteger;
import java.security.*;
import javax.crypto.*;
// can't use any BigInteger, has to be class notes
// crypto for aes and sha-256 hash is fine!
// idk what security is for....

// DONE BigInteger p = "b59dd79568817b4b9f6789822d22594f376e6a9abc0241846de426e5dd8f6eddef00b465f38f509b2b18351064704fe75f012fa346c5e2c442d7c99eac79b2bc8a202c98327b96816cb8042698ed3734643c4c05164e739cb72fba24f6156b6f47a7300ef778c378ea301e1141a6b25d48f1924268c62ee8dd3134745cdf7323"

// DONE BigInteger g = "44ec9d52c8f9189e49cd7c70253c2eb3154dd4f08467a64a0267c9defe4119f2e373388cfa350a4e66e432d638ccdc58eb703e31d4c84e50398f9f91677e88641a2d2f6157e2f4ec538088dcf5940b053c622e53bab0b4e84b1465f5738f549664bd7430961d3e5a2e7bceb62418db747386a58ff267a9939833beefb7a6fd68"

// DONE BigInteger aSVal = "5af3e806e0fa466dc75de60186760516792b70fdcd72a5b6238e6f6b76ece1f1b38ba4e210f61a2b84ef1b5dc4151e799485b2171fcf318f86d42616b8fd8111d59552e4b5f228ee838d535b4b987f1eaf3e5de3ea0c403a6c38002b49eade15171cb861b367732460e3a9842b532761c16218c4fea51be8ea0248385f6bac0d"

BigInteger b = 'calculate random 1023 numbit - 1 (be careful about negative)'

BigInteger B = 'g ^ b (mod p)'

BigInteger S = 'A ^ b (mod p)'

// s is too large (1024 bits) use SHA-256 to produce
// a bit digest from s giving a 256-bit
BiInteger k = 'above comment'

// binary file comes in
// encrypt using AES in CBC mode with key k and 128-bit blocksize

BigInteger IV = 'random 128-bit value'

// use following padding scheme:
// if message < block_size:
//    append(1 bit) and fill rest of block with 0-bits
// elif final_of_message == block_size:
//    createExtra(block) 100000 (starting with 1 bit and rest 0)

// DONE take in with sys.input ( external file)

//1. Calculating the shared secret value incorrectly (e.g. A^B (mod p) rather than A^b (mod p)).
//
//2. Calculating the AES key from the shared value using the character values in the string representation rather than the actual byte values.
//
//3. Converting BigInteger values to an array of bytes incorrectly - note that the BigInteger method toByteArray() uses a twos complement representation and may add an extra leading zero-valued byte if the first bit is set.
//
//4. Padding incorrectly: none of the padding mechanisms provided by the Java libraries corresponds to the required padding; you will need to use the NoPadding option and implement the padding yourself.
//
//5. Padding with the characters ‘0’ and '1' rather than the bit values 1 and 0..
//
//6. Giving decimal values rather than hexadecimal.
//
//7. Giving negative hex values.

//
//        Whats the best way of finding the AES key k? Mines saying its the wrong length whenever i check it
//
//
//
//        You pass the hash byte array to SecretKeySpec

//        generating everytime but outputting to the IV.txt so when he runs it it saves there to view


//        b is required to calculate s
//        B goes in DH.txt

//        For anyone working on the crypto project I sent an email for some clarification on the byte array to be hashed and here is what he responded with.
//        The hashed byte array should be a 128 item array of 8 bit unsigned items. If your Secret value s comes with a bitlength of less than 1024 add leading zeros.
//        Meaning if your s starts with “1000 0001  0000 0010  0000 0011” and is 1024 bits long your byte array should look like [129, 2, 3...]
//        if it were 1023 bits long 0100 0000 1000 0001 0000 0001 = [64, 129, 1...]
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class IDEA{

  private static byte[] key;
  private static int[] subKey;
  private static int[] decryptSubKey;
  private static String plaintext;
  private static byte[] message;

  /*
    Method creates a 128-bit key required for IDEA. SUN SHA1PRNG is
    a random number generator that is self-seeding using entropy derived
    from the operating system.
  */
  public static void keyGenerator(){
    try{
      SecureRandom generator = SecureRandom.getInstance("SHA1PRNG");
      key = new byte[16];
      generator.nextBytes(key);
    }

    catch(NoSuchAlgorithmException e){
      System.out.println("Error in Generating Key.");
    }
  }

  /*
  Method creates 52 16-bit subkeys that were generated from the original key. The
  first 8 subkeys are partitioned from the original key, whereas, all other subkeys
  are assigned by cyclically shifting the subkeys 25 positions to the left.
  */
  private static void splitKey(){
    subKey = new int[52];
    // Assignment of first 8 subkeys
    for(int idx = 0, i = 0; idx < 8; idx++, i+=2){
      subKey[idx] = ((key[i] & 0xFF << 8) | (key[i+1] & 0xFF));
    }

    int shiftCycle = 0, idxCount = 8;
    int[] tempKey = new int[16];
    // Total of 5 and half rounds are required to populate the remaining subkeys.
    while(shiftCycle < 6){
      //Shifting needed after every 8th subkey assigned
      for(int idx = 0; idx < 16; idx++){
        tempKey[idx] = key[idx] << 25;
      }
      //Assigns subkey (full round)
      if(idxCount < 47){
        for(int idx = 0; idx < 8; idx++, idxCount++){
          subKey[idxCount] = ((tempKey[idx] & 0xFF << 8) | (tempKey[idx+1] & 0xFF));
        }
      }
      //Assigns subkey (remaining half round)
      else{
        for(int idx = 0; idx < 4; idx++, idxCount++){
          subKey[idxCount] = ((tempKey[idx] & 0xFF << 8) | (tempKey[idx+1] & 0xFF));
        }
      }
      shiftCycle++;
    }

  }

  /*
  Inverts all 52 subkeys for decryption.
  */
  private static void invertSubKey() {
    decryptSubKey = new int[subKey.length];
    int p = 0;
    int i = 8 * 6;

    decryptSubKey[i + 0] = mulInv(subKey[p++]);
    decryptSubKey[i + 1] = addInv(subKey[p++]);
    decryptSubKey[i + 2] = addInv(subKey[p++]);
    decryptSubKey[i + 3] = mulInv(subKey[p++]);


    for (int r = 8 - 1; r >= 0; r--) {
      i = r * 6;
      int m = r > 0 ? 2 : 1;
      int n = r > 0 ? 1 : 2;
      decryptSubKey[i + 4] = subKey[p++];
      decryptSubKey[i + 5] = subKey[p++];
      decryptSubKey[i + 0] = mulInv(subKey[p++]);
      decryptSubKey[i + m] = addInv(subKey[p++]);
      decryptSubKey[i + n] = addInv(subKey[p++]);
      decryptSubKey[i + 3] = mulInv(subKey[p++]);
    }
  }
  /*
  Helper function to handle the generation of all subkeys for
  for encryption and decryption.
  */
  private static void handleSubKeys(){
    splitKey();
    invertSubKey();
  }

  /*
  This function breaks a users message into 64-bit chunks to process for
  cryption.
  */
  private static void splitPlainText(String pTxt){
    int difference = 0;
    String pad = "";
    int length = pTxt.getBytes().length * 8;

    //padding required
    if(pTxt.getBytes().length < 8 || (pTxt.getBytes().length * 8) % 64 != 0){

      if(pTxt.getBytes().length < 8)
        difference = 8 - pTxt.getBytes().length;

      else{
        while(length % 64 != 0){
          length++;
        }
        difference = (length / 8) - pTxt.getBytes().length;
      }

      for(int idx = 0; idx < difference; idx++){
        pad += "0";
      }
      pTxt = pad + pTxt;
    }
    message = pTxt.getBytes();

  }

  /*
  Removes padding ater decryption that was used for the encryption process.
  */
  private static String unPad(String decryptTxt){
    return decryptTxt.replaceAll("^0*", "");
  }

  private static void processCrypt(int[] key){
    try{
      for(int idx = 0, section = 0; idx < message.length/ 8; idx++, section += 8){
        cryption(key, section);
      }
    }
    catch(ArrayIndexOutOfBoundsException e){
      System.out.println("Error in processCrypt");
    }
  }

  /*
  This function handles the cryption process of IDEA for both decryption and
  encryption.
  */
  private static void cryption(int[] key, int index){
    int x0 = ((message[index + 0] & 0xFF) << 8) | (message[index + 1] & 0xFF);
    int x1 = ((message[index + 2] & 0xFF) << 8) | (message[index + 3] & 0xFF);
    int x2 = ((message[index + 4] & 0xFF) << 8) | (message[index + 5] & 0xFF);
    int x3 = ((message[index + 6] & 0xFF) << 8) | (message[index + 7] & 0xFF);
    //
    int p = 0;
    for (int round = 0; round < 8; round++) {
       int y0 = mul(x0, key[p++]);
       int y1 = add(x1, key[p++]);
       int y2 = add(x2, key[p++]);
       int y3 = mul(x3, key[p++]);
       //
       int t0 = mul(y0 ^ y2, key[p++]);
       int t1 = add(y1 ^ y3, t0);
       int t2 = mul(t1, key[p++]);
       int t3 = add(t0, t2);
       //
       x0 = y0 ^ t2;
       x1 = y2 ^ t2;
       x2 = y1 ^ t3;
       x3 = y3 ^ t3; }
    //
    int r0 = mul(x0, key[p++]);
    int r1 = add(x2, key[p++]);
    int r2 = add(x1, key[p++]);
    int r3 = mul(x3, key[p++]);
    //
    message[index + 0] = (byte)(r0 >> 8);
    message[index + 1] = (byte)r0;
    message[index + 2] = (byte)(r1 >> 8);
    message[index + 3] = (byte)r1;
    message[index + 4] = (byte)(r2 >> 8);
    message[index + 5] = (byte)r2;
    message[index + 6] = (byte)(r3 >> 8);
    message[index + 7] = (byte)r3;
  }

/*
  Addition in the additive group (in the range of 0 to 0xFFFF).
*/
private static int add (int a, int b) {
  return (a + b) & 0xFFFF;
}

/*
Additive Inverse (in the range of 0 to 0xFFFF).
*/
private static int addInv (int x) {
  return (0x10000 - x) & 0xFFFF;
}

/*
Multiplication in the multiplicative group (in the range of 0 to 0xFFFF).
*/
private static int mul (int a, int b ) {
  long r = (long)a * b;
  if (r != 0) {
    return (int)(r % 0x10001) & 0xFFFF;
  }
  else {
    return (1 - a - b) & 0xFFFF;
  }
}

/*
Multiplicative inverse (in the range of 0 to 0xFFFF).
*/
private static int mulInv (int x) {
  if (x <= 1) {
    return x;
  }
  int y = 0x10001;
  int t0 = 1;
  int t1 = 0;
  while (true) {
    t1 += y / x * t0;
    y %= x;
    if (y == 1) {
      return 0x10001 - t1;
    }
    t0 += x / y * t1;
    x %= y;
    if (x == 1) {
      return t0;
    }
  }
}


  public static void main(String[] args){
    keyGenerator();
    handleSubKeys();
    String mess = "hello how are you";
    splitPlainText(mess);
    String str = new String(message);
    System.out.println(str);
    processCrypt(subKey);
    String str1 = new String(message);
    System.out.println(str1);
    processCrypt(decryptSubKey);
    String str2 = new String(message);
    str2 = unPad(str2);
    System.out.println(str2);
  }
}

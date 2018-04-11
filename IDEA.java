import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class IDEA{

  private static byte[] key;
  private static int[] subKey;
  private static String plaintext;

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

  private static void splitPlainText(String pTxt){
    int difference = 0;
    String pad = "";
    int length = pTxt.getBytes().length * 8;

    System.out.println(pTxt.getBytes().length);
    //padding required
    if(pTxt.getBytes().length < 8 || (pTxt.getBytes().length * 8) % 64 != 0){
      if(pTxt.getBytes().length < 8)
        difference = 8 - pTxt.getBytes().length;
      else{
        while(length % 64 != 0){
          difference++;
          length++;
        }
      }


      for(int idx = 0; idx < difference; idx++){
        pad += "0";
      }
      pTxt = pad + pTxt;
    }
    byte[] message = pTxt.getBytes();
    System.out.println(difference);
    System.out.println(pTxt);
  }

  public static void main(String[] args){
    keyGenerator();
    splitKey();
    String mess = "hello how are you";
    splitPlainText(mess);
  }
}

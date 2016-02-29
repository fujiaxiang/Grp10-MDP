package utilities;

import java.math.BigInteger;

/**
 * Created by Fujitsu on 29/2/2016.
 */
public class Convertor {
    public static String convertToHex(String map){
        //remove breakline carriage return
        map = map.replaceAll("\r","");
        map = map.replaceAll("\n","");
        return new BigInteger(map,2).toString(16);
    }

    public static String convertFromHex(String hex){
        String ret =  new BigInteger(hex,16).toString(2);
        //append back 0
        System.out.println((ret.length()%4) + "");
        for(int i=ret.length()%4;i<4;i++)
            ret = "0"+ret;
        return ret;
    }

    public static void main(String args[]){
        String a = "0001" +
                "0010" +
                "0011" +
                "0100" +
                "0101" +
                "0110" +
                "0111" +
                "1000" +
                "1001" +
                "1010" +
                "1011" +
                "1100" +
                "1101" +
                "1110" +
                "1111";
        String hex = convertToHex(a);
        System.out.println(hex);
        String ret = convertFromHex(hex);
        System.out.println(ret);
        System.out.println(a);
        System.out.println(ret.compareTo(a));
        System.out.println(convertToHex(ret));
    }
}

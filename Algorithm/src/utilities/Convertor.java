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
        String map_desc = "";
        for(int i=0;i<map.length();i+=4)
            map_desc+=new BigInteger(map.substring(i,i+4),2).toString(16);
        return map_desc;
    }

    public static String convertFromHex(String hex){
        hex = hex.replaceAll("\r","");
        hex = hex.replaceAll("\n","");
        String ret =  "";
        for(int i=0;i<hex.length();i++){
            String bits = new BigInteger(Character.toString(hex.charAt(i)),16).toString(2);
            //append back 0
            if(bits.length()!=4)
                for(int j=bits.length();j<4;j++)
                    bits = "0"+bits;
            ret+=bits;
        }
        /*
        for(int i=0;i<ret.length()/15;i++){
            System.out.println(ret.substring(i*15,(i+1)*15));
        }*/
        return ret;
    }

    /*
    public static int[][] convertToBlock(int[] point,int size){
        int[][] blocks = new int[9][2];
        int n = 0;
        for(int i=-(size/2);i<=size/2;i++)
            for(int j=-(size/2);j<=size/2;j++)
                blocks[n++] = new int[]{point[0]+j,point[1]+i};
        return blocks;
    }
    */

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

package cn.boundaryx.rasp;

/**
 * @author tomato
 */
public class RASPJvm {

    public static boolean javaVersionSupported(){
        int version = 6;
        int majorVersion = getMajorVersion();
        if(majorVersion < version ){
            String message = String.format("[*]CLOUD-RASP java version is: %s RASP does not support below 6", majorVersion);
            System.out.println(message);
            return false;
        }
        return true;
    }

    private static int getMajorVersion(){
        String javaVersion = System.getProperty("java.version");
        String version = "1.";
        int majorVersion;
        if(javaVersion.startsWith(version)){
            majorVersion = Character.digit(javaVersion.charAt(2),10);
        }else{
            String majorAsString = javaVersion.split("\\.")[0];
            int indexOfDash = majorAsString.indexOf('-');
            if (indexOfDash > 0) {
                majorAsString = majorAsString.substring(0, indexOfDash);
            }
            majorVersion = Integer.parseInt(majorAsString);
        }
        return majorVersion;
    }
}

package cn.boundaryx.rasp;

import java.io.File;
import java.util.Map;

/**
 * @author tomato
 */
public class EntryPoint {

    private static final String RASP_HOME = (new File(Agent.class.getProtectionDomain().getCodeSource().getLocation().getFile())).getParentFile().getPath();
    private static final String RASP_LOADER_JAR = RASP_HOME + File.separator + "rasp-loader.jar";

    public static void main(String[] args) throws Exception {
        RASPVMLoader raspvmLoader = new RASPVMLoader();
        if (args.length < 1) {
            System.out.println("(Usage)：java -jar rasp-loader.jar [Options]");
            System.out.println("  2) attach [Java PID]");
            System.out.println();
            System.out.println("(EXAMPLES):");
            System.out.println("  java -jar rasp-loader.jar attach 10001");
            System.out.println();
            System.out.println("JVM Prcess List:");
            Map<String, String> processMap = raspvmLoader.listJVMPID();
            for (String processID : processMap.keySet()) {
                String name = processMap.get(processID);
                System.out.println("PID:" + processID + "\tProcessName:" + ("".equals(name) ? "NONE" : name));
            }
        } else if ("attach".equalsIgnoreCase(args[0])) {
            Object vm = raspvmLoader.attach(args[1]);
            raspvmLoader.loadAgent(vm, RASP_LOADER_JAR, args[0]);
            raspvmLoader.detach(vm);
        }
    }


}

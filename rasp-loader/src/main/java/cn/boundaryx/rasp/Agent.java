package cn.boundaryx.rasp;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.security.AllPermission;
import java.util.jar.JarFile;

/**
 * @author tomato
 */
public class Agent {
    private static final String RASP_HOME = (new File(Agent.class.getProtectionDomain().getCodeSource().getLocation().getFile())).getParentFile().getPath();
    private static final String RASP_CORE_JAR;
    private static final String RASP_LOADER_JAR;
    private static final String RASPLauncherClassName = "cn.boundaryx.rasp.RASPLauncher";
    private static final String ATTACH = "CloudRASP.attached";

    static {
        RASP_CORE_JAR = RASP_HOME + File.separator + "rasp-core.jar";
        RASP_LOADER_JAR = RASP_HOME + File.separator + "rasp-loader.jar";
    }

    /**
     * 运行前加载agent
     * @param agentArgs Agent参数
     * @param instrumentation inst
     */
    public static void premain(String agentArgs, Instrumentation instrumentation) {
        init("attach",instrumentation);
    }

    /**
     * 运行中加载agent
     * @param agentArgs Agent参数
     * @param instrumentation inst
     */
    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        init(agentArgs, instrumentation);
    }

    private static synchronized void init(String agentArgs, Instrumentation instrumentation){
        printLogo();
        if(!preCheck()){
            return;
        }
        String[] args = agentArgs != null ? agentArgs.split("\\s+") : new String[0];
        if (args.length > 0) {
            try {
                if(Boolean.getBoolean(ATTACH)){
                    System.err.println("[*]CloudRASP has been loaded，don't load again");
                    return;
                }
                if ("attach".equalsIgnoreCase(args[0])) {
                    System.out.println("[*]CloudRASP start loading");
                    install(agentArgs,instrumentation);
                    System.out.println("[*]CloudRASP load successfully");
                    System.setProperty(ATTACH,Boolean.TRUE.toString());
                }
            }catch (Throwable t){
                System.err.println("[*]CloudRASP failed to load");
                t.printStackTrace();
            }
        }
    }

    /**
     * 安装RASP
     * @param AgentArgs 启动参数
     * @param instrumentation inst
     * @throws Throwable 安装异常
     */
    private static synchronized void install(String AgentArgs,Instrumentation instrumentation) throws Throwable {
        instrumentation.appendToBootstrapClassLoaderSearch(new JarFile(RASP_LOADER_JAR));
        instrumentation.appendToBootstrapClassLoaderSearch(new JarFile(RASP_CORE_JAR));
        RASPLoader raspLoader = new RASPLoader();
        raspLoader.loaderJar(RASP_CORE_JAR);
        Class<?> RASPLauncher = RASPLoader.RASPClassLoader.loadClass(RASPLauncherClassName);
        RASPLauncher.getDeclaredMethod("launch",Instrumentation.class).invoke(RASPLauncher.newInstance(),instrumentation);
    }

    /**
     * 从资源文件中读取并打印logo
     */
    private static void printLogo(){
        InputStream inputStream = Agent.class.getResourceAsStream("/resources/logo");
        assert inputStream != null;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        try {
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            String logo = buffer.toString("UTF-8");
            System.out.println(logo);
        }catch (Exception e){
            System.out.println("[*]CLOUD-RASP Logo loading failed");
        }
    }

    private static boolean preCheck(){
        if(checkJBoss()){
            enableJboss7Modules();
        }
        enableOSGIDelegation();
        return RASPJvm.javaVersionSupported() && checkSecurityManager();
    }

    private static void enableJboss7Modules() {
        String JBOSS_SYSTEM_MODULES_KEY = "jboss.modules.system.pkgs";
        String RASP_AGENT_PACKAGES = "cn.boundaryx.rasp";
        String oldValue = System.getProperty(JBOSS_SYSTEM_MODULES_KEY, null);
        System.setProperty(JBOSS_SYSTEM_MODULES_KEY, oldValue == null ? RASP_AGENT_PACKAGES : oldValue + "," + RASP_AGENT_PACKAGES);
    }

    /**
     * 使得 OSGI 通过双亲委派机制加载 Agent中的类
     */
    private static void enableOSGIDelegation(){
        String OSGI_BOOT_DELEGATION = "org.osgi.framework.bootdelegation";
        String bootDelegation = System.getProperty(OSGI_BOOT_DELEGATION,null);
        System.setProperty(OSGI_BOOT_DELEGATION, bootDelegation == null ? "cn.boundaryx.rasp.*" : bootDelegation + "," + "cn.boundaryx.rasp.*");
    }

    /**
     *
     * @return Security Manager 状态
     */
    private static boolean checkSecurityManager(){
        SecurityManager securityManager = System.getSecurityManager();
        if(securityManager == null){
            return true;
        }else{
            try {
                securityManager.checkPermission(new AllPermission());
                return true;
            }catch (SecurityException securityException){
                System.out.println("[*]CLOUDRASP SecurityManager Check Failed");
                securityException.fillInStackTrace();
                return false;
            }
        }
    }

    /**
     *
     * @return 是否为JBoss
     */
    private static boolean checkJBoss(){
        boolean jboss = false;
        String spilt = System.getProperty("path.separator") == null ? ";" : System.getProperty("path.separator");
        String[] jars = System.getProperty("java.class.path").split(spilt);
        for (String jar : jars) {
            if (jar.endsWith("jboss-modules.jar")) {
                jboss = true;
                break;
            }
        }
        return jboss;
    }
}

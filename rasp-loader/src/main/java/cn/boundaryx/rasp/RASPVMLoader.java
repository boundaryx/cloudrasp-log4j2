package cn.boundaryx.rasp;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RASPVMLoader {

    private Class<?> VMClass;

    public RASPVMLoader() {
        String VMName = "com.sun.tools.attach.VirtualMachine";

        try {
            this.VMClass = Class.forName(VMName);
        } catch (ClassNotFoundException var6) {
            File ToolJar = this.getToolsPath();

            try {
                URL[] urls = new URL[]{ToolJar.toURI().toURL()};
                this.VMClass = (new URLClassLoader(urls)).loadClass(VMName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private File getToolsPath() {
        String javaHome = System.getenv().get("JAVA_HOME");
        File jarFile;
        if (javaHome != null) {
            jarFile = new File(javaHome);
        } else {
            jarFile = new File(System.getProperty("java.home"));
        }

        File toolsJar = new File(jarFile + "/lib/", "tools.jar");
        if (!toolsJar.exists()) {
            toolsJar = new File(toolsJar.getParentFile().getParentFile() + "/lib/", "tools.jar");
            if (!toolsJar.exists()) {
                throw new RuntimeException("Not running with JDK!");
            }
        }

        return toolsJar;
    }

    public Map<String, String> listJVMPID() throws Exception {
        Map<String, String> processMap = new HashMap<String,String>();
        List<?> list = (List<?>)this.VMClass.getDeclaredMethod("list").invoke(null);
        for (Object p : list) {
            Class<?> descriptorClass = p.getClass();
            String processId = (String) descriptorClass.getMethod("id").invoke(p);
            String displayName = (String) descriptorClass.getMethod("displayName").invoke(p);
            processMap.put(processId, displayName);
        }

        return processMap;
    }

    public Object attach(String pid) throws Exception {
        return this.VMClass.getDeclaredMethod("attach", String.class).invoke((Object)null, pid);
    }

    public void detach(Object vm) throws Exception {
        vm.getClass().getDeclaredMethod("detach").invoke(vm);
    }

    public void loadAgent(Object vm, String agentFile, String args) throws Exception {
        this.VMClass.getDeclaredMethod("loadAgent", String.class, String.class).invoke(vm, agentFile, args);
    }
}

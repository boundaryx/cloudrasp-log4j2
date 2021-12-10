package cn.boundaryx.rasp.hooks;

import cn.boundaryx.rasp.analyzer.JndiAnalyzer;
import cn.boundaryx.rasp.utils.BytecodeUtils;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;

import java.io.IOException;

public class JndiHook {

    public static boolean classMatch(String className) {
        return "javax/naming/InitialContext".equals(className);
    }

    public static void beforeMethod(CtClass ctClass) throws IOException, CannotCompileException, NotFoundException {
        String src = getEnhancedCodeWithException(JndiAnalyzer.class, "checkJndiStr",
                "$1", String.class);
        BytecodeUtils.insertBefore(ctClass, "lookup", "(Ljava/lang/String;)Ljava/lang/Object;", src);
    }

    public static byte[] transform(CtClass ctClass) {
        try {
            beforeMethod(ctClass);
            return ctClass.toBytecode();
        } catch (Exception e) {
            String classname = ctClass.getName();
            String message = "process class name is " + classname + " " + e.getMessage();
            System.err.println(message);
        }
        return null;
    }


    public static String getEnhancedCodeWithException(Class<?> callbackClass, String methodName, String paramString, Class<?>... parameterTypes) {
        String src;
        String callbackClassName = callbackClass.getName();
        src = callbackClassName + '.' + methodName + "(" + paramString + ");";
        /**String parameterTypesString = "";
        if (parameterTypes != null && parameterTypes.length > 0) {
            for (Class<?> parameterType : parameterTypes) {
                if (parameterType.getName().startsWith("[")) {
                    parameterTypesString += "Class.forName(\"" + parameterType.getName() + "\"),";
                } else {
                    parameterTypesString += (parameterType.getName() + ".class,");
                }
            }
            parameterTypesString = parameterTypesString.substring(0, parameterTypesString.length() - 1);
        }
        if ("".equals(parameterTypesString)) {
            parameterTypesString = null;
        } else {
            parameterTypesString = "new Class[]{" + parameterTypesString + "}";
        }
        src = "cn.boundaryx.rasp.RASPLoader.RASPClassLoader.loadClass(\"" + callbackClassName + "\").getMethod(\"" + methodName +
                    "\"," + parameterTypesString + ").invoke(null";
        if (paramString !=null) {
                src += (",new Object[]{" + paramString + "});");
        } else {
                src += ",null);";
        }**/
        src = "try {" + src + "} catch (cn.boundaryx.rasp.exception.RASPSecurityException RASPSecurityException) {throw RASPSecurityException;}";
        return src;
    }

}

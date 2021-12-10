package cn.boundaryx.rasp.utils;

import javassist.*;

import java.util.Collections;
import java.util.LinkedList;

public class BytecodeUtils {
    /**
     * 在目标类的目标方法的入口插入相应的源代码
     *
     * @param ctClass    目标类
     * @param methodName 目标方法名称
     * @param desc       目标方法的描述符号
     * @param src        待插入的源代码
     */
    public static void insertBefore(CtClass ctClass, String methodName, String desc, String src)
            throws CannotCompileException {

        LinkedList<CtBehavior> methods = getMethod(ctClass, methodName, desc, null);
        if (methods.size() > 0) {
            insertBefore(methods, src);
        } else {
        }
    }

    /**
     * 在目标类的目标方法的入口插入相应的源代码
     * 可排除一定的方法
     *
     * @param ctClass     目标类
     * @param methodName  目标方法名称
     * @param excludeDesc 排除的方法描述符
     * @param src         待插入的源代码
     */
    public static void insertBeforeWithExclude(CtClass ctClass, String methodName, String excludeDesc, String src)
            throws CannotCompileException {

        LinkedList<CtBehavior> methods = getMethod(ctClass, methodName, null, excludeDesc);
        if (methods.size() > 0) {
            insertBefore(methods, src);
        } else {
        }
    }

    public static void insertBeforeWithStartStrDesc(CtClass ctClass, String methodName, String startStrDesc, String src)
            throws CannotCompileException {

        LinkedList<CtBehavior> methods = getMethod(ctClass, methodName,startStrDesc);
        if (methods.size() > 0) {
            insertBefore(methods, src);
        } else {
        }
    }

    private static void insertBefore(LinkedList<CtBehavior> methods, String src)
            throws CannotCompileException {
        for (CtBehavior method : methods) {
            if (method != null) {
                insertBefore(method, src);
            }
        }
    }

    /**
     * 在目标类的一组重载的目标方法的入口插入相应的源代码
     *
     * @param ctClass    目标类
     * @param methodName 目标方法名称
     * @param allDesc    目标方法的一组描述符
     * @param src        待插入的源代码
     */
    public static void insertBefore(CtClass ctClass, String methodName, String src, String[] allDesc)
            throws NotFoundException, CannotCompileException {
        for (String desc : allDesc) {
            insertBefore(ctClass, methodName, desc, src);
        }
    }

    /**
     * 在目标类的目标方法的出口插入相应的源代码
     *
     * @param ctClass    目标类
     * @param methodName 目标方法名称
     * @param desc       目标方法的描述符号
     * @param src        待插入的源代码
     * @param asFinally  是否在抛出异常的时候同样执行该源代码
     */
    public static void insertAfter(CtClass ctClass, String methodName, String desc, String src, boolean asFinally)
            throws NotFoundException, CannotCompileException {

        LinkedList<CtBehavior> methods = getMethod(ctClass, methodName, desc, null);
        if (methods.size() > 0) {
            for (CtBehavior method : methods) {
                if (method != null) {
                    insertAfter(method, src, asFinally);
                }
            }
        } else {
        }

    }

    public static void insertAfterWithStartStrDesc(CtClass ctClass, String methodName, String startStrDesc, String src)
            throws CannotCompileException {

        LinkedList<CtBehavior> methods = getMethod(ctClass, methodName,startStrDesc);
        if (methods.size() > 0) {
            insertAfter(methods, src);
        } else {
        }
    }

    private static void insertAfter(LinkedList<CtBehavior> methods, String src)
            throws CannotCompileException {
        for (CtBehavior method : methods) {
            if (method != null) {
                insertAfter(method, src, false);
            }
        }
    }

    private static LinkedList<CtBehavior> getConstructor(CtClass ctClass, String desc) {
        LinkedList<CtBehavior> methods = new LinkedList<CtBehavior>();
        if (desc == null || desc.isEmpty()) {
            Collections.addAll(methods, ctClass.getDeclaredConstructors());
        } else {
            try {
                methods.add(ctClass.getConstructor(desc));
            } catch (NotFoundException e) {
                // ignore
            }
        }
        return methods;
    }

    /**
     * 获取特定类的方法实例
     * 如果描述符为空，那么返回所有同名的方法
     *
     * @param ctClass    javassist 类实例
     * @param methodName 方法名称
     * @param desc       方法描述符
     * @return 所有符合要求的方法实例
     * @see javassist.bytecode.Descriptor
     */
    private static LinkedList<CtBehavior> getMethod(CtClass ctClass, String methodName, String desc, String excludeDesc) {
        if ("<init>".equals(methodName)) {
            return getConstructor(ctClass, desc);
        }
        LinkedList<CtBehavior> methods = new LinkedList<CtBehavior>();
        if (desc == null || desc.isEmpty()) {
            CtMethod[] allMethods = ctClass.getDeclaredMethods();
            if (allMethods != null) {
                for (CtMethod method : allMethods) {
                    if (method != null
                            && !method.isEmpty()
                            && method.getName().equals(methodName)
                            && !method.getSignature().equals(excludeDesc)) {
                        methods.add(method);
                    }
                }
            }
        } else {
            try {
                CtMethod ctMethod = ctClass.getMethod(methodName, desc);
                if (ctMethod != null && !ctMethod.isEmpty()) {
                    methods.add(ctMethod);
                }
            } catch (NotFoundException e) {
                // ignore
            }
        }
        return methods;
    }

    private static LinkedList<CtBehavior> getMethod(CtClass ctClass, String methodName, String startStrDesc) {
        LinkedList<CtBehavior> methods = new LinkedList<CtBehavior>();
        CtMethod[] allMethods = ctClass.getDeclaredMethods();
        if (allMethods != null) {
            for (CtMethod method : allMethods) {
                if (method != null && !method.isEmpty() && method.getName().equals(methodName) && method.getSignature().startsWith(startStrDesc)) {
                    methods.add(method);
                }
            }
        }
        return methods;
    }

    /**
     * 在目标类的目标方法的入口插入相应的源代码
     *
     * @param method 目标方法
     * @param src    源代码
     */
    public static void insertBefore(CtBehavior method, String src) throws CannotCompileException {
        try {
            method.insertBefore(src);
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
    }

    /**
     * (none-javadoc)
     *
     * @see BytecodeUtils#insertAfter(CtClass, String, String, String, boolean)
     */
    public static void insertAfter(CtClass invokeClass, String methodName, String desc, String src)
            throws NotFoundException, CannotCompileException {
        insertAfter(invokeClass, methodName, desc, src, false);
    }

    /**
     * 在目标类的目标方法的出口插入相应的源代码
     *
     * @param method    目标方法
     * @param src       源代码
     * @param asFinally 是否在抛出异常的时候同样执行该源代码
     */
    public static void insertAfter(CtBehavior method, String src, boolean asFinally) throws CannotCompileException {
        try {
            method.insertAfter(src, asFinally);
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
    }

    public static void addCatch(CtClass ctClass, String methodName, String src, String[] allDesc, CtClass exceptionType){
        for (String desc : allDesc) {
            addCatch(ctClass, methodName, desc, src, exceptionType);
        }
    }

    public static void addCatch(CtClass ctClass, String methodName, String desc, String src, CtClass exceptionType){
        LinkedList<CtBehavior> methods = getMethod(ctClass, methodName, desc, null);
        if (methods.size() > 0) {
            addCatch(methods, src, exceptionType);
        } else {
        }
    }

    private static void addCatch(LinkedList<CtBehavior> methods, String src, CtClass exceptionType){
        for (CtBehavior method : methods) {
            if (method != null) {
                addCatch(method, src, exceptionType);
            }
        }
    }

    public static void addCatch(CtBehavior method, String src, CtClass exceptionType){
        try {
            method.addCatch(src, exceptionType);
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
    }
}

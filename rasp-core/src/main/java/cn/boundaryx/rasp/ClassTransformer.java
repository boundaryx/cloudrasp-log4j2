package cn.boundaryx.rasp;

import cn.boundaryx.rasp.hooks.JndiHook;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class ClassTransformer implements ClassFileTransformer {


    private Instrumentation inst;

    public ClassTransformer(Instrumentation inst) {
        this.inst = inst;
    }

    /**
     * 核心类修改方法
     *
     * @param loader              当前类加载器
     * @param className           类名
     * @param classBeingRedefined
     * @param protectionDomain
     * @param classfileBuffer
     * @return 修改过后的字节码
     */
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
            if (JndiHook.classMatch(className)) {
                CtClass ctClass = null;
                try {
                    ClassPool classPool = ClassPool.getDefault();
                    if (loader != null) {
                        classPool.appendClassPath(new LoaderClassPath(loader));
                    }
                    ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer),false);
                    classfileBuffer = JndiHook.transform(ctClass);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (ctClass != null) {
                        ctClass.detach();
                    }
                }
            }
        return classfileBuffer;
    }

    public void getRetransform(){
        Class<?>[] loadedClasses = inst.getAllLoadedClasses();
        for (Class<?> clazz : loadedClasses) {
            if (JndiHook.classMatch(clazz.getName().replace(".", "/"))) {
                if (!clazz.isInterface() || inst.isModifiableClass(clazz) && !clazz.getName().startsWith("java.lang.invoke.LambdaForm")) {
                    try {
                        inst.retransformClasses(clazz);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void init() {
        inst.addTransformer(this,true);
        this.getRetransform();
    }

}

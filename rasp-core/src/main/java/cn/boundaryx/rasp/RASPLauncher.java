package cn.boundaryx.rasp;


import java.lang.instrument.Instrumentation;

public class RASPLauncher {

    /**
     * RASP初始化
     *
     * @param instrumentation inst
     */
    public void launch(Instrumentation instrumentation) throws Exception{
        initTransformer(instrumentation);
    }

    /**
     * 初始化 ClassFileTransformer
     *
     * @param instrumentation inst
     */
    private void initTransformer(Instrumentation instrumentation) {
        ClassTransformer classTransformer = new ClassTransformer(instrumentation);
        classTransformer.init();
    }

}

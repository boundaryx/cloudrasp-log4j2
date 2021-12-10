package cn.boundaryx.rasp.utils;

import java.util.LinkedList;
import java.util.List;

public class StackTrace {

    /**
     * 获取反射Hook的堆栈信息,用于攻击检测判断
     *
     * @param hasLineNumber 是否显示行号
     * @return 堆栈数组
     */
    private static final int STACK_MAX = 500;

    public static List<String> filterReflectStackTraceList(StackTraceElement[] stackTrace, boolean hasLineNumber) {
        LinkedList<String> stacktrace = new LinkedList<String>();
        if (stackTrace.length > 2) {
            for(int index=0; index < stackTrace.length && index < STACK_MAX;index++){
                String stack = stackTrace[index].toString();
                if(index < 6 && (stack.startsWith("sun.reflect") || stack.startsWith("java.lang.reflect"))){
                    continue;
                }
                if (!stack.contains("cn.boundaryx.rasp")) {
                    if (hasLineNumber) {
                        stacktrace.add(stack);
                    } else {
                        stacktrace.add(stackTrace[index].getClassName() + "." + stackTrace[index].getMethodName());
                    }
                }
            }
        }
        return stacktrace;
    }

    /**
     * 获取堆栈信息,用于攻击检测判断
     *
     * @param hasLineNumber 是否显示行号
     * @return 堆栈数组
     */
    public static List<String> filterStackTraceList(StackTraceElement[] stackTrace, boolean hasLineNumber) {
        LinkedList<String> stacktrace = new LinkedList<String>();
        if (stackTrace.length > 2) {
            int i;
            for(i = 1; i < stackTrace.length; i++){
                if(!stackTrace[i].getClassName().startsWith("cn.boundaryx.rasp")){
                    break;
                }
            }
            int start = i;
            for(int j = start; j < stackTrace.length && j < STACK_MAX; j++ ){
                if (hasLineNumber) {
                    stacktrace.add(stackTrace[j].toString());
                } else {
                    stacktrace.add(stackTrace[j].getClassName() + "." + stackTrace[j].getMethodName());
                }
            }
        }
        return stacktrace;
    }
}

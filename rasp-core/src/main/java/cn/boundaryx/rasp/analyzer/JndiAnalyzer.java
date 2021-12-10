package cn.boundaryx.rasp.analyzer;

import cn.boundaryx.rasp.exception.RASPSecurityException;
import cn.boundaryx.rasp.utils.StackTrace;

import java.util.List;

public class JndiAnalyzer {

    private static final String Log4j = "org.apache.logging.log4j.core.lookup.Interpolator.lookup";

    public static void checkJndiStr(String JndiStr){

        List<String> stacktrace = StackTrace.filterReflectStackTraceList(new Throwable().getStackTrace(),false);

        for(String stack: stacktrace){
            if(stack.contains(Log4j)){
                RASPSecurityException securityException = new RASPSecurityException("Request blocked by CloudRASP");
                throw securityException;
            }
        }
    }




}

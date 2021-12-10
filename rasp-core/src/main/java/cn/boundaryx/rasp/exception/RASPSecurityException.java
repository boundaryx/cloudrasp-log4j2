package cn.boundaryx.rasp.exception;

/**
 * @author tomato
 */
public class RASPSecurityException extends RuntimeException {

    /**
     * RASP 阻断安全异常
     */
    public RASPSecurityException() {
        super();
    }


    public RASPSecurityException(String message) {
        super(message);
    }
}

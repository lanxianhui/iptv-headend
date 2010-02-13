package pl.lodz.p.cm.ctp.dao;

public class DAOConfigurationException extends RuntimeException {

	private static final long serialVersionUID = 8916134870362248423L;

    public DAOConfigurationException(String message) {
        super(message);
    }

    public DAOConfigurationException(Throwable cause) {
        super(cause);
    }

    public DAOConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

}
package pl.lodz.p.cm.ctp.dao;

public class DAOException extends Exception {

	private static final long serialVersionUID = -1536740064774056746L;

	public DAOException(String message) {
        super(message);
    }

    public DAOException(Throwable cause) {
        super(cause);
    }

    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }

}

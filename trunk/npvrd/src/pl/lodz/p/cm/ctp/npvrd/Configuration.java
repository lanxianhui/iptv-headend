package pl.lodz.p.cm.ctp.npvrd;

import java.io.Serializable;
import pl.lodz.p.cm.ctp.dao.DatabaseConfiguration;

public class Configuration implements Serializable {

	private static final long serialVersionUID = 1853011552618461040L;
	
	public DatabaseConfiguration database;
	public String recordings;
	public Integer prepTime;
	public Integer cleanerResolution;
	public Integer cleanerTolerance;
	public VlmConfig vlm;

}

package de.thb.fbi.msr.maus.uebung7.dataaccessremote.remote;

import javax.ws.rs.ApplicationPath;

import org.apache.log4j.Logger;

@ApplicationPath(value="/rest/*")
public class RemoteDataAccessRESTService extends javax.ws.rs.core.Application {

	protected static Logger logger = Logger.getLogger(RemoteDataAccessRESTService.class);
	
	public RemoteDataAccessRESTService() {
		logger.info("<Constructor>()");
	}
	
}

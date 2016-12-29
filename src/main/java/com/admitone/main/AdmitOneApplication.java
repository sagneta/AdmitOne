package com.admitone.main;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/services")
public class AdmitOneApplication extends Application {
	
    /////////////////////////////////////////////////////////////////////////
    // Left empty intentionally.                                           //
    // Do not fill in this space as it interferes with CDI which is used   //
    // across the code base. It is not necessary as jax annotations        //
    // will be scanned and recognized automatically.                       //
    /////////////////////////////////////////////////////////////////////////
}

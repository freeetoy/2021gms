package com.gms.web.admin.domain.common;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;


public class AppVersionVO implements Serializable {

private static final long serialVersionUID = 1331928141333422978L;
	
	/** App_Ver                */
	private String appVer;

	public String getAppVer() {
		return appVer;
	}

	public void setAppVer(String appVer) {
		this.appVer = appVer;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}

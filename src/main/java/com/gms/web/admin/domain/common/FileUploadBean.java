package com.gms.web.admin.domain.common;

import java.io.Serializable;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileUploadBean implements Serializable{
	
	private static final long serialVersionUID = 431928141433422978L;

	private MultipartFile file;
}

package com.waes.waesHttpEndpoint.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.waes.waesHttpEndpoint.dto.RequestDto;
import com.waes.waesHttpEndpoint.util.DataStorageProperties;
import com.waes.waesHttpEndpoint.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import com.wearewaes.binarydiff.exception.FileErrorException;


@Service
public class DataSaveService {
	
    private static final Logger logger = LoggerFactory.getLogger(DataSaveService.class);
	
    private final Path fileStorageLocation;
	@Autowired
	public DataSaveService(DataStorageProperties dataStorageProperties) {
		this.fileStorageLocation = Paths.get(dataStorageProperties.getUploadDir())
				.toAbsolutePath().normalize();
		logger.info("Root Folder to save files: {}" ,fileStorageLocation.toString());

		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception ex) {
			throw new FileErrorException("File creating error", ex);
		}
	}

   /**
	 * Method responsible for convert the Base64 data into saved file and save the file at the Left folder
	 * @param fileDto - DTO with String with file name identification and String with data in text format
	 * @return fileWithPath - String with the file name + file path
	 */
	public String saveLeftFile(RequestDto fileDto) {
		return this.saveFile(fileDto, true);
	}

	/**
	 * Method responsible for convert the Base64 data into saved file and save the file at the Right folder
	 * @param fileDto - DTO with String with file name identification and String with data in text format
	 * @return fileWithPath - String with the file name + file path
	 */
	public String saveRightFile(RequestDto fileDto) {
		return this.saveFile(fileDto, false);
	}
	
	/**
	 * Method responsible for convert the Base64 data into saved file
	 * @param fileDto - DTO with String with file name identification and String with data in text format
	 * @param left - Boolean to inform if it´s LEFT folder or RIGHT
	 * @return fileWithPath - String with the file name + file path
	 */
	private String saveFile(RequestDto fileDto, boolean left) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(fileDto.getName());
        String fileWithPath =  null;
        
        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileErrorException("Filename contains invalid path sequence:"+ fileName) ;
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = chooseSide(left);
            
            Files.createDirectories(targetLocation);
            logger.info("Complete Folder Path to saved files: {}", targetLocation.toString());
            
            fileWithPath = targetLocation.toString()+"/"+fileName; 
            logger.info("File with path: {}", fileWithPath);
            
            FileUtil.decodeBase64(fileDto.getData(),fileWithPath);

            return fileWithPath;
        } catch (IOException ex) {
            throw new FileErrorException("Could not store file: " + fileWithPath, ex);
        }



	}


	/**
	 * Inform the folder
	 * @param boolean true to left - false to right
	 * @return Path - folder path
	 */
	private Path chooseSide(boolean left) {
		String pathFolder = "left/";
		if(!left)
			pathFolder = "right/";
		return this.fileStorageLocation.resolve(pathFolder);
	}
	

	

}

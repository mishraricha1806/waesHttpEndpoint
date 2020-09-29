package com.waes.waesHttpEndpoint.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.waes.waesHttpEndpoint.dto.ResponseDto;
import com.waes.waesHttpEndpoint.util.DataStorageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




import com.wearewaes.binarydiff.exception.FileErrorException;


@Service
public class DataDiffService {
	
    private static final Logger logger = LoggerFactory.getLogger(DataDiffService.class);
	
    private final Path fileStorageLocation;

    @Autowired
    public DataDiffService(DataStorageProperties dataStorageProperties) {
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
     * Method responsible for inform if the FILE is equal into both sides- LEFT folder and RIGHT folder
     * Using the file name / identification, it compare the file and return the result
     * @param String identification / name
     * @return DTO FileDiffResponseDto with the comparison result 
     */
	public ResponseDto verifyDiffById(String id)  {
		
		ResponseDto dto = new ResponseDto();
		byte[] left = null;
		byte[] right = null;
		try {
			left = loadFile(id, true);
			 right = loadFile(id, false);
			 dto.setFilesAreEqual(getCheckSum(left,right));
		} catch (FileNotFoundException | NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		dto.setFileId(id);
		dto.setSameSize(left.length == right.length);
		dto.setSizeFileLeft(left.length);
		dto.setSizeFileRight(right.length);
		
		
		return dto;
		}
	/**
	 * Load file using the file name and choose the folder Left or Right - boolean true to LEFT false to RIGHT
	 * @param fileName - file name
	 * @param left - boolean choose the side
	 * @return byte[]  - file bytes
	 * @throws FileNotFoundException
	 */
	private byte[] loadFile(String fileName, boolean left) throws FileNotFoundException {
		Path targetLocation = chooseSide(left);
		Path filePath = targetLocation.resolve(fileName).normalize();
		try {
			return Files.readAllBytes(filePath);
		} catch (IOException ex) {
			throw new FileNotFoundException("Not found file in path: "+filePath.toString());
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

	/**
	 * Generate MD5 hash to verify the file checksum
	 * @param byte[] contentLeft - file from LEFT folder
	 * @param byte[] contentRight - file from RIGHT folder
	 * @return boolean if the hash has the same result
	 * @throws NoSuchAlgorithmException
	 */
	private  boolean getCheckSum(byte[] contentLeft, byte[] contentRight) throws NoSuchAlgorithmException {
		byte[] hashLeft = MessageDigest.getInstance("MD5").digest(contentLeft);
		byte[] hashRight = MessageDigest.getInstance("MD5").digest(contentRight);
		return MessageDigest.isEqual(hashLeft, hashRight);
	}
}

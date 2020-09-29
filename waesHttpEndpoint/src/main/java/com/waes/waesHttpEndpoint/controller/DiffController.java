package com.waes.waesHttpEndpoint.controller;


import com.waes.waesHttpEndpoint.dto.RequestDto;
import com.waes.waesHttpEndpoint.dto.ResponseDto;
import com.waes.waesHttpEndpoint.service.DataDiffService;
import com.waes.waesHttpEndpoint.service.DataSaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


@RestController
@RequestMapping("filediff/api")
public class DiffController {
	
	
	@Autowired
	DataDiffService dataDiffService;
	@Autowired
	DataSaveService dataSaveService;
	
	@GetMapping("/v1/diff/{id}")
	public ResponseEntity<ResponseDto> verifyDiffById(@PathVariable String id)  {
		ResponseDto response = dataDiffService.verifyDiffById(id);
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/v1/diff/{id}/left")
	public ResponseEntity<Object> createFileLeft(@PathVariable String id, @RequestBody String data) {

		RequestDto dto = new RequestDto(id,data);
		String idResponse = dataSaveService.saveLeftFile(dto);
		URI location =  ServletUriComponentsBuilder.
			fromCurrentRequest().
			path("/{idResponse}").
			buildAndExpand(id).toUri();
		
		return ResponseEntity.created(location).build();
	}
	
	@PostMapping("/v1/diff/{id}/right")
	public ResponseEntity<Object> createFileRight(@PathVariable String id, @RequestBody String data) {
		RequestDto dto = new RequestDto(id,data);
		String idResponse = dataSaveService.saveRightFile(dto);
		URI location =  ServletUriComponentsBuilder.
			fromCurrentRequest().
			path("/{idResponse}").
			buildAndExpand(id).toUri();
		
		return ResponseEntity.created(location).build();
	}

}

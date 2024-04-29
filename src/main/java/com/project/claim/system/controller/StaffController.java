//StaffController.java
package com.project.claim.system.controller;

import com.project.claim.system.dto.NewUserDTO;
import com.project.claim.system.dto.RegisterResponseDTO;
import com.project.claim.system.dto.StaffDTO;
import com.project.claim.system.repository.StaffRepository;
import com.project.claim.system.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/staffs")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;
    private final StaffRepository staffRepository;

//    CREATE STAFF
    @PostMapping("/create")
    public ResponseEntity<StaffDTO> createStaff (@RequestBody StaffDTO staffDTO) {
        StaffDTO createdStaff = staffService.createStaff(staffDTO);
        return new ResponseEntity<>(createdStaff, HttpStatus.CREATED);
    }

//    GET ALL STAFF
    @GetMapping()
    public List<StaffDTO> staffList () {
        return staffService.staffList();
    }

    // GET STAFF BY ID
    @GetMapping("{id}")
    public ResponseEntity<StaffDTO> viewStaff(@PathVariable("id") UUID id) { // Change method signature to accept UUID
        StaffDTO staffDTO = staffService.viewStaff(id);
        return new ResponseEntity<>(staffDTO, HttpStatus.OK);
    }

    // UPDATE STAFF
    @PutMapping("{id}")
    public ResponseEntity<StaffDTO> updateStaff(@RequestBody StaffDTO staffDTO, @PathVariable("id") UUID id) { // Change method signature to accept UUID
        StaffDTO updateDetails = staffService.updateStaff(staffDTO, id);
        return new ResponseEntity<>(updateDetails, HttpStatus.OK);
    }

    // DELETE STAFF
    @DeleteMapping("{id}")
    public ResponseEntity<StaffDTO> deleteStaff(@PathVariable("id") UUID id) { // Change method signature to accept UUID
        staffService.deleteStaff(id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    //create user by admin

    @PostMapping("/admin/create")
    public ResponseEntity<?> registerUser(@RequestBody StaffDTO staffDTO) {
        try {
            NewUserDTO newUser = staffService.createdByAdmin(staffDTO);
            return ResponseEntity.ok(newUser);
        } catch (HttpClientErrorException.Unauthorized unauthorized) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized Access");
        }
    }




}

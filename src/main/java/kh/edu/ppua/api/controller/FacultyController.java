package kh.edu.ppua.api.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kh.edu.ppua.api.model.FacultyEntity;
import kh.edu.ppua.api.service.FacultyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/faculty")
@Tag(name = "Faculty Controller", description = "APIs for Faculty")
public class FacultyController {

    @Autowired
    private FacultyService service;

    // Get all faculties
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search completed"),
            @ApiResponse(responseCode = "400", description = "Missing or invalid search parameters"),
            @ApiResponse(responseCode = "500", description = "Search service unavailable")
    })
    public List<FacultyEntity> getAllFaculties() {
        return service.getAllFaculties();
    }

    // Get faculty by ID
    @GetMapping("/{id}")
    public ResponseEntity<FacultyEntity> getFacultyById(@PathVariable Long id) {
        return service.getFacultyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create faculty
    @PostMapping
    public FacultyEntity createFaculty(@RequestBody FacultyEntity faculty) {
        return service.createFaculty(faculty);
    }

    // Update faculty
    @PutMapping("/{id}")
    public ResponseEntity<FacultyEntity> updateFaculty(@PathVariable Long id, @RequestBody FacultyEntity facultyDetails) {
        try {
            return ResponseEntity.ok(service.updateFaculty(id, facultyDetails));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete faculty
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFaculty(@PathVariable Long id) {
        service.deleteFaculty(id);
        return ResponseEntity.noContent().build();
    }
}
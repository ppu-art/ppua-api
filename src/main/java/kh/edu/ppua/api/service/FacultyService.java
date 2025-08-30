package kh.edu.ppua.api.service;

import kh.edu.ppua.api.model.FacultyEntity;
import kh.edu.ppua.api.repository.FacultyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FacultyService {

    @Autowired
    private FacultyRepository repository;

    // Get all faculty
    public List<FacultyEntity> getAllFaculties() {
        return repository.findAll();
    }

    // Get faculty by ID
    public Optional<FacultyEntity> getFacultyById(Long id) {
        return repository.findById(id);
    }

    // Create faculty
    public FacultyEntity createFaculty(FacultyEntity faculty) {
        return repository.save(faculty);
    }

    // Update faculty
    public FacultyEntity updateFaculty(Long id, FacultyEntity facultyDetails) {
        FacultyEntity faculty = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Faculty not found with id: " + id));
        faculty.setName(facultyDetails.getName());
        faculty.setNameKh(facultyDetails.getNameKh());
        faculty.setEmail(facultyDetails.getEmail());
        faculty.setPhone(facultyDetails.getPhone());
        return repository.save(faculty);
    }

    // Delete faculty
    public void deleteFaculty(Long id) {
        repository.deleteById(id);
    }
}
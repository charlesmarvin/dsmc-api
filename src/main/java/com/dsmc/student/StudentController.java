package com.dsmc.student;

import com.dsmc.common.service.encryption.EncryptionService;
import com.dsmc.common.util.PageableHelper;
import com.dsmc.student.domain.Student;
import com.dsmc.student.dto.StudentDTO;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@ResponseBody
@RequestMapping(value = "api/companies/{companyId}/students")
@PreAuthorize("#companyId == authentication.tenantId or hasRole('ROLE_ADMIN')")
@Api(value = "api/companies/{companyId}/students", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@SuppressWarnings("unused")
public class StudentController {

  private final StudentService studentService;
  private final ModelMapper modelMapper;

  @Autowired
  public StudentController(StudentService studentService,
                           EncryptionService encryptionService,
                           ModelMapper modelMapper) {
    this.studentService = studentService;
    this.modelMapper = modelMapper;
  }

  @RequestMapping(method = RequestMethod.GET, path = "/{studentId}")
  @ApiOperation(value = "Students by given Company Id and Student Id.")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Success"),
      @ApiResponse(code = 401, message = "Unauthorized"),
      @ApiResponse(code = 403, message = "Forbidden"),
      @ApiResponse(code = 500, message = "Failure")})
  public StudentDTO getStudent(@ApiParam(value = "Company Id", required = true) @PathVariable("companyId") String companyId,
                               @ApiParam(value = "Student Id", required = true) @PathVariable("studentId") String studentId) {
    return studentService.getStudent(companyId, studentId)
        .map(student -> modelMapper.map(student, StudentDTO.class))
        .orElseGet(() -> null);
  }

  @RequestMapping(method = RequestMethod.POST, path = "/")
  @ApiOperation(value = "Create a new Student under the given Company.")
  @ApiResponses(value = {
      @ApiResponse(code = 201, message = "Success"),
      @ApiResponse(code = 401, message = "Unauthorized"),
      @ApiResponse(code = 403, message = "Forbidden"),
      @ApiResponse(code = 404, message = "Not Found"),
      @ApiResponse(code = 500, message = "Failure")})
  public ResponseEntity<?> addStudent(@ApiParam(value = "Company Id", required = true) @PathVariable("companyId") String companyId,
                                      @ApiParam(value = "New Student object", required = true) @RequestBody StudentDTO newStudent) {
    return studentService.createStudent(companyId, modelMapper.map(newStudent, Student.class))
        .map(student -> {
          HttpHeaders httpHeaders = new HttpHeaders();
          httpHeaders.setLocation(ServletUriComponentsBuilder
              .fromCurrentRequest()
              .path("/{id}")
              .buildAndExpand(student.getId())
              .toUri());
          return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
        }).orElseThrow(() -> new RuntimeException("Error precessing request"));
  }

  @RequestMapping(method = RequestMethod.GET)
  @ApiOperation(value = "Get a paged list of Students under the given Company.")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Success"),
      @ApiResponse(code = 401, message = "Unauthorized"),
      @ApiResponse(code = 403, message = "Forbidden"),
      @ApiResponse(code = 500, message = "Failure")})
  public List<StudentDTO> getStudents(@ApiParam(value = "Company Id", required = true) @PathVariable("companyId") String companyId,
                                      @RequestParam(value = "page", required = false) Integer page,
                                      @RequestParam(value = "pageSize", required = false) Integer pageSize) {
    return studentService.getStudents(companyId, PageableHelper.getPageable(page, pageSize))
        .stream()
        .map(student -> modelMapper.map(student, StudentDTO.class))
        .collect(Collectors.toList());
  }
}

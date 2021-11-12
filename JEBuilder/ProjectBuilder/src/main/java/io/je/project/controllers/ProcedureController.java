package io.je.project.controllers;

import io.je.classbuilder.models.MethodModel;
import io.je.project.exception.JEExceptionHandler;
import io.je.project.services.ClassService;
import io.je.project.services.ProjectService;
import io.je.utilities.beans.JEMethod;
import io.je.utilities.beans.JEResponse;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.exceptions.ClassLoadException;
import io.je.utilities.models.LibModel;
import io.je.utilities.models.WorkflowModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

import static io.je.utilities.constants.JEMessages.ADDED_WORKFLOW_SUCCESSFULLY;
import static io.je.utilities.constants.JEMessages.PROCEDURE_ADDED_SUCCESSFULLY;


/*
* Rest controller for procedures
* */
@RestController
@RequestMapping(value = "/procedure")
@CrossOrigin(maxAge = 3600)
public class ProcedureController {

    @Autowired
    ProjectService projectService;

    @Autowired
    ClassService classService;


    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addProcedure(@RequestBody MethodModel m) {
        try {
            //projectService.getProject(m.getProjectId());
            classService.addProcedure(m);
        } catch (ClassLoadException e) {
            if(!e.getCompilationErrorMessage().isEmpty()) {
                return ResponseEntity.ok(new JEResponse(e.getCode(), e.getCompilationErrorMessage()));
            }
            return JEExceptionHandler.handleException(e);
        }
        catch (Exception e) {
            return JEExceptionHandler.handleException(e);
        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, PROCEDURE_ADDED_SUCCESSFULLY));
    }

    @PatchMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateProcedure(@RequestBody MethodModel m) {
        try {
            //projectService.getProject(m.getProjectId());
            classService.updateProcedure(m);
        } catch (ClassLoadException e) {
            if(!e.getCompilationErrorMessage().isEmpty()) {
                return ResponseEntity.ok(new JEResponse(e.getCode(), e.getCompilationErrorMessage()));
            }
            return JEExceptionHandler.handleException(e);
        }
        catch (Exception e) {
            return JEExceptionHandler.handleException(e);
        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, PROCEDURE_ADDED_SUCCESSFULLY));
    }

    @GetMapping(value = "/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getMethodByName(@PathVariable("name") String methodName) {
        try {
            MethodModel m = classService.getMethodModel(methodName);
            return ResponseEntity.ok(m);
        }
        catch (Exception exception) {
            return JEExceptionHandler.handleException(exception);
        }
    }

    @DeleteMapping(value = "/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> deleteProcedure(@PathVariable("name") String name) {
        try {
            classService.removeProcedure(name);
        }
        catch (Exception exception) {
            return JEExceptionHandler.handleException(exception);
        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.PROCEDURE_DELETED_SUCCESSFULLY));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getMethods() {
        try {
            List<MethodModel> list = classService.getAllMethods();
            if( !list.isEmpty()) {
                return ResponseEntity.ok(list);
            }

        }
        catch (Exception exception) {
            return JEExceptionHandler.handleException(exception);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value ="/getLibraries", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getLibraries() {
        try {
            List<LibModel> list = classService.getAllLibs();
            if( !list.isEmpty()) {
                return ResponseEntity.ok(list);
            }

        }
        catch (Exception exception) {
            return JEExceptionHandler.handleException(exception);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value ="/getLibraryById/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getLibraryById(@PathVariable("id") String id) {
        try {
            LibModel model = classService.getLibraryById(id);
            if( model != null) {
                return ResponseEntity.ok(model);
            }

        }
        catch (Exception exception) {
            return JEExceptionHandler.handleException(exception);
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/upload")
    public ResponseEntity<?> uploadJar(@ModelAttribute LibModel libModel) {

        try {
            classService.addJarToProject(libModel);

        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);
        }

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.PROJECT_UPDATED));
    }

    @DeleteMapping(value = "deleteLibrary/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> deleteLibrary(@PathVariable("id") String id) {
        try {
            classService.removeLibrary(id);
        }
        catch (Exception exception) {
            return JEExceptionHandler.handleException(exception);
        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.LIBRARY_DELETED_SUCCESSFULLY));
    }
}

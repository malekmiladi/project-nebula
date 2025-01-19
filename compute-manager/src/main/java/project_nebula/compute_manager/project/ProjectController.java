package project_nebula.compute_manager.project;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project_nebula.compute_manager.project.dto.ProjectData;
import project_nebula.compute_manager.project.dto.ProjectTagMetadata;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectData> createProject(@RequestBody ProjectData projectData) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(projectData));
    }

    @PutMapping("/{userId}/{projectId}")
    public ResponseEntity<ProjectData> updateProject(
        @PathVariable UUID userId,
        @PathVariable UUID projectId,
        @RequestBody ProjectData projectData
    ) {
        try {
            return ResponseEntity.ok(projectService.updateProject(projectId, projectData));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(projectData);
        }
    }

    @DeleteMapping("/{userId}/{projectId}")
    public ResponseEntity<Void> deleteProject(
        @PathVariable UUID userId,
        @PathVariable UUID projectId
    ) {
        try {
            projectService.deleteProject(projectId);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<ProjectData>> getAllProjects(@PathVariable UUID userId) {
        return ResponseEntity.ok(projectService.getAllProjects(userId));
    }

    @PostMapping("/{userId}/{projectId}/tag")
    public ResponseEntity<ProjectTagMetadata> addTag(
        @PathVariable UUID userId,
        @PathVariable UUID projectId,
        @RequestBody ProjectTagMetadata tag
    ) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(projectService.addTag(projectId, tag));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(tag);
        }
    }

    @PutMapping("/{userId}/{projectId}/tag/{tagId}")
    public ResponseEntity<ProjectTagMetadata> updateTag(
        @PathVariable UUID userId,
        @PathVariable UUID projectId,
        @PathVariable UUID tagId,
        @RequestBody ProjectTagMetadata tag
    ) {
        try {
            return ResponseEntity.ok().body(projectService.updateTag(projectId, tagId, tag));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(tag);
        }
    }

    @DeleteMapping("/{userId}/{projectId}/tag/{tagId}")
    public ResponseEntity<UUID> deleteTag(
        @PathVariable UUID userId,
        @PathVariable UUID projectId,
        @PathVariable UUID tagId
    ) {
        try {
            projectService.deleteTag(projectId, tagId);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

}

package trabajo.tenant.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import trabajo.tenant.DTO.PlanDTO;
import trabajo.tenant.service.PlanService;

@RestController
@RequestMapping("/api/planes")
public class PlanController {
    
    @Autowired
    private PlanService planService;

    @GetMapping
    public ResponseEntity<List<PlanDTO>> getAllPlanes() {
        System.out.println("getAllPlanes started");
        List<PlanDTO> planes = planService.getAllPlanes();
        return ResponseEntity.ok(planes);
    }

    @GetMapping("/active")
    public ResponseEntity<List<PlanDTO>> getActivePlanes() {
        List<PlanDTO> planes = planService.getActivePlanes();
        return ResponseEntity.ok(planes);
    }

    @GetMapping("/active/order-by-price")
    public ResponseEntity<List<PlanDTO>> getActivePlanesOrderByPrice() {
        List<PlanDTO> planes = planService.getActivePlanesOrderByPrice();
        return ResponseEntity.ok(planes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanDTO> getPlanById(@PathVariable Integer id) {
        try {
            PlanDTO plan = planService.getPlanById(id);
            return ResponseEntity.ok(plan);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<PlanDTO> createPlan(@Valid @RequestBody PlanDTO planDTO) {
        try {
            PlanDTO createdPlan = planService.createPlan(planDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPlan);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlanDTO> updatePlan(
            @PathVariable Integer id, 
            @Valid @RequestBody PlanDTO planDTO) {
        try {
            PlanDTO updatedPlan = planService.updatePlan(id, planDTO);
            return ResponseEntity.ok(updatedPlan);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Integer id) {
        try {
            planService.deletePlan(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivatePlan(@PathVariable Integer id) {
        try {
            planService.deactivatePlan(id);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activatePlan(@PathVariable Integer id) {
        try {
            planService.activatePlan(id);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
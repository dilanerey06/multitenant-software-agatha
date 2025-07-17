package trabajo.tenant.controller;

import java.util.List;
import java.util.Optional;

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
import trabajo.tenant.DTO.EstadoDTO;
import trabajo.tenant.service.EstadoService;

@RestController
@RequestMapping("/api/estados")
public class EstadoController {

    @Autowired
    private EstadoService estadoService;

    @GetMapping
    public ResponseEntity<List<EstadoDTO>> getAllEstados() {
        System.out.println("getAllEstados started");
        List<EstadoDTO> estados = estadoService.findAll();

        return ResponseEntity.ok(estados);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstadoDTO> getEstadoById(@PathVariable Integer id) {
        Optional<EstadoDTO> estado = estadoService.findById(id);
        return estado.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<EstadoDTO> getEstadoByNombre(@PathVariable String nombre) {
        Optional<EstadoDTO> estado = estadoService.findByNombre(nombre);
        return estado.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EstadoDTO> createEstado(@Valid @RequestBody EstadoDTO estadoDTO) {
        try {
            EstadoDTO savedEstado = estadoService.save(estadoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedEstado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstadoDTO> updateEstado(
            @PathVariable Integer id, 
            @Valid @RequestBody EstadoDTO estadoDTO) {
        try {
            EstadoDTO updatedEstado = estadoService.update(id, estadoDTO);
            return ResponseEntity.ok(updatedEstado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEstado(@PathVariable Integer id) {
        try {
            estadoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existsById(@PathVariable Integer id) {
        boolean exists = estadoService.existsById(id);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/exists/nombre/{nombre}")
    public ResponseEntity<Boolean> existsByNombre(@PathVariable String nombre) {
        boolean exists = estadoService.existsByNombre(nombre);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        long count = estadoService.count();
        return ResponseEntity.ok(count);
    }
}
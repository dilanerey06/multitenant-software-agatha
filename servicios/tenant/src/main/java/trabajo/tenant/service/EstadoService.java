package trabajo.tenant.service;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import trabajo.tenant.DTO.EstadoDTO;
import trabajo.tenant.entity.Estado;
import trabajo.tenant.mapper.EstadoMapper;
import trabajo.tenant.repository.EstadoRepository;

@Service
@Transactional
public class EstadoService {

    @Autowired
    private EstadoRepository estadoRepository;

    @Autowired
    private EstadoMapper estadoMapper;

    @Transactional(readOnly = true)
    public List<EstadoDTO> findAll() {
        List<Estado> estados = estadoRepository.findAll();
        return estadoMapper.toEstadoDTOList(estados);
    }

    @Transactional(readOnly = true)
    public Optional<EstadoDTO> findById(Integer id) {
        return estadoRepository.findById(id)
                .map(estadoMapper::toEstadoDTO);
    }

    @Transactional(readOnly = true)
    public Optional<EstadoDTO> findByNombre(String nombre) {
        return estadoRepository.findByNombre(nombre)
                .map(estadoMapper::toEstadoDTO);
    }

    public EstadoDTO save(EstadoDTO estadoDTO) {
        Estado estado = estadoMapper.toEstado(estadoDTO);
        Estado savedEstado = estadoRepository.save(estado);
        return estadoMapper.toEstadoDTO(savedEstado);
    }

    public EstadoDTO update(Integer id, EstadoDTO estadoDTO) {
        Optional<Estado> existingEstado = estadoRepository.findById(id);
        
        if (existingEstado.isPresent()) {
            Estado estado = existingEstado.get();
            estado.setNombre(estadoDTO.getNombre());
            estado.setDescripcion(estadoDTO.getDescripcion());
            
            Estado updatedEstado = estadoRepository.save(estado);
            return estadoMapper.toEstadoDTO(updatedEstado);
        }
        
        throw new RuntimeException("Estado no encontrado con ID: " + id);
    }

    public void deleteById(Integer id) {
        if (!estadoRepository.existsById(id)) {
            throw new RuntimeException("Estado no encontrado con ID: " + id);
        }
        estadoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsById(Integer id) {
        return estadoRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsByNombre(String nombre) {
        return estadoRepository.existsByNombre(nombre);
    }

    @Transactional(readOnly = true)
    public long count() {
        return estadoRepository.count();
    }
}
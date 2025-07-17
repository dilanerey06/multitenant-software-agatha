package trabajo.tenant.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import trabajo.tenant.DTO.PlanDTO;
import trabajo.tenant.entity.Plan;
import trabajo.tenant.mapper.PlanMapper;
import trabajo.tenant.repository.PlanRepository;

@Service
@Transactional
public class PlanService {
    
    @Autowired
    private PlanRepository planRepository;
    
    @Autowired
    private PlanMapper planMapper;

    public List<PlanDTO> getAllPlanes() {
        return planMapper.toPlanDTOList(planRepository.findAll());
    }

    public List<PlanDTO> getActivePlanes() {
        return planMapper.toPlanDTOList(planRepository.findByActivoTrue());
    }

    public List<PlanDTO> getActivePlanesOrderByPrice() {
        return planMapper.toPlanDTOList(planRepository.findActivePlanesOrderByPrice());
    }

    public PlanDTO getPlanById(Integer id) {
        Plan plan = planRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("No se encontró el plan con ID: " + id));
        return planMapper.toPlanDTO(plan);
    }

    public PlanDTO createPlan(PlanDTO planDTO) {
        if (planRepository.existsByNombre(planDTO.getNombre())) {
            throw new IllegalArgumentException("Ya existe un plan con el nombre: " + planDTO.getNombre());
        }

        Plan plan = planMapper.toPlan(planDTO);
        Plan saved = planRepository.save(plan);
        return planMapper.toPlanDTO(saved);
    }

    public PlanDTO updatePlan(Integer id, PlanDTO planDTO) {
        Plan existing = planRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("No se encontró el plan con ID: " + id));

        if (!existing.getNombre().equals(planDTO.getNombre()) &&
            planRepository.existsByNombre(planDTO.getNombre())) {
            throw new IllegalArgumentException("Ya existe un plan con el nombre: " + planDTO.getNombre());
        }

        existing.setNombre(planDTO.getNombre());
        existing.setDescripcion(planDTO.getDescripcion());
        existing.setPrecioMensual(planDTO.getPrecioMensual());
        existing.setLimiteUsuarios(planDTO.getLimiteUsuarios());
        existing.setLimitePedidosMes(planDTO.getLimitePedidosMes());
        existing.setLimitePedidosSimultaneos(planDTO.getLimitePedidosSimultaneos());
        existing.setActivo(planDTO.getActivo());

        return planMapper.toPlanDTO(planRepository.save(existing));
    }

    public void deletePlan(Integer id) {
        Plan plan = planRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("No se encontró el plan con ID: " + id));

        if (!plan.getTenants().isEmpty()) {
            throw new IllegalStateException("No se puede eliminar el plan porque tiene tenants asociados.");
        }

        planRepository.deleteById(id);
    }

    public void deactivatePlan(Integer id) {
        Plan plan = planRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("No se encontró el plan con ID: " + id));

        plan.setActivo(false);
        planRepository.save(plan);
    }

    public void activatePlan(Integer id) {
        Plan plan = planRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("No se encontró el plan con ID: " + id));

        plan.setActivo(true);
        planRepository.save(plan);
    }
}

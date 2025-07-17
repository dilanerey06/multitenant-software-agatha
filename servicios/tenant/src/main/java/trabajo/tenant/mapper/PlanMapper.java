package trabajo.tenant.mapper;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import trabajo.tenant.DTO.PlanDTO;
import trabajo.tenant.entity.Plan;

@Component
public class PlanMapper {

    public PlanDTO toPlanDTO(Plan plan) {
        if (plan == null) {
            return null;
        }

        PlanDTO dto = new PlanDTO();
        dto.setId(plan.getId());
        dto.setNombre(plan.getNombre());
        dto.setDescripcion(plan.getDescripcion());
        dto.setPrecioMensual(plan.getPrecioMensual());
        dto.setLimiteUsuarios(plan.getLimiteUsuarios());
        dto.setLimitePedidosMes(plan.getLimitePedidosMes());
        dto.setLimitePedidosSimultaneos(plan.getLimitePedidosSimultaneos());
        dto.setActivo(plan.getActivo());
        dto.setFechaCreacion(plan.getFechaCreacion());

        return dto;
    }

    public Plan toPlan(PlanDTO dto) {
        if (dto == null) {
            return null;
        }

        Plan plan = new Plan();
        plan.setId(dto.getId());
        plan.setNombre(dto.getNombre());
        plan.setDescripcion(dto.getDescripcion());
        plan.setPrecioMensual(dto.getPrecioMensual());
        plan.setLimiteUsuarios(dto.getLimiteUsuarios());
        plan.setLimitePedidosMes(dto.getLimitePedidosMes());
        plan.setLimitePedidosSimultaneos(dto.getLimitePedidosSimultaneos());
        plan.setActivo(dto.getActivo());
        plan.setFechaCreacion(dto.getFechaCreacion());

        return plan;
    }

    public List<PlanDTO> toPlanDTOList(List<Plan> planes) {
        if (planes == null) {
            return null;
        }

        return planes.stream()
                .map(this::toPlanDTO)
                .collect(Collectors.toList());
    }

    public List<Plan> toPlanList(List<PlanDTO> planesDTO) {
        if (planesDTO == null) {
            return null;
        }

        return planesDTO.stream()
                .map(this::toPlan)
                .collect(Collectors.toList());
    }
}
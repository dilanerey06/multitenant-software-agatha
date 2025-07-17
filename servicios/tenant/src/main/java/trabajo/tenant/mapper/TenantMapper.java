package trabajo.tenant.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import trabajo.tenant.DTO.TenantCreateDTO;
import trabajo.tenant.DTO.TenantDTO;
import trabajo.tenant.DTO.TenantInfoDTO;
import trabajo.tenant.DTO.TenantUpdateDTO;
import trabajo.tenant.entity.Estado;
import trabajo.tenant.entity.Plan;
import trabajo.tenant.entity.Tenant;
import trabajo.tenant.entity.TenantInfo;

@Component
public class TenantMapper {
    
    public TenantDTO toTenantDTO(Tenant tenant) {
        if (tenant == null) {
            return null;
        }
        
        TenantDTO dto = new TenantDTO();
        dto.setId(tenant.getId());
        dto.setNombreEmpresa(tenant.getNombreEmpresa());
        dto.setEmailContacto(tenant.getEmailContacto());
        dto.setIdAdminMensajeria(tenant.getIdAdminMensajeria());
        dto.setFechaCreacion(tenant.getFechaCreacion());
        dto.setFechaUltimaConexion(tenant.getFechaUltimaConexion());
        
        if (tenant.getEstado() != null) {
            dto.setEstadoId(tenant.getEstado().getId());
            dto.setEstadoNombre(tenant.getEstado().getNombre());
        }
        
        if (tenant.getPlan() != null) {
            dto.setPlanId(tenant.getPlan().getId());
            dto.setPlanNombre(tenant.getPlan().getNombre());
        }
        
        return dto;
    }
    
    public List<TenantDTO> toTenantDTOList(List<Tenant> tenants) {
        if (tenants == null) {
            return null;
        }
        
        return tenants.stream()
                .map(this::toTenantDTO)
                .collect(Collectors.toList());
    }
    
    public Tenant toTenantFromCreate(TenantCreateDTO createDTO) {
        if (createDTO == null) {
            return null;
        }
        
        Tenant tenant = new Tenant();
        tenant.setNombreEmpresa(createDTO.getNombreEmpresa());
        tenant.setEmailContacto(createDTO.getEmailContacto());
        
        if (createDTO.getPlanId() != null) {
            Plan plan = new Plan();
            plan.setId(createDTO.getPlanId());
            tenant.setPlan(plan);
        }
        
        return tenant;
    }
    
    public void updateTenantFromDTO(Tenant tenant, TenantUpdateDTO updateDTO) {
        if (tenant == null || updateDTO == null) {
            return;
        }
        
        tenant.setNombreEmpresa(updateDTO.getNombreEmpresa());
        tenant.setEmailContacto(updateDTO.getEmailContacto());
        tenant.setIdAdminMensajeria(updateDTO.getIdAdminMensajeria());
        
        if (updateDTO.getEstadoId() != null) {
            Estado estado = new Estado();
            estado.setId(updateDTO.getEstadoId());
            tenant.setEstado(estado);
        }
        
        if (updateDTO.getPlanId() != null) {
            Plan plan = new Plan();
            plan.setId(updateDTO.getPlanId());
            tenant.setPlan(plan);
        }
    }
    
    public Tenant toTenantFromUpdate(TenantUpdateDTO updateDTO) {
        if (updateDTO == null) {
            return null;
        }
        
        Tenant tenant = new Tenant();
        tenant.setNombreEmpresa(updateDTO.getNombreEmpresa());
        tenant.setEmailContacto(updateDTO.getEmailContacto());
        tenant.setIdAdminMensajeria(updateDTO.getIdAdminMensajeria());
        
        if (updateDTO.getEstadoId() != null) {
            Estado estado = new Estado();
            estado.setId(updateDTO.getEstadoId());
            tenant.setEstado(estado);
        }
        
        if (updateDTO.getPlanId() != null) {
            Plan plan = new Plan();
            plan.setId(updateDTO.getPlanId());
            tenant.setPlan(plan);
        }
        
        return tenant;
    }
    
    public TenantInfoDTO toTenantInfoDTO(TenantInfo tenantInfo) {
        if (tenantInfo == null) {
            return null;
        }
        
        TenantInfoDTO dto = new TenantInfoDTO();
        dto.setId(tenantInfo.getId());
        dto.setNombreEmpresa(tenantInfo.getNombreEmpresa());
        dto.setEmailContacto(tenantInfo.getEmailContacto());
        dto.setIdAdminMensajeria(tenantInfo.getIdAdminMensajeria());
        dto.setEstado(tenantInfo.getEstado());
        dto.setEstadoDescripcion(tenantInfo.getEstadoDescripcion());
        dto.setPlan(tenantInfo.getPlan());
        dto.setPlanDescripcion(tenantInfo.getPlanDescripcion());
        dto.setPrecioMensual(tenantInfo.getPrecioMensual());
        dto.setLimiteUsuarios(tenantInfo.getLimiteUsuarios());
        dto.setLimitePedidosMes(tenantInfo.getLimitePedidosMes());
        dto.setLimitePedidosSimultaneos(tenantInfo.getLimitePedidosSimultaneos());
        dto.setFechaCreacion(tenantInfo.getFechaCreacion());
        dto.setFechaUltimaConexion(tenantInfo.getFechaUltimaConexion());
        dto.setUltimaConexionTexto(tenantInfo.getUltimaConexionTexto());
        
        return dto;
    }
    
    public List<TenantInfoDTO> toTenantInfoDTOList(List<TenantInfo> tenantInfos) {
        if (tenantInfos == null) {
            return null;
        }
        
        return tenantInfos.stream()
                .map(this::toTenantInfoDTO)
                .collect(Collectors.toList());
    }
}
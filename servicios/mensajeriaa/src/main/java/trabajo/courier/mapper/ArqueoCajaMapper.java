package trabajo.courier.mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import trabajo.courier.DTO.ArqueoCajaDTO;
import trabajo.courier.entity.ArqueoCaja;
import trabajo.courier.entity.EmpresaMensajeria;
import trabajo.courier.entity.EstadoArqueo;
import trabajo.courier.entity.TipoTurno;
import trabajo.courier.entity.Usuario;
import trabajo.courier.request.CrearArqueoRequest;

@Component
public class ArqueoCajaMapper {

    @Autowired
    private IngresoArqueoMapper ingresoArqueoMapper;

    public ArqueoCajaDTO toDTO(ArqueoCaja entity) {
        if (entity == null) return null;

        ArqueoCajaDTO DTO = new ArqueoCajaDTO();
        DTO.setId(entity.getId());
        DTO.setTenantId(entity.getTenantId());
        DTO.setFecha(entity.getFecha());
        DTO.setEfectivoInicio(entity.getEfectivoInicio());
        DTO.setTotalIngresos(entity.getTotalIngresos());
        DTO.setEgresos(entity.getEgresos());
        DTO.setEfectivoReal(entity.getEfectivoReal());
        DTO.setDiferencia(entity.getDiferencia());
        DTO.setObservaciones(entity.getObservaciones());
        DTO.setFechaCreacion(entity.getFechaCreacion());

        if (entity.getMensajeria() != null) {
            DTO.setMensajeriaId(entity.getMensajeria().getId());
        }

        if (entity.getUsuario() != null) {
            DTO.setUsuarioId(entity.getUsuario().getId());
            DTO.setUsuarioNombre(entity.getUsuario().getNombres() + " " + entity.getUsuario().getApellidos());
        }

        if (entity.getTurno() != null) {
            DTO.setTurnoId(entity.getTurno().getId());
            DTO.setTurnoNombre(entity.getTurno().getNombre());
        }

        if (entity.getEstado() != null) {
            DTO.setEstadoId(entity.getEstado().getId());
            DTO.setEstadoNombre(entity.getEstado().getNombre());
        }

        if (entity.getIngresos() != null) {
            DTO.setIngresos(ingresoArqueoMapper.toDTOList(entity.getIngresos()));
        }

        return DTO;
    }

    public ArqueoCaja toEntity(ArqueoCajaDTO DTO) {
        if (DTO == null) return null;

        ArqueoCaja entity = new ArqueoCaja();
        entity.setId(DTO.getId());
        entity.setTenantId(DTO.getTenantId());
        entity.setFecha(DTO.getFecha());
        entity.setEfectivoInicio(DTO.getEfectivoInicio());
        entity.setTotalIngresos(DTO.getTotalIngresos());
        entity.setEgresos(DTO.getEgresos());
        entity.setEfectivoReal(DTO.getEfectivoReal());
        entity.setDiferencia(DTO.getDiferencia());
        entity.setObservaciones(DTO.getObservaciones());
        entity.setFechaCreacion(DTO.getFechaCreacion());

        if (DTO.getMensajeriaId() != null) {
            EmpresaMensajeria mensajeria = new EmpresaMensajeria();
            mensajeria.setId(DTO.getMensajeriaId());
            entity.setMensajeria(mensajeria);
        }

        if (DTO.getUsuarioId() != null) {
            Usuario usuario = new Usuario();
            usuario.setId(DTO.getUsuarioId());
            entity.setUsuario(usuario);
        }

        if (DTO.getTurnoId() != null) {
            TipoTurno turno = new TipoTurno();
            turno.setId(DTO.getTurnoId());
            entity.setTurno(turno);
        }

        if (DTO.getEstadoId() != null) {
            EstadoArqueo estado = new EstadoArqueo();
            estado.setId(DTO.getEstadoId());
            entity.setEstado(estado);
        }

        return entity;
    }

    public ArqueoCaja fromCrearRequest(CrearArqueoRequest request) {
        if (request == null) return null;

        ArqueoCaja entity = new ArqueoCaja();
        entity.setFecha(LocalDate.now());
        entity.setEfectivoInicio(request.getEfectivoInicio());
        entity.setObservaciones(request.getObservaciones());

        if (request.getTurnoId() != null) {
            TipoTurno turno = new TipoTurno();
            turno.setId(request.getTurnoId());
            entity.setTurno(turno);
        }

        return entity;
    }

    public List<ArqueoCajaDTO> toDTOList(List<ArqueoCaja> entities) {
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }
}

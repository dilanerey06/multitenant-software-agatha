package trabajo.courier.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import trabajo.courier.DTO.TarifaDTO;
import trabajo.courier.entity.Tarifa;
import trabajo.courier.mapper.TarifaMapper;
import trabajo.courier.repository.TarifaRepository;

@Service
public class TarifaService {

    private final TarifaRepository tarifaRepository;
    private final TarifaMapper tarifaMapper;

    public TarifaService(TarifaRepository tarifaRepository, TarifaMapper tarifaMapper) {
        this.tarifaRepository = tarifaRepository;
        this.tarifaMapper = tarifaMapper;
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<TarifaDTO> obtenerTodas(Long tenantId, Long mensajeriaId) {
        List<Tarifa> tarifas = tarifaRepository.findByTenantIdAndMensajeriaId(tenantId, mensajeriaId);
        return tarifaMapper.toDTO(tarifas);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<TarifaDTO> obtenerActivas(Long tenantId, Long mensajeriaId) {
        List<Tarifa> tarifas = tarifaRepository.findByTenantIdAndMensajeriaIdAndActivaTrue(tenantId, mensajeriaId);
        return tarifaMapper.toDTO(tarifas);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public TarifaDTO obtenerPorId(Long id, Long tenantId) {
        Tarifa tarifa = tarifaRepository.findByTenantIdAndId(tenantId, id)
            .orElseThrow(() -> new RuntimeException("Tarifa no encontrada"));
        return tarifaMapper.toDTO(tarifa);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public TarifaDTO obtenerTarifaPorDefecto(Long tenantId, Long mensajeriaId) {
        Tarifa tarifa = tarifaRepository.findFirstByTenantIdAndMensajeriaIdAndActivaTrueOrderByFechaCreacionDesc(
            tenantId, mensajeriaId)
            .orElseThrow(() -> new RuntimeException("No hay tarifas activas configuradas"));
        return tarifaMapper.toDTO(tarifa);
    }

    @Transactional
    public TarifaDTO crear(TarifaDTO tarifaDTO) {
        Tarifa tarifa = tarifaMapper.toEntity(tarifaDTO);
        tarifa = tarifaRepository.save(tarifa);
        return tarifaMapper.toDTO(tarifa);
    }

    @Transactional
    public TarifaDTO actualizar(Long id, TarifaDTO tarifaDTO) {
        Tarifa tarifa = tarifaRepository.findByTenantIdAndId(tarifaDTO.getTenantId(), id)
            .orElseThrow(() -> new RuntimeException("Tarifa no encontrada"));

        tarifaMapper.updateEntity(tarifaDTO, tarifa);
        tarifa = tarifaRepository.save(tarifa);
        return tarifaMapper.toDTO(tarifa);
    }

    @Transactional
    public void activarDesactivar(Long id, Long tenantId, boolean activa) {
        Tarifa tarifa = tarifaRepository.findByTenantIdAndId(tenantId, id)
            .orElseThrow(() -> new RuntimeException("Tarifa no encontrada"));

        tarifa.setActiva(activa);
        tarifaRepository.save(tarifa);
    }

    @Transactional
    public void eliminar(Long id, Long tenantId) {
        Tarifa tarifa = tarifaRepository.findByTenantIdAndId(tenantId, id)
            .orElseThrow(() -> new RuntimeException("Tarifa no encontrada"));

        // Si está siendo usada, solo desactivar
        if (tarifaRepository.existsPedidosWithTarifa(tarifa.getId())) {
            tarifa.setActiva(false);
            tarifaRepository.save(tarifa);
        } else {
            tarifaRepository.delete(tarifa);
        }
    }
}

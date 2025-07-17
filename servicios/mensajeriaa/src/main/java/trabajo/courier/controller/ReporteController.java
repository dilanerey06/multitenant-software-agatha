package trabajo.courier.controller;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import trabajo.courier.DTO.ArqueoCajaDTO;
import trabajo.courier.request.ReporteRequest;
import trabajo.courier.service.ReporteService;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @PostMapping("/arqueos")
    @SuppressWarnings("CallToPrintStackTrace")
    public ResponseEntity<Resource> generarReporteArqueos(
            @RequestBody ReporteRequest request,
            @RequestParam Long tenantId,
            @RequestParam Long mensajeriaId) {
        
        try {
            if (request.getFechaDesde().isAfter(request.getFechaHasta())) {
                return ResponseEntity.badRequest().build();
            }

            ByteArrayOutputStream outputStream;
            String contentType;
            String filename;

            if ("pdf".equalsIgnoreCase(request.getFormato())) {
                outputStream = reporteService.generarReportePDF(request, tenantId, mensajeriaId);
                contentType = "application/pdf";
                filename = String.format("reporte_arqueos_%s_%s.pdf", 
                    request.getFechaDesde(), request.getFechaHasta());
            } else {
                outputStream = reporteService.generarReporteExcel(request, tenantId, mensajeriaId);
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                filename = String.format("reporte_arqueos_%s_%s.xlsx", 
                    request.getFechaDesde(), request.getFechaHasta());
            }

            ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    
    @SuppressWarnings("CallToPrintStackTrace")
    @GetMapping("/arqueos/rango")
    public ResponseEntity<List<ArqueoCajaDTO>> obtenerArqueosRango(
            @RequestParam Long tenantId,
            @RequestParam Long mensajeriaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        
        try {
            List<ArqueoCajaDTO> arqueos = reporteService.obtenerArqueosEnRango(tenantId, mensajeriaId, desde, hasta);
            return ResponseEntity.ok(arqueos);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}

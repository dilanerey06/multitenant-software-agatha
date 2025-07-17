package trabajo.courier.service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import trabajo.courier.DTO.ArqueoCajaDTO;
import trabajo.courier.DTO.IngresoArqueoDTO;
import trabajo.courier.entity.ArqueoCaja;
import trabajo.courier.entity.IngresoArqueo;
import trabajo.courier.repository.ArqueoCajaRepository;
import trabajo.courier.repository.IngresoArqueoRepository;
import trabajo.courier.request.ReporteRequest;

@Service
public class ReporteService {

    @Autowired
    private ArqueoCajaRepository arqueoRepository;

    @Autowired
    private IngresoArqueoRepository ingresoRepository;

    public ByteArrayOutputStream generarReportePDF(ReporteRequest request, Long tenantId, Long mensajeriaId) throws Exception {
        List<ArqueoCaja> arqueos = arqueoRepository.findByTenantIdAndMensajeriaIdAndFechaBetween(tenantId, mensajeriaId, request.getFechaDesde(), request.getFechaHasta());

        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 10);

        Paragraph title = new Paragraph("Reporte de Arqueos", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph period = new Paragraph(
                String.format("Período: %s al %s", request.getFechaDesde(), request.getFechaHasta()),
                normalFont);
        period.setAlignment(Element.ALIGN_CENTER);
        document.add(period);
        document.add(new Paragraph(" "));

        BigDecimal totalEfectivo = arqueos.stream()
                .map(a -> a.getEfectivoReal() != null ? a.getEfectivoReal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalIngresos = arqueos.stream()
                .map(a -> a.getTotalIngresos() != null ? a.getTotalIngresos() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalEgresos = arqueos.stream()
                .map(a -> a.getEgresos() != null ? a.getEgresos() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalDiferencia = arqueos.stream()
                .map(a -> a.getDiferencia() != null ? a.getDiferencia() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        document.add(new Paragraph("Resumen General", headerFont));
        document.add(new Paragraph("Total Efectivo: $" + totalEfectivo, normalFont));
        document.add(new Paragraph("Total Ingresos: $" + totalIngresos, normalFont));
        document.add(new Paragraph("Total Egresos: $" + totalEgresos, normalFont));
        document.add(new Paragraph("Diferencia Total: $" + totalDiferencia, normalFont));
        document.add(new Paragraph(" "));

        // Tabla principal con columna de egresos
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{15, 20, 15, 20, 15, 15});

        String[] headers = {"Fecha", "Total Efectivo", "Total Ingresos", "Total Egresos", "Diferencia", "Estado"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }

        for (ArqueoCaja arqueo : arqueos) {
            table.addCell(new Phrase(arqueo.getFecha().toString(), normalFont));
            table.addCell(new Phrase("$" + (arqueo.getEfectivoReal() != null ? arqueo.getEfectivoReal() : BigDecimal.ZERO), normalFont));
            table.addCell(new Phrase("$" + (arqueo.getTotalIngresos() != null ? arqueo.getTotalIngresos() : BigDecimal.ZERO), normalFont));
            table.addCell(new Phrase("$" + (arqueo.getEgresos() != null ? arqueo.getEgresos() : BigDecimal.ZERO), normalFont));
            table.addCell(new Phrase("$" + (arqueo.getDiferencia() != null ? arqueo.getDiferencia() : BigDecimal.ZERO), normalFont));
            table.addCell(new Phrase(arqueo.getEstado().getNombre(), normalFont));
        }

        document.add(table);

        if (request.isIncluirDetalles()) {
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Detalle de Ingresos", headerFont));

            for (ArqueoCaja arqueo : arqueos) {
                List<IngresoArqueo> ingresos = ingresoRepository.findByArqueoIdOrderByFechaCreacionAsc(arqueo.getId());
                if (!ingresos.isEmpty()) {
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph("Arqueo del " + arqueo.getFecha(), headerFont));

                    PdfPTable ingresoTable = new PdfPTable(4);
                    ingresoTable.setWidthPercentage(100);
                    ingresoTable.setWidths(new float[]{40, 20, 20, 20});

                    String[] ingresoHeaders = {"Descripción", "Monto", "Tipo", "Hora"};
                    for (String h : ingresoHeaders) {
                        PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        ingresoTable.addCell(cell);
                    }

                    for (IngresoArqueo ingreso : ingresos) {
                        ingresoTable.addCell(new Phrase(ingreso.getDescripcion(), normalFont));
                        ingresoTable.addCell(new Phrase("$" + ingreso.getMonto(), normalFont));
                        ingresoTable.addCell(new Phrase(ingreso.getTipoIngreso().getNombre(), normalFont));
                        ingresoTable.addCell(new Phrase(ingreso.getFechaCreacion().toString(), normalFont));
                    }
                    document.add(ingresoTable);
                }
            }
        }

        document.close();
        return outputStream;
    }

    public ByteArrayOutputStream generarReporteExcel(ReporteRequest request, Long tenantId, Long mensajeriaId) throws Exception {
        List<ArqueoCaja> arqueos = arqueoRepository.findByTenantIdAndMensajeriaIdAndFechaBetween(
                tenantId, mensajeriaId, request.getFechaDesde(), request.getFechaHasta());

        ByteArrayOutputStream outputStream;
        try (Workbook workbook = new XSSFWorkbook()) {
            // Estilo para encabezados
            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Hoja de resumen con columna de egresos
            Sheet sheetResumen = workbook.createSheet("Resumen Arqueos");
            Row headerRow = sheetResumen.createRow(0);
            String[] resumenHeaders = {"Fecha", "Total Efectivo", "Total Ingresos", "Total Egresos", "Diferencia", "Estado"};
            for (int i = 0; i < resumenHeaders.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(resumenHeaders[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (ArqueoCaja arqueo : arqueos) {
                Row row = sheetResumen.createRow(rowNum++);
                row.createCell(0).setCellValue(arqueo.getFecha().toString());
                row.createCell(1).setCellValue((arqueo.getEfectivoReal() != null ? arqueo.getEfectivoReal() : BigDecimal.ZERO).doubleValue());
                row.createCell(2).setCellValue((arqueo.getTotalIngresos() != null ? arqueo.getTotalIngresos() : BigDecimal.ZERO).doubleValue());
                row.createCell(3).setCellValue((arqueo.getEgresos() != null ? arqueo.getEgresos() : BigDecimal.ZERO).doubleValue());
                row.createCell(4).setCellValue((arqueo.getDiferencia() != null ? arqueo.getDiferencia() : BigDecimal.ZERO).doubleValue());
                row.createCell(5).setCellValue(arqueo.getEstado().getNombre());
            }

            for (int i = 0; i < resumenHeaders.length; i++) {
                sheetResumen.autoSizeColumn(i);
            }

            // Detalle de ingresos si se solicita
            if (request.isIncluirDetalles()) {
                Sheet sheetIngresos = workbook.createSheet("Detalle Ingresos");
                Row headerRowIngresos = sheetIngresos.createRow(0);
                String[] ingresoHeaders = {"Fecha Arqueo", "Descripción", "Monto", "Tipo", "Hora"};
                for (int i = 0; i < ingresoHeaders.length; i++) {
                    Cell cell = headerRowIngresos.createCell(i);
                    cell.setCellValue(ingresoHeaders[i]);
                    cell.setCellStyle(headerStyle);
                }

                int rowNumIngresos = 1;
                for (ArqueoCaja arqueo : arqueos) {
                    List<IngresoArqueo> ingresos = ingresoRepository.findByArqueoIdOrderByFechaCreacionAsc(arqueo.getId());
                    for (IngresoArqueo ingreso : ingresos) {
                        Row row = sheetIngresos.createRow(rowNumIngresos++);
                        row.createCell(0).setCellValue(arqueo.getFecha().toString());
                        row.createCell(1).setCellValue(ingreso.getDescripcion());
                        row.createCell(2).setCellValue(ingreso.getMonto().doubleValue());
                        row.createCell(3).setCellValue(ingreso.getTipoIngreso().getNombre());
                        row.createCell(4).setCellValue(ingreso.getFechaCreacion().toString());
                    }
                }

                for (int i = 0; i < ingresoHeaders.length; i++) {
                    sheetIngresos.autoSizeColumn(i);
                }
            }

            outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
        }

        return outputStream;
    }

    public List<ArqueoCajaDTO> obtenerArqueosEnRango(Long tenantId, Long mensajeriaId, LocalDate desde, LocalDate hasta) {
        List<ArqueoCaja> arqueos = arqueoRepository.findByTenantIdAndMensajeriaIdAndFechaBetween(tenantId, mensajeriaId, desde, hasta);
        return arqueos.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    private ArqueoCajaDTO convertirADto(ArqueoCaja arqueo) {
        ArqueoCajaDTO dto = new ArqueoCajaDTO();
        dto.setId(arqueo.getId());
        dto.setFecha(arqueo.getFecha());
        dto.setTotalIngresos(arqueo.getTotalIngresos());
        dto.setEgresos(arqueo.getEgresos()); // Agregado el campo egresos
        dto.setEfectivoReal(arqueo.getEfectivoReal());
        dto.setDiferencia(arqueo.getDiferencia());
        dto.setEstadoId(arqueo.getEstado().getId());
        dto.setEstadoNombre(arqueo.getEstado().getNombre());
        dto.setIngresos(ingresoRepository.findByArqueoIdOrderByFechaCreacionAsc(arqueo.getId())
                .stream()
                .map(this::convertirIngresoADto)
                .collect(Collectors.toList()));
        return dto;
    }

    private IngresoArqueoDTO convertirIngresoADto(IngresoArqueo ingreso) {
        IngresoArqueoDTO dto = new IngresoArqueoDTO();
        dto.setId(ingreso.getId());
        dto.setArqueoId(ingreso.getArqueo().getId());
        dto.setTipoIngresoId(ingreso.getTipoIngreso().getId());
        dto.setTipoIngresoNombre(ingreso.getTipoIngreso().getNombre());
        if (ingreso.getPedido() != null) {
            dto.setPedidoId(ingreso.getPedido().getId());
        }
        dto.setMonto(ingreso.getMonto());
        dto.setDescripcion(ingreso.getDescripcion());
        dto.setFechaCreacion(ingreso.getFechaCreacion());
        return dto;
    }
}
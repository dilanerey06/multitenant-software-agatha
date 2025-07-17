package trabajo.courier.mapper;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Component;

@Component
public class MapperUtils {
    
    public static String formatearNombreCompleto(String nombres, String apellidos) {
        if (nombres == null && apellidos == null) return "";
        if (nombres == null) return apellidos;
        if (apellidos == null) return nombres;
        return nombres + " " + apellidos;
    }
    
    public static String formatearTelefono(String telefono) {
        if (telefono == null || telefono.length() < 10) return telefono;
        
        return String.format("(%s) %s-%s", 
            telefono.substring(0, 3),
            telefono.substring(3, 6),
            telefono.substring(6));
    }
    
    public static String formatearDireccionCompleta(String ciudad, String barrio, String direccion) {
        StringBuilder sb = new StringBuilder();
        
        if (direccion != null) sb.append(direccion);
        if (barrio != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(barrio);
        }
        if (ciudad != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(ciudad);
        }
        
        return sb.toString();
    }
    
    public static String generarCodigoPedido(Long id) {
        return "PED-" + String.format("%06d", id);
    }
    
    public static String generarCodigoArqueo(Long id, LocalDate fecha) {
        return "ARQ-" + fecha.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + 
               String.format("%03d", id);
    }
    
    public <T> T convertToDTO(Object source, Class<T> targetClass) {
        if (source == null) return null;
        
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException | BeansException e) {
            throw new RuntimeException("Error al convertir a DTO: " + e.getMessage(), e);
        }
    }
    
    public <T> T convertToEntity(Object source, Class<T> targetClass) {
        if (source == null) return null;
        
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException | BeansException e) {
            throw new RuntimeException("Error al convertir a entidad: " + e.getMessage(), e);
        }
    }
}
package trabajo.courier.service;

import org.springframework.stereotype.Service;

// Se espera a largo plazo implementar realmente el envio a correos
@Service
public class EmailService {

    public void enviarCredenciales(String destinatario, String usuario, String password) {
        System.out.println("===== Simulación de envío de correo =====");
        System.out.println("Para: " + destinatario);
        System.out.println("Asunto: Credenciales de acceso");
        System.out.println("Mensaje:");
        System.out.println("Hola " + usuario + ",\n\nTu contraseña temporal es: " + password +
                           "\n\nPor favor cámbiala después de ingresar al sistema.\n\nSaludos.");
    }
}

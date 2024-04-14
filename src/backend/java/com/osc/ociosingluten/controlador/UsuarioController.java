package com.osc.ociosingluten.controlador;

import com.osc.ociosingluten.controlador.DTO.UsuarioDTO;
import com.osc.ociosingluten.excepciones.UsuarioExisteException;
import com.osc.ociosingluten.modelo.Usuario;
import com.osc.ociosingluten.repositorio.UsuarioRepository;
import org.apache.coyote.Response;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.zip.DeflaterOutputStream;

@RestController
@RequestMapping("/ociosingluten")
@CrossOrigin("http://localhost:3000")
public class UsuarioController {

    @Autowired
    private UsuarioRepository repoUsu;

    //Obtener todos los usuarios que se encuentran registrados en la web, esto solo lo podría ver un usuario admin
    @GetMapping("/usuarios")
    public List<Usuario> cargarTodosUsuarios(){
        return repoUsu.findAll();
    }

    //Añadir el usuario
    @PostMapping("/usuarios")
    public void anadirUsuario(@RequestParam("dni") String dni,
                              @RequestParam("username") String username,
                              @RequestParam("nombre") String nombre,
                              @RequestParam("apellidos") String apellidos,
                              @RequestParam("fechaNacimiento") LocalDate fechaNacimiento,
                              @RequestParam("telefono") int telefono,
                              @RequestParam("fotoPerfil") byte[] fotoPerfil,
                              @RequestParam("email") String email,
                              @RequestParam("password") String password) throws IOException {
        Usuario usuario = new Usuario(dni, username, nombre, apellidos, fechaNacimiento, telefono, compressImage(fotoPerfil), email, password);
        repoUsu.save(usuario);
    }



    public static byte[] compressImage(byte[] input) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(input);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(outputStream)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                deflaterOutputStream.write(buffer, 0, bytesRead);
            }
            deflaterOutputStream.finish();
            return outputStream.toByteArray();
        }
    }
}
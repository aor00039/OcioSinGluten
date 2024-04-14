import com.osc.ociosingluten.modelo.Usuario;
import com.osc.ociosingluten.repositorio.UsuarioRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(classes=com.osc.ociosingluten.app.OcioSinGlutenApplication.class)
public class UsuarioRepositoryTest {

    @Autowired
    UsuarioRepository repositorio;

    @Test
    public void estOperacionesUsuario(){
        String email = "aor00039@red.ujaen.es";
        LocalDate fecha = LocalDate.of(2002, 10, 24);
        byte[] foto = null;
        Usuario usuario = new Usuario("78162640S", "aor00039", "Alvaro", "Ordoñez Romero", fecha, 670988953, foto, email, "holisimaF13");

        repositorio.save(usuario);

        Optional<Usuario> usuarioGuardado = repositorio.findByEmail(email);
        Optional<Usuario> usuarios = repositorio.findByUsername("aor00039");

        Assert.assertNotNull(usuarioGuardado);
        Assert.assertEquals("Alvaro",usuarioGuardado.get().getNombre());
        Assert.assertEquals("78162640S",usuarioGuardado.get().getDni());
        Assert.assertEquals("78162640S",usuarios.get().getDni());


        String dni = usuario.getDni();
        repositorio.removeUsuarioByDni(dni);

        Optional<Usuario> usuarioBorrado = repositorio.findByEmail(email);
        Assert.assertTrue(usuarioBorrado.isEmpty());

    }
}
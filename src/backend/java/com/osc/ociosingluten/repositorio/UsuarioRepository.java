package com.osc.ociosingluten.repositorio;

import com.osc.ociosingluten.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    @Transactional
    Optional<Usuario> findByEmail(String email);

    @Transactional
    Optional<Usuario> findByUsername(String username);

    @Transactional
    Optional<Usuario> findByDni(String dni);

    @Transactional
    Usuario save(Usuario usuario);

    @Transactional
    void removeUsuarioByDni(String dni);

    @Transactional
    default Usuario actualizarUsuario(Usuario usuario) {
        return save(usuario);
    }
}
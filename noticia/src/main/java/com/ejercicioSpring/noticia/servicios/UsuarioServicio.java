package com.ejercicioSpring.noticia.servicios;

import com.ejercicioSpring.noticia.entidades.Noticia;
import com.ejercicioSpring.noticia.entidades.Usuario;
import com.ejercicioSpring.noticia.excepciones.MiException;
import com.ejercicioSpring.noticia.repositorios.UsuarioRepositorio;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class UsuarioServicio implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private NoticiaServicio noticiaServicio;
    
    @Transactional
    public void registrar(String nombre, String email, String rol, String password, String password2) throws MiException {

        validar(nombre, email, password, password2);

        Usuario usuario = new Usuario();

        usuario.setNombre(nombre);
        usuario.setEmail(email);
        if (rol.equalsIgnoreCase("1")) {
            usuario.setRol("USER");
        }
        if (rol.equalsIgnoreCase("2")) {
            usuario.setRol("PER");
        }
        if (rol.equalsIgnoreCase("3")) {
            usuario.setRol("ADMIN");
        }

        usuario.setPassword(new BCryptPasswordEncoder().encode(password));
        usuario.setAlta(new Date());

        usuarioRepositorio.save(usuario);
        System.out.println("El usuario es " + usuario.getRol());
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepositorio.buscarPorEmail(email);

        if (usuario != null) {

            List<GrantedAuthority> permisos = new ArrayList();

            GrantedAuthority p = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toString());

            permisos.add(p);

            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

            HttpSession session = attr.getRequest().getSession(true);

            session.setAttribute("usuariosession", usuario);

            return new User(usuario.getEmail(), usuario.getPassword(), permisos);

        } else {

            return null;
        }

    }

    public Usuario buscarUsuario(Long id) {

        return usuarioRepositorio.getById(id);

    }

    public ArrayList<Usuario> buscarUsuarios() {

        ArrayList<Usuario> listaUsuarios = new ArrayList();

        listaUsuarios = (ArrayList<Usuario>) usuarioRepositorio.findAll();

        return listaUsuarios;

    }
    
    @Transactional
    public void modificarUsuario(Long id, String nombre, String email) throws MiException {

        Optional<Usuario> buscarUsuario = usuarioRepositorio.findById(id); //objeto contenedor que puede contener o no un valor no nulo. Devuelve true si tien un valor

        if (buscarUsuario.isPresent()) {

            Usuario usuario = buscarUsuario.get();

            usuario.setNombre(nombre);
            usuario.setEmail(email);

            usuarioRepositorio.save(usuario);

        }
    }

    @Transactional
    public void modificarSueldo(Long id, Double sueldo, Integer tarifa) throws MiException {

         if (id == null) {
            throw new MiException("El ID del usuario no puede ser nulo");
        }
        
        Optional<Usuario> buscarUsuario = usuarioRepositorio.findById(id); //objeto contenedor que puede contener o no un valor no nulo. Devuelve true si tien un valor

        if (buscarUsuario.isPresent()) {

            Usuario usuario = buscarUsuario.get();

            sueldo = sueldo + (noticiaServicio.contarNoticias(id) * tarifa);

            usuario.setSueldo(sueldo);

            usuarioRepositorio.save(usuario);

        }
    }

    public void validar(String nombre, String email, String password, String password2) throws MiException {

        if (nombre.isEmpty() || nombre == null) {
            throw new MiException("El nombre no puede ser nulo");
        }
        if (email.isEmpty() || email == null) {
            throw new MiException("El email no puede ser nulo");
        }
        if (password.isEmpty() || password == null || password.length() <= 5) {
            throw new MiException("La contraseña no puede ser nula o debe tener mas de 5 caracteres");
        }
        if (!password.equals(password2)) {
            throw new MiException("Las contraseñas ingresadas deben ser iguales");
        }
    }

}

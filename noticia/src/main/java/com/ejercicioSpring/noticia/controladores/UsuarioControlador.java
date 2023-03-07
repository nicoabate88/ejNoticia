
package com.ejercicioSpring.noticia.controladores;

import com.ejercicioSpring.noticia.entidades.Usuario;
import com.ejercicioSpring.noticia.excepciones.MiException;
import com.ejercicioSpring.noticia.servicios.NoticiaServicio;
import com.ejercicioSpring.noticia.servicios.UsuarioServicio;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/usuario")
public class UsuarioControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;
    @Autowired
    private NoticiaServicio noticiaServicio;

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_PER')") //para abrir este archivo es necesario loguearse como usuario
    @GetMapping("/")
    public String index() {

        return "redirect:../index/";
    }

    @GetMapping("/registrar")
    public String registrar() {

        return "registro_usuario.html";
    }

    @PostMapping("/registro")
    public String registro(@RequestParam String nombre, @RequestParam String email, @RequestParam String rol,
            @RequestParam String password, @RequestParam String password2, ModelMap modelo) throws MiException {

        try {
            usuarioServicio.registrar(nombre, email, rol, password, password2);
            modelo.put("exito", "Usuario registrado con exito");

        } catch (MiException ex) {

            modelo.put("error", ex.getMessage());
            modelo.put("nombre", nombre);
            modelo.put("email", email);

            return "registro_usuario.html";
        }

        return "registro_usuario.html";
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error, ModelMap modelo) {

        if (error != null) {
            modelo.put("error", "Usuario o Contraseña erronea");
        }

        return "login_usuario.html";

    }

    @GetMapping("/listar")
    public String listar(ModelMap modelo) {

        ArrayList<Usuario> lista = new ArrayList();

        lista = usuarioServicio.buscarUsuarios();

        modelo.addAttribute("usuarios", lista);

        return "listar_usuarios.html";

    }

    @GetMapping("/modificar/{id}")
    public String buscarUsuario(@PathVariable Long id, ModelMap modelo) {

        modelo.put("usuario", usuarioServicio.buscarUsuario(id));

        return "modificar_usuario.html";

    }

    @PostMapping("/modificarU/{id}")
    public String postmodificarNoticia(@RequestParam Long id, @RequestParam String nombre,
            @RequestParam String email, ModelMap modelo) throws MiException {

        usuarioServicio.modificarUsuario(id, nombre, email);

        return "redirect:../listar/";
    }

    @GetMapping("/listarP")
    public String listarPeriodista(ModelMap modelo) {

        ArrayList<Usuario> lista = new ArrayList();

        lista = usuarioServicio.buscarUsuarios();

        modelo.addAttribute("usuarios", lista);

        return "sueldo_usuarios.html";

    }

    @GetMapping("/modificarS/{id}")  //modificar Sueldo
    public String buscarPeriodista(@PathVariable Long id, ModelMap modelo) {

        modelo.put("usuario", usuarioServicio.buscarUsuario(id));
        modelo.put("cantidad", noticiaServicio.contarNoticias(id));

        return "modificar_sueldo.html";

    }

    @PostMapping("/modificarSu/{id}")
    public String postmodificarSueldo(@RequestParam Long id, @RequestParam(required = false) Double sueldo, @RequestParam(required = false) Integer tarifa,
            ModelMap modelo) throws MiException {

        usuarioServicio.modificarSueldo(id, sueldo, tarifa);

        return "redirect:../listarP/";

    }

}

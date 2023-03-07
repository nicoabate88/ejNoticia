package com.ejercicioSpring.noticia.controladores;

import com.ejercicioSpring.noticia.entidades.Noticia;
import com.ejercicioSpring.noticia.entidades.Usuario;
import com.ejercicioSpring.noticia.excepciones.MiException;
import com.ejercicioSpring.noticia.servicios.NoticiaServicio;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/")
@PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN','ROLE_PER')")
public class NoticiaControlador {

    @Autowired
    private NoticiaServicio noticiaServicio;

    @GetMapping("/index")
    public String index(ModelMap modelo) {

        ArrayList<Noticia> lista = new ArrayList();

        lista = noticiaServicio.listarNoticias();

        modelo.addAttribute("noticias", lista);

        return "index.html";
    }

    @GetMapping("/mostrar/{id}")
    public String mostrarNoticia(@PathVariable Long id, ModelMap modelo) {

        modelo.put("noticia", noticiaServicio.buscarNoticia(id));

        return "mostrar_noticia.html";
    }

    @GetMapping("/crear")
    public String getCrearNoticia(HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.addAttribute("usuario", logueado);

        return "crear_noticia.html";

    }

    @PostMapping("/crearN")
    public String postCrearNoticia(@RequestParam("titulo") String titulo, @RequestParam("cuerpo") String cuerpo,
            @RequestParam("file") MultipartFile imagen, @RequestParam("usuario") Usuario usuario, ModelMap modelo) {

        if (!imagen.isEmpty()) {
            Path directorioImagenes = Paths.get("src//main//resources//static/images");
            String rutaAbsoluta = directorioImagenes.toFile().getAbsolutePath();
            try {
                byte[] bytesImg = imagen.getBytes();
                Path rutaCompleta = Paths.get(rutaAbsoluta + "//" + imagen.getOriginalFilename());
                Files.write(rutaCompleta, bytesImg);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {

            noticiaServicio.crearNoticia(titulo, cuerpo, imagen, usuario);
            modelo.put("exito", "Noticia Cargada correctamente");

        } catch (MiException ex) {
            modelo.put("error", ex.getMessage());
            return "crear_noticia.html";
        }

        return "crear_noticia.html";
    }

    @GetMapping("/listar")
    public String listarNoticias(ModelMap modelo) {

        ArrayList<Noticia> lista = new ArrayList();

        lista = noticiaServicio.listarNoticias();

        modelo.addAttribute("noticias", lista);

        return "listar_noticias.html";
    }

    @GetMapping("/listarA")
    public String listarNoticiasAcciones(ModelMap modelo) {

        ArrayList<Noticia> lista = new ArrayList();

        lista = noticiaServicio.listarNoticias();

        modelo.addAttribute("noticias", lista);

        return "listar_acciones_noticia.html";
    }

    @GetMapping("/modificar/{id}")
    public String getModificarNoticia(@PathVariable Long id, ModelMap modelo) {

        modelo.put("noticia", noticiaServicio.buscarNoticia(id));

        return "modificar_noticia.html";
    }

    @PostMapping("/modificarN/{id}")
    public String postmodificarNoticia(@RequestParam("id") Long id, @RequestParam("titulo") String titulo,
            @RequestParam("cuerpo") String cuerpo, ModelMap modelo) throws MiException {

        noticiaServicio.modificarNoticia(id, titulo, cuerpo);

        return "redirect:../listarA/";
    }

    @GetMapping("/eliminar/{id}")
    public String getEliminarNoticia(@PathVariable Long id, ModelMap modelo) {

        modelo.put("noticia", noticiaServicio.buscarNoticia(id));

        return "eliminar_noticia.html";
    }

    @PostMapping("/eliminar/{id}")
    public String postEliminarNoticia(@PathVariable("id") Long id) {

        try {
            noticiaServicio.eliminarNoticia(id);

        } catch (MiException ex) {
            Logger.getLogger(NoticiaControlador.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "redirect:../listarA";
    }

}

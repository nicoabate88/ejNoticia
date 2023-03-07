
package com.ejercicioSpring.noticia.servicios;

import com.ejercicioSpring.noticia.entidades.Noticia;
import com.ejercicioSpring.noticia.entidades.Usuario;
import com.ejercicioSpring.noticia.excepciones.MiException;
import com.ejercicioSpring.noticia.repositorios.NoticiaRepositorio;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class NoticiaServicio {

    @Autowired
    private NoticiaRepositorio noticiaRepositorio;

    @Transactional
    public void crearNoticia(String titulo, String cuerpo, MultipartFile imagen, Usuario usuario) throws MiException {

        if (titulo.isEmpty() || titulo == null) {
            throw new MiException("El titulo de la noticia no puede estar vacio");
        }
        if (cuerpo.isEmpty() || cuerpo == null) {
            throw new MiException("El cuerpo de la noticia no puede estar vacio");
        }

        Noticia noticia = new Noticia();

        noticia.setTitulo(titulo);
        noticia.setCuerpo(cuerpo);
        noticia.setFecha(new Date());
        noticia.setImagen(imagen.getOriginalFilename());
        noticia.setUsuario(usuario);

        noticiaRepositorio.save(noticia);

    }

    public ArrayList<Noticia> listarNoticias() {

        ArrayList<Noticia> listaNoticias = new ArrayList();

        listaNoticias = (ArrayList<Noticia>) noticiaRepositorio.findAll();

        return listaNoticias;

    }

    public Noticia buscarNoticia(Long id) {

        return noticiaRepositorio.getOne(id);

    }

    @Transactional
    public void modificarNoticia(Long id, String titulo, String cuerpo) throws MiException {

        validar(id, titulo, cuerpo);

        Optional<Noticia> buscarNoticia = noticiaRepositorio.findById(id); //objeto contenedor que puede contener o no un valor no nulo. Devuelve true si tien un valor

        if (buscarNoticia.isPresent()) {

            Noticia noticia = buscarNoticia.get();

            noticia.setTitulo(titulo);
            noticia.setCuerpo(cuerpo);

            noticiaRepositorio.save(noticia);
        }
    }

    @Transactional
    public void eliminarNoticia(Long id) throws MiException {

        if (id == null) {
            throw new MiException("El ID no puede ser nulo");
        }

        noticiaRepositorio.deleteById(id);

    }

    public int contarNoticias(Long id) {

        return noticiaRepositorio.cantidadNoticia(id);
    }

    public void validar(Long id, String titulo, String cuerpo) throws MiException {

        if (id == null) {
            throw new MiException("El ID de la noticia no puede ser nulo");
        }
        if (titulo.isEmpty() || titulo == null) {
            throw new MiException("El titulo de la noticia no puede estar vacio");
        }
        if (cuerpo.isEmpty() || cuerpo == null) {
            throw new MiException("El cuerpo de la noticia no puede estar vacio");

        }

    }
}

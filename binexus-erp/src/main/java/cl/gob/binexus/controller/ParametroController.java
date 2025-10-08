package cl.gob.binexus.controller;

import cl.gob.binexus.domain.entity.Parametro;
import cl.gob.binexus.domain.entity.TipoParametro;
import cl.gob.binexus.domain.enums.EstadoParametro;
import cl.gob.binexus.dto.ParametroFormDTO;
import cl.gob.binexus.dto.TipoParametroFormDTO;
import cl.gob.binexus.service.ParametroService;
import cl.gob.binexus.service.TipoParametroService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/parametros")
public class ParametroController {

    private final TipoParametroService tipoParametroService;
    private final ParametroService parametroService;

    public ParametroController(TipoParametroService tipoParametroService, ParametroService parametroService) {
        this.tipoParametroService = tipoParametroService;
        this.parametroService = parametroService;
    }

    @GetMapping
    public String index(@RequestParam(value = "tipoId", required = false) Long tipoId,
                        @RequestParam(value = "parametroId", required = false) Long parametroId,
                        Model model) {
        TipoParametroFormDTO tipoForm = new TipoParametroFormDTO();
        ParametroFormDTO parametroForm = new ParametroFormDTO();
        if (tipoId != null) {
            TipoParametro tipo = tipoParametroService.obtenerPorId(tipoId);
            tipoForm.setId(tipo.getId());
            tipoForm.setDescripcion(tipo.getDescripcion());
            parametroForm.setTipoParametroId(tipo.getId());
        }
        if (parametroId != null) {
            Parametro parametro = parametroService.obtenerPorId(parametroId);
            parametroForm.setId(parametro.getId());
            parametroForm.setDescripcion(parametro.getDescripcion());
            parametroForm.setEstado(parametro.getEstado());
            parametroForm.setTipoParametroId(parametro.getTipoParametro().getId());
        }
        prepararModelo(model, tipoForm, parametroForm);
        model.addAttribute("pageTitle", "Parámetros");
        return "parameters/parameters";
    }

    @PostMapping("/tipos")
    public String guardarTipo(@Valid @ModelAttribute("tipoParametro") TipoParametroFormDTO tipoDto,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            prepararModelo(model, tipoDto, new ParametroFormDTO());
            model.addAttribute("pageTitle", "Parámetros");
            return "parameters/parameters";
        }
        try {
            TipoParametro tipo = tipoDto.getId() != null ? tipoParametroService.obtenerPorId(tipoDto.getId()) : new TipoParametro();
            tipo.setDescripcion(tipoDto.getDescripcion());
            tipoParametroService.guardar(tipo);
            redirectAttributes.addFlashAttribute("toastSuccess", "Tipo de parámetro guardado correctamente");
            return "redirect:/admin/parametros";
        } catch (IllegalArgumentException ex) {
            result.reject("error.global", ex.getMessage());
            prepararModelo(model, tipoDto, new ParametroFormDTO());
            model.addAttribute("pageTitle", "Parámetros");
            return "parameters/parameters";
        }
    }

    @PostMapping("/parametros")
    public String guardarParametro(@Valid @ModelAttribute("parametro") ParametroFormDTO parametroDto,
                                   BindingResult result,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            prepararModelo(model, new TipoParametroFormDTO(), parametroDto);
            model.addAttribute("pageTitle", "Parámetros");
            return "parameters/parameters";
        }
        try {
            TipoParametro tipo = tipoParametroService.obtenerPorId(parametroDto.getTipoParametroId());
            Parametro parametro = parametroDto.getId() != null ? parametroService.obtenerPorId(parametroDto.getId()) : new Parametro();
            parametro.setTipoParametro(tipo);
            parametro.setDescripcion(parametroDto.getDescripcion());
            parametro.setEstado(parametroDto.getEstado());
            parametroService.guardar(parametro);
            redirectAttributes.addFlashAttribute("toastSuccess", "Parámetro guardado correctamente");
            return "redirect:/admin/parametros?tipoId=" + tipo.getId();
        } catch (IllegalArgumentException ex) {
            result.reject("error.global", ex.getMessage());
            prepararModelo(model, new TipoParametroFormDTO(), parametroDto);
            model.addAttribute("pageTitle", "Parámetros");
            return "parameters/parameters";
        }
    }

    @PostMapping("/parametros/{id}/estado")
    public String cambiarEstado(@PathVariable Long id,
                                @RequestParam("estado") EstadoParametro estado,
                                RedirectAttributes redirectAttributes) {
        parametroService.cambiarEstado(id, estado);
        redirectAttributes.addFlashAttribute("toastSuccess", "Estado actualizado");
        return "redirect:/admin/parametros";
    }

    private void prepararModelo(Model model, TipoParametroFormDTO tipoForm, ParametroFormDTO parametroForm) {
        model.addAttribute("tipoParametro", tipoForm);
        model.addAttribute("parametro", parametroForm);
        model.addAttribute("tipos", tipoParametroService.listar());
        model.addAttribute("parametros", agruparParametros());
        model.addAttribute("estadosParametro", EstadoParametro.values());
    }

    private Map<TipoParametro, List<Parametro>> agruparParametros() {
        Map<TipoParametro, List<Parametro>> mapa = new LinkedHashMap<>();
        tipoParametroService.listar().forEach(tipo ->
                mapa.put(tipo, parametroService.listarPorTipo(tipo, false))
        );
        return mapa;
    }
}

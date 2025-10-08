package cl.gob.binexus.controller;

import cl.gob.binexus.domain.entity.Cliente;
import cl.gob.binexus.domain.enums.EstadoCliente;
import cl.gob.binexus.dto.ClienteFormDTO;
import cl.gob.binexus.service.ClienteService;
import cl.gob.binexus.service.OrganizacionService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final OrganizacionService organizacionService;

    public ClienteController(ClienteService clienteService, OrganizacionService organizacionService) {
        this.clienteService = clienteService;
        this.organizacionService = organizacionService;
    }

    @GetMapping
    public String listar(@RequestParam(value = "organizacionId", required = false) Long organizacionId,
                         @RequestParam(value = "estado", required = false) EstadoCliente estado,
                         @RequestParam(value = "q", required = false) String consulta,
                         Model model) {
        List<Cliente> clientes = null;
        if (organizacionId != null) {
            clientes = clienteService.buscar(organizacionService.obtenerPorId(organizacionId), estado, consulta);
        }
        model.addAttribute("clientes", clientes);
        model.addAttribute("organizaciones", organizacionService.listarOrganizaciones());
        model.addAttribute("organizacionSeleccionada", organizacionId);
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("estados", EstadoCliente.values());
        model.addAttribute("query", consulta);
        model.addAttribute("pageTitle", "Clientes");
        return "clients/clients-list";
    }

    @GetMapping("/new")
    public String nuevo(Model model) {
        prepararFormulario(model, new ClienteFormDTO());
        model.addAttribute("pageTitle", "Nuevo cliente");
        return "clients/clients-form";
    }

    @GetMapping("/{id}/edit")
    public String editar(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.obtenerPorId(id);
        ClienteFormDTO dto = mapearCliente(cliente);
        prepararFormulario(model, dto);
        model.addAttribute("pageTitle", "Editar cliente");
        return "clients/clients-form";
    }

    @PostMapping
    public String guardar(@Valid @ModelAttribute("cliente") ClienteFormDTO dto,
                          BindingResult result,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        boolean esNuevo = dto.getId() == null;
        if (result.hasErrors()) {
            prepararFormulario(model, dto);
            model.addAttribute("pageTitle", esNuevo ? "Nuevo cliente" : "Editar cliente");
            return "clients/clients-form";
        }
        try {
            Cliente cliente = esNuevo ? new Cliente() : clienteService.obtenerPorId(dto.getId());
            cliente.setNombre(dto.getNombre());
            cliente.setCorreo(dto.getCorreo());
            cliente.setTelefono(dto.getTelefono());
            cliente.setEstado(dto.getEstado());
            cliente.setOrganizacion(organizacionService.obtenerPorId(dto.getOrganizacionId()));
            clienteService.guardar(cliente);
            redirectAttributes.addFlashAttribute("toastSuccess", "Cliente guardado correctamente");
            return "redirect:/clientes";
        } catch (IllegalArgumentException ex) {
            result.reject("error.global", ex.getMessage());
            prepararFormulario(model, dto);
            model.addAttribute("pageTitle", esNuevo ? "Nuevo cliente" : "Editar cliente");
            return "clients/clients-form";
        }
    }

    private void prepararFormulario(Model model, ClienteFormDTO dto) {
        model.addAttribute("cliente", dto);
        model.addAttribute("organizaciones", organizacionService.listarOrganizaciones());
        model.addAttribute("estados", EstadoCliente.values());
    }

    private ClienteFormDTO mapearCliente(Cliente cliente) {
        ClienteFormDTO dto = new ClienteFormDTO();
        dto.setId(cliente.getId());
        dto.setNombre(cliente.getNombre());
        dto.setCorreo(cliente.getCorreo());
        dto.setTelefono(cliente.getTelefono());
        dto.setEstado(cliente.getEstado());
        dto.setOrganizacionId(cliente.getOrganizacion().getId());
        return dto;
    }
}

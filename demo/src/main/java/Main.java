

import entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("barberiasql2");
        EntityManager entityManager = emf.createEntityManager();

        try {
            entityManager.getTransaction().begin();
            demostrarRelaciones(entityManager);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            entityManager.close();
            emf.close();
        }
    }

    public static void demostrarRelaciones(EntityManager entityManager) {
        // Crear un cliente y sus detalles (relación 1-1)
        Cliente cliente1 = new Cliente();
        cliente1.setNombre("ROBERTO");
        cliente1.setApellidos("SUAREZ");
        cliente1.setEmail("ROBERTO@email.com");
        cliente1.setTelefono("1233-4456-74890");
        entityManager.persist(cliente1);

        DetallesCliente detallesCliente1 = new DetallesCliente();
        detallesCliente1.setCliente(cliente1);
        detallesCliente1.setFechaNacimiento(LocalDate.of(1990, 5, 15));
        detallesCliente1.setDireccion("JUAN DE SALINAS ");
        entityManager.persist(detallesCliente1);
        cliente1.setDetallesCliente(detallesCliente1); // Establecer la relación bidireccional

        // Crear un barbero
        Barbero barbero1 = new Barbero();
        barbero1.setNombre("kody");
        barbero1.setApellidos("ELABERTO");
        barbero1.setTelefono("987-654-3210");
        entityManager.persist(barbero1);

        // Crear servicios
        Servicio servicio1 = new Servicio();
        servicio1.setNombre("Corte MULLET");
        servicio1.setDescripcion("Corte estándar para caballero men flow");
        servicio1.setDuracion(30);
        servicio1.setPrecio(new BigDecimal("15.00"));
        entityManager.persist(servicio1);

        Servicio servicio2 = new Servicio();
        servicio2.setNombre("Afeitado Barba larga");
        servicio2.setDescripcion("Afeitado completo de barba con toalla caliente x2 los miercoles");
        servicio2.setDuracion(45);
        servicio2.setPrecio(new BigDecimal("23.00"));
        entityManager.persist(servicio2);

        // Crear una cita y relacionarla con el cliente y el barbero (relación 1-M)
        Cita cita1 = new Cita();
        cita1.setCliente(cliente1);
        cita1.setBarbero(barbero1);
        cita1.setFechaHora(Instant.now());
        cita1.setEstado("Completado");
        cita1.setLocal("Local 01");
        cita1.setNotas("El cliente quiere un corte degradado mullet.");
        entityManager.persist(cita1);

        // Crear entradas en CitaServicio para la relación muchos a muchos
        CitaServicioId citaServicioId1 = new CitaServicioId(cita1.getId(), servicio1.getId());
        CitaServicio citaServicio1 = new CitaServicio();
        citaServicio1.setId(citaServicioId1);
        citaServicio1.setCita(cita1);
        citaServicio1.setServicio(servicio1);
        citaServicio1.setDescripcion("Corte degradado a los lados y más largo arriba.");
        citaServicio1.setPrecio(new BigDecimal("18.00"));
        entityManager.persist(citaServicio1);

        CitaServicioId citaServicioId2 = new CitaServicioId(cita1.getId(), servicio2.getId());
        CitaServicio citaServicio2 = new CitaServicio();
        citaServicio2.setId(citaServicioId2);
        citaServicio2.setCita(cita1);
        citaServicio2.setServicio(servicio2);
        citaServicio2.setDescripcion("Afeitado completo con aceite de sándalo.");
        citaServicio2.setPrecio(new BigDecimal("20.00"));
        entityManager.persist(citaServicio2);

        // Demostrar las relaciones mediante consultas
        System.out.println("\n--- Demostración de Relaciones ---");

        // Obtener un cliente y sus detalles (1-1)
        Cliente clienteConsultado = entityManager.find(Cliente.class, cliente1.getId());
        System.out.println("Detalles del cliente: " + clienteConsultado.getNombre() + " " + clienteConsultado.getApellidos());
        if (clienteConsultado.getDetallesCliente() != null) {
            System.out.println("- Fecha de Nacimiento: " + clienteConsultado.getDetallesCliente().getFechaNacimiento());
            System.out.println("- Dirección: " + clienteConsultado.getDetallesCliente().getDireccion());
        }

        // Obtener un barbero y sus citas (1-M)
        Barbero barberoConsultado = entityManager.find(Barbero.class, barbero1.getId());
        System.out.println("\nCitas de " + barberoConsultado.getNombre() + " " + barberoConsultado.getApellidos() + ":");
        barberoConsultado.getCitas().forEach(cita -> System.out.println("- Fecha y Hora: " + cita.getFechaHora() +
        ", Cliente ID: " + cita.getCliente().getId()));

        // Obtener una cita y sus servicios (N-M a través de CitaServicio)
        Cita citaConsultada = entityManager.find(Cita.class, cita1.getId());

        System.out.println("\nServicios para la cita el " + citaConsultada.getFechaHora() + ":");
        citaConsultada.getCitaServicios().forEach(cs ->
                System.out.println("- Servicio: " + cs.getServicio().getNombre() + ", Descripción en cita: "
        + cs.getDescripcion() + ", Precio en cita: " + cs.getPrecio()));

        // Obtener un servicio y las citas en las que se ofrece (N-M a través de CitaServicio)
        Servicio servicioConsultado = entityManager.find(Servicio.class, servicio1.getId());
        System.out.println("\nEl servicio " + servicioConsultado.getNombre() + " está en las citas:");
        servicioConsultado.getCitaServicios().forEach(cs ->
                System.out.println("- Cita ID: " + cs.getCita().getId() + ", Descripción: " + cs.getDescripcion()));
    }
}
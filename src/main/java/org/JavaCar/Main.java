package org.JavaCar;

import java.io.File;
import java.io.IOException;

public class Main {
    // Constants
    private static final String DATA_DIR = "data";
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PWD = "admin";

    // Fitxers de dades
    private static final String[] DATA_FILES = {
            "clients.txt",
            "lloguers.txt",
            "vehicles.txt",
            "valoracions.txt"
    };

    public static void main(String[] args) {
        // Inicialitza els fitxers de dades
        inicialitzarFitxersDades();

        while (true) {
            System.out.println("\n=== BENVINGUT/DA AL SISTEMA DE LLOGUER ===");
            System.out.println("1. Entrar com a Client");
            System.out.println("2. Entrar com a Admin");
            System.out.println("3. Sortir");

            // Demana opció amb validació
            int option = AjudaEntrada.demanarNumero("Selecciona una opció: ", 1, 3);

            switch (option) {
                case 1 -> iniciarMenuClient();
                case 2 -> iniciarMenuAdmin();
                case 3 -> {
                    System.out.println("Gràcies per utilitzar el nostre sistema. Fins aviat!");
                    System.exit(0);
                }
            }
        }
    }

    // Funció per inicialitzar els fitxers de dades necessaris
    private static void inicialitzarFitxersDades() {
        // Crear directori si no existeix
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdir();
            System.out.println("Directori 'data' creat");
        }

        // Crear fitxers si no existeixen
        for (String filename : DATA_FILES) {
            try {
                File fitxer = new File(DATA_DIR + File.separator + filename);
                if (!fitxer.exists()) {
                    fitxer.createNewFile();
                    System.out.println("Fitxer creat: " + fitxer.getAbsolutePath());
                }
            } catch (IOException e) {
                System.err.println("Error al crear " + filename + ": " + e.getMessage());
            }
        }
    }

    // Funció per gestionar el menú del client
    private static void iniciarMenuClient() {
        System.out.println("\n--- ACCÉS CLIENT ---");
        String dni = AjudaEntrada.demanarDNI("Introdueix el teu DNI: ");

        ClientService clientService = new ClientService();
        Client client = clientService.trobarClient(dni);

        if (client != null) {
            System.out.println("Accés concedit. Benvingut/da " + client.getNom());

            // Inicialitza serveis
            VehicleService vehicleService = new VehicleService();
            LloguerService lloguerService = new LloguerService(vehicleService);

            // Mostra menú client
            ClientMenu clientMenu = new ClientMenu(client, vehicleService, lloguerService, clientService);
            clientMenu.mostrarMenu();
        } else {
            System.out.println("Client no trobat.");
            boolean volRegistrar = AjudaEntrada.demanarConfirmacio("Vols registrar-te?");
            if (volRegistrar) {
                registrarNouClient();
            }
        }
    }

    // Funció per gestionar el menú d'administrador
    private static void iniciarMenuAdmin() {
        System.out.println("\n--- ACCÉS ADMIN ---");
        System.out.println("Credencials --> Usuari: " + ADMIN_USER + " Contrasenya: " + ADMIN_PWD);

        String usuari = AjudaEntrada.demanarText("Usuari admin: ");
        String contrasenya = AjudaEntrada.demanarText("Contrasenya: ");

        if (ADMIN_USER.equals(usuari) && ADMIN_PWD.equals(contrasenya)) {
            System.out.println("Accés concedit. Mode administrador activat.");

            // Inicialitza serveis
            VehicleService vehicleService = new VehicleService();
            LloguerService lloguerService = new LloguerService(vehicleService);
            ClientService clientService = new ClientService();

            // Mostra menú admin
            AdminMenu adminMenu = new AdminMenu(vehicleService, lloguerService, clientService);
            adminMenu.mostrarMenu();
        } else {
            System.out.println("Credencials incorrectes. Accés denegat.");
        }
    }

    // Funció per registrar un nou client
    private static void registrarNouClient() {
        System.out.println("\n--- REGISTRE NOU CLIENT ---");
        String dni = AjudaEntrada.demanarDNI("DNI: ");

        // Verifica si el client ja existeix
        ClientService clientService = new ClientService();
        if (clientService.trobarClient(dni) != null) {
            System.out.println("Aquest DNI ja està registrat.");
            return;
        }

        String nom = AjudaEntrada.demanarText("Nom complet: ");
        String adreca = AjudaEntrada.demanarText("Adreça: ");

        try {
            Client nouClient = new Client(dni, nom, adreca);
            clientService.guardarClient(nouClient);
            System.out.println("Registre completat. Ara pots iniciar sessió.");
        } catch (Exception e) {
            System.out.println("Error en registrar: " + e.getMessage());
        }
    }
}
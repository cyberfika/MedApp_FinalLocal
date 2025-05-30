import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Classe responsável pela interface com o usuário para a visão do médico.
 * Gerencia as funcionalidades disponíveis para médicos no sistema, como
 * visualização de pacientes e consultas.
 */
public class DoctorView {
    // Constantes para arquivos
    //private static final String DOCTOR_CSV = "doctors_clean.csv";
    private static final String PATIENT_CSV = "patients.csv";
    private static final String APPOINTMENT_CSV = "appointments.csv";

    /**
     * Exibe as opções disponíveis para o médico e processa a seleção do usuário
     *
     * @param doctors Lista de médicos cadastrados no sistema
     * @param appointments Lista de consultas marcadas
     * @param patients Lista de pacientes cadastrados
     * @param search Flag para controlar o loop do menu
     * @param scanner Scanner para leitura de entrada do usuário
     */
    public static void checkOptions(List<Doctor> doctors, List<Appointment> appointments, List<Patient> patients, boolean search, Scanner scanner) {
        while (search) {
            try {
                // Solicitar CRM ao médico
                System.out.print("Digite seu CRM (somente números): ");
                String crm = scanner.nextLine().trim();

                if (!crm.matches("\\d+")) {
                    System.out.println("CRM inválido. Deve conter apenas números.");
                    continue;
                }

                // Buscar médico pelo CRM
                Doctor doctor = findDoctorByCRM(doctors, crm);

                if (doctor == null) {
                    System.out.println("Médico não encontrado. Deseja tentar novamente? (s/n): ");
                    String retry = scanner.nextLine();
                    if (!retry.equalsIgnoreCase("s")) {
                        search = false;
                    }
                    continue;
                }

                // Dar boas-vindas ao médico e mostrar o menu principal
                System.out.println("\nBem-vindo(a), Dr(a). " + doctor.getName() + "!");

                // Mostrar o menu para o médico
                doctorMenu(doctor, appointments, patients, scanner);
                search = false;

            } catch (Exception e) {
                System.err.println("Erro: " + e.getMessage());
                e.printStackTrace(System.err);
            }
        }
    }

    /**
     * Busca um médico pelo CRM na lista de médicos
     *
     * @param doctors Lista de médicos
     * @param crm CRM a ser buscado
     * @return Médico encontrado ou null se não encontrado
     */
    private static Doctor findDoctorByCRM(List<Doctor> doctors, String crm) {
        for (Doctor d : doctors) {
            if (d.getCode().equals(crm)) {
                return d;
            }
        }
        return null;
    }

    /**
     * Exibe o menu principal para o médico e processa a opção escolhida
     *
     * @param doctor Médico logado
     * @param allAppointments Todas as consultas do sistema
     * @param allPatients Todos os pacientes do sistema
     * @param scanner Scanner para leitura de entrada do usuário
     */
    private static void doctorMenu(Doctor doctor, List<Appointment> allAppointments, List<Patient> allPatients, Scanner scanner) {
        boolean continueMenu = true;

        while (continueMenu) {
            try {
                System.out.println("\n===== MENU DO MÉDICO =====");
                System.out.println("1 - Agendar nova consulta para paciente");
                System.out.println("2 - Ver consultas agendadas (futuras)");
                System.out.println("3 - Ver consultas realizadas");
                System.out.println("4 - Remarcar consultas");
                System.out.println("5 - Cancelar consultas agendadas");
                System.out.println("0 - Sair");
                System.out.print("\nEscolha uma opção: ");

                int option = Integer.parseInt(scanner.nextLine());

                switch (option) {
                    case 0:
                        continueMenu = false;
                        break;
                    case 1:
                        scheduleNewAppointment(doctor, allAppointments, allPatients, scanner);
                        break;
                    case 2:
                        viewFutureAppointments(doctor, allAppointments, allPatients, scanner);
                        break;
                    case 3:
                        viewPastAppointments(doctor, allAppointments, allPatients, scanner);
                        break;
                    case 4:
                        rescheduleAppointment(doctor, allAppointments, allPatients, scanner);
                        break;
                    case 5:
                        cancelAppointment(doctor, allAppointments, allPatients, scanner);
                        break;
                    default:
                        System.out.println("Opção inválida!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número.");
            } catch (Exception e) {
                System.err.println("Erro: " + e.getMessage());
                e.printStackTrace(System.err);

            }
        }
    }

    /**
     * Agenda uma consulta para um paciente (existente ou novo)
     *
     * @param doctor Médico que está agendando a consulta
     * @param allAppointments Lista de todas as consultas
     * @param allPatients Lista de todos os pacientes
     * @param scanner Scanner para leitura de entrada
     */
    private static void scheduleNewAppointment(Doctor doctor, List<Appointment> allAppointments, List<Patient> allPatients, Scanner scanner) {
        System.out.println("\n=== AGENDAR NOVA CONSULTA ===");
        System.out.println("1 - Para paciente existente");
        System.out.println("2 - Para novo paciente");
        System.out.println("0 - Voltar");
        System.out.print("Escolha uma opção: ");

        try {
            int option = Integer.parseInt(scanner.nextLine());

            Patient patient; //= null;

            switch (option) {
                case 0:
                    return;
                case 1:
                    patient = selectExistingPatient(allPatients, scanner);
                    break;
                case 2:
                    patient = registerNewPatient(scanner);
                    if (patient != null) {
                        allPatients.add(patient);
                    }
                    break;
                default:
                    System.out.println("Opção inválida!");
                    return;
            }

            if (patient == null) {
                return;
            }

            // Obter data da consulta
            System.out.print("Digite a data da consulta (yyyy-MM-dd): ");
            String dateStr = scanner.nextLine();

            // Obter hora da consulta
            System.out.print("Digite o horário da consulta (HH:mm): ");
            String timeStr = scanner.nextLine();

            try {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

                LocalDate appointmentDate = LocalDate.parse(dateStr, dateFormatter);
                LocalTime appointmentTime = LocalTime.parse(timeStr, timeFormatter);

                // Verificar se a data é no futuro
                if (appointmentDate.isBefore(LocalDate.now())) {
                    System.out.println("A data da consulta deve ser futura.");
                    return;
                }

                // Verificar se já existe consulta no mesmo horário para o médico
                boolean conflictFound = false;
                for (Appointment app : allAppointments) {
                    if (app.getDoctorCRM().equals(doctor.getCode()) &&
                            app.getDate().equals(appointmentDate) &&
                            app.getTime().equals(appointmentTime) &&
                            app.getStatus() == AppointmentStatus.PENDING) {
                        conflictFound = true;
                        break;
                    }
                }

                if (conflictFound) {
                    System.out.println("Já existe uma consulta agendada neste horário.");
                    return;
                }

                // Criar e salvar a nova consulta
                Appointment appointment = new Appointment(
                        appointmentDate,
                        appointmentTime,
                        patient.getCpf(),
                        doctor.getCode(),
                        AppointmentStatus.PENDING
                );

                appointment.saveToCSVFile(APPOINTMENT_CSV, true);

                // Adicionar a consulta à lista geral
                allAppointments.add(appointment);

                System.out.println("\nConsulta agendada com sucesso!");
                System.out.println("Paciente: " + patient.getName());
                System.out.println("Data e hora: " + appointment.getFormattedDateTime());

            } catch (DateTimeParseException e) {
                System.out.println("Formato de data ou hora inválido: " + e.getMessage());
            }

        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Digite um número.");
        }
    }

    /**
     * Seleciona um paciente existente no sistema
     *
     * @param allPatients Lista de todos os pacientes
     * @param scanner Scanner para leitura de entrada
     * @return Paciente selecionado ou null se cancelado
     */
    private static Patient selectExistingPatient(List<Patient> allPatients, Scanner scanner) {
        System.out.println("\nSelecione como deseja buscar o paciente:");
        System.out.println("1 - Por CPF");
        System.out.println("2 - Por nome");
        System.out.println("0 - Voltar");
        System.out.print("Escolha uma opção: ");

        try {
            int option = Integer.parseInt(scanner.nextLine());

            switch (option) {
                case 0:
                    return null;
                case 1:
                    return findPatientByCPF(allPatients, scanner);
                case 2:
                    return findPatientByName(allPatients, scanner);
                default:
                    System.out.println("Opção inválida!");
                    return null;
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Digite um número.");
            return null;
        }
    }

    /**
     * Busca um paciente pelo CPF
     *
     * @param allPatients Lista de todos os pacientes
     * @param scanner Scanner para leitura de entrada
     * @return Paciente encontrado ou null se não encontrado
     */
    private static Patient findPatientByCPF(List<Patient> allPatients, Scanner scanner) {
        System.out.print("Digite o CPF do paciente (somente números): ");
        String cpf = scanner.nextLine().trim();

        if (!cpf.matches("\\d{11}")) {
            System.out.println("CPF inválido. Deve conter exatamente 11 dígitos.");
            return null;
        }

        for (Patient p : allPatients) {
            if (p.getCpf().equals(cpf)) {
                System.out.println("Paciente encontrado: " + p.getName());
                return p;
            }
        }

        System.out.println("Paciente não encontrado com o CPF informado.");
        return null;
    }

    /**
     * Busca um paciente pelo nome
     *
     * @param allPatients Lista de todos os pacientes
     * @param scanner Scanner para leitura de entrada
     * @return Paciente selecionado ou null se cancelado
     */
    private static Patient findPatientByName(List<Patient> allPatients, Scanner scanner) {
        System.out.print("Digite o nome do paciente (ou parte do nome): ");
        String searchName = scanner.nextLine().trim().toLowerCase();

        List<Patient> matches = new ArrayList<>();
        for (Patient p : allPatients) {
            if (p.getName().toLowerCase().contains(searchName)) {
                matches.add(p);
            }
        }

        if (matches.isEmpty()) {
            System.out.println("Nenhum paciente encontrado com esse nome.");
            return null;
        }

        System.out.println("\nPacientes encontrados:");
        for (int i = 0; i < matches.size(); i++) {
            Patient p = matches.get(i);
            System.out.println((i + 1) + " - " + p.getName() + " (CPF: " + UIUtils.formatCPF(p.getCpf()) + ")");
        }

        System.out.print("\nDigite o número do paciente (0 para voltar): ");
        try {
            int selection = Integer.parseInt(scanner.nextLine());

            if (selection == 0) {
                return null;
            }

            if (selection < 1 || selection > matches.size()) {
                System.out.println("Seleção inválida.");
                return null;
            }

            return matches.get(selection - 1);

        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Digite um número.");
            return null;
        }
    }

    /**
     * Cadastra um novo paciente no sistema
     *
     * @param scanner Scanner para leitura de entrada
     * @return Novo paciente cadastrado ou null se cancelado
     */
    private static Patient registerNewPatient(Scanner scanner) {
        System.out.println("\n=== CADASTRAR NOVO PACIENTE ===");

        System.out.print("Digite o nome do paciente: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Nome não pode ser vazio.");
            return null;
        }

        System.out.print("Digite o CPF do paciente (somente números): ");
        String cpf = scanner.nextLine().trim();

        if (!cpf.matches("\\d{11}")) {
            System.out.println("CPF inválido. Deve conter exatamente 11 dígitos.");
            return null;
        }

        // Verificar se o paciente já existe
        List<Patient> existingPatients = Patient.loadFromCSV(PATIENT_CSV);
        for (Patient p : existingPatients) {
            if (p.getCpf().equals(cpf)) {
                System.out.println("Já existe um paciente com este CPF: " + p.getName());
                return p;
            }
        }

        // Salvar no arquivo CSV
        try (PrintWriter writer = new PrintWriter(new FileWriter(PATIENT_CSV, true))) {
            writer.println(name + "," + cpf);
        } catch (IOException error) {
            System.out.println("Erro ao salvar no arquivo CSV: " + error.getMessage());
            return null;
        }

        System.out.println("Paciente cadastrado com sucesso!");
        return new Patient(name, cpf);
    }

    /**
     * Exibe as consultas futuras (agendadas) do médico com opções de gerenciamento
     *
     * @param doctor Médico logado
     * @param allAppointments Todas as consultas
     * @param allPatients Todos os pacientes
     * @param scanner Scanner para leitura
     */
    private static void viewFutureAppointments(Doctor doctor, List<Appointment> allAppointments, List<Patient> allPatients, Scanner scanner) {
        List<Appointment> futureAppointments = new ArrayList<>();

        // Filtrar consultas pendentes (futuras) do médico
        for (Appointment app : allAppointments) {
            if (app.getDoctorCRM().equals(doctor.getCode()) && app.getStatus() == AppointmentStatus.PENDING) {
                futureAppointments.add(app);
            }
        }

        if (futureAppointments.isEmpty()) {
            System.out.println("Não há consultas agendadas para você.");
            return;
        }

        // Ordenar por data/hora
        futureAppointments.sort(Comparator.comparing(Appointment::getDate)
                .thenComparing(Appointment::getTime));

        System.out.println("\nSuas consultas agendadas:");

        for (int i = 0; i < futureAppointments.size(); i++) {
            Appointment app = futureAppointments.get(i);
            String patientName = getPatientName(allPatients, app.getPatientCPF());
            System.out.println((i + 1) + " - " + app.getFormattedDateTime() + " - Paciente: " + patientName);
        }

        System.out.println("\nDeseja gerenciar alguma consulta? (s/n): ");
        String response = scanner.nextLine();

        if (response.equalsIgnoreCase("s")) {
            System.out.print("Digite o número da consulta: ");
            try {
                int selection = Integer.parseInt(scanner.nextLine()) - 1;

                if (selection < 0 || selection >= futureAppointments.size()) {
                    System.out.println("Seleção inválida.");
                    return;
                }

                Appointment selectedAppointment = futureAppointments.get(selection);

                System.out.println("\nO que deseja fazer com esta consulta?");
                System.out.println("1 - Confirmar consulta");
                System.out.println("2 - Cancelar consulta");
                System.out.println("3 - Remarcar consulta");
                System.out.println("0 - Voltar");

                int action = Integer.parseInt(scanner.nextLine());

                switch (action) {
                    case 0:
                        return;
                    case 1:
                        System.out.println("Consulta confirmada para " + selectedAppointment.getFormattedDateTime());
                        break;
                    case 2:
                        doCancelAppointment(selectedAppointment, allAppointments);
                        break;
                    case 3:
                        doRescheduleAppointment(selectedAppointment, doctor, allAppointments, scanner);
                        break;
                    default:
                        System.out.println("Opção inválida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número.");
            }
        }
    }

    /**
     * Exibe as consultas já realizadas pelo médico
     *
     * @param doctor Médico logado
     * @param allAppointments Todas as consultas
     * @param allPatients Todos os pacientes
     * @param scanner Scanner para leitura
     */
    private static void viewPastAppointments(Doctor doctor, List<Appointment> allAppointments, List<Patient> allPatients, Scanner scanner) {
        System.out.println("\n=== CONSULTAR HISTÓRICO DE CONSULTAS ===");
        System.out.println("Selecione o período:");
        System.out.println("1 - Última semana");
        System.out.println("2 - Últimos 30 dias");
        System.out.println("3 - Últimos 90 dias");
        System.out.println("4 - Últimos 180 dias");
        System.out.println("5 - Período personalizado");
        System.out.println("0 - Voltar");
        System.out.print("Escolha uma opção: ");

        try {
            int option = Integer.parseInt(scanner.nextLine());

            if (option == 0) {
                return;
            }

            LocalDate endDate = LocalDate.now();
            LocalDate startDate;

            switch (option) {
                case 1: // Última semana
                    startDate = endDate.minusWeeks(1);
                    break;
                case 2: // Últimos 30 dias
                    startDate = endDate.minusDays(30);
                    break;
                case 3: // Últimos 90 dias
                    startDate = endDate.minusDays(90);
                    break;
                case 4: // Últimos 180 dias
                    startDate = endDate.minusDays(180);
                    break;
                case 5: // Período personalizado
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                        System.out.print("Digite a data inicial (yyyy-MM-dd): ");
                        startDate = LocalDate.parse(scanner.nextLine(), formatter);

                        System.out.print("Digite a data final (yyyy-MM-dd): ");
                        endDate = LocalDate.parse(scanner.nextLine(), formatter);
                    } catch (DateTimeParseException e) {
                        System.out.println("Formato de data inválido: " + e.getMessage());
                        return;
                    }
                    break;
                default:
                    System.out.println("Opção inválida!");
                    return;
            }

            showAppointmentsByPeriod(doctor, allAppointments, allPatients, startDate, endDate, scanner);

        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Digite um número.");
        }
    }

    /**
     * Exibe consultas de um médico em um período específico
     *
     * @param doctor Médico selecionado
     * @param allAppointments Todas as consultas
     * @param allPatients Todos os pacientes
     * @param startDate Data inicial do período
     * @param endDate Data final do período
     * @param scanner Scanner para leitura de entrada
     */
    private static void showAppointmentsByPeriod(Doctor doctor, List<Appointment> allAppointments, List<Patient> allPatients,
                                                 LocalDate startDate, LocalDate endDate, Scanner scanner) {
        List<Appointment> filtered = new ArrayList<>();

        for (Appointment appointment : allAppointments) {
            if (appointment.getDoctorCRM().equals(doctor.getCode()) &&
                    (appointment.getStatus() == AppointmentStatus.COMPLETED ||
                            (appointment.hasOccurred() && appointment.getStatus() != AppointmentStatus.CANCELLED)) &&
                    appointment.isInPeriod(startDate, endDate)) {
                filtered.add(appointment);
            }
        }

        if (filtered.isEmpty()) {
            System.out.println("Nenhuma consulta encontrada no período informado.");
            return;
        }

        // Ordenar por data/hora (mais recente primeiro)
        filtered.sort(Comparator.comparing(Appointment::getDate)
                .thenComparing(Appointment::getTime).reversed());

        System.out.println("\nConsultas realizadas no período de " +
                startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                " a " +
                endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ":");

        List<String> formattedAppointments = new ArrayList<>();
        for (Appointment ap : filtered) {
            String patientName = getPatientName(allPatients, ap.getPatientCPF());
            formattedAppointments.add(ap.getFormattedDateTime() + " - Paciente: " + patientName +
                    " (CPF: " + UIUtils.formatCPF(ap.getPatientCPF()) + ")");
        }

        UIUtils.paginateList(formattedAppointments, 10, scanner);
    }

    /**
     * Interface para remarcar consultas do médico
     *
     * @param doctor Médico logado
     * @param allAppointments Todas as consultas
     * @param allPatients Todos os pacientes
     * @param scanner Scanner para leitura
     */
    private static void rescheduleAppointment(Doctor doctor, List<Appointment> allAppointments, List<Patient> allPatients, Scanner scanner) {
        System.out.println("\n=== REMARCAR CONSULTAS ===");
        System.out.println("Selecione o filtro:");
        System.out.println("1 - Consultas da semana");
        System.out.println("2 - Consultas do mês");
        System.out.println("3 - Consultas por paciente");
        System.out.println("0 - Voltar");
        System.out.print("Escolha uma opção: ");

        try {
            int option = Integer.parseInt(scanner.nextLine());

            if (option == 0) {
                return;
            }

            List<Appointment> filteredAppointments = new ArrayList<>();
            LocalDate today = LocalDate.now();

            switch (option) {
                case 1: // Consultas da semana
                    LocalDate endOfWeek = today.plusDays(7);
                    for (Appointment app : allAppointments) {
                        if (app.getDoctorCRM().equals(doctor.getCode()) &&
                                app.getStatus() == AppointmentStatus.PENDING &&
                                app.isInPeriod(today, endOfWeek)) {
                            filteredAppointments.add(app);
                        }
                    }
                    break;
                case 2: // Consultas do mês
                    LocalDate endOfMonth = today.plusMonths(1);
                    for (Appointment app : allAppointments) {
                        if (app.getDoctorCRM().equals(doctor.getCode()) &&
                                app.getStatus() == AppointmentStatus.PENDING &&
                                app.isInPeriod(today, endOfMonth)) {
                            filteredAppointments.add(app);
                        }
                    }
                    break;
                case 3: // Consultas por paciente
                    Patient patient = selectExistingPatient(allPatients, scanner);
                    if (patient == null) {
                        return;
                    }

                    for (Appointment app : allAppointments) {
                        if (app.getDoctorCRM().equals(doctor.getCode()) &&
                                app.getPatientCPF().equals(patient.getCpf()) &&
                                app.getStatus() == AppointmentStatus.PENDING) {
                            filteredAppointments.add(app);
                        }
                    }
                    break;
                default:
                    System.out.println("Opção inválida!");
                    return;
            }

            if (filteredAppointments.isEmpty()) {
                System.out.println("Nenhuma consulta encontrada para o filtro selecionado.");
                return;
            }

            // Ordenar por data/hora
            filteredAppointments.sort(Comparator.comparing(Appointment::getDate)
                    .thenComparing(Appointment::getTime));

            System.out.println("\nConsultas disponíveis para remarcação:");
            for (int i = 0; i < filteredAppointments.size(); i++) {
                Appointment app = filteredAppointments.get(i);
                String patientName = getPatientName(allPatients, app.getPatientCPF());
                System.out.println((i + 1) + " - " + app.getFormattedDateTime() +
                        " - Paciente: " + patientName);
            }

            System.out.print("\nDigite o número da consulta para remarcar (0 para voltar): ");
            int selection = Integer.parseInt(scanner.nextLine());

            if (selection == 0) {
                return;
            }

            if (selection < 1 || selection > filteredAppointments.size()) {
                System.out.println("Seleção inválida.");
                return;
            }

            Appointment selectedAppointment = filteredAppointments.get(selection - 1);
            doRescheduleAppointment(selectedAppointment, doctor, allAppointments, scanner);

        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Digite um número.");
        }
    }

    /**
     * Executa a remarcação de uma consulta
     *
     * @param appointment Consulta a ser remarcada
     * @param doctor Médico dono da consulta
     * @param allAppointments Todas as consultas
     * @param scanner Scanner para leitura
     */
    private static void doRescheduleAppointment(Appointment appointment, Doctor doctor, List<Appointment> allAppointments, Scanner scanner) {
        try {
            // Obter nova data
            System.out.print("Digite a nova data da consulta (yyyy-MM-dd): ");
            String dateStr = scanner.nextLine();

            // Obter nova hora
            System.out.print("Digite o novo horário da consulta (HH:mm): ");
            String timeStr = scanner.nextLine();

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            LocalDate newDate = LocalDate.parse(dateStr, dateFormatter);
            LocalTime newTime = LocalTime.parse(timeStr, timeFormatter);

            // Verificar se a data é no futuro
            if (newDate.isBefore(LocalDate.now())) {
                System.out.println("A data da consulta deve ser futura.");
                return;
            }

            // Verificar se já existe consulta no mesmo horário para o médico
            boolean conflictFound = false;
            for (Appointment app : allAppointments) {
                if (app != appointment &&
                        app.getDoctorCRM().equals(doctor.getCode()) &&
                        app.getDate().equals(newDate) &&
                        app.getTime().equals(newTime) &&
                        app.getStatus() == AppointmentStatus.PENDING) {
                    conflictFound = true;
                    break;
                }
            }

            if (conflictFound) {
                System.out.println("Já existe uma consulta agendada neste horário.");
                return;
            }

            // Remover a consulta antiga
            int indexToRemove = -1;
            for (int i = 0; i < allAppointments.size(); i++) {
                Appointment app = allAppointments.get(i);
                if (app.getDate().equals(appointment.getDate()) &&
                        app.getTime().equals(appointment.getTime()) &&
                        app.getPatientCPF().equals(appointment.getPatientCPF()) &&
                        app.getDoctorCRM().equals(appointment.getDoctorCRM())) {

                    indexToRemove = i;
                    break;
                }
            }

            if (indexToRemove != -1) {
                // Criar nova consulta com os mesmos dados, exceto data e hora
                Appointment newAppointment = new Appointment(
                        newDate,
                        newTime,
                        appointment.getPatientCPF(),
                        appointment.getDoctorCRM(),
                        AppointmentStatus.PENDING
                );

                // Substituir na lista
                allAppointments.set(indexToRemove, newAppointment);

                // Salvar a lista atualizada
                Appointment.saveAppointmentsToCSV(allAppointments, APPOINTMENT_CSV);

                System.out.println("Consulta remarcada com sucesso!");
                System.out.println("Nova data e hora: " + newAppointment.getFormattedDateTime());
            } else {
                System.out.println("Erro: Consulta não encontrada na lista.");
            }

        } catch (DateTimeParseException e) {
            System.out.println("Formato de data ou hora inválido: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            e.printStackTrace(System.err);

        }
    }

    /**
     * Interface para cancelar consultas agendadas do médico
     *
     * @param doctor Médico logado
     * @param allAppointments Todas as consultas
     * @param allPatients Todos os pacientes
     * @param scanner Scanner para leitura
     */
    private static void cancelAppointment(Doctor doctor, List<Appointment> allAppointments, List<Patient> allPatients, Scanner scanner) {
        List<Appointment> futureAppointments = new ArrayList<>();

        // Filtrar consultas pendentes (futuras) do médico
        for (Appointment app : allAppointments) {
            if (app.getDoctorCRM().equals(doctor.getCode()) && app.getStatus() == AppointmentStatus.PENDING) {
                futureAppointments.add(app);
            }
        }

        if (futureAppointments.isEmpty()) {
            System.out.println("Não há consultas agendadas para cancelar.");
            return;
        }

        // Ordenar por data/hora
        futureAppointments.sort(Comparator.comparing(Appointment::getDate)
                .thenComparing(Appointment::getTime));

        System.out.println("\nConsultas que podem ser canceladas:");

        for (int i = 0; i < futureAppointments.size(); i++) {
            Appointment app = futureAppointments.get(i);
            String patientName = getPatientName(allPatients, app.getPatientCPF());
            System.out.println((i + 1) + " - " + app.getFormattedDateTime() + " - Paciente: " + patientName);
        }

        System.out.print("\nDigite o número da consulta para cancelar (0 para voltar): ");
        try {
            int selection = Integer.parseInt(scanner.nextLine());

            if (selection == 0) {
                return;
            }

            if (selection < 1 || selection > futureAppointments.size()) {
                System.out.println("Seleção inválida.");
                return;
            }

            Appointment selectedAppointment = futureAppointments.get(selection - 1);

            System.out.println("\nTem certeza que deseja cancelar a consulta de " +
                    getPatientName(allPatients, selectedAppointment.getPatientCPF()) +
                    " em " + selectedAppointment.getFormattedDateTime() + "? (s/n): ");
            String confirm = scanner.nextLine();

            if (confirm.equalsIgnoreCase("s")) {
                doCancelAppointment(selectedAppointment, allAppointments);
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Digite um número.");
        }
    }

    /**
     * Executa o cancelamento de uma consulta
     *
     * @param appointment Consulta a ser cancelada
     * @param allAppointments Todas as consultas
     */
    private static void doCancelAppointment(Appointment appointment, List<Appointment> allAppointments) {
        try {
            // Encontrar e atualizar a consulta na lista
            for (Appointment app : allAppointments) {
                if (app.getDate().equals(appointment.getDate()) &&
                        app.getTime().equals(appointment.getTime()) &&
                        app.getPatientCPF().equals(appointment.getPatientCPF()) &&
                        app.getDoctorCRM().equals(appointment.getDoctorCRM())) {

                    app.setStatus(AppointmentStatus.CANCELLED);
                    break;
                }
            }

            // Salvar a lista atualizada
            Appointment.saveAppointmentsToCSV(allAppointments, APPOINTMENT_CSV);

            System.out.println("Consulta cancelada com sucesso!");

        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    /**
     * Obtém o nome do paciente a partir do CPF
     *
     * @param allPatients Lista de todos os pacientes
     * @param cpf CPF do paciente
     * @return Nome do paciente ou "(Paciente não encontrado)" se não encontrado
     */
    private static String getPatientName(List<Patient> allPatients, String cpf) {
        for (Patient p : allPatients) {
            if (p.getCpf().equals(cpf)) {
                return p.getName();
            }
        }
        return "(Paciente não encontrado)";
    }
}
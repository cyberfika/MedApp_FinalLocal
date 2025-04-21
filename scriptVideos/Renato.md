# Script de Contribuição - Renato

## Arquivos: Appointment.java e AppointmentStatus.java

"Minha contribuição para o sistema de gerenciamento de clínica médica concentrou-se no núcleo funcional das consultas médicas, desenvolvendo tanto a classe principal de Consulta quanto o sistema de status que gerencia seu ciclo de vida.

No arquivo Appointment.java, criei uma classe robusta que representa uma consulta médica no sistema. Implementei atributos fundamentais como data, hora, CPF do paciente e CRM do médico. Desenvolvi dois construtores: um básico e outro que permite definir explicitamente o status da consulta.

Adicionei métodos funcionais importantes:
- belongsToPatient: verifica se uma consulta pertence a um determinado paciente
- hasOccurred: determina se uma consulta já aconteceu com base na data atual
- isPending: verifica se a consulta ainda está pendente
- isInPeriod: determina se a consulta está dentro de um intervalo de datas específico
- getFormattedDateTime: formata data e hora para exibição padronizada

Implementei a persistência de dados com métodos para salvar e carregar consultas de arquivos CSV, incluindo o método toCSVFormat para conversão dos dados em formato adequado para armazenamento. Também adicionei métodos estáticos como filterByPatient para filtrar consultas por paciente específico.

Para complementar a funcionalidade principal, desenvolvi o arquivo AppointmentStatus.java, que implementa um enum para representar os possíveis estados de uma consulta:
- PENDING: consulta agendada para data futura
- COMPLETED: consulta que já ocorreu
- CANCELLED: consulta cancelada pelo médico ou paciente

Cada status contém uma descrição em português para melhorar a experiência do usuário, acessível através do método getDescription. Esta abordagem usando enum trouxe maior robustez ao sistema, eliminando o uso de constantes numéricas e melhorando a legibilidade do código.

O sistema de status permite o acompanhamento completo do ciclo de vida das consultas, desde o agendamento até sua conclusão ou cancelamento. A implementação facilita a verificação de disponibilidade de horários, prevenção de conflitos e gerenciamento eficiente da agenda.

Meu trabalho estabeleceu a fundação para todas as operações relacionadas a consultas no sistema, permitindo que as interfaces de médico, paciente e administrador manipulem estas informações de forma consistente e confiável."
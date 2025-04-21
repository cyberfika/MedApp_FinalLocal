# Script de Contribuição - Angelo

## Arquivos: Patient.java e PatientView.java

"Minha contribuição para o sistema de gerenciamento de clínica médica focou no desenvolvimento da entidade Paciente e sua interface de usuário.

No arquivo Patient.java, desenvolvi uma classe robusta que representa um paciente no sistema. Implementei atributos essenciais como nome e CPF, além de uma lista de consultas associadas a cada paciente. Criei construtores flexíveis que permitem inicializar um paciente com ou sem uma lista de consultas pré-definida.

Adicionei métodos importantes como getters para acesso aos dados, um método addAppointment para vincular consultas ao paciente, e um método isValidCPF para validação dos números de CPF, garantindo que contenham exatamente 11 dígitos.

Um componente fundamental foi o método loadFromCSV, que realiza a leitura de pacientes a partir de arquivos CSV, permitindo a persistência de dados entre execuções do sistema. Este método inclui tratamento de exceções e ignora linhas inválidas, garantindo robustez.

Para a interface do usuário, desenvolvi o PatientView.java, que oferece uma experiência completa para os pacientes. Implementei um menu intuitivo com opções para agendamento, visualização, remarcação e cancelamento de consultas.

Funcionalidades importantes incluem:
- Autenticação por CPF
- Visualização paginada de consultas futuras e passadas
- Marcação de novas consultas com verificação de conflitos
- Remarcação e cancelamento de consultas existentes
- Confirmação de presença em consultas agendadas

Integrei validações para garantir que datas sejam futuras e que não haja conflitos de horário. Também implementei a paginação de resultados utilizando a classe UIUtils, para melhorar a experiência ao visualizar muitas consultas.

Trabalhei na formatação adequada das informações, utilizando o método getFormattedDateTime da classe Appointment para garantir consistência na exibição de datas e horários em todo o sistema.

Esta implementação permite que pacientes gerenciem completamente suas consultas médicas, oferecendo uma experiência completa e robusta como parte fundamental do sistema de gerenciamento da clínica."
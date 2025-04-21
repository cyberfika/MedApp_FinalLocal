# Script de Contribuição - Fernando

## Arquivos: Main.java e AdminView.java

"Minha contribuição para o sistema de gerenciamento de clínica médica centrou-se no desenvolvimento do ponto de entrada da aplicação (Main.java) e da interface administrativa (AdminView.java).

No arquivo Main.java, implementei o método principal que inicializa todo o sistema. Estruturei um fluxo lógico de execução que começa com o carregamento dos dados a partir dos arquivos CSV, criando as listas de médicos, pacientes e consultas. Desenvolvi um loop principal que apresenta as opções para os diferentes tipos de usuários: administrador, paciente e médico.

Uma função crucial que implementei foi o método associateAppointmentsToPatients, que estabelece a conexão entre pacientes e suas consultas, garantindo a integridade referencial dos dados. Essa associação é fundamental para que cada paciente tenha acesso às suas consultas específicas.

Quanto ao arquivo AdminView.java, desenvolvi uma interface completa para as funções administrativas do sistema. Implementei um sistema de autenticação que verifica credenciais armazenadas em um arquivo de propriedades, garantindo que apenas usuários autorizados acessem as funcionalidades administrativas.

Criei um menu administrativo com opções para:
- Gerenciamento de médicos (cadastro, exclusão, correção e listagem)
- Gerenciamento de pacientes (cadastro, exclusão e correção)
- Gerenciamento de consultas (agendamento, cancelamento e alteração)

Uma melhoria significativa foi a implementação da funcionalidade de marcação do médico como 'removido', em vez de excluí-lo completamente do sistema, preservando o histórico. Também adicionei a função de reintegração, permitindo reverter essa exclusão lógica.

No cadastro de pacientes, implementei a funcionalidade que oferece automaticamente o agendamento de uma consulta após o cadastro, melhorando a experiência do usuário administrador.

O tratamento de exceções foi uma preocupação constante no meu desenvolvimento, garantindo que erros como entradas inválidas ou falhas no acesso aos arquivos sejam tratados adequadamente, com mensagens informativas para o usuário.

Meu trabalho estabeleceu a base estrutural do sistema e forneceu as ferramentas necessárias para a administração completa da clínica médica, integrando-se perfeitamente com os módulos desenvolvidos pelos outros membros da equipe."
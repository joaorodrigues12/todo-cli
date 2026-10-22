# todo-cli

Um gerenciador de tarefas de terminal criado para praticar Java 25, modelagem de domínio, testes automatizados, persistência em JSON e CI com Maven.

[![CI](https://github.com/joaorodrigues12/todo-cli/actions/workflows/ci.yml/badge.svg)](https://github.com/joaorodrigues12/todo-cli/actions/workflows/ci.yml)

## Requisitos

- JDK 25
- Maven 3.9 ou superior

Não há banco de dados ou serviço externo. Os dados são guardados em um arquivo JSON local.

## Instalação e compilação

Clone ou abra o projeto e compile o JAR executável com as dependências incluídas:

```bash
mvn clean verify
```

O arquivo executável será criado em `target/todo-cli-0.1.0-SNAPSHOT.jar`.

## Comandos

Execute os exemplos a partir da pasta do projeto:

```bash
java -jar target/todo-cli-0.1.0-SNAPSHOT.jar help
java -jar target/todo-cli-0.1.0-SNAPSHOT.jar add "Estudar Java 25"
java -jar target/todo-cli-0.1.0-SNAPSHOT.jar list
java -jar target/todo-cli-0.1.0-SNAPSHOT.jar complete 1de98785-5c38-4a91-b61d-78ba2c97ec4f
java -jar target/todo-cli-0.1.0-SNAPSHOT.jar filter --status pending
java -jar target/todo-cli-0.1.0-SNAPSHOT.jar filter --status completed
```

O `add` recebe a descrição como um único argumento; use aspas para descrições com espaços. O comando `complete` recebe o UUID apresentado ao adicionar ou listar uma tarefa.

Por padrão, os dados ficam em `.todo-cli/tasks.json`, relativo à pasta de execução. Para usar outro arquivo, informe a propriedade `todo.file` antes de `-jar`:

```bash
java -Dtodo.file="$HOME/.local/share/todo-cli/tasks.json" -jar target/todo-cli-0.1.0-SNAPSHOT.jar list
```

No PowerShell, por exemplo:

```powershell
java "-Dtodo.file=$HOME\.todo-cli\tasks.json" -jar target\todo-cli-0.1.0-SNAPSHOT.jar list
```

## Arquitetura

- **`domain`**: `Task` é um record imutável com UUID, descrição, `Instant` de criação e estado. A validação do record impede estados incoerentes; concluir cria outra instância e preserva a primeira data de conclusão.
- **`application`**: `TaskService` coordena as operações usando a interface `TaskRepository`. `TodoCli` integra parser, serviço e saída do terminal.
- **`cli`**: `Command` é uma sealed interface cujas únicas implementações permitidas são os records aninhados `Add`, `ListAll`, `Complete`, `Filter` e `Help`. O compilador pode verificar que o tratamento dos comandos cobre esse conjunto fechado.
- **`persistence`**: `JsonTaskRepository` usa Jackson e o módulo Java Time para ler e gravar os records em JSON.

A separação permite testar regras, parser, arquivo e fluxo da CLI sem misturar essas responsabilidades. O serviço depende de uma interface, então seus testes podem usar repositórios temporários ou substitutos em memória.

## Testes

Execute toda a verificação Maven:

```bash
mvn verify
```

Os testes cobrem invariantes e conclusão do domínio, argumentos válidos e inválidos do parser, leitura/escrita e corrupção do JSON, além do fluxo de adicionar, listar, concluir e filtrar. Os testes de persistência e integração usam diretórios temporários do JUnit.

## Limitações

- A escrita usa arquivo temporário no mesmo diretório e tenta substituir o destino atomicamente. Alguns sistemas de arquivos não suportam movimento atômico; nesse caso, o repositório usa uma substituição normal.
- A substituição reduz a chance de deixar um JSON parcialmente gravado, mas não implementa bloqueio entre processos. Duas instâncias simultâneas podem ler a mesma versão e a última gravação pode descartar alterações da outra.
- Se o JSON estiver inválido ou incompatível com o modelo, o comando falha e informa o caminho do arquivo. O conteúdo original é preservado para diagnóstico ou recuperação manual.
- O arquivo local não é criptografado e não substitui um banco de dados para uso multiusuário.
- A criação de um JAR executável não disponibiliza automaticamente um comando global chamado `todo`; por enquanto, execute-o com `java -jar`.

## CI

O workflow [CI](.github/workflows/ci.yml) configura o JDK 25 e executa `mvn --batch-mode --no-transfer-progress verify` em push e pull request.

## Roadmap

- [x] Criar estrutura Maven, domínio, parser, armazenamento JSON e CLI.
- [x] Adicionar testes automatizados e workflow de CI.
- [x] Publicar o repositório no GitHub e configurar o badge real do workflow.
- [ ] Avaliar exportação e remoção de tarefas sem comprometer a simplicidade do projeto.
- [ ] Considerar empacotamento nativo ou instalador após estabilizar a interface de comandos.

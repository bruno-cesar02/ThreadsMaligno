# ThreadsMaligno

Aplicação Java de terminal para comparar a ordenação de um vetor de números com uma única thread e com múltiplas threads. O projeto utiliza **Merge Sort** e permite consultar o resultado e os tempos de execução pelo menu interativo.

## Funcionalidades

- Criar vetores de valores do tipo `byte`, manualmente ou com números aleatórios.
- Ordenar em ordem crescente com uma única thread.
- Dividir o vetor em partes e ordená-las com múltiplas threads.
- Intercalar as partes ordenadas até obter o vetor completo.
- Exibir um intervalo do vetor e consultar os tempos das duas modalidades.
- Criar outro vetor sem reiniciar o programa.

## Requisitos

- **JDK 8 ou superior**, com os comandos `java` e `javac` disponíveis no terminal.
- Nenhuma dependência externa: o projeto utiliza apenas a biblioteca padrão do Java.

O repositório não define uma versão específica do JDK e não utiliza Maven ou Gradle.

## Como executar

Clone o repositório e entre na pasta do projeto:

```bash
git clone https://github.com/bruno-cesar02/ThreadsMaligno.git
cd ThreadsMaligno
```

Compile os arquivos e execute a classe principal:

```bash
mkdir -p out
javac -encoding UTF-8 -d out src/*.java
java -cp out Main
```

Os comandos acima são para Bash, como no Linux, macOS ou Git Bash. A pasta `out/` está no `.gitignore`.

### IntelliJ IDEA

1. Abra a pasta do projeto.
2. Configure um JDK nas configurações do projeto.
3. Caso necessário, marque `src/` como **Sources Root**.
4. Execute o método `main` de `Main.java`.

## Como usar

Ao iniciar, informe o tamanho do vetor. Em seguida, digite `s` para gerar valores aleatórios ou `n` para inserir os valores manualmente. Cada resposta deve ser enviada em uma linha.

Os valores devem estar entre **-128 e 127**, intervalo permitido pelo tipo `byte`.

| Opção | Ação |
| --- | --- |
| `1` | Ordenar com uma única thread, sem dividir o trabalho entre threads. |
| `2` | Ordenar com múltiplas threads e intercalar os resultados. |
| `3` | Mostrar um intervalo do vetor. |
| `4` | Montar outro vetor. |
| `5` | Encerrar o programa. |
| `6` | Mostrar os tempos de ordenação em milissegundos. |

Na opção `3`, as posições começam em **1** e os dois limites são inclusivos. Antes de ordenar, o programa mostra o vetor original; após ordenar, mostra o resultado da última ordenação.

### Exemplo

Para ordenar os valores `5`, `-2`, `3` e `0`:

1. Informe `4` como tamanho do vetor.
2. Responda `n` à geração automática.
3. Digite os quatro valores, um por linha.
4. Escolha a opção `1` ou `2`.
5. Escolha a opção `3` e informe os limites `1` e `4`.

O trecho exibido será:

```text
[-2, 0, 3, 5]
```

## Como a ordenação funciona

`Ordenadora` implementa o Merge Sort: divide o vetor em duas metades, ordena cada metade recursivamente e combina os resultados com `Misturadora.merge`.

Na opção paralela, a quantidade de threads de ordenação é calculada como:

```java
Math.max(1, Runtime.getRuntime().availableProcessors() - 1)
```

O vetor é dividido entre essas threads, distribuindo também os elementos restantes quando a divisão não é exata. Após aguardar as ordenações com `join()`, o programa intercala os resultados aos pares usando threads `Misturadora`. Um pedaço sem par segue para a próxima rodada até restar um único vetor.

As duas opções preservam a entrada original, permitindo comparar as modalidades sobre os mesmos dados. Os tempos ficam disponíveis na opção `6` e são reiniciados ao criar outro vetor.

## Estrutura do projeto

```text
ThreadsMaligno/
├── src/
│   ├── Main.java
│   ├── Ordenadora.java
│   ├── Misturadora.java
│   ├── Teclado.java
│   └── MaiorVetorAproximado.java
├── .gitignore
└── README.md
```

| Classe | Responsabilidade |
| --- | --- |
| `Main` | Entrada do programa, menu, divisão do vetor e medição dos tempos. |
| `Ordenadora` | Thread que ordena uma cópia do vetor recebido usando Merge Sort. |
| `Misturadora` | Intercala dois vetores já ordenados; também oferece o método estático `merge`. |
| `Teclado` | Leitura e conversão dos dados informados no terminal. |
| `MaiorVetorAproximado` | Experimento independente para estimar o tamanho de um `byte[]` que cabe na memória disponível. |

## Experimento de memória

Após compilar o projeto, execute:

```bash
java -Xmx256m -cp out MaiorVetorAproximado
```

O parâmetro `-Xmx256m` limita o heap da JVM a 256 MB nesse experimento. A classe tenta alocar vetores progressivamente maiores até encontrar uma falha de memória ou atingir seu limite de crescimento.

O resultado é o maior tamanho **testado** com sucesso, não o máximo exato nem uma garantia de tamanho seguro para ordenação. O Merge Sort cria vetores auxiliares e exige memória adicional.


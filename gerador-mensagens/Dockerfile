FROM openjdk:17-jdk-slim

# Instalar Maven
RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*

# Definir diretório de trabalho
WORKDIR /app

# Copiar arquivos de configuração Maven
COPY pom.xml .

# Baixar dependências
RUN mvn dependency:resolve

# Copiar código fonte
COPY src ./src

# Compilar aplicação
RUN mvn package -DskipTests

# Criar diretório para imagens
RUN mkdir -p /app/images

# Comando para executar a aplicação
CMD ["java", "-jar", "target/gerador-mensagens-1.0.0.jar"]
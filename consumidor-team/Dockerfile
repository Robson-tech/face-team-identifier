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

# Comando para executar a aplicação
CMD ["java", "-jar", "target/consumidor-team-1.0.0.jar"]
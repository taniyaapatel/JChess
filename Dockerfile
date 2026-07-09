FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

RUN mkdir -p out && javac -d out $(find src -name "*.java")

EXPOSE 8080

CMD ["sh", "-c", "java -cp out com.chess.Main ${PORT:-8080}"]

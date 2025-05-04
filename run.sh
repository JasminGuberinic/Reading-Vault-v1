#!/bin/bash
#!/bin/bash

# Kompajliranje aplikacije
./gradlew clean build

# Provjeri da li je build uspješan
if [ $? -ne 0 ]; then
    echo "Build nije uspješan. Prekidam izvršavanje."
    exit 1
fi

# Pronađi tačno ime JAR fajla
JAR_FILE=$(find build/libs -name "*-all.jar" | head -n 1)

if [ -z "$JAR_FILE" ]; then
    echo "Nije pronađen JAR fajl. Provjerite build proces."
    exit 1
fi

echo "Pokrećem aplikaciju iz: $JAR_FILE"

# Pokretanje aplikacije
java -jar "$JAR_FILE" server
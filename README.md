# Job-App-Tracker

hadi hiya la branche principale dyal backend li anbqaw nmergiw fiha ay tache salat

## Télécharger l'image officielle

9bel khas ikon docker cha3l men moraha :

Ouvrez votre terminal et exécutez la commande suivante :
```bash
docker pull mongodb/mongodb-community-server:latest
```
apres matkmel f l pull dyal tapew :
```bash
docker run --name mongodb -p 27017:27017 -d mongodb/mongodb-community-server:latest
```
Apres siro l terminal o diro la commande :
```bash
docker exec -it mongodb mongosh
```
Apres fesh dkhlo lih diro la commande besh t9dro diro la creation dyal la base de données dyal service--notifications:
```bash
use service-notifications
```

--------------------------------
salma fathi : 14/11/2026

*Dans pom.xml du service utilisateur j'ai ajoute 
 <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>

<dependency>
    <groupId>org.keycloak</groupId>
    <artifactId>keycloak-admin-client</artifactId>
    <version>26.0.0</version>
</dependency>



*Dans docker pour keycloak

docker run -p 8180:8080 \
  -e KC_BOOTSTRAP_ADMIN_USERNAME=admin \
  -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin \
  quay.io/keycloak/keycloak:26.0 start-dev


*Dans application.properties(client-secret: Client -> user-service -> Credentials -> Client secret)

keycloak.server-url=http://localhost:8180
keycloak.realm=jobtracker
keycloak.admin-client-id=user-service
keycloak.login-client-id=jobtracker-frontend
keycloak.admin-client-secret=WK7YAlXmj4L0KSnuPp39xTNJbbkzHlmp

logging.level.org.springframework.security=DEBUG




# Jalon V5 - Service Notification

## 1. Configuration du serveur SMTP local avec Mailpit

Après l'ajout de la dépendance **Java Mail Sender**, nous avons installé un serveur SMTP local à l'aide de **Mailpit** via Docker :

```bash
docker run -d --name mailpit \
-p 127.0.0.1:1025:1025 \
-p 127.0.0.1:8025:8025 \
axllent/mailpit
```

Une fois le conteneur démarré, l'interface web de Mailpit sur **http://localhost:8025** qui permet de visualiser les emails envoyés par l'application.

![Capture du serveur SMTP local Mailpit](job-tracker-app/mailpit-screen.png)

---

## 2. Configuration du broker RabbitMQ

Après l'ajout de la dépendance **Spring for RabbitMQ** avec son interface d'administration :

```bash
docker run -d --name rabbitmq \
-p 127.0.0.1:5672:5672 \
-p 127.0.0.1:15672:15672 \
-e RABBITMQ_DEFAULT_USER=tp \
-e RABBITMQ_DEFAULT_PASS=tp123 \
-v tp-rabbitmq-data:/var/lib/rabbitmq \
rabbitmq:4-management
```

L'interface d'administration RabbitMQ est accessible sur le port **http://localhost:15672**.

---

## 3. Test de l'envoi d'emails via Swagger UI

Après la configuration du service d'envoi d'emails, nous avons testé l'API à l'aide de **Swagger UI** afin de vérifier le bon fonctionnement de l'envoi des messages vers le serveur SMTP Mailpit.

![Capture du Test Swagger](job-tracker-app/test-swagger.png)

### Dépendance Swagger utilisée

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>3.2.0</version>
</dependency>
```

---

## 4. Communication asynchrone avec RabbitMQ

Après la configuration de **RabbitMQ**, de la classe **RabbitConfig** et de **RabbitTemplate**, nous avons mis en place la communication asynchrone entre les services.

Lors de la création d'une candidature, un message est publié dans RabbitMQ afin d'être traité par le service de notification.

Test de l'API de création d'une candidature :

![Capture du Test Api](job-tracker-app/test-api.png)

---

## 5. Résultat final

Une fois la candidature créée, le message est envoyé à RabbitMQ puis consommé par le service de notification, qui génère et envoie un email de confirmation au candidat via Mailpit.

![Capture d'une Candidature crée](job-tracker-app/candidature-cree.png)



# 📌 Job Tracker App — Microservices

Application de suivi de candidatures d'emploi basée sur une **architecture microservices**. Chaque service possède sa propre responsabilité, sa propre base de données et communique via **HTTP** (requêtes synchrones) et **Kafka** (événements asynchrones).

---

## 🏗 Architecture globale

![Architecture globale du système](architecture-projet-microservices.png)

> *Schéma de l'architecture microservices du Job Tracker : Frontend Angular → Gateway → Services métier → Bases de données + Kafka.*

**Infrastructure :** Service Gateway (`8081`) · Config Server (`8888`) · Service Discovery (`8080`) · Kafka

| Service | Port | BDD | Responsabilité |
|---|---|---|---|
| **service-utilisateurs** | `8082` | PostgreSQL | Identité, authentification JWT, profil |
| **service-candidatures** | `8083` | PostgreSQL | CRUD des candidatures, statuts |
| **service-notifications** | `8084` | MongoDB | Rappels et notifications |
| **service-statistiques** | `8085` | PostgreSQL | Analyses et taux de réponse |

---

## 📐 Diagramme de classes

![Diagramme de classes](diagramme-classe.jpeg)

> *Modèle de données des services et flux d'événements Kafka.*

- **`Application`** : `id`, `userId`, `companyName`, `jobTitle`, `status`, `appliedAt`, `nextFollowUpDate`…
- **`ApplicationStatus`** (enum) : `SENT`, `INTERVIEW`, `REJECTED`, `ACCEPTED`
- **`User`** : `id`, `nom`, `prenom`, `email`, `password`, `createdAt`, `updatedAt`
- **`ApplicationEvent`** : `eventType`, `oldStatus`, `newStatus`
- **`Notification`** : `userId`, `applicationId`, `type`, `title`, `message`, `read`
- **`Statistics`** : agrégats par utilisateur (`totalApplications`, `interviews`, `rejectedApplications`…)


---

## 📨 Événements Kafka

- **`APPLICATION_CREATED`** → création d'une candidature
- **`APPLICATION_STATUS_CHANGED`** → changement de statut (`oldStatus` → `newStatus`)
- **`APPLICATION_FOLLOW_UP_REQUIRED`** → relance nécessaire

---

## 🎯 Périmètre V1

**Obligatoire :**
- **Utilisateurs** : Inscription, Connexion, JWT, Profil
- **Candidatures** : CRUD, Statuts, Filtres principaux
- **Notifications** : Kafka, Rappels, Marquer comme lue
- **Statistiques** : Total, Répartition par statut, Entretiens, Refus, Acceptations, Taux de réponse

**V2 :** Recherche avancée · Statistiques par période · Graphiques · Salaire · Localisation · Notes · Photo de profil

---

## 🛠 Stack technique

Angular · Spring Cloud Gateway · Spring Boot · JWT · Spring Cloud Config · Eureka · Apache Kafka · PostgreSQL · MongoDB

---

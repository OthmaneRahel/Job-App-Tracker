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

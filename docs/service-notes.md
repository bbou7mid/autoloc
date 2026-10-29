# Atelier 4 — Couche Service, IoC et Injection de Dépendances

## A. Modes d'injection comparés

| Critère | Injection par constructeur (recommandée) | Injection par attribut (`@Autowired` sur champ) |
| :--- | :--- | :--- |
| **Champ `final` possible ?** | **Oui** (l'immutabilité est garantie dès l'instanciation) | **Non** (les champs ne peuvent pas être `final`) |
| **Dépendance visible dans la signature ?** | **Oui** (explicitement déclarée dans les paramètres du constructeur) | **Non** (masquée, non visible de l'extérieur sans introspection) |
| **Utilisable avec `new` hors conteneur ?** | **Oui** (idéal pour les tests unitaires sans contexte Spring / mocks passés au `new`) | **Non** (la dépendance reste `null`, nécessite reflection ou conteneur) |
| **Signalé par SonarQube for IDE ?** | **Non** (conforme aux règles de Clean Code) | **Oui** (Règle `java:S6813` : *"Avoid field injection"*) |

---

## B. Services de l'application AutoLoc

| Service | Dépendances injectées | Mode | Justification |
| :--- | :--- | :--- | :--- |
| `AgenceServiceImpl` | `IAgenceRepository` | Constructeur (`@RequiredArgsConstructor`) | Dépendance obligatoire, immutabilité garantie (`final`), instanciation sûre sans état modifiable. |
| `ClientServiceImpl` | `IClientRepository` | Constructeur (`@RequiredArgsConstructor`) | Injection explicite via interface, découplage fort et testabilité unitaire aisée. |
| `ContratServiceImpl` | `IContratRepository` | Constructeur (`@RequiredArgsConstructor`) | Accès au proxy Spring Data JPA pour les opérations CRUD et cascade sur les paiements. |
| `EmployeServiceImpl` | `IEmployeRepository` | Constructeur (`@RequiredArgsConstructor`) | Dépendance injectée à l'initialisation, aucun risque d'état incohérent (`null`). |
| `EquipementServiceImpl` | `IEquipementRepository` | Constructeur (`@RequiredArgsConstructor`) | Respect strict des conventions IoC Spring et immutabilité. |
| `MaintenanceServiceImpl` | `IMaintenanceRepository` | Constructeur (`@RequiredArgsConstructor`) | Injection d'abstraction (interface), suppression de tout couplage fort. |
| `PaiementServiceImpl` | `IPaiementRepository` | Constructeur (`@RequiredArgsConstructor`) | Service en lecture seule, injection propre du repository sans injection par champ. |
| `ReservationServiceImpl` | `IReservationRepository` | Constructeur (`@RequiredArgsConstructor`) | Dépendance obligatoire injectée par constructeur unique généré par Lombok. |
| `VehiculeServiceImpl` | `IVehiculeRepository` | Constructeur (`@RequiredArgsConstructor`) | Service métier sans état injecté de façon robuste par son constructeur. |

---

## C. Diagnostic des messages d'erreur du conteneur Spring

### Message 1 : `Parameter 0 of constructor in ContratController required a bean of type 'IContratService' that could not be found`
- **Deux causes les plus probables :**
  1. *Oubli de l'annotation `@Service`* sur `ContratServiceImpl` : Spring ne détecte pas la classe comme un bean géré par le conteneur IoC lors du component scan.
  2. *Problème de package / scan* : `ContratServiceImpl` se trouve dans un package non scanné (en dehors de l'arborescence racine `@SpringBootApplication` de `tn.esprit.autoloc`).
- **Correction :** Vérifier que `ContratServiceImpl` est annoté avec `@Service` et placé dans le sous-package `tn.esprit.autoloc.service`.

### Message 2 : `Parameter 0 of constructor in NotificationController required a single bean, but 2 were found: emailNotificateur, smsNotificateur`
- **Correction 1 (avec `@Primary`) :** Placer `@Primary` sur la classe de l'implémentation par défaut (ex. `@Primary @Component public class EmailNotificateur ...`).
- **Correction 2 (avec `@Qualifier`) :** Placer `@Qualifier("emailNotificateur")` directement sur le paramètre du constructeur du consommateur (`NotificationController(@Qualifier("emailNotificateur") INotificateur notificateur)`).

### Message 3 : `The dependencies of some of the beans in the application context form a cycle: clientServiceImpl <-> reservationServiceImpl`
- **Pourquoi l'injection par constructeur rend-elle ce cycle impossible à contourner ?**
  Pour créer le bean `clientServiceImpl`, Spring doit d'abord appeler son constructeur en lui passant `reservationServiceImpl`. Mais pour créer `reservationServiceImpl`, Spring doit d'abord lui passer `clientServiceImpl`. Aucun des deux ne peut être instancié en premier.
- **Piste de conception pour supprimer le cycle :**
  1. Extraire la logique partagée dans un troisième service intermédiaire (ex. `ReservationValidationService`).
  2. Faire communiquer les services via des événements Spring d'application (`ApplicationEventPublisher` / `@EventListener`).

---

## D. Anomalies SonarQube for IDE relevées et corrigées

1. **Règle `java:S6813` (Field dependency injection should not be used)** :
   - *Explication :* L'injection par champ (`@Autowired private ...`) nuit à la testabilité unitaire et empêche de rendre le champ `final`.
   - *Correction :* Utilisation de champs `private final` et de l'annotation `@RequiredArgsConstructor` de Lombok.
2. **Règle `java:S112` (Generic exceptions should never be thrown)** :
   - *Explication :* Lever une exception générique telle que `RuntimeException` masque la nature métier de l'erreur.
   - *Correction :* Création et utilisation d'une exception métier explicite `ResourceNotFoundException`.
3. **Règle `java:S1144` / imports inutilisés et code mort** :
   - *Explication :* Présence d'imports inutilisés ou de classes sans constructeur adéquat.
   - *Correction :* Nettoyage complet des imports et structuration stricte des packages.

---

## E. Questions de compréhension

1. **Où est le `new` de `ContratServiceImpl` et qui l'exécute ?**
   Il n'y a aucun `new` explicite dans le code source du projet. C'est le conteneur IoC de Spring (la `BeanFactory` / l'`ApplicationContext`) qui instancie la classe par réflexion au démarrage lors du scan des composants `@Service`.

2. **Pourquoi un contrôleur dépendra-t-il de `IContratService` et non de `ContratServiceImpl` ?**
   Pour respecter le principe d'inversion des dépendances (DIP des principes SOLID). Le contrôleur dépend d'un contrat d'interface abstrait, ce qui permet de modifier ou remplacer l'implémentation (ou d'utiliser un mock lors des tests) sans jamais impacter le contrôleur.

3. **Pourquoi un service singleton doit-il rester sans état ? Donner un exemple de bug.**
   Un bean singleton est partagé par tous les threads et toutes les requêtes HTTP concurrentes de l'application. S'il possède un attribut d'instance mutable (ex. `private Long dernierIdClientConsulte`), les requêtes de plusieurs utilisateurs simultanés vont écraser cette variable en concurrence (Race Condition), provoquant des corruptions de données et des fuites d'informations entre utilisateurs.

4. **Pourquoi `update` charge-t-il l'entité existante avant d'appeler `save` au lieu de lui passer directement l'objet reçu ?**
   Passer directement l'objet reçu à `save` déclencherait un écrasement de tous les champs non renseignés (qui deviendraient `null` en base) et, en présence de relations avec `orphanRemoval = true` (comme les paiements d'un contrat), cela supprimerait involontairement tous les enregistrements enfants associés. Le chargement préalable permet de n'appliquer que les modifications autorisées.

5. **Différence `@Component` vs `@Bean` ? `@Primary` vs `@Qualifier` ? Pourquoi `IPaiementService` est en lecture seule ?**
   - `@Component` s'applique au niveau de la classe pour que Spring la détecte automatiquement par scan. `@Bean` s'applique sur une méthode dans une classe de configuration `@Configuration` pour déclarer et instancier manuellement un bean (souvent issu d'une bibliothèque tierce).
   - `@Primary` définit le bean prioritaire par défaut en cas d'ambiguïté. `@Qualifier` permet au consommateur de nommer explicitement le bean précis qu'il souhaite injecter.
   - `IPaiementService` est en lecture seule (`findById`, `findAll`) car dans le modèle métier AutoLoc, un paiement fait partie intégrante d'un contrat (relation de composition avec cascade). Les créations, modifications et suppressions de paiements doivent impérativement être orchestrées via le contrat pour maintenir l'intégrité financière.

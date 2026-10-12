# Atelier 2 : associations, cascade et fetch

## Objectif

Le mapping JPA traduit le modèle AutoLoc tout en respectant le cycle de vie
métier des entités. Les associations utilisent explicitement `FetchType.LAZY`.
Les cascades sont limitées aux opérations attendues : une association ne signifie
pas automatiquement que les deux entités doivent être créées ou supprimées ensemble.

Les classes concernées se trouvent dans le package
[Domain](../src/main/java/tn/esprit/hamzaslimani4ieti4/Domain).

## Synthèse des choix

La cascade indiquée s'applique dans le sens de la flèche. Sauf mention contraire,
`orphanRemoval` vaut `false` et le côté inverse ne possède aucune cascade.

| Association | Fetch | Cascade | Justification métier |
|---|---|---|---|
| Contrat → Paiement | LAZY | ALL + orphanRemoval | Les paiements dépendent du contrat. Les opérations sur le contrat se propagent à ses paiements. Supprimer le contrat supprime ses paiements ; retirer un paiement de la collection d'un contrat géré entraîne aussi sa suppression au flush. |
| Agence → Véhicule | LAZY | Aucune | Le véhicule conserve un cycle de vie indépendant et peut changer d'agence. Les opérations sur l'agence ne doivent pas créer, modifier ou supprimer ses véhicules automatiquement. |
| Agence → Employé | LAZY | Aucune | Un employé peut être réaffecté. Supprimer une agence ne doit pas supprimer les employés. |
| Véhicule ↔ Équipement | LAZY | Aucune, des deux côtés | Les équipements sont partagés. Supprimer un véhicule ne doit pas supprimer ses équipements. Un `Set` des deux côtés évite les doublons d'objets selon leur égalité Java. |
| Client → Réservation | LAZY | PERSIST | La création d'un client peut persister ses nouvelles réservations. MERGE et REMOVE ne sont pas propagés : les réservations existantes se gèrent explicitement et leur historique doit être conservé. |
| Réservation → Véhicule | LAZY | Aucune | Le véhicule appartient au parc, pas au cycle de vie de la réservation. Il doit déjà être persisté ou être enregistré explicitement. |
| Véhicule → Réservation | LAZY | Aucune | Le lien inverse sert à consulter les réservations. Supprimer un véhicule ne doit pas effacer l'historique des locations. |
| Réservation → Contrat | LAZY | ALL | Le contrat accompagne la réservation : PERSIST, MERGE, REMOVE, REFRESH et DETACH sont propagés. `orphanRemoval` n'est pas activé ; retirer ou remplacer la référence ne supprime donc pas automatiquement l'ancien contrat. |
| Véhicule → Maintenance | LAZY | PERSIST | La création d'un véhicule peut persister ses nouvelles maintenances. Leur suppression n'est pas propagée pour préserver l'historique d'entretien. |

Les associations inverses Véhicule → Agence, Employé → Agence,
Réservation → Client, Maintenance → Véhicule, Contrat → Réservation et
Paiement → Contrat sont également `LAZY`, sans cascade. Une opération sur un
enfant ne doit pas supprimer son parent.

L'absence de cascade REMOVE ne garantit pas que la suppression d'un parent soit
autorisée par la base : les clés étrangères peuvent la bloquer. Les références
doivent alors être réaffectées ou retirées explicitement avant cette suppression.

## Multiplicités et côtés propriétaires

Le côté propriétaire écrit la clé étrangère ou la table de jointure.
`mappedBy` désigne le nom du champ Java propriétaire, et non une colonne SQL.

| Association | Annotations | Côté propriétaire / stockage | Côté inverse |
|---|---|---|---|
| Agence ↔ Véhicule | OneToMany / ManyToOne | `Vehicule.agence` : `vehicule.id_agence` | `Agence.vehicules`, mappedBy = `agence` |
| Agence ↔ Employé | OneToMany / ManyToOne | `Employe.agence` : `employe.id_agence` | `Agence.employes`, mappedBy = `agence` |
| Véhicule ↔ Équipement | ManyToMany / ManyToMany | `Vehicule.equipements` : table `vehicule_equipement`, colonnes `id_vehicule` et `id_equipement` | `Equipement.vehicules`, mappedBy = `equipements` |
| Client ↔ Réservation | OneToMany / ManyToOne | `Reservation.client` : `reservation.id_client` | `Client.reservations`, mappedBy = `client` |
| Véhicule ↔ Réservation | OneToMany / ManyToOne | `Reservation.vehicule` : `reservation.id_vehicule` | `Vehicule.reservations`, mappedBy = `vehicule` |
| Réservation ↔ Contrat | OneToOne / OneToOne | `Contrat.reservation` : `contrat.id_reservation`, contrainte UNIQUE | `Reservation.contrat`, mappedBy = `reservation` |
| Véhicule ↔ Maintenance | OneToMany / ManyToOne | `Maintenance.vehicule` : `maintenance.id_vehicule` | `Vehicule.maintenances`, mappedBy = `vehicule` |
| Contrat ↔ Paiement | OneToMany / ManyToOne | `Paiement.contrat` : `paiement.id_contrat` | `Contrat.paiements`, mappedBy = `contrat` |

Les noms des clés étrangères sont fixés par `@JoinColumn`; la table
Véhicule/Équipement est fixée par `@JoinTable`. La cascade ALL de
Réservation → Contrat est volontairement placée sur le côté inverse :
la propriété de la relation et la propagation du cycle de vie sont deux notions
distinctes.

Les deux côtés d'une association bidirectionnelle doivent être synchronisés en
Java. Par exemple, ajouter un paiement à `contrat.getPaiements()` doit aussi
s'accompagner de `paiement.setContrat(contrat)`. Une cascade ne renseigne pas
automatiquement la clé étrangère si le champ propriétaire n'est pas affecté.

## Stratégie de chargement

- Charger une agence ne doit pas charger systématiquement tous ses véhicules et
  employés ; charger un véhicule ne doit pas charger tout son historique,
  ses réservations et ses équipements.
- `LAZY` est une demande de chargement différé, pas une garantie d'absence de
  requêtes. En particulier, le côté inverse d'un OneToOne optionnel peut nécessiter
  une requête pour déterminer si le contrat existe. Le comportement réel dépend
  du fournisseur JPA et de l'instrumentation bytecode.
- Accéder aux associations nécessaires dans une transaction active. Pour une
  future API, construire des DTO plutôt que sérialiser directement tout le graphe
  d'entités.
- Pour un écran ou un traitement nécessitant une association, utiliser une requête
  ciblée avec `JOIN FETCH` ou un graphe d'entités. Cela limite les requêtes N+1 sans
  rendre toutes les associations EAGER.

## Conventions Clean Code

- Lombok ciblé : `@Getter`, `@Setter`, `@NoArgsConstructor`,
  `@AllArgsConstructor`, `@Builder` et `@ToString`, sans `@Data`.
- `@ToString.Exclude` sur les associations évite les récursions bidirectionnelles
  et les chargements déclenchés par l'affichage de ces relations.
- Nommage conservé pour rester cohérent avec les classes déjà présentes et le
  modèle du projet. Les champs de collection sont au pluriel.
- Aucun `@EqualsAndHashCode` automatique sur le graphe d'entités. Les `Set`
  utilisent actuellement l'identité Java des objets : deux instances distinctes
  représentant la même ligne ne sont pas automatiquement considérées égales.

## Capture de référence

![Schéma MySQL AutoLoc fourni pour l'atelier 2](images/schema-autoloc.png)

Source : capture fournie par l'auteur, datée du 12 octobre 2026 à 11:46:05.
Elle illustre les tables et les liens visibles à cet instant, mais ne constitue
pas une validation complète du mapping final.

La capture montre aussi des traces d'un ancien schéma : la table
`vehicule_equipements` apparaît en plus de `vehicule_equipement`, et la colonne
`vehicule.agence_id_agence` apparaît au lieu de `vehicule.id_agence`.
Le mapping actuel attend uniquement la table de jointure `vehicule_equipement`
et la colonne `id_agence`. Ces traces doivent être vérifiées lors d'une nouvelle
génération dans une base de test dédiée ; aucune table n'a été supprimée pour
modifier cette capture.

## Validation et limites

Le test [DomainAssociationsTests](../src/test/java/tn/esprit/hamzaslimani4ieti4/Domain/DomainAssociationsTests.java)
vérifie :

1. La validité des métadonnées Hibernate pour les neuf entités.
2. Les propriétaires, `mappedBy`, noms des colonnes, table de jointure, types
   `Set`, fetch, cascades et `orphanRemoval` déclarés.
3. L'absence de récursion de `toString()` sur un graphe bidirectionnel.

Commande Windows :

```powershell
.\mvnw.cmd -Dtest=DomainAssociationsTests test
```

Ces tests s'exécutent sans connexion MySQL. Ils ne vérifient pas les suppressions
réelles au flush, les cascades en base ni le nombre de requêtes du chargement
différé. Ces comportements restent à tester dans une base isolée.

La configuration actuelle utilise `spring.jpa.hibernate.ddl-auto=create-drop` :
elle est destinée à l'atelier et peut supprimer les tables mappées au démarrage
et à l'arrêt. Ne pas lancer cette configuration sur une base contenant des
données à conserver. Les identifiants de production doivent être externalisés,
sans être versionnés.
